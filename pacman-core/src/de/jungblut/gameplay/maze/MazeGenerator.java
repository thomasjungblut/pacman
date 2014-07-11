package de.jungblut.gameplay.maze;

public interface MazeGenerator {

  public Maze generateMaze(int height, int width, double wallSparsity,
      double foodSparsity);

}
