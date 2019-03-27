package practica_busqueda;

// Basic A* agent
import core.game.StateObservation;
import tools.Vector2d;
import tools.pathfinder.Node;
import tools.pathfinder.PathFinder;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class AEstrella {
  private PathFinder pf;

  public AEstrella(){
    ArrayList<Integer> tiposObs = new ArrayList<>();
    tiposObs.add(0);  // <- Muros
    tiposObs.add(7);  // <- Piedras

    // Init pathfinder
    pf = new PathFinder(tiposObs);
  }

  public void run(StateObservation so){
    pf.run(so);
  }


  /**
   * Checks if a position is safe
   * @param position The position to check
   * @param stateObs The current state observation
   * @return Whether it is safe
   */
  public boolean isSafe(Vector2d position, StateObservation stateObs){
    int x = (int) position.x;
    int y = (int) position.y;

    for (core.game.Observation obs : stateObs.getObservationGrid()[x][y])
      if(obs.itype == 7 || obs.itype == 10 || obs.itype == 11 || obs.itype == 0)
        return false;
    return true;
  }

  public ArrayList<Node> getNeighbours(Node node, StateObservation stateObs) {
    ArrayList<Node> neighbours = new ArrayList<>();
    int x = (int) (node.position.x);
    int y = (int) (node.position.y);

    //up, down, left, right
    int[] x_arrNeig = new int[]{0,    0,    -1,    1};
    int[] y_arrNeig = new int[]{-1,   1,     0,    0};

    for(int i = 0; i < x_arrNeig.length; ++i)
      if(isSafe(new Vector2d(x+x_arrNeig[i], y+y_arrNeig[i]), stateObs))
        neighbours.add(new Node(new Vector2d(x+x_arrNeig[i], y+y_arrNeig[i])));
    return neighbours;
  }

  public ArrayList<Node> getPath(StateObservation stateObs, Vector2d start, Vector2d end){
    return pf.getPath(start, end);
  }

  /***********************
   * Funciones para A*   *
   ***********************/

  /**
   * Función heurística
   * @param curNode
   * @param goalNode
   * @return
   */
  private static double heuristicEstimatedCost(Node curNode, Node goalNode)
  {
    //4-way: using Manhattan
    double xDiff = Math.abs(curNode.position.x - goalNode.position.x);
    double yDiff = Math.abs(curNode.position.y - goalNode.position.y);
    return xDiff + yDiff;

    //This is Euclidean distance(sub-optimal here).
    //return curNode.position.dist(goalNode.position);
  }

  private ArrayList<Node> calculatePath(Node node)
  {
    ArrayList<Node> path = new ArrayList<Node>();
    while(node != null)
    {
      if(node.parent != null) //to avoid adding the start node.
      {
        node.setMoveDir(node.parent);
        path.add(0,node);
      }
      node = node.parent;
    }
    return path;
  }

  /**
   * findPath con A estrella
   * @param start
   * @param goal
   * @return
   */
  private ArrayList<Node> findPath(StateObservation stateObs, Node start, Node goal)
  {
    Node node = null;
    PriorityQueue<Node> openList = new PriorityQueue<>();
    PriorityQueue<Node> closedList = new PriorityQueue<>();

    start.totalCost = 0.0f;
    start.estimatedCost = heuristicEstimatedCost(start, goal);

    openList.add(start);

    while(openList.size() != 0)
    {
      node = openList.poll();
      closedList.add(node);

      if(node.position.equals(goal.position))
        return calculatePath(node);

      ArrayList<Node> neighbours = getNeighbours(node, stateObs);

      for (Node neighbour : neighbours) {
        double curDistance = neighbour.totalCost;

        if (!openList.contains(neighbour) && !closedList.contains(neighbour)) {
          neighbour.totalCost = curDistance + node.totalCost;
          neighbour.estimatedCost = heuristicEstimatedCost(neighbour, goal);
          neighbour.parent = node;

          openList.add(neighbour);

        } else if (curDistance + node.totalCost < neighbour.totalCost) {
          neighbour.totalCost = curDistance + node.totalCost;
          neighbour.parent = node;

          openList.remove(neighbour);

          closedList.remove(neighbour);

          openList.add(neighbour);
        }
      }

    }

    if(!node.position.equals(goal.position))
      return null;

    return calculatePath(node);

  }

}
