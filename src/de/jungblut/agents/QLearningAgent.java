package de.jungblut.agents;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.jungblut.classification.nn.MultilayerPerceptron;
import de.jungblut.classification.nn.MultilayerPerceptron.MultilayerPerceptronConfiguration;
import de.jungblut.gameplay.Environment;
import de.jungblut.math.activation.ActivationFunction;
import de.jungblut.math.activation.ActivationFunctionSelector;

/**
 * An agent that learns to play pacman through qlearning value iterations and a
 * neural network.
 * 
 * @author thomas.jungblut
 * 
 */
public class QLearningAgent extends EnvironmentAgent {

  /**
   * Features: <br/>
   * - distance to closest ghost<br/>
   * - distance to closest food<br/>
   * - number of ghosts in a radius of n-blocks<br/>
   */
  private final int[] layers = { 2, 4 };
  private final ActivationFunction[] activations = {
      ActivationFunctionSelector.LINEAR.get(),
      ActivationFunctionSelector.SIGMOID.get() };

  // pacman animation
  private final BufferedImage[] sprites = new BufferedImage[2];

  private MultilayerPerceptron network;

  public QLearningAgent(Environment env) {
    super(env);
    try {
      sprites[0] = ImageIO.read(new File("sprites/pacpix_0.gif"));
      sprites[1] = ImageIO.read(new File("sprites/pacpix_3.gif"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // network = MultilayerPerceptronConfiguration.newConfiguration(layers,
    // activations, minimizer, maxIteration);
  }

  @Override
  public void move() {
    // TODO get actions based on our qlearning output

    super.move();
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
