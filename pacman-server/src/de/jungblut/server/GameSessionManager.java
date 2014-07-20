package de.jungblut.server;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameSessionManager {

  private final ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, String> playerToSession = new ConcurrentHashMap<>();

  public void establishNewSession(Set<String> players) {
    String sessionToken = UUID.randomUUID().toString();
    sessions.put(sessionToken, new GameSession(sessionToken, players));
    for (String player : players) {
      playerToSession.put(player, sessionToken);
    }
  }

  public GameSession getSession(String sessionToken) {
    return sessions.get(sessionToken);
  }

  public GameSession getSessionForPlayer(String playerToken) {
    String sessionToken = playerToSession.get(playerToken);
    if (sessionToken == null) {
      return null;
    }
    return getSession(sessionToken);
  }

  public int currentSessions() {
    return sessions.size();
  }

}
