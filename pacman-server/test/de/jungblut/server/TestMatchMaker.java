package de.jungblut.server;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class TestMatchMaker {

  @Test(timeout = 120_000)
  public void testMatchMakingSimple() throws Exception {

    GameSessionManager manager = new GameSessionManager();
    MatchMaker maker = new MatchMaker(manager);

    // TODO rewrite this to a set, to test multiple games to be match maked in
    // the multithreading
    // TODO also test starvation issues- ideally we would add bots to the match
    // maker then

    String firstPlayer = UUID.randomUUID().toString();
    String secondPlayer = UUID.randomUUID().toString();

    // single player, no match
    int waiting = maker.enqueuePlayer(firstPlayer);
    Assert.assertEquals(1, waiting);
    GameSession sessionForPlayer = manager.getSessionForPlayer(firstPlayer);
    Assert.assertNull(sessionForPlayer);

    // enqueue another one- should generate a match
    waiting = maker.enqueuePlayer(secondPlayer);
    Assert.assertEquals(2, waiting);

    // await a match to be made
    while (manager.getSessionForPlayer(firstPlayer) == null) {
      Thread.sleep(100);
    }

    sessionForPlayer = manager.getSessionForPlayer(firstPlayer);
    Assert.assertNotNull(sessionForPlayer);
    GameSession sessionSecondPlayer = manager.getSessionForPlayer(secondPlayer);
    Assert.assertNotNull(sessionSecondPlayer);

    Assert.assertEquals(sessionSecondPlayer.getSessionToken(),
        sessionForPlayer.getSessionToken());

    Assert.assertEquals(2, sessionForPlayer.getPlayers().size());
    Assert.assertTrue(sessionForPlayer.getPlayers().contains(firstPlayer));
    Assert.assertTrue(sessionForPlayer.getPlayers().contains(secondPlayer));
  }

}
