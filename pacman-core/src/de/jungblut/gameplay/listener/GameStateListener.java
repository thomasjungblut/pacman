package de.jungblut.gameplay.listener;

public interface GameStateListener {

  /**
   * Triggered when the game changes state.
   * 
   * @param won true if won, false if lost.
   * @return true for game restart, false for exit.
   */
  public boolean gameStateChanged(boolean won);

}
