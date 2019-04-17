package practica_busqueda;

// Basic A* agent
import core.game.Observation;
import core.game.StateObservation;
import tools.Vector2d;
import tools.pathfinder.Node;
import tools.pathfinder.PathFinder;

import java.util.ArrayList;
import java.util.PriorityQueue;

class AEstrella {
  private static PathFinder pf; // A pathfinder (for the heuristic)
  private Objective curObjective; // The current objective
  private ArrayList<Vector2d> goals; // The current list of goals


  AEstrella(StateObservation so){
    ArrayList<Integer> tiposObs = new ArrayList<>();
    tiposObs.add(0);  // <- Muros
    tiposObs.add(7);  // <- Piedras

    // Init pathfinder
    pf = new PathFinder(tiposObs);
    pf.run(so);
    curObjective = Objective.GEMS;
    goals = new ArrayList<>();
  }


  /**
   * Updates list of goals
   * @param stateObs
   * MUST be called at the beginning of getPath.
   */
  private void updateGoals(StateObservation stateObs){
    goals.clear();
    if(curObjective == Objective.ROCKS){
      ArrayList<Observation> rockPositions = stateObs.getMovablePositions()[0];

      for (Observation rockCore : rockPositions) {
        Vector2d pos = new Vector2d(
             (int) (rockCore.position.x / stateObs.getBlockSize()),
          (int) (rockCore.position.y / stateObs.getBlockSize()) + 1);// debajo de la roca

        if(isSafe(pos, stateObs))
          goals.add(pos);
      }
    }
    else{
      // Get list of goals as core.game.Observations
      ArrayList<Observation> goalsCore;

      if(curObjective == Objective.EXIT){
        goalsCore = new ArrayList<>();
        goalsCore.add(stateObs.getPortalsPositions(stateObs.getAvatarPosition())[0].get(0));
      } else{ // GEMS
        goalsCore = stateObs.getResourcesPositions(stateObs.getAvatarPosition())[0];
      }

      // Update them as goals with game coordinates
      for(Observation goalCore : goalsCore) {
        practica_busqueda.Observation goal = new practica_busqueda.Observation(goalCore, stateObs.getBlockSize());
        goals.add(new Vector2d(goal.getX(), goal.getY()));
      }
    }
  }


  /**
   * Checks if a position is safe
   * @param position The position to check (in world coordinates)
   * @param stateObs The current state observation
   * @return Whether `position` is safe
   */
  boolean isSafe(Vector2d position, StateObservation stateObs){
    int x = (int) position.x;
    int y = (int) position.y;

    for (core.game.Observation obs : stateObs.getObservationGrid()[x][y])
      if(obs.itype == 7 || obs.itype == 0)
        return false;

    // Walls
    /*for (core.game.Observation obs : stateObs.getImmovablePositions()[0]) {
      practica_busqueda.Observation newObs = new practica_busqueda.Observation(obs, stateObs.getBlockSize());
      if (newObs.getX() == x && newObs.getY() == y)
        return false;
    }

    // Rocks
    for (core.game.Observation obs : stateObs.getMovablePositions()[0]) {
      practica_busqueda.Observation newObs = new practica_busqueda.Observation(obs, stateObs.getBlockSize());
      System.out.println("[isSafe] Roca en : " + newObs.getX() + "," + newObs.getY());
      if (newObs.getX() == x && newObs.getY() == y)
        return false;
    }*/

    return true;
  }

  /**
   * Get (reachable) neighbors from a node
   * @param node Node to build the neighbor list from
   * @param stateObs The current state of the game
   * @return An ArrayList of reachable neighbors
   */
  ArrayList<Node> getNeighbours(Node node, StateObservation stateObs) {
    ArrayList<Node> neighbours = new ArrayList<>();
    int x = (int) node.position.x;
    int y = (int) node.position.y;

    //up, down, left, right
    int[] x_arrNeig = new int[]{0,    0,    -1,    1};
    int[] y_arrNeig = new int[]{-1,   1,     0,    0};

    for(int i = 0; i < x_arrNeig.length; ++i) {
      Vector2d neighbourPos = new Vector2d(x + x_arrNeig[i], y + y_arrNeig[i]);
      if (isSafe(neighbourPos, stateObs))
        neighbours.add(new Node(neighbourPos));
    }
    return neighbours;
  }


  /* Funciones para A */

  /**
   * Heuristic function
   * @param curNode The current node
   * @return An estimation of the cost to reach the current goal
   */
  private double heuristicEstimatedCost(Node curNode){
    double cost = Double.POSITIVE_INFINITY;

    for(Vector2d goal : goals){
      ArrayList<Node> path = pf.getPath(curNode.position, goal);

      double pathCost;
      if(path == null) { // Pathfinder no encuentra camino
        double xDiff = Math.abs(curNode.position.x - goal.x);
        double yDiff = Math.abs(curNode.position.y - goal.y);
        pathCost = xDiff + yDiff; // FIXME: Sería mejor guiarse sólo por las alcanzables inicialmente si hay alguna
      } else {
        pathCost = path.size(); // FIXME: Ajustar por elementos peligrosos
      }
      cost = Math.min(cost, pathCost);
    }

    return cost;
  }

  /**
   * Construct path from final node
   * @param node The goal node
   * @return A list of nodes that gets you to the goal node
   */
  private ArrayList<Node> calculatePath(Node node) {
    ArrayList<Node> path = new ArrayList<>();
    while(node != null) {
      if(node.parent != null){ //to avoid adding the start node.
        node.setMoveDir(node.parent);
        path.add(0,node);
      }
      node = node.parent;
    }
    return path;
  }

  /**
   * getPath towards the goal using A* algorithm
   * @param startPos The starting position
   * @param stateObs The current state of the game
   * @param objective The current objective
   * @return The list of nodes that gets you to the end (or null if there is no path)
   */
  ArrayList<Node> getPath(StateObservation stateObs, Vector2d startPos, Objective objective){
    curObjective = objective;

    System.out.println("[AEstrella.getPath] Objetivo: " +  curObjective);
    updateGoals(stateObs); // IMPORTANTE (!)

    if(goals.isEmpty()){
      System.err.println("No hay metas para " + curObjective);
      return null;
    }

    Node node = null;
    PriorityQueue<Node> openList = new PriorityQueue<>();
    PriorityQueue<Node> closedList = new PriorityQueue<>();

    Node start = new Node(startPos);
    start.totalCost = 0.0f;
    start.estimatedCost = heuristicEstimatedCost(start);

    openList.add(start);

    while(openList.size() != 0){
      node = openList.poll();
      closedList.add(node);

      if(goals.contains(node.position))
        return calculatePath(node);

      ArrayList<Node> neighbours = getNeighbours(node, stateObs);

      for (Node neighbour : neighbours) {
        double curDistance = neighbour.totalCost;

        if (!openList.contains(neighbour) && !closedList.contains(neighbour)){
          neighbour.totalCost = curDistance + node.totalCost;
          neighbour.estimatedCost = heuristicEstimatedCost(neighbour);
          neighbour.parent = node;

          openList.add(neighbour);

        } else if (curDistance + node.totalCost < neighbour.totalCost){
          neighbour.totalCost = curDistance + node.totalCost;
          neighbour.parent = node;

          openList.remove(neighbour);
          closedList.remove(neighbour);
          openList.add(neighbour);
        }
      }

    }

    assert node != null;
    if(!goals.contains(node.position)){
      System.err.println("[getPath] No hay camino hacia " + curObjective);
      return null;
    }

    return calculatePath(node);

  }

}
