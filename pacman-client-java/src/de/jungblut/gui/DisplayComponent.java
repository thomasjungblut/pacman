package de.jungblut.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;

import de.jungblut.agents.Agent;
import de.jungblut.agents.FollowerGhost;
import de.jungblut.agents.GhostPlayer;
import de.jungblut.agents.QLearningAgent;
import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.PacmanGameEngine;
import de.jungblut.gameplay.maze.Maze;
import de.jungblut.gameplay.maze.NaiveMapGenerator;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.dense.DenseDoubleVector;

public class DisplayComponent extends JComponent implements KeyListener {

  private static final long serialVersionUID = 1L;

  private static final ScheduledExecutorService THREAD_POOL = Executors
      .newScheduledThreadPool(1);

  private static final double WALL_SPARSITY = 0.4; // =60% walls
  private static final double FOOD_SPARSITY = 0.4; // =60% food

  private static final int BLOCK_SIZE = MainWindow.BLOCK_SIZE;

  private static final String FOOD_GIF = "food.gif";
  private static final String GHOST_0_GIF = "ghost_0.gif";
  private static final String GHOST_1_GIF = "ghost_1.gif";
  private static final String PACMAN_OPEN_GIF = "pacpix_0.gif";
  private static final String PACMAN_CLOSED_GIF = "pacpix_1.gif";

  private static final Color BACKGROUND_COLOR = Color.BLACK;
  private static final Color ROAD_COLOR = BACKGROUND_COLOR;
  private static final Color WALL_COLOR = Color.BLUE;
  private static final Color FONT_COLOR = Color.WHITE;
  private static final Color END_SCREEN_FONT_COLOR = FONT_COLOR;
  private static final Font END_SCREEN_FONT = new Font("Serif", Font.BOLD, 24);

  private final MainWindow mainWindow;

  private volatile Environment environment;
  private volatile PacmanGameEngine engine;

  private DoubleVector initialWeights = new DenseDoubleVector(
      QLearningAgent.NUM_FEATURES);

  private ScheduledFuture<?> gameFuture;

  public DisplayComponent(MainWindow mainWindow) throws IOException {
    this.mainWindow = mainWindow;
    initSpriteCache();
    init();
  }

  public void init() {
    if (gameFuture != null) {
      gameFuture.cancel(true);
    }
    Maze maze = new NaiveMapGenerator().generateMaze(MainWindow.FRAME_HEIGHT
        / BLOCK_SIZE, MainWindow.FRAME_WIDTH / BLOCK_SIZE, WALL_SPARSITY,
        FOOD_SPARSITY);

    ArrayList<Agent> bots = new ArrayList<>();
    bots.add(new FollowerGhost(maze));
    bots.add(new GhostPlayer(maze));

    environment = new Environment(maze,
        new QLearningAgent(maze, initialWeights), bots);
    engine = new PacmanGameEngine(environment);

    engine.registerGameStateCallback((boolean x) -> {
      // do an auto restart if our pacman is not human
        if (!environment.getPacman().isHuman()) {
          if (environment.getPacman() instanceof QLearningAgent) {
            initialWeights = ((QLearningAgent) environment.getPacman())
                .getWeights();
          }
          init();
        }
      });

    for (Agent a : environment.getAgents()) {
      if (a instanceof KeyListener) {
        this.addKeyListener((KeyListener) a);
      }
    }
    addKeyListener(this);

    // update the game 10 times a second
    gameFuture = THREAD_POOL.scheduleAtFixedRate(() -> engine.doGameUpdates(),
        100l, 1000l / 10l, TimeUnit.MILLISECONDS);
  }

  @Override
  protected void paintComponent(Graphics g) {
    // always antialiaze
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    // check if the game is running normally
    // paint the world
    for (int x = 0; x < environment.getMaze().getHeight(); x++) {
      for (int y = 0; y < environment.getMaze().getWidth(); y++) {
        switch (environment.getMaze().getState(x, y)) {
          case FOOD:
            g.drawImage(SpriteCache.getInstance().getImage(FOOD_GIF), y
                * BLOCK_SIZE, x * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
            break;
          case ROAD:
            g.setColor(ROAD_COLOR);
            g.fillRect(y * BLOCK_SIZE, x * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            g.setColor(BACKGROUND_COLOR);
            break;
          case WALL:
            g.setColor(WALL_COLOR);
            g.fillRect(y * BLOCK_SIZE, x * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            g.setColor(BACKGROUND_COLOR);
            break;
          default:
            break;
        }
      }
    }
    // paint the actors
    for (int i = 0; i < environment.getAgents().size(); i++) {
      Agent agent = environment.getAgents().get(i);
      BufferedImage sprite = SpriteCache.getInstance().getImage(GHOST_0_GIF,
          agent.getDirection());
      if (agent.isHuman() && !agent.isPacman()) {
        sprite = SpriteCache.getInstance().getImage(GHOST_1_GIF,
            agent.getDirection());
      }
      if (agent.isPacman()) {
        // do a very simply animation
        sprite = SpriteCache.getInstance().getImage(
            engine.getTicks() % 2 == 0 ? PACMAN_OPEN_GIF : PACMAN_CLOSED_GIF,
            agent.getDirection());
      }
      g.drawImage(sprite, agent.getYPosition() * BLOCK_SIZE,
          agent.getXPosition() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
    }
    // print fps
    int fps = mainWindow.getFps();
    g.setColor(FONT_COLOR);
    g.drawString("FPS: " + fps, 10, 10);
    g.setColor(BACKGROUND_COLOR);
    if (!engine.isRunning()) {
      String s = "You " + (engine.hasWon() ? "won" : "failed miserably")
          + "! Continue? Y/N";
      // calculate the font sizes and center appropriately
      FontMetrics fm = g.getFontMetrics(END_SCREEN_FONT);
      java.awt.geom.Rectangle2D rect = fm.getStringBounds(s, g);
      int textHeight = (int) (rect.getHeight());
      int textWidth = (int) (rect.getWidth());
      int panelHeight = this.getHeight();
      int panelWidth = this.getWidth();
      int x = (panelWidth - textWidth) / 2;
      int y = (panelHeight - textHeight) / 2 + fm.getAscent();
      // fill the background, so our text doesn't vanish
      g.fillRect(x, y - fm.getAscent(), textWidth, textHeight);
      g.setColor(END_SCREEN_FONT_COLOR);
      g.setFont(END_SCREEN_FONT);
      g.drawString(s, x, y);
      g.setColor(BACKGROUND_COLOR);
    }
  }

  @Override
  public boolean isDoubleBuffered() {
    return true;
  }

  @Override
  public void keyTyped(KeyEvent e) {
    if (!engine.isRunning()) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_Y:
          init();
          break;
        case KeyEvent.VK_N:
          mainWindow.setRunning(false);
          break;
      }
      return;
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    keyTyped(e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    keyTyped(e);
  }

  private void initSpriteCache() throws IOException {
    SpriteCache.getInstance().registerResource(FOOD_GIF, Optional.empty());
    SpriteCache.getInstance().registerResource(GHOST_0_GIF, Optional.empty());
    SpriteCache.getInstance().registerResource(GHOST_1_GIF, Optional.empty());
    SpriteCache.getInstance().registerResource(PACMAN_OPEN_GIF,
        Optional.empty());
    SpriteCache.getInstance().registerResource(PACMAN_CLOSED_GIF,
        Optional.empty());
  }

}
