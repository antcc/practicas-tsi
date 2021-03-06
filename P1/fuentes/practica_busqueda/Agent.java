package practica_busqueda;

import core.game.Observation;
import core.game.StateObservation;
import java.util.ArrayList;
import java.util.Random;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import tools.pathfinder.Node;

/**
 * Agent class
 *
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

  /*
   *********************************************
   Reactive behavior
   *********************************************
  */

  /**
   * Checks if a position is safe (for escaping)
   *
   * @param position The position to check (in world coordinates)
   * @param stateObs The current state observation
   * @return Whether `position` is safe
   */
  private boolean isSafeforEscape(Vector2d position, StateObservation stateObs) {
    int x = (int) position.x;
    int y = (int) position.y;

    // Walls
    for (Observation obs : stateObs.getImmovablePositions()[0]) {
      practica_busqueda.Observation newObs =
          new practica_busqueda.Observation(obs, stateObs.getBlockSize());
      if (newObs.getX() == x && newObs.getY() == y) return false;
    }

    // Rocks
    if (stateObs.getMovablePositions() != null) {
      for (Observation obs : stateObs.getMovablePositions()[0]) {
        practica_busqueda.Observation newObs =
            new practica_busqueda.Observation(obs, stateObs.getBlockSize());
        if (newObs.getX() == x && newObs.getY() == y) return false;
      }
    }

    return true;
  }

  /**
   * Get neighbors for escaping
   *
   * @param node Node to build the neighbor list from
   * @param stateObs The current state of the game
   * @return An ArrayList of reachable neighbors
   */
  private ArrayList<Node> getNeighboursForEscaping(Node node, StateObservation stateObs) {
    ArrayList<Node> neighbours = new ArrayList<>();
    int x = (int) node.position.x;
    int y = (int) node.position.y;

    // up, down, left, right
    int[] x_arrNeig = new int[] {0, 0, -1, 1};
    int[] y_arrNeig = new int[] {-1, 1, 0, 0};

    for (int i = 0; i < x_arrNeig.length; ++i) {
      Vector2d neighbourPos = new Vector2d(x + x_arrNeig[i], y + y_arrNeig[i]);
      if (isSafeforEscape(neighbourPos, stateObs)) neighbours.add(new Node(neighbourPos));
    }
    return neighbours;
  }

  /**
   * Indica si el jugador está en una situación de peligro en el camino actual
   *
   * @param stateObs The current state
   * @return Si está en peligro
   */
  private boolean shouldEscape(
      StateObservation stateObs, Types.ACTIONS action, int[] x_arrNeig, int[] y_arrNeig) {
    StateObservation newStateObs = stateObs.copy();
    newStateObs.advance(action);

    Vector2d position = newStateObs.getAvatarPosition();
    int x = (int) position.x / newStateObs.getBlockSize();
    int y = (int) position.y / newStateObs.getBlockSize();

    for (int i = 0; i < x_arrNeig.length; ++i) {
      int newX = x + x_arrNeig[i];
      int newY = y + y_arrNeig[i];

      if (newX >= 0
          && newX < newStateObs.getObservationGrid().length
          && newY >= 0
          && newY < newStateObs.getObservationGrid()[newX].length) {
        for (core.game.Observation obs : newStateObs.getObservationGrid()[newX][newY]) {
          if (obs.itype == 10 || obs.itype == 11) {
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
   *
   * @param stateObs El estado actual
   * @return La acción para evitar el peligro
   */
  public Types.ACTIONS escape(StateObservation stateObs) {
    ArrayList<Node> vecinos2 = getNeighboursForEscaping(new Node(ultimaPos), stateObs);

    ArrayList<Node> vecinos = new ArrayList<>();

    // self, up, down, left, right and diagonals
    int[] x_arrNeig = new int[] {0, 1, -1, 0, 0, 1, -1, 1, -1};
    int[] y_arrNeig = new int[] {0, 0, 0, 1, -1, 1, -1, -1, 1};

    for (int i = 0; i < 3; i++) {
      for (Node vecino : vecinos2)
        if (!shouldEscape(stateObs, getAction(ultimaPos, vecino.position), x_arrNeig, y_arrNeig))
          vecinos.add(vecino);

      if (vecinos.isEmpty()) { // Eliminar restricciones progresivamente
        if (i == 0) {
          x_arrNeig = new int[] {0, 1, -1, 0, 0};
          y_arrNeig = new int[] {0, 0, 0, 1, -1};
        }
        if (i == 1) {
          x_arrNeig = new int[] {0};
          y_arrNeig = new int[] {0};
        }
      } else {
        break;
      }
    }

    if (vecinos.isEmpty()) return Types.ACTIONS.ACTION_NIL;

    ArrayList<core.game.Observation>[] npcPositions = stateObs.getNPCPositions();

    // Sort safe neighbors by distance to all nearby monsters
    vecinos.sort(
        (n1, n2) -> {
          double dist1 = 0, dist2 = 0;
          if (npcPositions != null) {
            for (ArrayList<Observation> npcs : npcPositions) {
              for (Observation npc : npcs) {
                if (npc.position.dist(n1.position) < 6) // Umbral de distancia
                dist1 += -npc.position.dist(n1.position);
                if (npc.position.dist(n2.position) < 6) dist2 += -npc.position.dist(n2.position);
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

    Types.ACTIONS action = getAction(ultimaPos, vecinoElegido.position);
    if (path != null) path.clear();
    return action;
  }

  private Types.ACTIONS randomEscape(StateObservation stateObs) {
    ArrayList<Node> vecinos2 = getNeighboursForEscaping(new Node(ultimaPos), stateObs);

    ArrayList<Node> vecinos = new ArrayList<>();

    int[] x_arrNeig = new int[] {0};
    int[] y_arrNeig = new int[] {0};

    for (Node vecino : vecinos2)
      if (!shouldEscape(stateObs, getAction(ultimaPos, vecino.position), x_arrNeig, y_arrNeig))
        vecinos.add(vecino);

    if (vecinos.isEmpty()) return Types.ACTIONS.ACTION_NIL;

    int p = randomGenerator.nextInt(vecinos.size());
    Node vecinoElegido = vecinos.get(p);

    Types.ACTIONS action = getAction(ultimaPos, vecinoElegido.position);
    if (path != null) path.clear();
    return action;
  }

  /*
   *********************************************
   Main function
   *********************************************
  */

  /**
   * Get action to get from a node to a different node
   *
   * @param from the origin node
   * @param to the destination node
   * @return the action to get from `from.position` to `to.position`.
   */
  private Types.ACTIONS getAction(Vector2d from, Vector2d to) {
    if (to.x != from.x)
      if (to.x > from.x) return Types.ACTIONS.ACTION_RIGHT;
      else return Types.ACTIONS.ACTION_LEFT;
    else if (to.y > from.y) return Types.ACTIONS.ACTION_DOWN;
    else return Types.ACTIONS.ACTION_UP;
  }

  // Basic A* agent act method
  @Override
  public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    // Casillas a comprobar durante el escape
    int[] x_arrNeig = new int[] {0, 1, -1, 0, 0, 1, -1, 1, -1};
    int[] y_arrNeig = new int[] {0, 0, 0, 1, -1, 1, -1, -1, 1};

    // Obtén la posición actual
    PlayerObservation avatar = getPlayer(stateObs);
    Vector2d nuevaPos = new Vector2d(avatar.getX(), avatar.getY());

    if (ultimaPos.equals(nuevaPos)) {
      // Si no se ha movido incrementamos contador y comprobamos bucle
      isInLoop++;
      if (isInLoop > 5) {
        x_arrNeig = new int[] {0};
        y_arrNeig = new int[] {0};

        if (isInLoop > 10) return randomEscape(stateObs);
      }
    } else {
      // Si se ha movido reiniciamos el contador y quitamos una casilla del path.
      isInLoop = 0;
      if (path != null && !path.isEmpty()) path.remove(0);
    }

    ultimaPos = nuevaPos;

    // Actualiza path
    if (path == null) { // No hemos encontrado camino; probamos a mover una roca
      path = finder.getPath(stateObs, ultimaPos, Objective.ROCKS);
      waitForPath = 4; // Cuando se vacíe el path, esperamos 4 ticks a que la roca caiga
    } else if (path.isEmpty()) { // Hemos terminado de hacer el path actual
      if (waitForPath-- > 0) return escape(stateObs); // En espera
      else { // ve hacia objetivo
        Objective objective =
            getNumGems(stateObs) < NUM_GEMS_FOR_EXIT ? Objective.GEMS : Objective.EXIT;
        path = finder.getPath(stateObs, ultimaPos, objective);
      }
    }

    // Calcula siguiente acción

    Types.ACTIONS action; // Acción a realizar

    try {
      Vector2d siguientePos = path.get(0).position;

      if (!finder.isSafe(siguientePos, stateObs)
          || shouldEscape(stateObs, getAction(ultimaPos, siguientePos), x_arrNeig, y_arrNeig)) {
        action = escape(stateObs);
      } else {
        action = getAction(ultimaPos, siguientePos);
      }

    } catch (IndexOutOfBoundsException | NullPointerException e) {
      action = escape(stateObs);
    }

    return action;
  }
}
