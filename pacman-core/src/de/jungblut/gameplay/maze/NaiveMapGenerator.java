package de.jungblut.gameplay.maze;

import java.awt.Point;
import java.util.Random;

import de.jungblut.gameplay.maze.Maze.BlockState;
import de.jungblut.gameplay.maze.Maze.Direction;

public class NaiveMapGenerator implements MazeGenerator {

  private final Random random = new Random();

  @Override
  public Maze generateMaze(int height, int width, double wallSparsity,
      double foodSparsity) {
    BlockState[][] environment = new BlockState[height][width];
    Maze maze = new Maze(height, width, environment);
    generateMaze(maze, wallSparsity);
    spawnFood(maze, foodSparsity);
    maze.setFoodRemaining(maze.getFoodPoints().size());
    return maze;
  }

  private void spawnFood(Maze maze, double foodSparsity) {
    // basically spawn food everywhere nothing else is while taking the food
    // sparsity into account
    for (int h = 0; h < maze.getHeight(); h++) {
      for (int w = 0; w < maze.getWidth(); w++) {
        if (maze.getBlockStates()[h][w] == BlockState.ROAD
            && random.nextDouble() > foodSparsity) {
          maze.getBlockStates()[h][w] = BlockState.FOOD;
        }
      }
    }
  }

  /**
   * Pseudo-pacman maze generator. Pretty crazy logic.
   */
  private void generateMaze(Maze maze, double wallSparsity) {
    for (int h = 0; h < maze.getHeight(); h++) {
      for (int w = 0; w < maze.getWidth(); w++) {
        if (h == 0 || w == 0 || w == maze.getWidth() - 1
            || h == maze.getHeight() - 1 || random.nextDouble() > wallSparsity) {
          maze.getBlockStates()[h][w] = BlockState.WALL;
        } else {
          maze.getBlockStates()[h][w] = BlockState.ROAD;
        }
      }
    }

    // remove walls that are deadends
    // we need at least 4 passes to get out all the surroundings.
    for (int i = 0; i < 4; i++) {
      for (int h = 0; h < maze.getHeight(); h++) {
        for (int w = 0; w < maze.getWidth(); w++) {
          int surrounds = 0;
          for (Direction d : Direction.values()) {
            if (maze.getState(h, w, d) == BlockState.WALL
                && !(h == 0 || w == 0 || w == maze.getWidth() - 1 || h == maze
                    .getHeight() - 1)) {
              surrounds++;
            }
          }
          if (surrounds > 2) {
            for (Direction d : Direction.values()) {
              Point point = Maze.getPoint(h, w, d);
              if (maze.getState(point.x, point.y) == BlockState.WALL
                  && !(point.x == 0 || point.y == 0
                      || point.y == maze.getWidth() - 1 || point.x == maze
                      .getHeight() - 1)) {
                maze.getBlockStates()[point.x][point.y] = BlockState.ROAD;
                break;
              }
            }
          }
        }
      }
    }
  }

}
