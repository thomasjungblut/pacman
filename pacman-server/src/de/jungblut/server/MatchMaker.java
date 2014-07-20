package de.jungblut.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MatchMaker {

  private static final Log LOG = LogFactory.getLog(MatchMaker.class);

  private static final int OPTIMAL_PLAYER_COUNT = 2;

  // contains the request GUIDs, will be picked up by the matchmaker
  private final ConcurrentLinkedQueue<String> requestQueue = new ConcurrentLinkedQueue<>();
  private final ScheduledExecutorService scheduler = Executors
      .newSingleThreadScheduledExecutor();

  private final AtomicInteger queueSize = new AtomicInteger();

  private final GameSessionManager manager;

  public MatchMaker(GameSessionManager manager) {
    this.manager = manager;
    scheduler.scheduleAtFixedRate(this::tryToMakeMatch, 0l, 100l,
        TimeUnit.MILLISECONDS);
  }

  /**
   * @return the count of how many players are currently waiting for a game to
   *         be established.
   */
  public int enqueuePlayer(String playerToken) {
    requestQueue.add(playerToken);
    return queueSize.incrementAndGet();
  }

  public int waitingPlayers() {
    return queueSize.get();
  }

  // TODO test starvation issues- ideally we would add bots after 1 minute of
  // waiting
  private void tryToMakeMatch() {

    int currentSize = queueSize.get();
    while (currentSize >= OPTIMAL_PLAYER_COUNT) {
      Set<String> matched = new HashSet<>();
      int remaining = 0;
      for (int i = 0; i < OPTIMAL_PLAYER_COUNT; i++) {
        matched.add(requestQueue.poll());
        remaining = queueSize.decrementAndGet();
      }
      LOG.info("Matched players: " + matched + "! Remaining waits: "
          + remaining);
      manager.establishNewSession(matched);
      currentSize = queueSize.get();
    }

  }

}
