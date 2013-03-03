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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import de.jungblut.agents.Agent;
import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.FoodConsumerListener;
import de.jungblut.gameplay.GameStateListener;

public class DisplayComponent extends JComponent implements KeyListener {

  private static final long serialVersionUID = 1L;

  private static final Color BACKGROUND_COLOR = Color.BLACK;
  private static final Color ROAD_COLOR = BACKGROUND_COLOR;
  private static final Color WALL_COLOR = Color.BLUE;
  private static final Color FONT_COLOR = Color.WHITE;
  private static final Color END_SCREEN_FONT_COLOR = FONT_COLOR;
  private static final Font END_SCREEN_FONT = new Font("Serif", Font.BOLD, 24);

  private final MainWindow mainWindow;
  private final BufferedImage foodSprite;

  private Environment environment;
  // note that this is a triple state for null=running, true=won, false=lost
  private Boolean won;

  private List<FoodConsumerListener> foodNotifier;
  private List<GameStateListener> gameStateNotifier;

  public DisplayComponent(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
    init();
    try {
      foodSprite = ImageIO.read(new File("sprites/food.gif"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    addKeyListener(this);
  }

  public void init() {
    won = null;
    environment = new Environment(MainWindow.FRAME_WIDTH,
        MainWindow.FRAME_HEIGHT, MainWindow.BLOCK_SIZE);
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
      List<Agent> agents = environment.getAgents();
      for (int i = 0; i < agents.size(); i++) {
        Agent agent = agents.get(i);
        agent.move();
        if (agent.isHuman()) {
          if (environment
              .removeFood(agent.getXPosition(), agent.getYPosition())) {
            for (FoodConsumerListener listener : foodNotifier) {
              listener.consumedFood(agent.getXPosition(), agent.getYPosition(),
                  environment.getFoodRemaining());
            }
            if (environment.getFoodRemaining() <= 0) {
              won = true;
              break;
            }
          }
        }

        // check for other agent collisions
        for (int j = 0; j < agents.size(); j++) {
          Agent agent2 = agents.get(j);
          // check for agent collisions with a human
          if (agent2.isHuman() && !agent.isHuman()) {
            if (agent.getXPosition() == environment.getHumanPlayer()
                .getXPosition()
                && agent.getYPosition() == environment.getHumanPlayer()
                    .getYPosition()) {
              won = false;
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
    for (int x = 0; x < environment.getHeight(); x++) {
      for (int y = 0; y < environment.getWidth(); y++) {
        switch (environment.getState(x, y)) {
          case FOOD:
            g.drawImage(foodSprite, y * environment.getBlockSize(), x
                * environment.getBlockSize(), environment.getBlockSize(),
                environment.getBlockSize(), null);
            break;
          case ROAD:
            g.setColor(ROAD_COLOR);
            g.fillRect(y * environment.getBlockSize(),
                x * environment.getBlockSize(), environment.getBlockSize(),
                environment.getBlockSize());
            g.setColor(BACKGROUND_COLOR);
            break;
          case WALL:
            g.setColor(WALL_COLOR);
            g.fillRect(y * environment.getBlockSize(),
                x * environment.getBlockSize(), environment.getBlockSize(),
                environment.getBlockSize());
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
      g.drawImage(agent.getSprite(),
          agent.getYPosition() * environment.getBlockSize(),
          agent.getXPosition() * environment.getBlockSize(),
          environment.getBlockSize(), environment.getBlockSize(), null);
    }
    // print fps
    int fps = mainWindow.getFps();
    g.setColor(FONT_COLOR);
    g.drawString("FPS: " + fps, 10, 10);
    g.setColor(BACKGROUND_COLOR);
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
}
