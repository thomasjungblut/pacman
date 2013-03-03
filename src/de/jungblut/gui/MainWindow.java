package de.jungblut.gui;

import java.awt.Color;

import javax.swing.JFrame;

public class MainWindow extends JFrame {

  public static int TARGET_FPS = 6;

  private static final long serialVersionUID = 1L;

  private static final int X_OFFSET = 1220;
  private static final int Y_OFFSET = 200;
  static final int FRAME_WIDTH = 500;
  static final int FRAME_HEIGHT = 300;
  static final int BLOCK_SIZE = 20;

  private volatile boolean running = true;
  private int fps;

  private DisplayComponent displayComponent;

  public MainWindow() {
    super("Pacman");
    this.displayComponent = new DisplayComponent(this);
    add(displayComponent);
    displayComponent.setFocusable(true);
    displayComponent.requestFocusInWindow();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setBounds(X_OFFSET, Y_OFFSET, FRAME_WIDTH, FRAME_HEIGHT + BLOCK_SIZE);
    getContentPane().setBackground(Color.BLACK);
    setVisible(true);
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

      displayComponent.doGameUpdates(delta);
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

    // if we quit our running task, we can exit the JVM
    System.exit(0);
  }

  private void render() {
    displayComponent.repaint();
  }

  public int getFps() {
    return this.fps;
  }

  void setRunning(boolean running) {
    this.running = running;
  }

  public static void main(String[] args) {
    MainWindow win = new MainWindow();
    win.run();
  }

}
