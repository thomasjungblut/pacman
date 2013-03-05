package de.jungblut.agents;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.Environment.Direction;

/**
 * Basic Human Player with a keyboard listener.
 * 
 * @author thomas.jungblut
 * 
 */
public class PacmanPlayer extends EnvironmentAgent implements KeyListener {

  // pacman animation
  private final BufferedImage[] sprites = new BufferedImage[2];

  public PacmanPlayer(Environment env) {
    super(env);
    try {
      sprites[0] = ImageIO.read(new File("sprites/pacpix_0.gif"));
      sprites[1] = ImageIO.read(new File("sprites/pacpix_3.gif"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_DOWN:
        if (!isBlocked(Direction.DOWN)) {
          direction = Direction.DOWN;
        }
        break;
      case KeyEvent.VK_LEFT:
        if (!isBlocked(Direction.LEFT)) {
          direction = Direction.LEFT;
        }
        break;
      case KeyEvent.VK_RIGHT:
        if (!isBlocked(Direction.RIGHT)) {
          direction = Direction.RIGHT;
        }
        break;
      case KeyEvent.VK_UP:
        if (!isBlocked(Direction.UP)) {
          direction = Direction.UP;
        }
        break;
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    keyTyped(e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    keyTyped(e);
  }

  @Override
  public BufferedImage[] getAnimationSprites() {
    return sprites;
  }

  @Override
  protected int getNumAnimationSprites() {
    return 2;
  }

  @Override
  public boolean isHuman() {
    return true;
  }

}
