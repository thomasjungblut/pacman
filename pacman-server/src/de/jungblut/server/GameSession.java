package de.jungblut.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import de.jungblut.agents.QLearningAgent;
import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.PacmanGameEngine;
import de.jungblut.gameplay.maze.Maze;
import de.jungblut.gameplay.maze.Maze.BlockState;
import de.jungblut.gameplay.maze.NaiveMapGenerator;
import de.jungblut.thrift.Point;

public class GameSession {

  private static final int FRAME_WIDTH = 500;
  private static final int FRAME_HEIGHT = 300;
  private static final int BLOCK_SIZE = 20;

  private static final double WALL_SPARSITY = 0.4; // =60% walls
  private static final double FOOD_SPARSITY = 0.4; // =60% food

  private final String sessionToken;
  private final Set<String> players;

  private Environment environment;
  private PacmanGameEngine engine;
  private CountDownLatch startingLatch;

  public GameSession(String sessionToken, Set<String> players) {
    this.sessionToken = sessionToken;
    this.players = players;
    this.startingLatch = new CountDownLatch(players.size());
    // setup the environment
    init();
  }

  private void init() {
    // use the naive map generator to create a 500x300 pixel map with 20 pixel
    // width per block
    Maze maze = new NaiveMapGenerator().generateMaze(FRAME_HEIGHT / BLOCK_SIZE,
        FRAME_WIDTH / BLOCK_SIZE, WALL_SPARSITY, FOOD_SPARSITY);

    // TODO add virtual bots implementing the RPC interfaces
    environment = new Environment(maze, new QLearningAgent(maze),
        Collections.emptyList());
    engine = new PacmanGameEngine(environment);
  }

  public void awaitStart() throws InterruptedException {
    startingLatch.countDown();
    startingLatch.await();
  }

  public Set<String> getPlayers() {
    return Collections.unmodifiableSet(this.players);
  }

  public String getSessionToken() {
    return this.sessionToken;
  }

  public Point getPacmanPosition() {
    return new Point(environment.getPacman().getXPosition(), environment
        .getPacman().getYPosition());
  }

  public List<List<Byte>> getBoard() {
    List<List<Byte>> board = new ArrayList<>();

    BlockState[][] blockStates = environment.getMaze().getBlockStates();
    for (int i = 0; i < blockStates.length; i++) {
      ArrayList<Byte> row = new ArrayList<>();
      for (int j = 0; j < blockStates[i].length; j++) {
        row.add(blockStates[i][j].getId());
      }
      board.add(row);
    }

    return board;
  }

}
