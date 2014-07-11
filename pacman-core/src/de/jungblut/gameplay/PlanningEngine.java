package de.jungblut.gameplay;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Planning engine that can be used to plan and retrieve actions.
 * 
 * @author thomas.jungblut
 * 
 * @param <T> action type.
 */
public final class PlanningEngine<T> {

  private final Deque<T> plan = new LinkedList<>();

  /**
   * @return retrieves & removes the next plan, can be null if empty.
   */
  public T nextAction() {
    return plan.poll();
  }

  /**
   * Plans the next action.
   */
  public void plan(T action) {
    plan.add(action);
  }

  /**
   * plans the next action, checks if the last action added is equal. If so it
   * will not add it, else it will add it at the end of the plan.
   */
  public void planDistinct(T action) {
    if (plan.isEmpty() || !plan.getLast().equals(action)) {
      plan.add(action);
    }
  }

  /**
   * Throws the plan into the trash bin.
   */
  public void clear() {
    plan.clear();
  }

  public int size() {
    return plan.size();
  }

  public boolean isEmpty() {
    return size() == 0;
  }

}
