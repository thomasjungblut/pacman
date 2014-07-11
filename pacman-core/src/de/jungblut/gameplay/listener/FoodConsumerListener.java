package de.jungblut.gameplay.listener;

import de.jungblut.gameplay.Environment;

public interface FoodConsumerListener {

  public void consumedFood(Environment env, int x, int y, int foodRemaining);

}
