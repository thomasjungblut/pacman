package de.jungblut.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;

import de.jungblut.thrift.GameState;
import de.jungblut.thrift.Match;
import de.jungblut.thrift.MatchService;

public class GameServerMain implements MatchService.Iface {

  private static final Log LOG = LogFactory.getLog(GameServerMain.class);

  private static final GameSessionManager SESSION_MANAGER = new GameSessionManager();
  private static final MatchMaker MATCH_MAKER = new MatchMaker(SESSION_MANAGER);

  @Override
  public String queueForGame() throws TException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Match pollGameSetupCompleted(String requestToken) throws TException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GameState getInitialGameState(String sessionToken,
      String clientIdentifier) throws TException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void awaitStart(String sessionToken, String clientIdentifier)
      throws TException {
    // TODO Auto-generated method stub

  }

  public static void main(String[] args) {

  }

}
