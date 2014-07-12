package de.jungblut.agents;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import de.jungblut.gameplay.maze.Maze;
import de.jungblut.gameplay.maze.Maze.Direction;

/**
 * Basic Human Player with a keyboard listener.
 * 
 * @author thomas.jungblut
 * 
 */
public abstract class ControllableAgent extends EnvironmentAgent implements
    KeyListener {

  private Maze env;

  public ControllableAgent(Maze env) {
    super(env);
    this.env = env;
  }

  @Override
  public void keyTyped(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_DOWN:
        if (!isBlocked(env, Direction.DOWN)) {
          direction = Direction.DOWN;
        }
        break;
      case KeyEvent.VK_LEFT:
        if (!isBlocked(env, Direction.LEFT)) {
          direction = Direction.LEFT;
        }
        break;
      case KeyEvent.VK_RIGHT:
        if (!isBlocked(env, Direction.RIGHT)) {
          direction = Direction.RIGHT;
        }
        break;
      case KeyEvent.VK_UP:
        if (!isBlocked(env, Direction.UP)) {
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
  public boolean isHuman() {
    return true;
  }

}
