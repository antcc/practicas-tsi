(define (domain zeno-travel)


(:requirements
  :typing
  :fluents
  :derived-predicates
  :negative-preconditions
  :universal-preconditions
  :disjuntive-preconditions
  :conditional-effects
  :htn-expansion

  ; Requisitos adicionales para el manejo del tiempo
  :durative-actions
  :metatags
 )

(:types aircraft person city - object)
(:constants slow fast - object)
(:predicates (at ?x - (either person aircraft) ?c - city)
             (in ?p - person ?a - aircraft)
             (different ?x ?y) (igual ?x ?y)
             (destino ?p - person ?c - city)
             (hay-fuel-slow ?a ?c1 ?c2)
             (hay-fuel-fast ?a ?c1 ?c2)
             (able-time-slow ?a ?c1 ?c2)
             (able-time-fast ?a ?c1 ?c2)
             )
(:functions (fuel ?a - aircraft)
            (distance ?c1 - city ?c2 - city)
            (slow-speed ?a - aircraft)
            (fast-speed ?a - aircraft)
            (slow-burn ?a - aircraft)
            (fast-burn ?a - aircraft)
            (capacity ?a - aircraft)
            (people ?a - aircraft)
            (max-people ?a - aircraft)
            (refuel-rate ?a - aircraft)
            (total-fuel-used ?a - aircraft)
            (fuel-limit ?a - aircraft)
            (time-consumed ?a - aircraft)
            (time-limit ?a - aircraft)
            (boarding-time)
            (debarking-time)
            )

;; el consecuente "vac�o" se representa como "()" y significa "siempre verdad"
(:derived
  (igual ?x ?x) ())

(:derived
  (different ?x ?y) (not (igual ?x ?y)))



;; este literal derivado se utiliza para deducir, a partir de la información en el estado actual,
;; si hay fuel suficiente para que el avión ?a vuele de la ciudad ?c1 a la ?c2
;; el antecedente de este literal derivado comprueba si el fuel actual de ?a es mayor que 1.
;; En este caso es una forma de describir que no hay restricciones de fuel. Pueden introducirse una
;; restricción más copleja  si en lugar de 1 se representa una expresión más elaborada (esto es objeto de
;; los siguientes ejercicios).
(:derived

  (hay-fuel-slow ?a - aircraft ?c1 - city ?c2 - city)
  (>= (fuel ?a) (* (slow-burn ?a) (distance ?c1 ?c2))))

  (:derived

    (hay-fuel-fast ?a - aircraft ?c1 - city ?c2 - city)
    (>= (fuel ?a) (* (fast-burn ?a) (distance ?c1 ?c2) )))

(:derived

 (able-time-slow ?a - aircraft ?c1 - city ?c2 - city)
 (<= (+ (time-consumed ?a) (/ (distance ?c1 ?c2) (slow-speed ?a))) (time-limit ?a))
 )

(:derived

  (able-time-fast ?a - aircraft ?c1 - city ?c2 - city)
  (<= (+ (time-consumed ?a) (/ (distance ?c1 ?c2) (fast-speed ?a))) (time-limit ?a))
  )


(:task transport-person
	:parameters ()

   (:method Case1 ; Persona en avión, avión en destino
              :precondition (and
                             (in ?p - person ?a - aircraft)
                             (at ?a - aircraft ?c - city)
                             (destino ?p ?c)
                             )
              :tasks (
                      (debark ?p ?a ?c)
                      (transport-person)
                      )
              )

     (:method Case2 ; Persona no en avión, avión en ciudad de persona
              :precondition (and
                             (at ?p - person ?c1 - city)
                             (destino ?p - person ?c2 - city)
                             (at ?a - aircraft ?c1 - city)
                             (not (= ?c1 ?c2))
                             )
              :tasks (
                      (board ?p ?a ?c1)
                      (transport-person)
              )
      )

     (:method Case3 ; Persona en avión y no en destino
              :precondition (and
                             (in ?p - person ?a - aircraft)
                             (at ?a - aircraft ?c1 - city)
                             (destino ?p - person ?c2 - city)
                             (not (= ?c1 ?c2))
                             )

              :tasks (
                      (mover-avion ?a ?c1 ?c2)
                      (transport-person)
                      )
              )

     (:method Case4 ; Todos en sitios distintos
              :precondition (and
                             (at ?p - person ?c1 - city)
                             (at ?a - aircraft ?c2 - city)
                             (destino ?p - person ?c3 - city)
                             (not (= ?c1 ?c2))
                             (not (= ?c1 ?c3))
                             )
              :tasks(
                     (mover-avion ?a ?c2 ?c1)
                     (board ?p ?a ?c1)
                     (transport-person)
                     )
              )

          (:method Case5 ; Fin de la recursividad
        	 :precondition (and (at ?p - person ?c - city)
                         (destino ?p ?c)
                         )
        	 :tasks () ; Nada
           )
  )

(:task mover-avion
 :parameters (?a - aircraft ?c1 - city ?c2 -city)
 (:method fuel-fast
 		:precondition (and (hay-fuel-fast ?a ?c1 ?c2)
    (>= (fuel-limit ?a) (+ (* (fast-burn ?a) (distance ?c1 ?c2)) (total-fuel-used ?a)))
    (able-time-fast ?a ?c1 ?c2))

 		:tasks ((zoom ?a ?c1 ?c2))
 	)

 	(:method refuel-fast
 		:precondition (and (>= (fuel-limit ?a)
    (+ (* (fast-burn ?a) (distance ?c1 ?c2)) (total-fuel-used ?a)))
    (able-time-fast ?a ?c1 ?c2))

 		:tasks ((refuel ?a ?c1) (zoom ?a ?c1 ?c2))
 	)

 	(:method fuel-slow
 		:precondition (and (hay-fuel-slow ?a ?c1 ?c2)
    (>= (fuel-limit ?a) (+ (* (slow-burn ?a) (distance ?c1 ?c2)) (total-fuel-used ?a)))
    (able-time-slow ?a ?c1 ?c2))

 		:tasks ((fly ?a ?c1 ?c2))
 	)

 	(:method refuel-slow
 		:precondition (and (>= (fuel-limit ?a)
    (+ (* (fast-burn ?a) (distance ?c1 ?c2)) (total-fuel-used ?a)))
    (able-time-slow ?a ?c1 ?c2))

 		:tasks ((refuel ?a ?c1) (fly ?a ?c1 ?c2))
 )
    )


(:import "Primitivas-Zenotravel-v04.pddl")


)
