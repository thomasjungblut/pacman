package de.jungblut.agents;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import de.jungblut.gameplay.Environment.Direction;

/**
 * Drawable agent, takes care of the animation handling and rotating images.
 * 
 * @author thomas.jungblut
 * 
 */
public abstract class DrawableAgent implements Agent {

  private int currentAnimationIndex = 0;
  private BufferedImage[][] directionCache;

  public DrawableAgent() {
    directionCache = new BufferedImage[Direction.values().length][getNumAnimationSprites()];
    for (int i = 0; i < directionCache.length; i++) {
      directionCache[i] = new BufferedImage[getNumAnimationSprites()];
    }
  }

  /**
   * Takes care of animating the sprites.
   */
  @Override
  public BufferedImage getSprite() {
    BufferedImage img = rotateCached(
        getAnimationSprites()[currentAnimationIndex], currentAnimationIndex,
        getDirection());
    if (getAnimationSprites().length - 1 <= currentAnimationIndex++) {
      currentAnimationIndex = 0;
    }
    return img;
  }

  /**
   * @return get the images of an animation as array.
   */
  protected abstract BufferedImage[] getAnimationSprites();

  /**
   * @return the number of sprites returned by {@link #getAnimationSprites()}.
   */
  protected int getNumAnimationSprites() {
    return 1;
  }

  /**
   * A rotated image cache for not rotating stuff everytime.
   */
  private BufferedImage rotateCached(BufferedImage img, int animationIndex,
      Direction d) {
    if (directionCache[d.getIndex()][animationIndex] == null) {
      directionCache[d.getIndex()][animationIndex] = rotate(img, d);
    }
    return directionCache[d.getIndex()][animationIndex];
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

  @Override
  public boolean isHuman() {
    return false;
  }

}
