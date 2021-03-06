package de.jungblut.gameplay.listener;

public interface GameStateListener {

  /**
   * Triggered when the game changes state.
   * 
   * @param won true if won, false if lost.
   */
  public void gameStateChanged(boolean won);

}
