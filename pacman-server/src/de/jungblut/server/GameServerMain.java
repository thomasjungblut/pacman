package de.jungblut.server;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;

import de.jungblut.thrift.GameState;
import de.jungblut.thrift.Match;
import de.jungblut.thrift.MatchService;

public class GameServerMain implements MatchService.Iface {

  private static final Log LOG = LogFactory.getLog(GameServerMain.class);

  private final GameSessionManager sessionManager = new GameSessionManager();
  private final MatchMaker matchMaker = new MatchMaker(sessionManager);

  @Override
  public String queueForGame() throws TException {
    String uuid = UUID.randomUUID().toString();
    int waiting = matchMaker.enqueuePlayer(uuid);
    LOG.info("Enqueuing " + uuid + ", waiting players: " + waiting);
    return uuid;
  }

  @Override
  public Match pollGameSetupCompleted(String requestToken) throws TException {
    GameSession session = sessionManager.getSessionForPlayer(requestToken);

    if (session == null) {
      return new Match(false, 0, null, null);
    }

    return new Match(false, session.getPlayers().size(),
        session.getSessionToken(), requestToken);
  }

  @Override
  public GameState getInitialGameState(String sessionToken,
      String clientIdentifier) throws TException {
    GameSession session = sessionManager.getSession(sessionToken);

    // TODO get the position of the virtual bots implementing the RPC interfaces
    return new GameState(session.getBoard(), session.getPacmanPosition(),
        new ArrayList<>(), 0);
  }

  @Override
  public void awaitStart(String sessionToken, String clientIdentifier)
      throws TException {

    GameSession session = sessionManager.getSession(sessionToken);
    try {
      session.awaitStart();
    } catch (InterruptedException e) {
      throw new TException(e);
    }
  }

  public static void main(String[] args) {

  }

}
