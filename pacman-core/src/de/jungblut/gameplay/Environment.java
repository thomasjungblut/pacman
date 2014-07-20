package de.jungblut.gameplay;

import java.util.ArrayList;
import java.util.List;

import de.jungblut.agents.Agent;
import de.jungblut.gameplay.maze.Maze;

/**
 * The game environment. Takes care of the world by: generating it, give helper
 * methods for interacting with the environment and taking care of the agents.
 * 
 * @author thomas.jungblut
 * 
 */
public class Environment {

  private final Maze maze;
  private final Agent pacman;
  private final List<? extends Agent> ghosts;

  public Environment(Maze maze, Agent pacman, List<? extends Agent> ghosts) {
    this.maze = maze;
    this.pacman = pacman;
    this.ghosts = ghosts;
  }

  public List<Agent> getAgents() {
    ArrayList<Agent> lst = new ArrayList<>(ghosts);
    lst.add(pacman);
    return lst;
  }

  public List<? extends Agent> getGhosts() {
    return this.ghosts;
  }

  public Agent getPacman() {
    return this.pacman;
  }

  public Maze getMaze() {
    return this.maze;
  }

  /**
   * @return true if there is a player on the tile.
   */
  public boolean isPlayerOnTile(int height, int width, List<Agent> agentList) {
    for (Agent agent : agentList) {
      if (agent.getXPosition() == height && agent.getYPosition() == width)
        return true;
    }
    return false;
  }

}
