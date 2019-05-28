---
title: Memoria Práctica 3
subtitle: Técnicas de los Sistemas Inteligentes. Grupo Lunes.
date: Curso 2018-2019
fontsize: 11pt
author: Antonio Coín Castro
documentclass: scrartcl
---

CAMBIAR LA RUTA RELATIVA DE LAS PRIMITIVAS EN LOS DOMINIOS

# Ejercicio 1

(:method Case3 ; si no está en la ciudad de destino, y avión y persona están en distinta ciudad
 :precondition (and (at ?p - person ?c1 - city)
                    (at ?a - aircraft ?c2 - city))

 :tasks (
        (mover-avion ?a ?c2 ?c1)
        (board ?p ?a ?c1)
        (mover-avion ?a ?c1 ?c)
        (debark ?p ?a ?c))
   )

# Ejercicio 2

Se considera que te puedes quedar exactamente a 0 de fuel.

Se modifica    (hay-fuel ?a - aircraft ?c1 - city ?c2 - city)
  (>= (fuel ?a) (* (slow-burn ?a) (distance ?c1 ?c2))))

Y se añade


   (:method no-fuel
     :tasks ((refuel ?a ?c1)
              (fly ?a ?c1 ?c2))

   )

# Ejercicio 3

Se añade la función (fuel-limit)

Se dividen en dos los predicados de hay-fuel (y sus derivados):


  (hay-fuel-slow ?a - aircraft ?c1 - city ?c2 - city)
  (>= (fuel ?a) (* (slow-burn ?a) (distance ?c1 ?c2))))

  (:derived

    (hay-fuel-fast ?a - aircraft ?c1 - city ?c2 - city)
    (>= (fuel ?a) (* (fast-burn ?a) (distance ?c1 ?c2) )))

Se modifica la tarea mover-avion. Se asume que siempre que podamos hacer refuel para volar rápido le damos prioridad.

(:method fuel-fast
   :precondition (and (hay-fuel-fast ?a ?c1 ?c2) (>= (fuel-limit) (+ (* (fast-burn ?a) (distance ?c1 ?c2)) (total-fuel-used))))

   :tasks ((zoom ?a ?c1 ?c2))
 )

 (:method refuel-fast
   :precondition (and (not (hay-fuel-fast ?a ?c1 ?c2)) (>= (fuel-limit) (+ (* (fast-burn ?a) (distance ?c1 ?c2)) (total-fuel-used))))

   :tasks ((refuel ?a ?c1) (zoom ?a ?c1 ?c2))
 )

 (:method fuel-slow
   :precondition (and (hay-fuel-slow ?a ?c1 ?c2) (>= (fuel-limit) (+ (* (slow-burn ?a) (distance ?c1 ?c2)) (total-fuel-used))))

   :tasks ((fly ?a ?c1 ?c2))
 )

 (:method refuel-slow
   :precondition (and (not (hay-fuel-slow ?a ?c1 ?c2)) (>= (fuel-limit) (+ (* (fast-burn ?a) (distance ?c1 ?c2)) (total-fuel-used))))

   :tasks ((refuel ?a ?c1) (fly ?a ?c1 ?c2))
)
