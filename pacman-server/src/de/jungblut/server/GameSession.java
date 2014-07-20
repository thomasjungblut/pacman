package de.jungblut.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.sun.xml.internal.ws.Closeable;

import de.jungblut.agents.QLearningAgent;
import de.jungblut.agents.RpcAgent;
import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.PacmanGameEngine;
import de.jungblut.gameplay.maze.Maze;
import de.jungblut.gameplay.maze.Maze.BlockState;
import de.jungblut.gameplay.maze.NaiveMapGenerator;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.dense.DenseDoubleVector;
import de.jungblut.thrift.Point;

public class GameSession implements Closeable {

  private static final int FRAME_WIDTH = 500;
  private static final int FRAME_HEIGHT = 300;
  private static final int BLOCK_SIZE = 20;

  private static final double WALL_SPARSITY = 0.4; // =60% walls
  private static final double FOOD_SPARSITY = 0.4; // =60% food

  private final String sessionToken;
  private final Set<String> players;

  // TODO this will make the agent dumb as hell, we should store the
  // most-winning weights somewhere
  private final DoubleVector initialWeights = new DenseDoubleVector(
      QLearningAgent.NUM_FEATURES);

  private Environment environment;
  private PacmanGameEngine engine;
  private CountDownLatch startingLatch;
  private List<RpcAgent> agents;

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
    agents = players.stream().map((s) -> new RpcAgent(maze, s))
        .collect(Collectors.toList());
    environment = new Environment(maze,
        new QLearningAgent(maze, initialWeights), agents);
    engine = new PacmanGameEngine(environment);
  }

  public void awaitStart() throws InterruptedException {
    // TODO this breaks if the same client awaits multiple times
    startingLatch.countDown();
    startingLatch.await(2l, TimeUnit.MINUTES);
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

  public List<Point> getPlayerPositions() {
    return agents.stream()
        .map((agent) -> new Point(agent.getXPosition(), agent.getYPosition()))
        .collect(Collectors.toList());
  }

  public int getPlayerIndex(String clientIdentifier) {
    for (int i = 0; i < agents.size(); i++) {
      if (agents.get(i).getClientToken().equals(clientIdentifier)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public void close() {
    // TODO in case the game needs to be closed during setup failures
  }

}
