package de.jungblut.server;

import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import de.jungblut.thrift.GameState;
import de.jungblut.thrift.Match;

public class TestGameServer {

  @Test(timeout = 120_000)
  public void testGameServerEndToEnd() {
    GameServerMain server = new GameServerMain();

    IntStream
        .range(0, 2)
        .parallel()
        .forEach(
            (index) -> {
              try {
                String requestToken = server.queueForGame();
                Match match = null;
                while (!(match = server.pollGameSetupCompleted(requestToken))
                    .isGameReady()) {
                  Thread.sleep(100l);
                }

                Assert.assertEquals(2, match.numCurrentPlayers);

                GameState gameState = server.getInitialGameState(
                    match.getSessionToken(), requestToken);
                Assert.assertNotNull(gameState.getPacmanStart());
                Assert.assertNotNull(gameState.getGhostStart());
                Assert.assertEquals(2, gameState.getGhostStart().size());

                server.awaitStart(match.getSessionToken(), requestToken);
                // we should land here and start the game finally
                Assert.assertTrue(true);

              } catch (Exception e) {
                e.printStackTrace();
                // fail the test in this case
                Assert.fail("Caught an exception during the game simulation!");
              }
            });

  }
}
