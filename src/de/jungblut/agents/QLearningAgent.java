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

  private static final double LEARNING_RATE = 0.2;
  private static final double DISCOUNT_FACTOR = 0.8;
  private static final double EXPLORATION_PROBABILITY = 0.5;

  private static final int FOOD_REWARD = 250;
  private static final int WON_REWARD = 500;
  private static final int LOST_REWARD = -50;

  /**
   * Features: <br/>
   * - distance to closest ghost<br/>
   * - distance to closest food<br/>
   * - free directions <br/>
   * Ideas: <br/>
   * - number of ghosts in a radius of n-blocks<br/>
   * - TODO direction for the closest food/ghost<br/>
   */

  private final int[] layers = { 6, 4 };
  private final ActivationFunction[] activations = {
      ActivationFunctionSelector.LINEAR.get(),
      ActivationFunctionSelector.SIGMOID.get() };

  // pacman animation
  private final BufferedImage[] sprites = new BufferedImage[2];
  private MultilayerPerceptron network;
  private QLearningMinimizer minimizer = new QLearningMinimizer();

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
    minimizer.setFeatures(new DenseDoubleVector(layers[0]));
    minimizer.setOutcome(new DenseDoubleVector(layers[layers.length - 1]));
    network = MultilayerPerceptronConfiguration.newConfiguration(layers,
        activations, minimizer, 1).build();
    // do a first trainingstep to init everything inside
    network.trainStochastic();
    System.out.println("Starting new epoch: "
        + Arrays.toString(minimizer.getTheta().toArray()));
  }

  @Override
  public void move() {
    if (rand.nextDouble() > (1d - EXPLORATION_PROBABILITY)) {
      direction = RandomGhost.getRandomDirection(getEnvironment(),
          getYPosition(), getXPosition(), rand);
    } else {
      lastFeatures = buildFeatureVector();
      lastPrediction = network.predict(lastFeatures);
      int maxIndex = lastPrediction.maxIndex();
      direction = Direction.values()[maxIndex];
      System.out.println(lastFeatures + " -> " + lastPrediction + " = "
          + direction);
    }
    super.move();
  }

  /*
   * Callbacks that help us to see if our decisions were good.
   */

  @Override
  public void consumedFood(int x, int y, int foodRemaining) {
    if (lastPrediction == null) {
      lastPrediction = new DenseDoubleVector(4);
    }
    minimizer.update(lastPrediction, FOOD_REWARD, LEARNING_RATE,
        DISCOUNT_FACTOR);
    System.out.println("Updated theta: "
        + Arrays.toString(minimizer.getTheta().toArray()));
    updateThetaInNetwork();
  }

  @Override
  public boolean gameStateChanged(boolean won) {
    System.out.println(won ? "We WON OMG!" : "fail.");
    if (lastPrediction == null) {
      lastPrediction = new DenseDoubleVector(4);
    }
    minimizer.update(lastPrediction, won ? WON_REWARD : LOST_REWARD,
        LEARNING_RATE, DISCOUNT_FACTOR);
    System.out.println("Updated theta: "
        + Arrays.toString(minimizer.getTheta().toArray()));
    updateThetaInNetwork();
    // TODO check if we need have exceeded our #epochs
    return true;
  }

  private void updateThetaInNetwork() {
    network.trainStochastic(minimizer.getTheta());
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

    int leftBlocked = getEnvironment().isBlocked(getYPosition(),
        getXPosition(), Direction.LEFT) ? 1 : 0;
    int rightBlocked = getEnvironment().isBlocked(getYPosition(),
        getXPosition(), Direction.RIGHT) ? 1 : 0;
    int upBlocked = getEnvironment().isBlocked(getYPosition(), getXPosition(),
        Direction.UP) ? 1 : 0;
    int downBlocked = getEnvironment().isBlocked(getYPosition(),
        getXPosition(), Direction.DOWN) ? 1 : 0;

    return new DenseDoubleVector(new double[] { ArrayUtils.min(agentDists),
        ArrayUtils.min(foodDists), leftBlocked, rightBlocked, upBlocked,
        downBlocked });
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
