package de.jungblut.agents;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import de.jungblut.gameplay.Environment;
import de.jungblut.gameplay.Environment.BlockState;
import de.jungblut.gameplay.PlanningEngine;
import de.jungblut.graph.DenseGraph;
import de.jungblut.graph.Graph;
import de.jungblut.graph.model.VertexImpl;
import de.jungblut.graph.search.AStar;
import de.jungblut.graph.search.DistanceMeasurer;
import de.jungblut.graph.search.WeightedEdgeContainer;

/**
 * Simple follower agent that plans a shortest path to the human player and
 * executes it without recalculating it all the time. Thus following the walked
 * path of the human player - a stalker ghost ;)
 * 
 * @author thomas.jungblut
 * 
 */
public class FollowerGhost extends EnvironmentAgent {

  private static final Object TAKEN = new Object();

  private boolean stalker = false;

  private BufferedImage[] sprites = new BufferedImage[1];
  private PlanningEngine<Point> plan = new PlanningEngine<>();
  private DenseGraph<Object> graph;

  public FollowerGhost(Environment env) {
    super(env);

    try {
      sprites[0] = ImageIO.read(new File("sprites/ghost_1.gif"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    graph = new DenseGraph<>(getEnvironment().getHeight(), getEnvironment()
        .getWidth());

    // add the vertices from the environment
    for (int h = 0; h < getEnvironment().getHeight(); h++) {
      for (int w = 0; w < getEnvironment().getWidth(); w++) {
        BlockState state = getEnvironment().getState(h, w);
        if (state != BlockState.WALL) {
          // adjacency should be resolved by the internal graph
          graph.addVertex(new VertexImpl<>(new Point(h, w), TAKEN));
        }
      }
    }
  }

  @Override
  public void move() {
    Agent humanPlayer = getEnvironment().getHumanPlayer();
    // if we are no stalker, we always compute the shortest path thus catching
    // the human faster
    if (!stalker) {
      plan.clear();
    }
    // in case we are, we are just following the way the human took
    if (plan.isEmpty()) {
      computePath(humanPlayer);
    }

    // now run along the path
    Point nextAction = plan.nextAction();
    Point currentPoint = new Point(x, y);
    if (nextAction != null && !nextAction.equals(currentPoint)) {
      this.direction = getEnvironment().getDirection(x, y, nextAction.x,
          nextAction.y);
      plan.planDistinct(new Point(humanPlayer.getXPosition(), humanPlayer
          .getYPosition()));
    }

    super.move();
  }

  /**
   * Do some A* with my graph lib and the manhattan distance heuristic.
   */
  private void computePath(Agent humanPlayer) {
    Point dest = new Point(humanPlayer.getXPosition(),
        humanPlayer.getYPosition());
    Point current = new Point(x, y);

    AStar<Point, Object> search = new AStar<>();
    WeightedEdgeContainer<Point> res = search.startAStarSearch(graph, current,
        dest, new DistanceMeasurer<Point, Object, Integer>() {
          // simple manhattan distance.
          // TODO this heuristic needs a measure of walls between pacman and the
          // enemy as this is frequently causing ties in the heuristic making
          // the ghost look stupid switching between two tiles.
          @Override
          public double measureDistance(Graph<Point, Object, Integer> g,
              Point start, Point goal) {
            double sum = 0d;
            sum += Math.abs(start.x - goal.x);
            sum += Math.abs(start.y - goal.y);
            return sum;
          }
        });
    List<Point> path = res.reconstructPath(dest);
    if (!path.isEmpty()) {
      Collections.reverse(path);
      // remove current position
      if (path.get(0).equals(current)) {
        path.remove(0);
      }
      // add the destination vertex as well
      path.add(dest);
      for (Point p : path) {
        plan.plan(p);
      }
    }
  }

  @Override
  public BufferedImage[] getAnimationSprites() {
    return sprites;
  }

}
