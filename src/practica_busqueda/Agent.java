package practica_busqueda;

// General java imports
import java.util.ArrayList;
import java.util.Random;

// General game imports
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

// Basic A* agent
import tools.pathfinder.Node;

/**
 * Agent class
 * @author Antonio Coín y Pablo Baeyens
 */
public class Agent extends BaseAgent {

  // Basic A* agent
  private AEstrella finder;
  private ArrayList<Node> path = new ArrayList<>();
  private Vector2d ultimaPos;
  private Random randomGenerator = new Random();
  private int waitForPath;

  public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
    super(so, elapsedTimer);

    // Init pathfinder
    finder = new AEstrella(so);

    // Get last known position
    PlayerObservation player = getPlayer(so);
    ultimaPos = new Vector2d(player.getX(), player.getY());
    waitForPath = 0;
  }


  /**
   * Indica si el jugador está en una situación de peligro en el camino actual
   * FIXME: Comprobar también monstruos cercanos?
   * FIXME: ¿Por qué dice que ha muerto cuando no va en realidad a morir?
   * FIXME: Si se añade esta condición no consigue escapar. ¿Por qué?
   * @param stateObs The current state
   * @return Si está en peligro
   */
  public boolean shouldEscape(StateObservation stateObs, Types.ACTIONS action){
    StateObservation newStateObs = stateObs.copy();
    newStateObs.advance(action);

    Vector2d position = newStateObs.getAvatarPosition();
    int x = (int) position.x / newStateObs.getBlockSize();
    int y = (int) position.y / newStateObs.getBlockSize();

    for (core.game.Observation obs : newStateObs.getObservationGrid()[x][y])
      if(obs.itype == 10 || obs.itype == 11)
        return true;

    return !newStateObs.isAvatarAlive();
  }

  /**
   * Escapa de una situación de peligro
   * @param stateObs El estado actual
   * @return La acción para evitar el peligro
   */
  public Types.ACTIONS escape(StateObservation stateObs){
    ArrayList<Node> vecinos2 = finder.getNeighbours(new Node(ultimaPos), stateObs);

    ArrayList<Node> vecinos = new ArrayList<>();
    for(Node vecino : vecinos2){
      if(!shouldEscape(stateObs, getAction(ultimaPos, vecino.position))){
        vecinos.add(vecino);
      }
    }

    if(vecinos.isEmpty()){
      System.out.println("No se encontró ninguna ruta de escape desde " + ultimaPos);
      return Types.ACTIONS.ACTION_NIL;
    }

    int p = randomGenerator.nextInt(vecinos.size());
    Node vecinoElegido = vecinos.get(p);
    ArrayList<core.game.Observation>[] npcPositions = stateObs.getNPCPositions();

    if(npcPositions != null) {
      double maxDist = Double.NEGATIVE_INFINITY;
      for (Node vecino : vecinos) {
        double dist = 0;
        for (ArrayList<core.game.Observation> npcs : npcPositions)
          for (Observation npc : npcs)
            dist += npc.position.dist(vecino.position);
        if (maxDist < dist) {
          vecinoElegido = vecino;
          maxDist = dist;
        }
      }
    }


    Types.ACTIONS action = getAction(ultimaPos, vecinoElegido.position);
    if(path != null)
      path.clear();
    return action;

  }

  /*
    *********************************************
    Test act methods
    *********************************************
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

  // Basic A* agent act method
  @Override
  public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
    // Set default action
    Types.ACTIONS action;

    // Get current position and clear path if needed
    PlayerObservation avatar = getPlayer(stateObs);
    if (((avatar.getX() != ultimaPos.x) || (avatar.getY() != ultimaPos.y))
      && path != null && !path.isEmpty()) {
      path.remove(0);
    }

    if (ultimaPos.equals(new Vector2d(avatar.getX(), avatar.getY()))) {
      //System.out.println("[act] No se ha movido de " + ultimaPos);
    }

    ultimaPos = new Vector2d(avatar.getX(), avatar.getY());
    //System.out.println("[act] " + ultimaPos);


    // Update path
    if(path == null){ // No hemos encontrado camino; probamos a mover una roca
      finder.setObjective(AEstrella.Objective.ROCKS);
      path = finder.getPath(stateObs,ultimaPos);
      waitForPath = 4; // Cuando se vacíe el path, esperamos 4 ticks a que la roca caiga
    }
    else if (path.isEmpty()) { // Hemos terminado de hacer el path actual
      if (getNumGems(stateObs) < NUM_GEMS_FOR_EXIT)
        finder.setObjective(AEstrella.Objective.GEMS);
      else
        finder.setObjective(AEstrella.Objective.EXIT);

      if(waitForPath > 0) {
        waitForPath--;
        return escape(stateObs);
      } else
        path = finder.getPath(stateObs, ultimaPos);
    }


    // Calculate next action
    try {
      Vector2d siguientePos = path.get(0).position;

      if(!finder.isSafe(siguientePos, stateObs) || shouldEscape(stateObs,getAction(ultimaPos, siguientePos))) {
        action = escape(stateObs);
      } else{
        action = getAction(ultimaPos, siguientePos);
      }

    } catch(IndexOutOfBoundsException|NullPointerException e) {
      System.err.println("[Agent.act] Path vacío: " + e);
      action = escape(stateObs);

    }

    try {
      Thread.sleep(100);
    } catch(Exception e){

    }


    return action;
  }
}
