package practica_busqueda;

// General java imports
import java.util.ArrayList;
import java.util.Random;

// General game imports
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

// Basic A* agent
import tools.pathfinder.Node;
import tools.pathfinder.PathFinder;

/**
 * Agent class
 * @author Antonio Coín y Pablo Baeyens
 */
public class Agent extends BaseAgent {

  // Basic A* agent
  private PathFinder pf;
  private ArrayList<Node> path = new ArrayList<>();
  private Vector2d ultimaPos;
  private Random randomGenerator = new Random();

  public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
    super(so, elapsedTimer);

    // Basic A* agent
    // Set obstacle types
    ArrayList<Integer> tiposObs = new ArrayList<>();
    tiposObs.add(0);  // <- Muros
    tiposObs.add(7);  // <- Piedras

    // Init pathfinder
    pf = new PathFinder(tiposObs);
    pf.run(so);

    // Get last known position
    PlayerObservation player = getPlayer(so);
    ultimaPos = new Vector2d(player.getX(), player.getY());
  }

  /**
   * Checks if a position is safe
   * @param position The position to check
   * @param stateObs The current state observation
   * @return Whether it is safe
   */
  private boolean isSafe(Vector2d position, StateObservation stateObs){
    int x = (int) position.x;
    int y = (int) position.y;

    for (core.game.Observation obs : stateObs.getObservationGrid()[x][y])
      if(obs.itype == 7 || obs.itype == 10 || obs.itype == 11 || obs.itype == 0)
        return false;
    return true;
  }

  /**
   * Indica si el jugador está en una situación de peligro en el camino actual
   * @// FIXME: 19/03/19 Comprobar también monstruos cercanos?
   * @param stateObs
   * @return Si está en peligro
   */
  public boolean shouldEscape(StateObservation stateObs){
    Vector2d siguientePos = path.get(0).position;
    StateObservation newStateObs = stateObs.copy();
    newStateObs.advance(getAction(ultimaPos,siguientePos));
    return !newStateObs.isAvatarAlive();
  }

  /**
   * Escapa de una situación de peligro
   * @param stateObs El estado actual
   * @return La acción para evitar el peligro
   */
  public Types.ACTIONS escape(StateObservation stateObs){
    ArrayList<Vector2d> seguras = new ArrayList<>();
    for (Node vecino : pf.getNeighbours(new Node(ultimaPos)))
      if(isSafe(vecino.position, stateObs))
        seguras.add(vecino.position);

    if(seguras.isEmpty()){
      System.out.println("No se encontró ninguna ruta de escape desde " + ultimaPos);
      return Types.ACTIONS.ACTION_NIL;
    }

    int p = randomGenerator.nextInt(seguras.size());
    Types.ACTIONS action = getAction(ultimaPos, seguras.get(p));
    path = new ArrayList<>();
    return action;

  }

  /**
   * *********************************************
   * Test act methods
   * *********************************************
   */

  /**
   * Get action to get from a node to a different node
   * @param from the origin node
   * @param to the destination node
   * @return the action to get from `from.position` to `to.position`.
   */
  private Types.ACTIONS getAction(Vector2d from, Vector2d to){
    if(to.x != from.x) {
      if (to.x > from.x) {
        return Types.ACTIONS.ACTION_RIGHT;
      } else {
        return Types.ACTIONS.ACTION_LEFT;
      }
    }
    else {
      if(to.y > from.y) {
        return Types.ACTIONS.ACTION_DOWN;
      } else {
        return Types.ACTIONS.ACTION_UP;
      }
    }
  }

  /**
   * Calculate a new path
   * @param stateObs The current state
   */
  private void calculateNewPath(StateObservation stateObs){
    PlayerObservation avatar = getPlayer(stateObs);
    // Get current gem count
    int gems = getNumGems(stateObs);
    // Look for the exit (all gems collected)
    if (gems == NUM_GEMS_FOR_EXIT) {
      // Select nearest exit
      ArrayList<core.game.Observation>[] exitList
        = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
      Observation exit = new Observation(exitList[0].get(0), stateObs.getBlockSize());

      // Calculate shortest path to nearest exit
      path = pf.getPath(ultimaPos, new Vector2d(exit.getX(), exit.getY()));
    }

    // Look for another gem
    else {
      // Select nearest gem
      ArrayList<core.game.Observation>[] gemList
        = stateObs.getResourcesPositions(stateObs.getAvatarPosition());
      Observation gem = new Observation(gemList[0].get(0), stateObs.getBlockSize());

      // Calculate shortest path to nearest exit
      path = pf.getPath(ultimaPos, new Vector2d(gem.getX(), gem.getY()));
    }
  }

  // Basic A* agent act method
  @Override
  public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    // Set default action
    Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;

    // Get current position and clear path if needed
    PlayerObservation avatar = getPlayer(stateObs);
    if (((avatar.getX() != ultimaPos.x) || (avatar.getY() != ultimaPos.y))
      && path != null && !path.isEmpty()) {
      path.remove(0);
    }

    ultimaPos = new Vector2d(avatar.getX(), avatar.getY());

    // Update path
    if (path == null || path.isEmpty()) {
      calculateNewPath(stateObs);
    }


    // Calculate next action
    try {
      Vector2d siguientePos = path.get(0).position;

      if(shouldEscape(stateObs) || !isSafe(siguientePos, stateObs)) {
        action = escape(stateObs);
        System.out.println("Escapando..." + action);
      } else{
        action = getAction(ultimaPos, siguientePos);
      }

    } catch(IndexOutOfBoundsException|NullPointerException e) {
      System.err.println("El path está vacío: " + e);
      action = escape(stateObs);
    }

    return action;
  }
}
