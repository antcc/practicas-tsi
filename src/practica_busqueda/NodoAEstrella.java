package practica_busqueda;

import core.game.StateObservation;
import tools.Vector2d;
import tools.pathfinder.Node;

import java.util.ArrayList;

public class NodoAEstrella extends Node{
  public NodoAEstrella parent;
  StateObservation stateObs;

  public NodoAEstrella(StateObservation so){
    super(so.getAvatarPosition());
    stateObs = so;
  }


  @Override
  public int compareTo(Node n) {
    if(this.estimatedCost + this.totalCost < n.estimatedCost + n.totalCost)
      return -1;
    if(this.estimatedCost + this.totalCost > n.estimatedCost + n.totalCost)
      return 1;
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    return this.stateObs.equals(((NodoAEstrella)o).stateObs);
  }
}
