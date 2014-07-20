package de.jungblut.server;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameSessionManager {

  // TODO do we need a garbage collector for finished game sessions?
  private final ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, String> playerToSession = new ConcurrentHashMap<>();

  @SuppressWarnings("resource")
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

  public void cleanupSession(String sessionToken) {
    try (GameSession gameSession = sessions.get(sessionToken)) {
      for (String player : gameSession.getPlayers()) {
        playerToSession.remove(player);
      }
      sessions.remove(sessionToken);
    }
  }

}
