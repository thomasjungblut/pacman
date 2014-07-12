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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JComponent;

import de.jungblut.agents.Agent;
import de.jungblut.agents.FollowerGhost;
import de.jungblut.agents.GhostPlayer;
import de.jungblut.agents.QLearningAgent;
import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.listener.FoodConsumerListener;
import de.jungblut.gameplay.listener.GameStateListener;
import de.jungblut.gameplay.maze.Maze;
import de.jungblut.gameplay.maze.NaiveMapGenerator;

public class DisplayComponent extends JComponent implements KeyListener {

  private static final long serialVersionUID = 1L;

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

  private Environment environment;

  private volatile Lock winLock = new ReentrantLock();
  // note that this is a triple state for null=running, true=won, false=lost
  private volatile Boolean won;

  private List<FoodConsumerListener> foodNotifier;
  private List<GameStateListener> gameStateNotifier;

  private long ticks;

  public DisplayComponent(MainWindow mainWindow) throws IOException {
    this.mainWindow = mainWindow;
    initSpriteCache();

    init();
    addKeyListener(this);
  }

  public void init() {
    won = null;
    ticks = 0;
    Maze maze = new NaiveMapGenerator().generateMaze(MainWindow.FRAME_HEIGHT
        / BLOCK_SIZE, MainWindow.FRAME_WIDTH / BLOCK_SIZE, WALL_SPARSITY,
        FOOD_SPARSITY);

    ArrayList<Agent> bots = new ArrayList<>();
    bots.add(new FollowerGhost(maze));
    bots.add(new GhostPlayer(maze));

    environment = new Environment(maze, new QLearningAgent(maze), bots);

    foodNotifier = new ArrayList<>();
    gameStateNotifier = new ArrayList<>();
    // register the callbacks
    for (Agent a : environment.getAgents()) {
      if (a instanceof KeyListener) {
        this.addKeyListener((KeyListener) a);
      }
      if (a instanceof FoodConsumerListener) {
        foodNotifier.add((FoodConsumerListener) a);
      }
      if (a instanceof GameStateListener) {
        gameStateNotifier.add((GameStateListener) a);
      }
    }
  }

  public void doGameUpdates(double delta) {
    if (won == null) {
      ticks++;
      List<Agent> agents = environment.getAgents();
      for (int i = 0; i < agents.size(); i++) {
        Agent agent = agents.get(i);
        agent.move(environment);
        if (agent.isPacman()) {
          if (environment.getMaze().removeFood(agent.getXPosition(),
              agent.getYPosition())) {
            for (FoodConsumerListener listener : foodNotifier) {
              listener.consumedFood(environment, agent.getXPosition(), agent
                  .getYPosition(), environment.getMaze().getFoodRemaining());
            }
            if (environment.getMaze().getFoodRemaining() <= 0) {
              try {
                winLock.lock();
                won = true;
              } finally {
                winLock.unlock();
              }
              break;
            }
          }
        }

        // check for other agent collisions
        for (int j = 0; j < agents.size(); j++) {
          Agent agent2 = agents.get(j);
          // check for agent collisions with a pacman
          if (agent2.isPacman() && !agent.isPacman()) {
            if (agent.getXPosition() == environment.getHuman().getXPosition()
                && agent.getYPosition() == environment.getHuman()
                    .getYPosition()) {
              try {
                winLock.lock();
                won = false;
              } finally {
                winLock.unlock();
              }
              break;
            }
          }
        }
      }
      if (won != null) {
        for (GameStateListener listener : gameStateNotifier) {
          if (listener.gameStateChanged(won)) {
            init();
          } else {
            mainWindow.setRunning(false);
          }
        }
      }
    }
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
        sprite = SpriteCache.getInstance().getImage(
            ticks % 2 == 0 ? PACMAN_OPEN_GIF : PACMAN_CLOSED_GIF,
            agent.getDirection());
      }
      g.drawImage(sprite, agent.getYPosition() * BLOCK_SIZE,
          agent.getXPosition() * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
    }
    // print fps
    int fps = mainWindow.getFps();
    g.setColor(FONT_COLOR);
    g.drawString("FPS: " + fps, 10, 10);
    g.drawString("Exploration probabaility: "
        + QLearningAgent.EXPLORATION_PROBABILITY, 80, 10);
    g.setColor(BACKGROUND_COLOR);
    try {
      winLock.lock();
      if (won != null) {
        String s = "You " + (won ? "won" : "failed miserably")
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
    } finally {
      winLock.unlock();
    }
  }

  @Override
  public boolean isDoubleBuffered() {
    return true;
  }

  @Override
  public void keyTyped(KeyEvent e) {
    if (won != null) {
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
    switch (e.getKeyCode()) {
      case KeyEvent.VK_F1:
        if (MainWindow.TARGET_FPS > 1) {
          MainWindow.TARGET_FPS--;
        }
        break;
      case KeyEvent.VK_F2:
        MainWindow.TARGET_FPS++;
        break;
      case KeyEvent.VK_F3:
        if (QLearningAgent.EXPLORATION_PROBABILITY > 0) {
          QLearningAgent.EXPLORATION_PROBABILITY -= 0.1;
        }
        break;
      case KeyEvent.VK_F4:
        if (QLearningAgent.EXPLORATION_PROBABILITY < 1d) {
          QLearningAgent.EXPLORATION_PROBABILITY += 0.1;
        }
        break;
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
