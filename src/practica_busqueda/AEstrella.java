package practica_busqueda;

// Basic A* agent
import core.game.StateObservation;
import tools.Vector2d;
import tools.pathfinder.Node;
import tools.pathfinder.PathFinder;

import java.util.ArrayList;

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


  public ArrayList<Node> getNeighbours(Node node, StateObservation stateObs) {
    ArrayList<Node> neighbours = new ArrayList<Node>();
    int x = (int) (node.position.x);
    int y = (int) (node.position.y);

    //up, down, left, right
    int[] x_arrNeig = new int[]{0,    0,    -1,    1};
    int[] y_arrNeig = new int[]{-1,   1,     0,    0};

    for(int i = 0; i < x_arrNeig.length; ++i)
      neighbours.add(new Node(new Vector2d(x+x_arrNeig[i], y+y_arrNeig[i])));
    return neighbours;
  }

  public ArrayList<Node> getPath(Vector2d start, Vector2d end){
    return pf.getPath(start, end);
  }

}
