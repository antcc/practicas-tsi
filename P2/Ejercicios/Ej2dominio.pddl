(define (domain ej2-domain)

  (:requirements :strips :equality :typing)

  (:types
    character obj - locatable
    player npc - character
    zone orient - object
  )

  (:predicates
    (player-looking ?o - orient);
    (connected ?z1 ?z2 - zone ?o - orient)
    (at ?l - locatable ?z - zone)
    (has-obj ?c - character)
    (hand ?p - player ?o - obj)
  )

  (:functions
    (distance ?z1 ?z2 - zone)
    (total-cost)
  )

  (:action turn-left
	  :parameters (?o - orient)
	  :precondition (player-looking ?o)
	  :effect (and
      (when (player-looking n) (and (player-looking w)))
      (when (player-looking s) (and (player-looking e)))
      (when (player-looking e) (and (player-looking n)))
      (when (player-looking w) (and (player-looking s)))
      (not (player-looking ?o))
    )
  )

  (:action turn-right
    :parameters (?o - orient)
    :precondition (player-looking ?o)
    :effect (and
      (when (player-looking n) (and (player-looking e)))
      (when (player-looking s) (and (player-looking w)))
      (when (player-looking e) (and (player-looking s)))
      (when (player-looking w) (and (player-looking n)))
      (not (player-looking ?o))
    )
  )

  (:action move
    :parameters (?p - player ?z1 ?z2 - zone ?o - orient)
    :precondition (and
      (at ?p ?z1)
      (player-looking ?o)
      (connected ?z1 ?z2 ?o)
    )
    :effect (and
      (not (at ?p ?z1))
      (at ?p ?z2)
      (increase (total-cost) (distance ?z1 ?z2))
    )
  )

  (:action give
    :parameters (?p - player ?n - npc ?o - obj ?z - zone)
    :precondition (and
      (hand ?p ?o)
      (at ?p ?z)
      (at ?n ?z))
    :effect (and
      (not (hand ?p ?o))
      (not (has-obj ?p))
      (has-obj ?n)
    )
  )

  (:action pick-up
    :parameters (?p - player ?o - obj ?z - zone)
    :precondition (and
      (at ?p ?z)
      (at ?o ?z)
      (not (has-obj ?p))
    )
    :effect (and
      (has-obj ?p)
      (hand ?p ?o)
      (not (at ?o ?z))
    )
  )

  (:action drop
    :parameters (?p - player ?o - obj ?z - zone)
    :precondition (and
      (at ?p ?z)
      (hand ?p ?o)
    )
    :effect (and
      (not (has-obj ?p))
      (not (hand ?p ?o))
      (at ?o ?z)
    )
  )

)
