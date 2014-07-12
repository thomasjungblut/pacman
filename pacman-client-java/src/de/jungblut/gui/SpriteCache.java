package de.jungblut.gui;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import javax.imageio.ImageIO;

import de.jungblut.gameplay.maze.Maze.Direction;

public class SpriteCache {

  private static final String SPRITE_BASE_PATH = "sprites/";

  private static final SpriteCache INSTANCE = new SpriteCache();

  private SpriteCache() {
  }

  private final HashMap<String, BufferedImage> resources = new HashMap<>();

  public void registerResource(String name, Optional<String> resourcePath)
      throws IOException {
    if (!resources.containsKey(name)) {
      File path = new File(SPRITE_BASE_PATH + name);
      if (resourcePath != null && resourcePath.isPresent()) {
        path = new File(resourcePath.get());
      }
      try {
        BufferedImage img = ImageIO.read(path);
        resources.put(name, img);
        // also add the rotations to this cache
        for (Direction d : Direction.values()) {
          resources.put(getRotatedKeyName(name, d), rotate(img, d));
        }
      } catch (Exception e) {
        System.out.println("Failure reading image under " + path + "!");
        throw e;
      }
    } else {
      System.out.println("Already found a registered resources for \"" + name
          + "\"! Ignoring the new one.");
    }
  }

  public BufferedImage getImage(String name) {
    return resources.get(name);
  }

  public BufferedImage getImage(String name, Direction d) {
    return resources.get(getRotatedKeyName(name, d));
  }

  public static SpriteCache getInstance() {
    return INSTANCE;
  }

  public static String getRotatedKeyName(String name, Direction d) {
    return name + "_" + d.name();
  }

  /**
   * All the sprites are turning left, so if for RIGHT we have to rotate 180°,
   * everything else is calculated in clock-wise rotations.
   */
  private BufferedImage rotate(BufferedImage img, Direction d) {
    switch (d) {
      case DOWN:
        return rotateImage(img, 270d);
      case LEFT:
        return img;
      case UP:
        return rotateImage(img, 90d);
      case RIGHT:
        return flip(img);
    }

    return null;
  }

  /**
   * @return a new and rotated image.
   */
  private BufferedImage rotateImage(BufferedImage src, double degrees) {
    AffineTransform affineTransform = AffineTransform.getRotateInstance(
        Math.toRadians(degrees), src.getWidth() / 2, src.getHeight() / 2);
    BufferedImage rotatedImage = new BufferedImage(src.getWidth(),
        src.getHeight(), BufferedImage.TYPE_INT_ARGB);
    AffineTransformOp op = new AffineTransformOp(affineTransform,
        AffineTransformOp.TYPE_BICUBIC);
    rotatedImage = op.filter(src, rotatedImage);
    return rotatedImage;
  }

  /**
   * @return a horizontally flipped image.
   */
  private BufferedImage flip(BufferedImage img) {
    int w = img.getWidth();
    int h = img.getHeight();
    BufferedImage dimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = dimg.createGraphics();
    g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
    g.dispose();
    return dimg;
  }

}
