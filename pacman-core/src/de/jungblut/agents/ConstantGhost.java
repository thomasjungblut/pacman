package de.jungblut.agents;

import de.jungblut.gameplay.maze.Maze;

/**
 * Constant direction ghost.
 * 
 * @author thomas.jungblut
 * 
 */
public class ConstantGhost extends EnvironmentAgent {

  public ConstantGhost(Maze env) {
    super(env);
  }

  @Override
  public String[] getAnimationSprites() {
    return new String[] { "ghost_0.gif" };
  }

}
