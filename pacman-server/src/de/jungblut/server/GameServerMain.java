package de.jungblut.server;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.thrift.TException;

import de.jungblut.thrift.GameState;
import de.jungblut.thrift.Match;
import de.jungblut.thrift.MatchService;

public class GameServerMain implements MatchService.Iface {

  // contains the request GUIDs, will be picked up by the matchmaker
  private final ConcurrentLinkedQueue<String> requestQueue = new ConcurrentLinkedQueue<>();

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
