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
 * An agent that learns to play pacman through qlearning value iterations and a
 * neural network. <a
 * href="http://mechanistician.blogspot.de/2009/05/pacman-and-reinforcement
 * -learning.html">Some nice wrap up can be found here</a>
 * 
 * @author thomas.jungblut
 * 
 */
public class QLearningAgent extends EnvironmentAgent implements
    GameStateListener, FoodConsumerListener {

  public static double EXPLORATION_PROBABILITY = 0.2;

  private static final int NUM_FEATURES = 25;
  private static final double LEARNING_RATE = 0.025;
  private static final double DISCOUNT_FACTOR = 0.001;

  private static final double FOOD_REWARD = 0.25;
  private static final double WON_REWARD = 10;
  private static final double LOST_REWARD = -1;
  private static int epoch = 0;

  private static DoubleVector weights;

  /**
   * Features: <br/>
   * - distance to closest ghost<br/>
   * - distance to closest food<br/>
   * - direction for the closest food <br/>
   * - directions blocked <br/>
   * - direction for the closest ghost<br/>
   */

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
    if (weights == null) {
      weights = new DenseDoubleVector(NUM_FEATURES);
      for (int i = 0; i < weights.getLength(); i++) {
        weights.set(i, rand.nextDouble());
      }
    }
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
      for (Direction d : Direction.values()) {
        qValues[d.getIndex()] = getQValue(x, y, d);
      }
      direction = Direction.values()[ArrayUtils.maxIndex(qValues)];
      System.out.println(Arrays.toString(qValues) + " -> " + direction);
    }
    super.move();
  }

  public double getQValue(int x, int y, Direction direction) {
    Point point = getEnvironment().getPoint(x, y, direction);
    if (graph.getVertexIDSet().contains(point)) {
      DoubleVector features = buildFeatureVector(point.x, point.y);
      if (features.getLength() != weights.getLength()) {
        throw new IllegalArgumentException(features.getLength() + " != "
            + weights.getLength());
      }
      return weights.dot(features);
    } else {
      return -Double.MAX_VALUE;
    }
  }

  /*
   * Callbacks that help us to see if our decisions were good.
   */

  @Override
  public void consumedFood(int x, int y, int foodRemaining) {
    double[] qValues = new double[4];
    for (Direction d : Direction.values()) {
      qValues[d.getIndex()] = getQValue(x, y, d);
    }
    reward(FOOD_REWARD, ArrayUtils.max(qValues));
  }

  @Override
  public boolean gameStateChanged(boolean won) {
    System.out.println((won ? "We WON OMG!" : "fail.")
        + "\n\n---------------------------");
    // next state is zero
    reward(won ? WON_REWARD : LOST_REWARD, 0);
    // TODO check if we need have exceeded our #epochs
    return true;
  }

  private void reward(double reward, double maxNextState) {
    weights = weights.add(LEARNING_RATE
        * (reward + DISCOUNT_FACTOR * maxNextState));
  }

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

    return new DenseDoubleVector(new double[] { 1,
        agentDists[minAgentDistanceIndex] < 1d ? 1 : 0,
        agentDists[minAgentDistanceIndex] < 2d ? 1 : 0,
        agentDists[minAgentDistanceIndex] < 3d ? 1 : 0,
        agentDists[minAgentDistanceIndex] < 5d ? 1 : 0,
        agentDists[minAgentDistanceIndex] < 10d ? 1 : 0,
        agentDists[minAgentDistanceIndex] < 20d ? 1 : 0,
        foodDists[minFoodDistanceIndex] < 1d ? 1 : 0,
        foodDists[minFoodDistanceIndex] < 2d ? 1 : 0,
        foodDists[minFoodDistanceIndex] < 3d ? 1 : 0,
        foodDists[minFoodDistanceIndex] < 5d ? 1 : 0,
        foodDists[minFoodDistanceIndex] < 10d ? 1 : 0,
        foodDists[minFoodDistanceIndex] < 20d ? 1 : 0, leftFood, rightFood,
        upFood, downFood, leftBlocked, rightBlocked, upBlocked, downBlocked,
        leftAgent, rightAgent, upAgent, downAgent });
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
