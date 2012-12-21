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
    direction = Direction.values()[rand.nextInt(4)];
    super.move();
  }

  @Override
  public BufferedImage[] getAnimationSprites() {
    return sprites;
  }

}
