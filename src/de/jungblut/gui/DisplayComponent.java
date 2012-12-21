package de.jungblut.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyListener;

import javax.swing.JComponent;

import de.jungblut.agents.Agent;
import de.jungblut.gameplay.Environment;

public class DisplayComponent extends JComponent {

  private static final long serialVersionUID = 1L;

  private static final Color BACKGROUND = Color.BLACK;
  private static final Color ROAD = BACKGROUND;
  private static final Color WALL = Color.BLUE;
  private static final Color FONT = Color.WHITE;

  private final Environment environment;

  private final MainWindow mainWindow;

  public DisplayComponent(MainWindow mainWindow, Environment environment) {
    this.mainWindow = mainWindow;
    this.environment = environment;
    for (Agent a : environment.getAgents()) {
      if (a instanceof KeyListener) {
        addKeyListener((KeyListener) a);
      }
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    // paint the world
    for (int x = 0; x < environment.getHeight(); x++) {
      for (int y = 0; y < environment.getWidth(); y++) {
        switch (environment.getState(x, y)) {
          case FOOD:
            break;
          case ROAD:
            g.setColor(ROAD);
            g.fillRect(y * environment.getBlockSize(),
                x * environment.getBlockSize(), environment.getBlockSize(),
                environment.getBlockSize());
            g.setColor(BACKGROUND);
            break;
          case WALL:
            g.setColor(WALL);
            g.fillRect(y * environment.getBlockSize(),
                x * environment.getBlockSize(), environment.getBlockSize(),
                environment.getBlockSize());
            g.setColor(BACKGROUND);
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
    g.setColor(FONT);
    g.drawString("FPS: " + fps, 10, 10);
    g.setColor(BACKGROUND);
  }

  @Override
  public boolean isFocusable() {
    return true;
  }

  @Override
  public boolean isDoubleBuffered() {
    return true;
  }
}
