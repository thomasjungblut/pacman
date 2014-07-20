package de.jungblut.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class TestMatchMaker {

  @Test(timeout = 10_000)
  public void testMatchMakingSimple() throws Exception {

    GameSessionManager manager = new GameSessionManager();
    MatchMaker maker = new MatchMaker(manager);

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
    Assert.assertEquals(1, manager.currentSessions());
    Assert.assertEquals(2, sessionForPlayer.getPlayers().size());
    Assert.assertTrue(sessionForPlayer.getPlayers().contains(firstPlayer));
    Assert.assertTrue(sessionForPlayer.getPlayers().contains(secondPlayer));
  }

  @Test(timeout = 10_000)
  public void testMatchMakingMultipleThreads() throws Exception {

    GameSessionManager manager = new GameSessionManager();
    final Set<String> set = Collections
        .newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    final MatchMaker maker = new MatchMaker(manager);

    IntStream.range(0, 200).parallel().forEach((x) -> {
      String playerUid = UUID.randomUUID().toString();
      set.add(playerUid);
      maker.enqueuePlayer(playerUid);
    });

    while (maker.waitingPlayers() > 0) {
      Thread.sleep(100);
    }

    Set<String> distinctSessions = new HashSet<>();
    for (String s : set) {
      GameSession session = manager.getSessionForPlayer(s);
      Assert.assertNotNull(session);
      distinctSessions.add(session.getSessionToken());
    }

    Assert.assertEquals(100, manager.currentSessions());
    Assert.assertEquals(100, distinctSessions.size());

  }

}
