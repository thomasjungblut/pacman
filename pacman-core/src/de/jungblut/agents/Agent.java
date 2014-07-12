package de.jungblut.agents;

import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.maze.Maze.Direction;

/**
 * Agent interface to implement a agent in the gaming world. Most
 * implementations should inherit from {@link EnvironmentAgent}.
 * 
 * @author thomas.jungblut
 * 
 */
public interface Agent {

  /**
   * @return the current X position.
   */
  public int getXPosition();

  /**
   * @return the current Y position.
   */
  public int getYPosition();

  /**
   * @return the current movement direction.
   */
  public Direction getDirection();

  /**
   * Main game logic for an agent.
   */
  public void move(Environment environment);

  /**
   * @return true if the implemented agent is playing a pacman.
   */
  public boolean isPacman();

  /**
   * @return true if the implemented agent is a human.
   */
  public boolean isHuman();

}
