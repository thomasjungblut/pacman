package de.jungblut.agents;

import java.awt.event.KeyListener;

import de.jungblut.gameplay.maze.Maze;

/**
 * Basic Human Player with a keyboard listener.
 * 
 * @author thomas.jungblut
 * 
 */
public class GhostPlayer extends ControllableAgent implements KeyListener {

  public GhostPlayer(Maze env) {
    super(env);
  }

}
