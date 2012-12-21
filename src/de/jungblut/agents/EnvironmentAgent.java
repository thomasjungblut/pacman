package de.jungblut.agents;

import java.awt.Point;

import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.Environment.BlockState;
import de.jungblut.gameplay.Environment.Direction;

/**
 * Agent that interacts with its environment. This class takes care of the
 * random initialization spot, the direction and movement handling.
 * 
 * @author thomas.jungblut
 * 
 */
public abstract class EnvironmentAgent extends DrawableAgent {

  private final Environment environment;

  protected int x;
  protected int y;
  protected Direction direction = Direction.RIGHT;

  public EnvironmentAgent(Environment env) {
    Point freeSpot = env.getFreeSpot();
    this.environment = env;
    this.x = freeSpot.x;
    this.y = freeSpot.y;
  }

  public EnvironmentAgent(Environment env, int x, int y) {
    this.environment = env;
    this.x = x;
    this.y = y;
  }

  /**
   * Moves in the currently defined direction if it is not blocked.
   */
  @Override
  public void move() {
    switch (getDirection()) {
      case DOWN:
        if (!isBlocked(Direction.DOWN)) {
          x++;
        }
        break;
      case LEFT:
        if (!isBlocked(Direction.LEFT)) {
          y--;
        }
        break;
      case RIGHT:
        if (!isBlocked(Direction.RIGHT)) {
          y++;
        }
        break;
      case UP:
        if (!isBlocked(Direction.UP)) {
          x--;
        }
        break;
    }
  }

  /**
   * @return true if the way of the given direction is blocked.
   */
  protected boolean isBlocked(Direction d) {
    Point point = environment.getPoint(x, y, d);
    return getEnvironment().getState(point.x, point.y) == BlockState.WALL;
  }

  @Override
  public Environment getEnvironment() {
    return this.environment;
  }

  @Override
  public int getXPosition() {
    return this.x;
  }

  @Override
  public int getYPosition() {
    return this.y;
  }

  @Override
  public Direction getDirection() {
    return this.direction;
  }

}
