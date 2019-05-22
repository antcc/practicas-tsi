(define (domain ej4-domain)

  (:requirements :strips :equality :typing)

  (:types
    character obj - locatable
    normal-obj - obj
    player npc - character
    zone orient type - object
  )

  (:predicates
    (player-looking ?o - orient);
    (connected ?z1 ?z2 - zone ?o - orient)
    (at ?l - locatable ?z - zone)
    (has-obj ?c - character)
    (hand ?p - player ?o - obj)
    (zone-type ?z - zone ?t - type)
    (backpack-empty ?p - player)
    (backpack ?p - player ?o - obj)
    (is-bikini ?o - obj)
    (is-sneakers ?o - obj)
    (has-bikini ?p - player)
    (has-sneakers ?p - player)
  )

  (:functions
    (distance ?z1 ?z2 - zone)
    (total-cost)
    (points ?n - npc ?o - normal-obj)
    (total-points)
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
      (not (zone-type ?z2 precipicio))
      (not (and (zone-type ?z2 bosque) (not (has-sneakers ?p))))
      (not (and (zone-type ?z2 agua) (not (has-bikini ?p))))
    )
    :effect (and
      (not (at ?p ?z1))
      (at ?p ?z2)
      (increase (total-cost) (distance ?z1 ?z2))
    )
  )

  (:action give
    :parameters (?p - player ?n - npc ?z - zone ?o - normal-obj)
    :precondition (and
      (hand ?p ?o)
      (at ?p ?z)
      (at ?n ?z))
    :effect (and
      (not (hand ?p ?o))
      (not (has-obj ?p))
      (has-obj ?n)
      (increase (total-points) (points ?n ?o))
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
      (when (is-bikini ?o) (has-bikini ?p))
      (when (is-sneakers ?o) (has-sneakers ?p))
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
      (when (is-bikini ?o) (not (has-bikini ?p)))
      (when (is-sneakers ?o) (not (has-sneakers ?p)))
    )
  )

  (:action put-in-backpack
    :parameters(?p - player ?o - obj)
    :precondition(and
      (backpack-empty ?p)
      (hand ?p ?o)
    )
    :effect (and
      (not (hand ?p ?o))
      (not (has-obj ?p))
      (not (backpack-empty ?p))
      (backpack ?p ?o)
    )
  )

  (:action remove-from-backpack
    :parameters(?p - player ?o - obj)
    :precondition(and
      (backpack ?p ?o)
      (not (has-obj ?p))
    )
    :effect (and
      (hand ?p ?o)
      (has-obj ?p)
      (backpack-empty ?p)
      (not (backpack ?p ?o))
    )
  )

)
