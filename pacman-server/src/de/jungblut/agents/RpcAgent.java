package de.jungblut.agents;

import de.jungblut.gameplay.maze.Maze;

public class RpcAgent extends EnvironmentAgent {

  private final String clientToken;

  public RpcAgent(Maze env, String clientToken) {
    super(env);
    this.clientToken = clientToken;
  }

  public String getClientToken() {
    return this.clientToken;
  }

}
