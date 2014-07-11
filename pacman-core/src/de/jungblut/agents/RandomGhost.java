package de.jungblut.agents;

import java.util.Random;

import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.maze.Maze;
import de.jungblut.gameplay.maze.Maze.Direction;

/**
 * Random direction ghost. Go home ghost, you're drunk!
 * 
 * @author thomas.jungblut
 * 
 */
public class RandomGhost extends EnvironmentAgent {

  private Random rand;

  public RandomGhost(Maze env) {
    super(env);
    rand = new Random();
  }

  @Override
  public void move(Environment env) {
    direction = getRandomDirection(env.getMaze(), x, y, rand);
    super.move(env);
  }

  static Direction getRandomDirection(Maze env, int x, int y, Random rand) {
    Direction direction = Direction.LEFT;
    // don't move to blocked directions, also if our way isn't blocked- run
    // along until blocked. Make 10 tries then give up...
    int tries = 10;
    while (env.isBlocked(y, x, direction) && --tries > 0) {
      direction = Direction.values()[rand.nextInt(4)];
    }
    return direction;
  }

  @Override
  public String[] getAnimationSprites() {
    return new String[] { "ghost_0.gif" };
  }

}
