package de.jungblut.agents;

import java.awt.image.BufferedImage;

import de.jungblut.utils.SpriteCache;

/**
 * Drawable agent, takes care of the animation handling and rotating images.
 * 
 * @author thomas.jungblut
 * 
 */
public abstract class AnimatedAgent implements Agent {

  private int currentAnimationIndex = 0;

  @Override
  public BufferedImage getSprite() {
    BufferedImage img = SpriteCache.getInstance().getImage(
        getAnimationSprites()[currentAnimationIndex], getDirection());
    if (getAnimationSprites().length - 1 <= currentAnimationIndex++) {
      currentAnimationIndex = 0;
    }
    return img;
  }

  /**
   * @return get the string keys of an animation as array.
   */
  protected abstract String[] getAnimationSprites();

  @Override
  public boolean isHuman() {
    return false;
  }

}
