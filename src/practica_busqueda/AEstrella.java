package practica_busqueda;

// Basic A* agent
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;
import tools.pathfinder.Node;
import tools.pathfinder.PathFinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

class AEstrella {
  private static PathFinder pf; // A pathfinder (for the heuristic)
  private boolean findExit; // If the current goal is to find the exit or the gems
  private ArrayList<practica_busqueda.Observation> goals; // The current list of goals
  private static final  ArrayList<Types.ACTIONS>
    lista_acciones = new ArrayList<>(Arrays.asList(
      Types.ACTIONS.ACTION_UP,
      Types.ACTIONS.ACTION_DOWN,
      Types.ACTIONS.ACTION_RIGHT,
      Types.ACTIONS.ACTION_LEFT));

  AEstrella(StateObservation so){
    ArrayList<Integer> tiposObs = new ArrayList<>();
    tiposObs.add(0);  // <- Muros
    tiposObs.add(7);  // <- Piedras

    // Init pathfinder
    pf = new PathFinder(tiposObs);
    pf.run(so);
    findExit = false; // Initially we look for gems
    goals    = new ArrayList<>();
  }

  /**
   * Updates list of goals
   * @param stateObs
   * MUST be called at the beginning of getPath.
   */
  private void updateGoals(StateObservation stateObs){
    // Get list of goals as core.game.Observations
    ArrayList<Observation> goalsCore;
    if(findExit){
      goalsCore = new ArrayList<>();
      goalsCore.add(stateObs.getPortalsPositions(stateObs.getAvatarPosition())[0].get(0));
    } else{
      goalsCore = stateObs.getResourcesPositions(stateObs.getAvatarPosition())[0];
    }

    // Update them as goals with game coordinates
    goals.clear();
    for(Observation goalCore : goalsCore)
      goals.add(new practica_busqueda.Observation(goalCore,stateObs.getBlockSize()));
  }


  /**
   * Activates looking-for-exit mode
   */
  void lookForExit(){
    findExit = true;
  }

  /**
   * Checks if a position is safe
   * @param position The position to check
   * @param stateObs The current state observation
   * @return Whether `position` is safe
   */
  boolean isSafe(Vector2d position, StateObservation stateObs){
    int x = (int) position.x;
    int y = (int) position.y;

    for (core.game.Observation obs : stateObs.getObservationGrid()[x][y])
      if(obs.itype == 7 || obs.itype == 0 || obs.itype == 10 || obs.itype == 11)
        return false;
    return true;
  }

  private Vector2d getNewPosition(Vector2d curPosition, Types.ACTIONS action){
    Vector2d newPosition = curPosition;
    switch (action){
      case ACTION_UP:
        newPosition = new Vector2d(curPosition.x, curPosition.y - 1);
        break;
      case ACTION_RIGHT:
        newPosition = new Vector2d(curPosition.x+1, curPosition.y);
        break;
      case ACTION_DOWN:
        newPosition = new Vector2d(curPosition.x, curPosition.y + 1);
        break;
      case ACTION_LEFT:
        newPosition = new Vector2d(curPosition.x-1, curPosition.y);
        break;
    }

    return newPosition;
  }

  /**
   * Get (reachable) neighbors from a node
   * @param node Node to build the neighbor list from
   * @return An ArrayList of reachable neighbors
   */
  ArrayList<NodoAEstrella> getNeighbours(NodoAEstrella node) {
    ArrayList<NodoAEstrella> neighbours = new ArrayList<>();
    StateObservation curState = node.stateObs;

    for (Types.ACTIONS action : lista_acciones){
      Vector2d newPosition = getNewPosition(node.position,action);
      // FIXME Hay coordenadas de pixel que deberían ser de juego
      int x = (int) newPosition.x/curState.getBlockSize();
      int y = (int) newPosition.y/curState.getBlockSize();
      System.out.println(newPosition);
      final ArrayList<Observation> observations = curState.getObservationGrid()[x][y];
      for(Observation obs : observations)
        if(obs.itype == 0 || obs.itype == 7)
          continue;

      StateObservation newState = curState.copy();
      newState.advance(action);
      if(newState.isAvatarAlive()){
        neighbours.add(new NodoAEstrella(newState));
      }
    }
    return neighbours;
  }


  /* Funciones para A */

  /**
   * Checks if goal has been reached
   * @param position The current position
   * @return Whether the current position is the position of a goal
   */
  private boolean reachedGoal(Vector2d position){
    for (practica_busqueda.Observation goal: goals)
      if(goal.getX() == position.x && goal.getY() == position.y)
        return true;
    return false;
  }

  /**
   * Heuristic function
   * @param curNode The current node
   * @return An estimation of the cost to reach the current goal
   */
  private double heuristicEstimatedCost(NodoAEstrella curNode){
    double cost = Double.POSITIVE_INFINITY;

    for(practica_busqueda.Observation goal : goals){
      ArrayList<Node> path = pf.getPath(curNode.position, new Vector2d(goal.getX(), goal.getY()));

      double pathCost;
      if(path == null) { // Pathfinder no encuentra camino
        double xDiff = Math.abs(curNode.position.x - goal.getX());
        double yDiff = Math.abs(curNode.position.y - goal.getY());
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
   * FIXME En búsqueda en estados es mejor devolver la lista de acciones directamente.
   * @param node The goal node
   * @return A list of nodes that gets you to the goal node
   */
  private ArrayList<NodoAEstrella> calculatePath(NodoAEstrella node) {
    ArrayList<NodoAEstrella> path = new ArrayList<>();
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
   * @return The list of nodes that gets you to the end (or null if there is no path)
   */
  ArrayList<NodoAEstrella> getPath(StateObservation stateObs, Vector2d startPos){
    updateGoals(stateObs); // IMPORTANTE (!)
    NodoAEstrella node = null;
    PriorityQueue<NodoAEstrella> openList = new PriorityQueue<>();
    PriorityQueue<NodoAEstrella> closedList = new PriorityQueue<>();

    NodoAEstrella start = new NodoAEstrella(stateObs);
    start.totalCost = 0.0f;
    start.estimatedCost = heuristicEstimatedCost(start);

    openList.add(start);

    while(openList.size() != 0)
    {
      node = openList.poll();
      closedList.add(node);

      if(reachedGoal(node.position))
        return calculatePath(node);

      ArrayList<NodoAEstrella> neighbours = getNeighbours(node);

      for (NodoAEstrella neighbour : neighbours) {
        double curDistance = neighbour.totalCost;

        if (!openList.contains(neighbour) && !closedList.contains(neighbour)) {
          neighbour.totalCost = curDistance + node.totalCost;
          neighbour.estimatedCost = heuristicEstimatedCost(neighbour);
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

    assert node != null;
    if(!reachedGoal(node.position))
      return null;

    return calculatePath(node);

  }

}
