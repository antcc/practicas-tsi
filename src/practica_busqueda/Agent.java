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

  // Current path
  private ArrayList<Node> path = new ArrayList<>();

  // Last position
  private Vector2d ultimaPos;

  // How many turns in the same position so far
  private int isInLoop;

  // How many turns to wait until creating path
  private int waitForPath;

  // RNG
  private Random randomGenerator = new Random();

  // FIXME Borrar DEBUGs
  private final static boolean DEBUG = false;


  public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
    super(so, elapsedTimer);

    // Init pathfinder
    finder = new AEstrella(so);

    // Get last known position
    PlayerObservation player = getPlayer(so);
    ultimaPos = new Vector2d(player.getX(), player.getY());
    waitForPath = 0;
    isInLoop = 0;
  }

  /**
   * Indica si el jugador está en una situación de peligro en el camino actual
   * FIXME: Comprobar también monstruos cercanos?
   * FIXME: ¿Por qué dice que ha muerto cuando no va en realidad a morir?
   * FIXME: Si se añade esta condición no consigue escapar. ¿Por qué?
   * @param stateObs The current state
   * @return Si está en peligro
   */
  private boolean shouldEscape(StateObservation stateObs, Types.ACTIONS action,
                               int[] x_arrNeig, int[] y_arrNeig){
    StateObservation newStateObs = stateObs.copy();
    newStateObs.advance(action);

    Vector2d position = newStateObs.getAvatarPosition();
    int x = (int) position.x / newStateObs.getBlockSize();
    int y = (int) position.y / newStateObs.getBlockSize();

    for(int i = 0; i < x_arrNeig.length; ++i) {
      int newX = x + x_arrNeig[i];
      int newY = y + y_arrNeig[i];

      if (newX >= 0 && newX < newStateObs.getObservationGrid().length
          && newY >= 0 && newY < newStateObs.getObservationGrid()[newX].length) {
        for (core.game.Observation obs : newStateObs.getObservationGrid()[newX][newY]) {
          if(obs.itype == 10 || obs.itype == 11) {
            if (x == newX && y == newY) // Monster in our next move
              return true;
          }
        }
      }
    }

    return !newStateObs.isAvatarAlive();
  }

  /**
   * Escapa de una situación de peligro
   * @param stateObs El estado actual
   * @return La acción para evitar el peligro
   */
  public Types.ACTIONS escape(StateObservation stateObs){
    ArrayList<Node> vecinos2 = finder.getNeighbours2(new Node(ultimaPos), stateObs);

    ArrayList<Node> vecinos = new ArrayList<>();

    // self, up, down, left, right
    int[] x_arrNeig = new int[]{0, 1, -1, 0, 0};
    int[] y_arrNeig = new int[]{0, 0, 0, 1, -1};

    for (int i = 0; i < 2; i++) {
      for(Node vecino : vecinos2){
        if(!shouldEscape(stateObs, getAction(ultimaPos, vecino.position), x_arrNeig, y_arrNeig)){
          if(DEBUG) System.out.println("[escape desde " + ultimaPos + "] " + vecino.position);
          vecinos.add(vecino);
        }
      }

      if(vecinos.isEmpty()){ // Intentar una segunda vuelta sin tantas restricciones
        x_arrNeig = new int[]{0};
        y_arrNeig = new int[]{0};
      }
      else {
        break;
      }
    }

    if (vecinos.isEmpty()) {
      if(DEBUG) System.out.println("No se encontró ninguna ruta de escape desde " + ultimaPos);
      return Types.ACTIONS.ACTION_NIL;
    }

    ArrayList<core.game.Observation>[] npcPositions = stateObs.getNPCPositions();

    // Sort safe neighbors by distance to all nearby monsters
    vecinos.sort((n1, n2) -> {
      double dist1 = 0, dist2 = 0;
      if(npcPositions != null) {
        for (ArrayList<Observation> npcs : npcPositions) {
          for (Observation npc : npcs) {
            if (npc.position.dist(n1.position) < 6) // Umbral de distancia
              dist1 += -npc.position.dist(n1.position);
            if (npc.position.dist(n2.position) < 6)
              dist2 += -npc.position.dist(n2.position);
          }
        }
      }
      double diff = dist1 - dist2;
      return (int) diff;
    });

    PlayerObservation avatar = getPlayer(stateObs);

    Node vecinoElegido = null;
    for (Node vecino : vecinos) {
      StateObservation newStateObs = stateObs.copy();
      newStateObs.advance(getAction(ultimaPos, vecino.position));
      PlayerObservation newAvatar = getPlayer(newStateObs);
      if (newAvatar.getOrientation() == avatar.getOrientation()) {
        vecinoElegido = vecino; // Solo puede haber uno que se mueva de casilla
        break;
      }
    }

    if (vecinoElegido == null) {
      // Elegir aleatoriamente entre los dos mejores vecinos
      int p = randomGenerator.nextInt(vecinos.size() > 2 ? 2 : vecinos.size());
      vecinoElegido = vecinos.get(p);
    }

    if(DEBUG) System.out.println("[vecinoElegido] "+ vecinoElegido.position);

    Types.ACTIONS action = getAction(ultimaPos, vecinoElegido.position);
    if(path != null)
      path.clear();
    return action;

  }

  public Types.ACTIONS randomEscape(StateObservation stateObs){
    ArrayList<Node> vecinos2 = finder.getNeighbours2(new Node(ultimaPos), stateObs);

    ArrayList<Node> vecinos = new ArrayList<>();

    // self, up, down, left, right
    int[] x_arrNeig = new int[]{0};
    int[] y_arrNeig = new int[]{0};

    for(Node vecino : vecinos2){
      if(!shouldEscape(stateObs, getAction(ultimaPos, vecino.position), x_arrNeig, y_arrNeig)){
        if(DEBUG) System.out.println("[escape desde " + ultimaPos + "] " + vecino.position);
        vecinos.add(vecino);
      }
    }

    if (vecinos.isEmpty()) {
      if(DEBUG) System.out.println("No se encontró ninguna ruta de escape desde " + ultimaPos);
      return Types.ACTIONS.ACTION_NIL;
    }

    int p = randomGenerator.nextInt(vecinos.size());
    Node vecinoElegido = vecinos.get(p);

    if(DEBUG) System.out.println("[vecinoElegido] "+ vecinoElegido.position);

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

  /**
   * Imprime dónde hay rocas
   * FIXME Borrar
   * @param stateObs Current state of the game
   */
  private void printRocas(StateObservation stateObs){
    System.out.print("Rocas según getMovablePositions: [");
    for(Observation obs : stateObs.getMovablePositions()[0]){
      Vector2d position =
        new Vector2d((int) obs.position.x / stateObs.getBlockSize(),(int)obs.position.y / stateObs.getBlockSize());
      System.out.print(position + ", ");
    }
    System.out.println("]");

    System.out.print("Rocas según getObservationGrid: [");
        for(int x = 0; x < stateObs.getObservationGrid().length; x++){
          for(int y = 0; y < stateObs.getObservationGrid()[x].length; y++){
            for(Observation obs : stateObs.getObservationGrid()[x][y]){
              if(obs.itype == 7){
                System.out.print(x + ":" + y + ", ");
              }
            }
          }
        }
    System.out.println("]");
  }

  // Basic A* agent act method
  @Override
  public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
    Types.ACTIONS action;

    // self, up, down, left, right
    int[] x_arrNeig = new int[]{0, 1, -1, 0, 0};
    int[] y_arrNeig = new int[]{0, 0, 0, 1, -1};

    // Get current position and clear path if needed
    PlayerObservation avatar = getPlayer(stateObs);
    if (((avatar.getX() != ultimaPos.x) || (avatar.getY() != ultimaPos.y))
      && path != null && !path.isEmpty()) {
      path.remove(0);
    }

    if (ultimaPos.equals(new Vector2d(avatar.getX(), avatar.getY()))) {
      isInLoop++;
      if(isInLoop > 5){
        if(DEBUG) System.out.println("En bucle durante " + isInLoop + " turnos.");
        x_arrNeig = new int[]{0};
        y_arrNeig = new int[]{0};

        if (isInLoop > 10)
          return randomEscape(stateObs);
      }
    }else {
      isInLoop = 0;
    }


    ultimaPos = new Vector2d(avatar.getX(), avatar.getY());
    //System.out.println("[act] Estoy en: " + ultimaPos);

    // Update path
    if(path == null){ // No hemos encontrado camino; probamos a mover una roca
      path = finder.getPath(stateObs,ultimaPos, Objective.ROCKS);
      waitForPath = 4; // Cuando se vacíe el path, esperamos 4 ticks a que la roca caiga
    }
    else if (path.isEmpty()) { // Hemos terminado de hacer el path actual
      if(waitForPath-- > 0) // Espera
        return escape(stateObs);
      else { // ve hacia objetivo
        Objective objective
         = getNumGems(stateObs) < NUM_GEMS_FOR_EXIT ? Objective.GEMS : Objective.EXIT;
        path = finder.getPath(stateObs, ultimaPos, objective);
      }
    }

    //printRocas(stateObs);

    // Calculate next action
    try {
      Vector2d siguientePos = path.get(0).position;

      if(!finder.isSafe(siguientePos, stateObs) || shouldEscape(stateObs, getAction(ultimaPos, siguientePos), x_arrNeig, y_arrNeig)) {
        action = escape(stateObs);
      } else{
        action = getAction(ultimaPos, siguientePos);
      }

    } catch(IndexOutOfBoundsException|NullPointerException e) {
      if(DEBUG) System.out.println("[act] Path vacío: " + e);
      action = escape(stateObs);
    }


    if(DEBUG) {
      try {
        Thread.sleep(100);
      } catch (Exception ignored) {}
    }

    if(DEBUG) System.out.println("[act] Realizada: " + action);
    return action;
  }
}
