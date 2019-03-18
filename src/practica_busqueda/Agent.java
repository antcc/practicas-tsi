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
 * @author Antonio Coín
 */
public class Agent extends BaseAgent {
    
    // Agent mode: 1 = random, 2 = basic A*
    protected static int mode = 2;
    
    // Random agent
    private Random randomGenerator;
    
    // Basic A* agent
    private PathFinder pf;
    private ArrayList<Node> path = new ArrayList<>();
    private PlayerObservation ultimaPos;
    
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        super(so, elapsedTimer);
        
        // Random agent
        if (mode == 1) {
            randomGenerator = new Random();
        }
        
        // Basic A* agent
        else if (mode == 2) {
            // Set obstacle types
            ArrayList<Integer> tiposObs = new ArrayList();
            tiposObs.add(0);  // <- Muros
            tiposObs.add(7);  // <- Piedras
            
            // Init pathfinder
            pf = new PathFinder(tiposObs);
            pf.run(so);
            
            // Get last known position
            ultimaPos = getPlayer(so);   
        }
    }
    
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        switch (mode) {
            case 1:  // Random agent
                return actRandom(stateObs, elapsedTimer);
            case 2:  // Basic A* agent
                return actBasicAStar(stateObs, elapsedTimer);
            default:
                return Types.ACTIONS.ACTION_NIL;
        }
    }
    
    /**
     * *********************************************
     * Test act methods
     * *********************************************
     */
    
    // Random agent act method
private Types.ACTIONS actRandom(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        //Get the available actions in this game.
        ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();

        //Determine an index randomly and get the action to return.
        int index = randomGenerator.nextInt(actions.size());
        Types.ACTIONS action = actions.get(index);

        //Return the action.
        return action;
    }
    
    // Basic A* agent act method
    private Types.ACTIONS actBasicAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // Set default action
        Types.ACTIONS action = Types.ACTIONS.ACTION_NIL;
        
        // Get current position and clear path if needed
        PlayerObservation avatar = getPlayer(stateObs);
        if (((avatar.getX() != ultimaPos.getX()) || (avatar.getY() != ultimaPos.getY()))
                && !path.isEmpty()) {
            path.remove(0);
        }
        
        // Get current gem count
        int gems = getNumGems(stateObs);
        
        // Update path
        if (path.isEmpty()) {
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
        
        // Calculate next action
        if (path != null) {
            Node siguientePos = path.get(0);
            
            if(siguientePos.position.x != avatar.getX()) {
                if (siguientePos.position.x > avatar.getX()) {
                    action = Types.ACTIONS.ACTION_RIGHT;
                }
                
                else {
                    action = Types.ACTIONS.ACTION_LEFT;
                }
            }
            
            else {
                if(siguientePos.position.y > avatar.getY()) {
                    action = Types.ACTIONS.ACTION_DOWN;
                }
                
                else {
                    action = Types.ACTIONS.ACTION_UP;
                }
            }
            
            ultimaPos = avatar;
        }
        
        return action;
    }
}
