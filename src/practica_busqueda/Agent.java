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
  private PlayerObservation ultimaPos;

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
    ultimaPos = getPlayer(so);
  }

  /*  public boolean shouldEscape(StateObservation stateObs){
      System.out.println("Peligro");
      Node next = path.get(0);
      StateObservation newStateObs = stateObs.copy();
      newStateObs.advance(getAction(ultimaPos,next));
      return !newStateObs.isAvatarAlive();
  }

  public Types.ACTIONS escape(StateObservation stateObs){

    for (Node vecino : pf.getNeighbours(ultimaPos)){
      StateObservation newStateObs = stateObs.copy();
      Types.ACTIONS action = getAction(ultimaPos, vecino);
      newStateObs.advance(action);
      if(newStateObs.isAvatarAlive())
        return action;
    }
    return Types.ACTIONS.ACTION_NIL;
  }*/

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
  private Types.ACTIONS getAction(Node from, Node to){
    if(to.position.x != from.position.x) {
      if (to.position.x > from.position.x) {
        return Types.ACTIONS.ACTION_RIGHT;
      } else {
        return Types.ACTIONS.ACTION_LEFT;
      }
    }
    else {
      if(to.position.y > from.position.y) {
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
      path = pf.getPath(new Vector2d(avatar.getX(), avatar.getY()),
        new Vector2d(exit.getX(), exit.getY()));
    }

    // Look for another gem
    else {
      // Select nearest gem
      ArrayList<core.game.Observation>[] gemList
        = stateObs.getResourcesPositions(stateObs.getAvatarPosition());
      Observation gem = new Observation(gemList[0].get(0), stateObs.getBlockSize());

      // Calculate shortest path to nearest exit
      path = pf.getPath(new Vector2d(avatar.getX(), avatar.getY()),
        new Vector2d(gem.getX(), gem.getY()));
    }
  }

  // Basic A* agent act method
  @Override
  public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    // Set default action
    Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;

    // Get current position and clear path if needed
    PlayerObservation avatar = getPlayer(stateObs);
    if (((avatar.getX() != ultimaPos.getX()) || (avatar.getY() != ultimaPos.getY()))
      && !path.isEmpty()) {
      path.remove(0);
    }
    ultimaPos = avatar;

    // Update path
    if (path.isEmpty()) {
      calculateNewPath(stateObs);
    }

    // Calculate next action
    if (path != null) {
      Node siguientePos = path.get(0);
      action = getAction(new Node(new Vector2d(avatar.getX(), avatar.getY())), siguientePos);
    }

    return action;
  }
}
