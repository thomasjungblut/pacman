package de.jungblut.agents;

import java.awt.Point;

import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.maze.Maze;
import de.jungblut.gameplay.maze.Maze.BlockState;
import de.jungblut.gameplay.maze.Maze.Direction;

/**
 * Agent that interacts with its environment. This class takes care of the
 * random initialization spot, the direction and movement handling.
 * 
 * @author thomas.jungblut
 * 
 */
public abstract class EnvironmentAgent extends AnimatedAgent {

  protected int x;
  protected int y;
  protected Direction direction = Direction.RIGHT;

  public EnvironmentAgent(Maze env) {
    Point freeSpot = env.getFreeSpot();
    this.x = freeSpot.x;
    this.y = freeSpot.y;
  }

  public EnvironmentAgent(Maze env, int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Moves in the currently defined direction if it is not blocked.
   */
  @Override
  public void move(Environment env) {
    switch (getDirection()) {
      case DOWN:
        if (!isBlocked(env.getMaze(), Direction.DOWN)) {
          x++;
        }
        break;
      case LEFT:
        if (!isBlocked(env.getMaze(), Direction.LEFT)) {
          y--;
        }
        break;
      case RIGHT:
        if (!isBlocked(env.getMaze(), Direction.RIGHT)) {
          y++;
        }
        break;
      case UP:
        if (!isBlocked(env.getMaze(), Direction.UP)) {
          x--;
        }
        break;
    }
  }

  /**
   * @return true if the way of the given direction is blocked.
   */
  protected boolean isBlocked(Maze maze, Direction d) {
    Point point = Maze.getPoint(x, y, d);
    return maze.getState(point.x, point.y) == BlockState.WALL;
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
