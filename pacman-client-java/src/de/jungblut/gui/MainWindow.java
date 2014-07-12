package de.jungblut.gui;

import java.awt.Color;

import javax.swing.JFrame;

public class MainWindow extends JFrame {

  public static int TARGET_FPS = 60;
  public static final int BLOCK_SIZE = 20;

  private static final int ONE_BILLION = 1_000_000_000;
  private static final long serialVersionUID = 1L;

  private static final int X_OFFSET = 1220;
  private static final int Y_OFFSET = 200;
  static final int FRAME_WIDTH = 500;
  static final int FRAME_HEIGHT = 300;

  private volatile boolean running = true;
  private int currentFps;

  private DisplayComponent displayComponent;

  public MainWindow() throws Exception {
    super("Pacman");
    displayComponent = new DisplayComponent(this);
    displayComponent.setFocusable(true);
    displayComponent.requestFocusInWindow();
    add(displayComponent);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setBounds(X_OFFSET, Y_OFFSET, FRAME_WIDTH, FRAME_HEIGHT + BLOCK_SIZE);
    getContentPane().setBackground(Color.BLACK);
    setVisible(true);
  }

  public void run() throws Exception {
    long lastLoopTime = System.nanoTime();
    long lastFpsTime = 0;
    int fps = 0;
    while (running) {
      long optimalTime = ONE_BILLION / TARGET_FPS;
      long now = System.nanoTime();
      long updateLength = now - lastLoopTime;
      lastLoopTime = now;
      lastFpsTime += updateLength;
      fps++;

      if (lastFpsTime >= ONE_BILLION) {
        this.currentFps = fps;
        lastFpsTime = 0;
        fps = 0;
      }

      displayComponent.doGameUpdates();
      render();

      long sleepTime = (lastLoopTime - System.nanoTime() + optimalTime) / 1000000;
      if (sleepTime > 0) {
        Thread.sleep(sleepTime);
      }
    }

    // if we quit our running task, we can exit the JVM
    System.exit(0);
  }

  private void render() {
    displayComponent.repaint();
  }

  public int getFps() {
    return this.currentFps;
  }

  void setRunning(boolean running) {
    this.running = running;
  }

  public static void main(String[] args) throws Exception {
    MainWindow win = new MainWindow();
    win.run();
  }

}
