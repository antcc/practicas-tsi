package practica_busqueda;

/**
 * Enum that indicates what objectives to look for.
 */
public enum Objective {
  GEMS, // Look for any reachable gems
  EXIT, // Look for the level's exit
  ROCKS // Look for positions below rocks (in order to move them)
}
