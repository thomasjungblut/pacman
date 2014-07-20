package de.jungblut.agents;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.PlanningEngine;
import de.jungblut.gameplay.listener.FoodConsumerListener;
import de.jungblut.gameplay.listener.GameStateListener;
import de.jungblut.gameplay.maze.Maze;
import de.jungblut.gameplay.maze.Maze.Direction;
import de.jungblut.graph.DenseGraph;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.dense.DenseDoubleVector;
import de.jungblut.utils.ArrayUtils;

/**
 * An agent that learns to play pacman through approximate qlearning. <a href=
 * "http://inst.eecs.berkeley.edu/~cs188/sp09/projects/reinforcement/reinforcement.html"
 * >Some nice wrap up can be found here</a>
 * 
 * @author thomas.jungblut
 * 
 */
public class QLearningAgent extends EnvironmentAgent implements
    GameStateListener, FoodConsumerListener {

  private static final Log LOG = LogFactory.getLog(QLearningAgent.class);

  public static final int NUM_FEATURES = 16;

  private static final double EXPLORATION_PROBABILITY = 0.05;
  private static final double LEARNING_RATE = 0.3;
  private static final double DISCOUNT_FACTOR = 0.8;

  private static final double FOOD_REWARD = 1;
  private static final double WON_REWARD = 10;
  private static final double LOST_REWARD = -10;

  private DoubleVector weights;
  private DoubleVector lastActionFeatures;

  private DenseGraph<Object> graph;
  private Random rand = new Random();

  public QLearningAgent(Maze env, DoubleVector initialWeights) {
    super(env);
    graph = FollowerGhost.createGraph(env);
    lastActionFeatures = null;
    if (initialWeights == null) {
      initialWeights = new DenseDoubleVector(NUM_FEATURES);
    }
    this.weights = initialWeights;
  }

  @Override
  public void move(Environment env) {
    // explore
    if (rand.nextDouble() > (1d - EXPLORATION_PROBABILITY)) {
      direction = RandomGhost.getRandomDirection(env.getMaze(), getXPosition(),
          getYPosition(), rand);
    } else {
      // check for every q value from this direction
      double[] qValues = new double[4];
      DoubleVector[] features = new DoubleVector[4];
      for (Direction d : Direction.values()) {
        features[d.getIndex()] = getFeatures(env, x, y, d);
        qValues[d.getIndex()] = getQValue(features[d.getIndex()]);
      }
      int selectedAction = ArrayUtils.maxIndex(qValues);
      direction = Direction.values()[selectedAction];
      lastActionFeatures = features[selectedAction];
    }
    super.move(env);
  }

  public DoubleVector getFeatures(Environment env, int x, int y,
      Direction direction) {
    Point point = Maze.getPoint(x, y, direction);
    if (graph.getVertexIDSet().contains(point)) {
      DoubleVector features = buildFeatureVector(env, point.x, point.y);
      if (features.getLength() != weights.getLength()) {
        throw new IllegalArgumentException(features.getLength() + " != "
            + weights.getLength());
      }
      return features;
    }
    return null;
  }

  public double getQValue(DoubleVector features) {
    return features == null ? -Double.MAX_VALUE : weights.dot(features);
  }

  /*
   * Callbacks that help us to see if our decisions were good.
   */

  @Override
  public void consumedFood(Environment env, int x, int y, int foodRemaining) {
    double[] qValues = new double[4];
    for (Direction d : Direction.values()) {
      qValues[d.getIndex()] = getQValue(getFeatures(env, x, y, d));
    }
    reward(FOOD_REWARD, ArrayUtils.max(qValues));
  }

  @Override
  public void gameStateChanged(boolean won) {
    // next state is always zero
    reward(won ? WON_REWARD : LOST_REWARD, 0);
    LOG.info(won ? "We WON OMG!" : "failed.");
  }

  private void reward(double reward, double maxNextState) {
    double correction = (reward + DISCOUNT_FACTOR * maxNextState);
    DoubleVector correctedWeights = weights.subtractFrom(correction);
    DoubleVector update = correctedWeights.multiply(LEARNING_RATE);
    if (lastActionFeatures != null) {
      update = update.multiply(lastActionFeatures);
    }
    LOG.info("Rewarding " + reward + ". Updating weights by: " + update);
    weights = weights.add(update);
  }

  /**
   * Features: <br/>
   * - distance to closest ghost<br/>
   * - distance to closest food<br/>
   * - direction for the closest food <br/>
   * - directions blocked <br/>
   * - direction for the closest ghost<br/>
   */
  private DoubleVector buildFeatureVector(Environment env, int x, int y) {
    List<? extends Agent> agents = env.getGhosts();
    Maze m = env.getMaze();
    double[] agentDists = new double[agents.size()];
    for (int i = 0; i < agents.size(); i++) {
      agentDists[i] = distance(x, y, agents.get(i).getXPosition(), agents
          .get(i).getYPosition());
    }
    int minAgentDistanceIndex = ArrayUtils.minIndex(agentDists);
    Agent nearestAgent = agents.get(minAgentDistanceIndex);
    Point nearestAgentPoint = new Point(nearestAgent.getXPosition(),
        nearestAgent.getYPosition());
    PlanningEngine<Point> agentPlan = new PlanningEngine<>();
    FollowerGhost.computePath(graph, agentPlan, nearestAgentPoint, x, y);
    Point nextAction = agentPlan.nextAction();
    Direction nextAgentDirection = direction;
    if (nextAction != null) {
      nextAgentDirection = m.getDirection(x, y, nextAction.x, nextAction.y);
    }

    List<Point> foodPoints = m.getFoodPoints();
    if (foodPoints.isEmpty()) {
      return new DenseDoubleVector(NUM_FEATURES);
    }
    double[] foodDists = new double[foodPoints.size()];
    for (int i = 0; i < foodPoints.size(); i++) {
      foodDists[i] = distance(x, y, foodPoints.get(i).x, foodPoints.get(i).y);
    }

    int minFoodDistanceIndex = ArrayUtils.minIndex(foodDists);
    Point nearestFoodTile = foodPoints.get(minFoodDistanceIndex);
    PlanningEngine<Point> plan = new PlanningEngine<>();
    FollowerGhost.computePath(graph, plan, nearestFoodTile, x, y);
    nextAction = plan.nextAction();
    Direction nextFoodDirection = direction;
    if (nextAction != null) {
      nextFoodDirection = m.getDirection(x, y, nextAction.x, nextAction.y);
    }

    int leftFood = nextFoodDirection == Direction.LEFT ? 0 : 1;
    int rightFood = nextFoodDirection == Direction.RIGHT ? 0 : 1;
    int upFood = nextFoodDirection == Direction.UP ? 0 : 1;
    int downFood = nextFoodDirection == Direction.DOWN ? 0 : 1;

    int leftAgent = nextAgentDirection == Direction.LEFT ? 0 : 1;
    int rightAgent = nextAgentDirection == Direction.RIGHT ? 0 : 1;
    int upAgent = nextAgentDirection == Direction.UP ? 0 : 1;
    int downAgent = nextAgentDirection == Direction.DOWN ? 0 : 1;

    int leftBlocked = m.isBlocked(getYPosition(), getXPosition(),
        Direction.LEFT) ? 0 : 1;
    int rightBlocked = m.isBlocked(getYPosition(), getXPosition(),
        Direction.RIGHT) ? 0 : 1;
    int upBlocked = m.isBlocked(getYPosition(), getXPosition(), Direction.UP) ? 0
        : 1;
    int downBlocked = m.isBlocked(getYPosition(), getXPosition(),
        Direction.DOWN) ? 0 : 1;

    boolean ghostNearby = agentDists[minAgentDistanceIndex] < 4d;
    boolean ghostVeryNear = agentDists[minAgentDistanceIndex] < 2d;

    double foodDist = foodDists[minFoodDistanceIndex]
        / (m.getHeight() * m.getWidth());
    DenseDoubleVector feature = new DenseDoubleVector(new double[] { 1,
        foodDist, ghostNearby ? 1d : 0d, ghostVeryNear ? 1d : 0d, leftFood,
        rightFood, upFood, downFood, leftBlocked, rightBlocked, upBlocked,
        downBlocked, leftAgent, rightAgent, upAgent, downAgent });
    return feature.divide(feature.sum());
  }

  public double distance(int x, int y, int x2, int y2) {
    double sum = 0d;
    sum += Math.abs(x - x2);
    sum += Math.abs(y - y2);
    return sum;
  }

  @Override
  public boolean isPacman() {
    return true;
  }

  public DoubleVector getWeights() {
    return this.weights;
  }

}
