package de.jungblut.gui;

import java.awt.Color;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import de.jungblut.agents.Agent;
import de.jungblut.gameplay.Environment;

public class MainWindow extends JFrame {

  public static int TARGET_FPS = 5;

  private static final long serialVersionUID = 1L;

  private static final int X_OFFSET = 1220;
  private static final int Y_OFFSET = 200;
  private static final int FRAME_WIDTH = 500;
  private static final int FRAME_HEIGHT = 300;
  private static final int BLOCK_SIZE = 20;

  private boolean running = true;
  private int fps;

  private Environment environment;

  public MainWindow() {
    super("Pacman");
    init();
    add(new DisplayComponent(this, environment));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setFocusable(true);
    setBounds(X_OFFSET, Y_OFFSET, FRAME_WIDTH, FRAME_HEIGHT + BLOCK_SIZE);
    getContentPane().setBackground(Color.BLACK);
    setVisible(true);
  }

  private void init() {
    environment = new Environment(FRAME_WIDTH, FRAME_HEIGHT, BLOCK_SIZE);
    for (Agent a : environment.getAgents()) {
      if (a instanceof KeyListener) {
        this.addKeyListener((KeyListener) a);
      }
    }
  }

  public void run() {
    long lastLoopTime = System.nanoTime();
    long lastFpsTime = 0;
    int fps = 0;
    while (running) {
      long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
      long now = System.nanoTime();
      long updateLength = now - lastLoopTime;
      lastLoopTime = now;
      double delta = updateLength / ((double) OPTIMAL_TIME);
      lastFpsTime += updateLength;
      fps++;

      if (lastFpsTime >= 1000000000) {
        this.fps = fps;
        lastFpsTime = 0;
        fps = 0;
      }

      doGameUpdates(delta);
      render();

      try {
        long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
        if (sleepTime > 0) {
          Thread.sleep(sleepTime);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  private void doGameUpdates(double delta) {
    for (Agent agent : environment.getAgents()) {
      agent.move();
    }
  }

  private void render() {
    repaint();
  }

  public int getFps() {
    return this.fps;
  }

  public static void main(String[] args) {
    MainWindow win = new MainWindow();
    win.run();
  }

}
