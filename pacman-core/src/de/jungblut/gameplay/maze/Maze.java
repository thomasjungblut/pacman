package de.jungblut.gameplay.maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Maze {

  public enum Direction {
    LEFT(0), RIGHT(1), UP(2), DOWN(3);

    private int index;

    Direction(int index) {
      this.index = index;
    }

    public int getIndex() {
      return this.index;
    }

  }

  public enum BlockState {
    WALL(0), FOOD(1), ROAD(2);

    private byte id;

    private BlockState(int id) {
      this.id = (byte) id;
    }

    public byte getId() {
      return id;
    }

    @Override
    public String toString() {
      return this.name().substring(0, 1);
    };
  }

  private BlockState[][] blockStates;
  private int height;
  private int width;
  private int foodRemaining;

  private final Random rnd = new Random();

  public Maze(int height, int width, BlockState[][] states) {
    this.width = width;
    this.height = height;
    this.blockStates = states;
  }

  /**
   * Get the state of the environment at a given point.
   * 
   * @param height the height in the world.
   * @param width the width in the world.
   * @return if the coordinates are out of bounds it will always return WALL,
   *         else the state at the coordinates.
   */
  public BlockState getState(int height, int width) {
    if (height < 0 || height >= this.height || width < 0 || width >= this.width) {
      return BlockState.WALL;
    }
    return blockStates[height][width];
  }

  /**
   * Calculates the blockstate from a given coordinate and a direction.
   * 
   * @return the blockstate at the point, defined by the current coordinate and
   *         a direction.
   */
  public BlockState getState(int height, int width, Direction d) {
    Point point = getPoint(height, width, d);
    return getState(point.x, point.y);
  }

  /**
   * Calculates the point from a given coordinate and a direction.
   * 
   * @return the point, defined by the current coordinate and a direction.
   */
  public static Point getPoint(int height, int width, Direction d) {
    switch (d) {
      case DOWN:
        height++;
        break;
      case LEFT:
        width--;
        break;
      case RIGHT:
        width++;
        break;
      case UP:
        height--;
        break;
    }
    return new Point(height, width);
  }

  /**
   * Calculates the direction of two <b>adjacent</b> points (the first point's
   * direction to the other).
   * 
   * @param height the first points height.
   * @param width the first poitns width.
   * @param height2 the second points height.
   * @param width2 the second points width.
   * @throws IllegalArgumentException if the points are equal (there is no STAY
   *           direction) and if the two points are not adjacent.
   * @return the correct direction between two points.
   */
  public Direction getDirection(int height, int width, int height2, int width2)
      throws IllegalArgumentException {
    int hDiff = height - height2;
    int wDiff = width - width2;

    if (hDiff == 0 && wDiff == 0) {
      throw new IllegalArgumentException(
          "Both points are equal! There is no direction!");
    } else {
      if (wDiff == 0) {
        if (hDiff < 0) {
          return Direction.DOWN;
        } else {
          return Direction.UP;
        }
      } else if (hDiff == 0) {
        if (wDiff < 0) {
          return Direction.RIGHT;
        } else {
          return Direction.LEFT;
        }
      }
    }
    throw new IllegalArgumentException("Two points were not adjacent! "
        + height + "/" + width + " vs. " + height2 + "/" + width2);
  }

  /**
   * @return a random free spot in the environment, e.G. to place an agent.
   */
  public Point getFreeSpot() {
    for (int i = 0; i < 1000; i++) {
      int h = rnd.nextInt(getHeight());
      int w = rnd.nextInt(getWidth());
      BlockState state = getState(h, w);
      if (state != BlockState.WALL) {
        return new Point(h, w);
      }
    }
    return new Point(1, 1);
  }

  /**
   * @return true if the requested tile is not blocked = a wall.
   */
  public boolean isBlocked(int x, int y, Direction d) {
    return getState(y, x, d) == BlockState.WALL;
  }

  /**
   * Removes the food tile on the given point and replaces it with normal road.
   * 
   * @return true if food was removed, false if not.
   */
  public boolean removeFood(int height, int width) {
    if (blockStates[height][width] == BlockState.FOOD) {
      blockStates[height][width] = BlockState.ROAD;
      decrementFood();
      return true;
    }
    return false;
  }

  /**
   * @return the points in the environment, where food is available.
   */
  public List<Point> getFoodPoints() {
    List<Point> lst = new ArrayList<>();

    for (int h = 0; h < getHeight(); h++) {
      for (int w = 0; w < getWidth(); w++) {
        if (blockStates[h][w] == BlockState.FOOD) {
          lst.add(new Point(h, w));
        }
      }
    }
    return lst;
  }

  public BlockState[][] getBlockStates() {
    return this.blockStates;
  }

  public int getHeight() {
    return this.height;
  }

  public int getWidth() {
    return this.width;
  }

  public int getFoodRemaining() {
    return this.foodRemaining;
  }

  public void decrementFood() {
    this.foodRemaining--;
  }

  public void setFoodRemaining(int foodRemaining) {
    this.foodRemaining = foodRemaining;
  }

}
