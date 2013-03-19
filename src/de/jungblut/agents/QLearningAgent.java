package de.jungblut.agents;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import de.jungblut.datastructure.ArrayUtils;
import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.Environment.Direction;
import de.jungblut.gameplay.FoodConsumerListener;
import de.jungblut.gameplay.GameStateListener;
import de.jungblut.gameplay.PlanningEngine;
import de.jungblut.graph.DenseGraph;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.dense.DenseDoubleVector;

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

  public static double EXPLORATION_PROBABILITY = 0.05;

  private static final int NUM_FEATURES = 16;
  private static final double LEARNING_RATE = 0.3;
  private static final double DISCOUNT_FACTOR = 0.8;

  private static final double FOOD_REWARD = 1;
  private static final double WON_REWARD = 10;
  private static final double LOST_REWARD = -10;
  private static int epoch = 0;

  private static DoubleVector weights = new DenseDoubleVector(NUM_FEATURES);
  private static DoubleVector lastActionFeatures;

  // animation
  private final BufferedImage[] sprites = new BufferedImage[2];

  private DenseGraph<Object> graph;
  private Random rand = new Random();

  public QLearningAgent(Environment env) {
    super(env);
    try {
      sprites[0] = ImageIO.read(new File("sprites/pacpix_0.gif"));
      sprites[1] = ImageIO.read(new File("sprites/pacpix_3.gif"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    graph = FollowerGhost.createGraph(env);
    lastActionFeatures = null;
    System.out.println("Starting epoch " + (epoch++) + " -> "
        + Arrays.toString(weights.toArray()));
  }

  @Override
  public void move() {
    // explore
    if (rand.nextDouble() > (1d - EXPLORATION_PROBABILITY)) {
      direction = RandomGhost.getRandomDirection(getEnvironment(),
          getXPosition(), getYPosition(), rand);
    } else {
      // check for every q value from this direction
      double[] qValues = new double[4];
      DoubleVector[] features = new DoubleVector[4];
      for (Direction d : Direction.values()) {
        features[d.getIndex()] = getFeatures(x, y, d);
        qValues[d.getIndex()] = getQValue(features[d.getIndex()]);
      }
      int selectedAction = ArrayUtils.maxIndex(qValues);
      direction = Direction.values()[selectedAction];
      lastActionFeatures = features[selectedAction];
    }
    super.move();
  }

  public DoubleVector getFeatures(int x, int y, Direction direction) {
    Point point = getEnvironment().getPoint(x, y, direction);
    if (graph.getVertexIDSet().contains(point)) {
      DoubleVector features = buildFeatureVector(point.x, point.y);
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
  public void consumedFood(int x, int y, int foodRemaining) {
    double[] qValues = new double[4];
    for (Direction d : Direction.values()) {
      qValues[d.getIndex()] = getQValue(getFeatures(x, y, d));
    }
    reward(FOOD_REWARD, ArrayUtils.max(qValues));
  }

  @Override
  public boolean gameStateChanged(boolean won) {
    // next state is always zero
    reward(won ? WON_REWARD : LOST_REWARD, 0);
    System.out.println((won ? "We WON OMG!" : "fail.")
        + "\n\n---------------------------");
    // TODO check if we need have exceeded our #epochs
    return true;
  }

  private void reward(double reward, double maxNextState) {
    double correction = (reward + DISCOUNT_FACTOR * maxNextState);
    DoubleVector correctedWeights = weights.subtractFrom(correction);
    DoubleVector update = correctedWeights.multiply(LEARNING_RATE);
    if (lastActionFeatures != null) {
      update = update.multiply(lastActionFeatures);
    }
    System.out.println("Rewarding " + reward + ". Updating weights by: "
        + update);
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
  private DoubleVector buildFeatureVector(int x, int y) {
    List<Agent> agents = getEnvironment().getBotAgents();
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
      nextAgentDirection = getEnvironment().getDirection(x, y, nextAction.x,
          nextAction.y);
    }

    List<Point> foodPoints = getEnvironment().getFoodPoints();
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
      nextFoodDirection = getEnvironment().getDirection(x, y, nextAction.x,
          nextAction.y);
    }

    int leftFood = nextFoodDirection == Direction.LEFT ? 0 : 1;
    int rightFood = nextFoodDirection == Direction.RIGHT ? 0 : 1;
    int upFood = nextFoodDirection == Direction.UP ? 0 : 1;
    int downFood = nextFoodDirection == Direction.DOWN ? 0 : 1;

    int leftAgent = nextAgentDirection == Direction.LEFT ? 0 : 1;
    int rightAgent = nextAgentDirection == Direction.RIGHT ? 0 : 1;
    int upAgent = nextAgentDirection == Direction.UP ? 0 : 1;
    int downAgent = nextAgentDirection == Direction.DOWN ? 0 : 1;

    int leftBlocked = getEnvironment().isBlocked(getYPosition(),
        getXPosition(), Direction.LEFT) ? 0 : 1;
    int rightBlocked = getEnvironment().isBlocked(getYPosition(),
        getXPosition(), Direction.RIGHT) ? 0 : 1;
    int upBlocked = getEnvironment().isBlocked(getYPosition(), getXPosition(),
        Direction.UP) ? 0 : 1;
    int downBlocked = getEnvironment().isBlocked(getYPosition(),
        getXPosition(), Direction.DOWN) ? 0 : 1;

    boolean ghostNearby = agentDists[minAgentDistanceIndex] < 4d;
    boolean ghostVeryNear = agentDists[minAgentDistanceIndex] < 2d;

    double foodDist = foodDists[minFoodDistanceIndex]
        / (getEnvironment().getHeight() * getEnvironment().getWidth() / getEnvironment()
            .getBlockSize());
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
  public boolean isHuman() {
    // fake beeing human to train
    return true;
  }

  @Override
  public BufferedImage[] getAnimationSprites() {
    return sprites;
  }

  @Override
  protected int getNumAnimationSprites() {
    return 2;
  }

}