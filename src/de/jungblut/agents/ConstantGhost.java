package de.jungblut.agents;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.jungblut.gameplay.Environment;

/**
 * Constant direction ghost.
 * 
 * @author thomas.jungblut
 * 
 */
public class ConstantGhost extends EnvironmentAgent {

  private BufferedImage[] sprites = new BufferedImage[1];

  public ConstantGhost(Environment env) {
    super(env);
    try {
      sprites[0] = ImageIO.read(new File("sprites/ghost_0.gif"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void move() {
    super.move();
  }

  @Override
  public BufferedImage[] getAnimationSprites() {
    return sprites;
  }

}
