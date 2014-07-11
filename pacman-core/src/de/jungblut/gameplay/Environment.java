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
  private final Agent human;
  private final List<Agent> bots;

  public Environment(Maze maze, Agent human, List<Agent> bots) {
    this.maze = maze;
    this.human = human;
    this.bots = bots;
  }

  public List<Agent> getAgents() {
    ArrayList<Agent> lst = new ArrayList<>(bots);
    lst.add(human);
    return lst;
  }

  public List<Agent> getBots() {
    return this.bots;
  }

  public Agent getHuman() {
    return this.human;
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
