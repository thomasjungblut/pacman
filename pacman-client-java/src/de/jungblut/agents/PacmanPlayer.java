package de.jungblut.agents;

import java.awt.event.KeyListener;

import de.jungblut.gameplay.maze.Maze;

/**
 * Basic Human Player with a keyboard listener.
 * 
 * @author thomas.jungblut
 * 
 */
public class PacmanPlayer extends ControllableAgent implements KeyListener {

  public PacmanPlayer(Maze env) {
    super(env);
  }

  @Override
  public boolean isPacman() {
    return true;
  }
}
