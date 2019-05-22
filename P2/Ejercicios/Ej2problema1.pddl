(define (problem ej2-test)

  (:domain ej2-domain)

  (:objects
    z1 z2 z3 z4 z5 z6 z7 z8 z9 z10 z11 - zone
    princesa principe bruja profesor diCaprio - npc
    oscar1 oscar2 manzana rosa algoritmo oro - obj
    n s e w - orient
    jugador - player
  )

  (:init
    (= (total-cost) 0)

    (connected z1 z2 e)
    (connected z2 z1 w)
    (= (distance z1 z2) 2)
    (= (distance z2 z1) 2)
    (connected z2 z7 s)
    (connected z7 z2 n)
    (= (distance z2 z7) 2)
    (= (distance z7 z2) 2)
    (connected z3 z4 e)
    (connected z4 z3 w)
    (= (distance z3 z4) 1)
    (= (distance z4 z3) 1)
    (connected z3 z8 s)
    (connected z8 z3 n)
    (= (distance z3 z8) 1)
    (= (distance z8 z3) 1)
    (connected z4 z9 s)
    (connected z9 z4 n)
    (= (distance z4 z9) 1)
    (= (distance z9 z4) 1)
    (connected z5 z10 n)
    (connected z10 z5 s)
    (= (distance z5 z10) 1)
    (= (distance z10 z5) 1)
    (connected z6 z7 e)
    (connected z7 z6 w)
    (= (distance z6 z7) 1)
    (= (distance z7 z6) 1)
    (connected z7 z8 e)
    (connected z8 z7 w)
    (= (distance z7 z8) 1)
    (= (distance z8 z7) 1)
    (connected z1 z5 n)
    (connected z5 z1 s)
    (= (distance z1 z5) 1)
    (= (distance z5 z1) 1)
    (connected z1 z6 s)
    (connected z6 z1 n)
    (= (distance z1 z6) 2)
    (= (distance z6 z1) 2)
    (connected z8 z11 e)
    (connected z11 z8 w)
    (= (distance z8 z11) 1)
    (= (distance z11 z8) 1)

    (at bruja z5)
    (at princesa z4)
    (at principe z9)
    (at profesor z7)
    (at diCaprio z6)

    (at oscar1 z10)
    (at oscar2 z11)
    (at manzana z2)
    (at rosa z8)
    (at algoritmo z3)
    (at oro z1)

    (at jugador z1)
    (player-looking n)
  )

  (:goal (and
    (has-obj princesa)
    (has-obj principe)
    (has-obj bruja)
    (has-obj profesor)
    (has-obj diCaprio))
  )

  (:metric minimize (total-cost))

)
