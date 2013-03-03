package de.jungblut.gameplay;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.jungblut.agents.Agent;
import de.jungblut.agents.FollowerGhost;
import de.jungblut.agents.QLearningAgent;
import de.jungblut.agents.RandomGhost;

/**
 * The game environment. Takes care of the world by: generating it, give helper
 * methods for interacting with the environment and taking care of the agents.
 * 
 * @author thomas.jungblut
 * 
 */
public class Environment {

  private static final double WALL_SPARSITY = 0.4; // =60% walls
  private static final double FOOD_SPARSITY = 0.8; // =20% food

  public enum BlockState {
    WALL, FOOD, ROAD;
    @Override
    public String toString() {
      return this.name().substring(0, 1);
    };
  }

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

  private final List<Agent> agentList = new ArrayList<>();

  private final BlockState[][] environment;
  private final int blockSize;
  private final int height;
  private final int width;

  private Agent humanPlayer;
  private int foodRemaining;

  /**
   * @param width the window width.
   * @param height the window height.
   * @param blockSize the blocksize to use (1px in environment = blockSize-px on
   *          monitor size).
   */
  public Environment(int width, int height, int blockSize) {
    this.width = width / blockSize;
    this.height = height / blockSize;
    this.blockSize = blockSize;
    this.environment = new BlockState[this.height][this.width];
    generateMaze();
    initAgents();
    spawnFood();
  }

  private void spawnFood() {
    // basically spawn food everywhere nothing else is while taking the food
    // sparsity into account
    Random rnd = new Random();
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        if (environment[h][w] == BlockState.ROAD && !isPlayerOnTile(h, w)
            && rnd.nextDouble() > FOOD_SPARSITY) {
          foodRemaining++;
          environment[h][w] = BlockState.FOOD;
        }
      }
    }
  }

  /**
   * Adds the agents to the world.
   */
  private void initAgents() {
    // humanPlayer = new PacmanPlayer(this);
    humanPlayer = new QLearningAgent(this);
    agentList.add(humanPlayer);
    agentList.add(new RandomGhost(this));
    agentList.add(new FollowerGhost(this));
  }

  /**
   * Pseudo-pacman maze generator. Pretty crazy logic.
   */
  public void generateMaze() {
    Random rnd = new Random();
    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        if (h == 0 || w == 0 || w == width - 1 || h == height - 1
            || rnd.nextDouble() > WALL_SPARSITY) {
          environment[h][w] = BlockState.WALL;
        } else {
          environment[h][w] = BlockState.ROAD;
        }
      }
    }

    // remove walls that are deadends
    // we need at least 4 passes to get out all the surroundings.
    for (int i = 0; i < 4; i++) {
      for (int h = 0; h < height; h++) {
        for (int w = 0; w < width; w++) {
          int surrounds = 0;
          for (Direction d : Direction.values()) {
            if (getState(h, w, d) == BlockState.WALL
                && !(h == 0 || w == 0 || w == width - 1 || h == height - 1)) {
              surrounds++;
            }
          }
          if (surrounds > 2) {
            for (Direction d : Direction.values()) {
              Point point = getPoint(h, w, d);
              if (getState(point.x, point.y) == BlockState.WALL
                  && !(point.x == 0 || point.y == 0 || point.y == width - 1 || point.x == height - 1)) {
                environment[point.x][point.y] = BlockState.ROAD;
                break;
              }
            }
          }
        }
      }
    }
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
    if (height < 0 || height >= this.height || width < 0 || width >= this.width)
      return BlockState.WALL;
    return environment[height][width];
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
  public Point getPoint(int height, int width, Direction d) {
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
    Random rnd = new Random();
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
   * @return true if the requested tile is not blocked
   */
  public boolean isBlocked(int x, int y, Direction d) {
    return getState(y, x, d) != BlockState.ROAD;
  }

  /**
   * @return true if there is a player on the tile.
   */
  public boolean isPlayerOnTile(int height, int width) {
    for (Agent agent : agentList) {
      if (agent.getXPosition() == height && agent.getYPosition() == width)
        return true;
    }
    return false;
  }

  /**
   * Removes the food tile on the given point and replaces it with normal road.
   * 
   * @return true if food was removed, false if not.
   */
  public boolean removeFood(int height, int width) {
    if (environment[height][width] == BlockState.FOOD) {
      environment[height][width] = BlockState.ROAD;
      foodRemaining--;
      return true;
    }
    return false;
  }

  /**
   * @return the points in the environment, where food is available.
   */
  public List<Point> getFoodPoints() {
    List<Point> lst = new ArrayList<>();

    for (int h = 0; h < height; h++) {
      for (int w = 0; w < width; w++) {
        if (environment[h][w] == BlockState.FOOD) {
          lst.add(new Point(h, w));
        }
      }
    }
    return lst;
  }

  /**
   * @return number of food tiles remaining.
   */
  public int getFoodRemaining() {
    return this.foodRemaining;
  }

  /**
   * @return the blocksize.
   */
  public int getBlockSize() {
    return this.blockSize;
  }

  /**
   * @return the height.
   */
  public int getHeight() {
    return this.height;
  }

  /**
   * @return the width.
   */
  public int getWidth() {
    return this.width;
  }

  /**
   * @return the agents.
   */
  public List<Agent> getAgents() {
    return this.agentList;
  }

  /**
   * @return the agents that are not human.
   */
  public List<Agent> getBotAgents() {
    List<Agent> al = new ArrayList<>(this.agentList);
    Iterator<Agent> iterator = al.iterator();
    while (iterator.hasNext()) {
      Agent next = iterator.next();
      if (next.isHuman()) {
        iterator.remove();
      }
    }
    return al;
  }

  /**
   * @return the human player.
   */
  public Agent getHumanPlayer() {
    return this.humanPlayer;
  }

}
