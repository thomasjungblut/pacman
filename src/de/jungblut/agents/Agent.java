package de.jungblut.agents;

import java.awt.image.BufferedImage;

import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.Environment.Direction;

/**
 * Agent interface to implement a agent in the gaming world. Most
 * implementations should inherit from {@link EnvironmentAgent}.
 * 
 * @author thomas.jungblut
 * 
 */
public interface Agent {

  /**
   * @return get a sprite of the agent, this is called everytime in the gameloop
   *         so it can change within an animation.
   */
  public BufferedImage getSprite();

  /**
   * @return the environment where the agent exists.
   */
  public Environment getEnvironment();

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
  public void move();

  /**
   * @return true if the implemented agent is a human.
   */
  public boolean isHuman();

}
