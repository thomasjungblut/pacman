package de.jungblut.agents;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.Environment.Direction;

/**
 * Random direction ghost. Go home ghost, you're drunk!
 * 
 * @author thomas.jungblut
 * 
 */
public class RandomGhost extends EnvironmentAgent {

  private BufferedImage[] sprites = new BufferedImage[1];
  private Random rand;

  public RandomGhost(Environment env) {
    super(env);
    try {
      sprites[0] = ImageIO.read(new File("sprites/ghost_0.gif"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    rand = new Random();
  }

  @Override
  public void move() {
    direction = getRandomDirection(getEnvironment(), x, y, rand);
    super.move();
  }

  static Direction getRandomDirection(Environment env, int x, int y, Random rand) {
    Direction direction = Direction.LEFT;
    // don't move to blocked directions, also if our way isn't blocked- run
    // along until blocked. Make 10 tries then give up...
    int tries = 10;
    while (env.isBlocked(y, x, direction) && --tries > 0) {
      direction = Direction.values()[rand.nextInt(4)];
    }
    return direction;
  }

  @Override
  public BufferedImage[] getAnimationSprites() {
    return sprites;
  }

}
