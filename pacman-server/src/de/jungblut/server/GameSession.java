package de.jungblut.server;

import java.util.Collections;
import java.util.Set;

public class GameSession {

  private String sessionToken;
  private Set<String> players;

  public GameSession(String sessionToken, Set<String> players) {
    this.sessionToken = sessionToken;
    this.players = players;
  }

  public Set<String> getPlayers() {
    return Collections.unmodifiableSet(this.players);
  }

  public String getSessionToken() {
    return this.sessionToken;
  }

}
