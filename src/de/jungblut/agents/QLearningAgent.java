package de.jungblut.agents;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import de.jungblut.ai.QLearningMinimizer;
import de.jungblut.classification.nn.MultilayerPerceptron;
import de.jungblut.classification.nn.MultilayerPerceptron.MultilayerPerceptronConfiguration;
import de.jungblut.datastructure.ArrayUtils;
import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.Environment.Direction;
import de.jungblut.gameplay.FoodConsumerListener;
import de.jungblut.gameplay.GameStateListener;
import de.jungblut.gameplay.PlanningEngine;
import de.jungblut.graph.DenseGraph;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.activation.ActivationFunction;
import de.jungblut.math.activation.ActivationFunctionSelector;
import de.jungblut.math.dense.DenseDoubleVector;

/**
 * An agent that learns to play pacman through qlearning value iterations and a
 * neural network.
 * 
 * @author thomas.jungblut
 * 
 */
public class QLearningAgent extends EnvironmentAgent implements
    GameStateListener, FoodConsumerListener {

  private static final double LEARNING_RATE = 0.1;
  private static final double DISCOUNT_FACTOR = 0.01;
  private static final double EXPLORATION_PROBABILITY = 0;

  private static final double FOOD_REWARD = 1;
  private static final double WON_REWARD = 10;
  private static final double LOST_REWARD = -10;
  private static final double WALL_MISS_REWARD = 0;
  private static final double WALL_RUNNER_REWARD = -10;

  // note that this is constant throughout all our runs
  private static final QLearningMinimizer Q_LEARNING_MINIMIZER = new QLearningMinimizer();

  private static int epoch = 0;

  /**
   * Features: <br/>
   * - distance to closest ghost<br/>
   * - distance to closest food<br/>
   * - direction for the closest food <br/>
   * - directions blocked <br/>
   * Ideas: <br/>
   * - number of ghosts in a radius of n-blocks<br/>
   * - direction for the closest ghost<br/>
   */

  private final int[] layers = { 10, 4 };
  private final ActivationFunction[] activations = {
      ActivationFunctionSelector.LINEAR.get(),
      ActivationFunctionSelector.LINEAR.get() };
  // pacman animation
  private final BufferedImage[] sprites = new BufferedImage[2];

  private DenseGraph<Object> graph;
  private MultilayerPerceptron network;

  private DenseDoubleVector lastPrediction;
  private DoubleVector lastFeatures;

  private Random rand = new Random();

  public QLearningAgent(Environment env) {
    super(env);
    try {
      sprites[0] = ImageIO.read(new File("sprites/pacpix_0.gif"));
      sprites[1] = ImageIO.read(new File("sprites/pacpix_3.gif"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    // initialize to zeros
    Q_LEARNING_MINIMIZER.setFeatures(new DenseDoubleVector(layers[0]));
    Q_LEARNING_MINIMIZER.setOutcome(new DenseDoubleVector(
        layers[layers.length - 1]));
    network = MultilayerPerceptronConfiguration.newConfiguration(layers,
        activations, Q_LEARNING_MINIMIZER, 1).build();
    // do a first trainingstep to init everything inside
    if (Q_LEARNING_MINIMIZER.getTheta() == null) {
      network.trainStochastic();
    } else {
      network.trainStochastic(Q_LEARNING_MINIMIZER.getTheta());
    }
    System.out.println("Starting epoch (" + (epoch++) + "): "
        + Arrays.toString(Q_LEARNING_MINIMIZER.getTheta().toArray()));
    graph = FollowerGhost.createGraph(env);
  }

  @Override
  public void move() {
    boolean explore = rand.nextDouble() > (1d - EXPLORATION_PROBABILITY);
    if (!explore) {
      lastFeatures = buildFeatureVector();
      lastPrediction = network.predict(lastFeatures);
      int index = lastPrediction.maxIndex();
      direction = Direction.values()[index];
      System.out.println(lastFeatures + " -> " + lastPrediction + " = "
          + direction);
    }
    boolean isBlocked = isBlocked(direction);
    if (explore) {
      direction = RandomGhost.getRandomDirection(getEnvironment(),
          getXPosition(), getYPosition(), rand);
    }
    if (isBlocked) {
      reward(WALL_RUNNER_REWARD);
    } else {
      reward(WALL_MISS_REWARD);
      super.move();
    }
  }

  /*
   * Callbacks that help us to see if our decisions were good.
   */

  @Override
  public void consumedFood(int x, int y, int foodRemaining) {
    reward(FOOD_REWARD);
  }

  @Override
  public boolean gameStateChanged(boolean won) {
    System.out.println((won ? "We WON OMG!" : "fail.")
        + "\n\n---------------------------");
    reward(won ? WON_REWARD : LOST_REWARD);
    // TODO check if we need have exceeded our #epochs
    return true;
  }

  private void reward(double reward) {
    if (lastPrediction == null) {
      lastPrediction = new DenseDoubleVector(4);
    }
    Q_LEARNING_MINIMIZER.update(lastPrediction, reward, LEARNING_RATE,
        DISCOUNT_FACTOR);
    updateThetaInNetwork();
  }

  private void updateThetaInNetwork() {
    network.trainStochastic(Q_LEARNING_MINIMIZER.getTheta());
  }

  private DoubleVector buildFeatureVector() {
    List<Agent> agents = getEnvironment().getBotAgents();
    double[] agentDists = new double[agents.size()];
    for (int i = 0; i < agents.size(); i++) {
      agentDists[i] = distance(x, y, agents.get(i).getXPosition(), agents
          .get(i).getYPosition());
    }

    List<Point> foodPoints = getEnvironment().getFoodPoints();
    double[] foodDists = new double[foodPoints.size()];
    for (int i = 0; i < foodPoints.size(); i++) {
      foodDists[i] = distance(x, y, foodPoints.get(i).x, foodPoints.get(i).y);
    }

    int minFoodDistanceIndex = ArrayUtils.minIndex(foodDists);
    Point nearestFoodTile = foodPoints.get(minFoodDistanceIndex);
    PlanningEngine<Point> plan = new PlanningEngine<>();
    FollowerGhost.computePath(graph, plan, nearestFoodTile, getXPosition(),
        getYPosition());
    Point nextAction = plan.nextAction();
    Direction nextFoodDirection = direction;
    if (nextAction != null) {
      nextFoodDirection = getEnvironment().getDirection(x, y, nextAction.x,
          nextAction.y);
    }

    int leftFood = nextFoodDirection == Direction.LEFT ? 0 : 1;
    int rightFood = nextFoodDirection == Direction.RIGHT ? 0 : 1;
    int upFood = nextFoodDirection == Direction.UP ? 0 : 1;
    int downFood = nextFoodDirection == Direction.DOWN ? 0 : 1;

    int leftBlocked = getEnvironment().isBlocked(getYPosition(),
        getXPosition(), Direction.LEFT) ? 0 : 1;
    int rightBlocked = getEnvironment().isBlocked(getYPosition(),
        getXPosition(), Direction.RIGHT) ? 0 : 1;
    int upBlocked = getEnvironment().isBlocked(getYPosition(), getXPosition(),
        Direction.UP) ? 0 : 1;
    int downBlocked = getEnvironment().isBlocked(getYPosition(),
        getXPosition(), Direction.DOWN) ? 0 : 1;

    return new DenseDoubleVector(new double[] { ArrayUtils.min(agentDists),
        foodDists[minFoodDistanceIndex], leftFood, rightFood, upFood, downFood,
        leftBlocked, rightBlocked, upBlocked, downBlocked });
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
