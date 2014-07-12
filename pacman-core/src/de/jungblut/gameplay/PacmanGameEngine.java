package de.jungblut.gameplay;

import java.util.ArrayList;
import java.util.List;

import de.jungblut.agents.Agent;
import de.jungblut.gameplay.listener.FoodConsumerListener;
import de.jungblut.gameplay.listener.GameStateListener;

public class PacmanGameEngine {

  // that this is a triple state for null=running, true=won, false=lost
  private volatile Boolean won;
  private long ticks;

  private final List<FoodConsumerListener> foodNotifier = new ArrayList<>();
  private final List<GameStateListener> gameStateNotifier = new ArrayList<>();
  private final Environment environment;

  public PacmanGameEngine(Environment environment) {
    this.environment = environment;
    // register the agent callbacks
    for (Agent a : environment.getAgents()) {
      if (a instanceof FoodConsumerListener) {
        foodNotifier.add((FoodConsumerListener) a);
      }
      if (a instanceof GameStateListener) {
        gameStateNotifier.add((GameStateListener) a);
      }
    }
  }

  public void registerGameStateCallback(GameStateListener listener) {
    gameStateNotifier.add(listener);
  }

  public void registerFoodConsumedCallback(FoodConsumerListener listener) {
    foodNotifier.add(listener);
  }

  public Environment getEnvironment() {
    return this.environment;
  }

  public long getTicks() {
    return this.ticks;
  }

  public boolean hasWon() {
    return this.won != null && this.won == true;
  }

  public boolean isRunning() {
    return this.won == null;
  }

  public void doGameUpdates() {
    if (won == null) {
      ticks++;
      List<Agent> agents = environment.getAgents();
      for (int i = 0; i < agents.size(); i++) {
        Agent agent = agents.get(i);
        agent.move(environment);
        if (agent.isPacman()) {
          if (environment.getMaze().removeFood(agent.getXPosition(),
              agent.getYPosition())) {
            for (FoodConsumerListener listener : foodNotifier) {
              listener.consumedFood(environment, agent.getXPosition(), agent
                  .getYPosition(), environment.getMaze().getFoodRemaining());
            }
            if (environment.getMaze().getFoodRemaining() <= 0) {
              changeStateToWon();
              break;
            }
          }
        }

        // check for other agent collisions
        for (int j = 0; j < agents.size(); j++) {
          Agent agent2 = agents.get(j);
          // check for agent collisions with a pacman
          if (agent2.isPacman() && !agent.isPacman()) {
            if (agent.getXPosition() == environment.getPacman().getXPosition()
                && agent.getYPosition() == environment.getPacman()
                    .getYPosition()) {
              changeStateToLost();
              break;
            }
          }
        }
      }
    }
  }

  private void changeStateToWon() {
    changeGameState(true);
  }

  private void changeStateToLost() {
    changeGameState(false);
  }

  private synchronized void changeGameState(boolean won) {
    this.won = won;
    for (GameStateListener listener : gameStateNotifier) {
      listener.gameStateChanged(won);
    }
  }

}
