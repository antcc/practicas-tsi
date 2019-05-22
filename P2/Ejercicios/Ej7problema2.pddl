(define 
(problem ej7-test2)

	(:domain ej7-domain)

	(:objects
		n s e w - orient
		z1 z2 z3 z4 z5 z6 z7 z8 z9 z10 z11 z12 z13 z14 z15 z16 z17 z18 z19 z20 z21 z22 z23 z24 z25 - zone
		algoritmo1 - normal-obj
		bikini1 - obj
		oscar2 - normal-obj
		zapatilla1 - obj
		player1 - dealer
		bikini2 - obj
		rosa1 - normal-obj
		oscar1 - normal-obj
		principe1 - npc
		manzana3 - normal-obj
		manzana1 - normal-obj
		player2 - picker
		oro2 - normal-obj
		dealer - dealer
		oro1 - normal-obj
		manzana2 - normal-obj
		oro3 - normal-obj
		bruja1 - npc
		bosque arena piedra agua precipicio - type
	)

	(:init

		(connected z1 Z2 e)
		(connected Z2 z1 w)
		(connected Z2 Z3 e)
		(connected Z3 Z2 w)
		(connected Z3 z4 e)
		(connected z4 Z3 w)
		(connected z4 z5 e)
		(connected z5 z4 w)
		(connected z12 z13 e)
		(connected z13 z12 w)
		(connected z13 z14 e)
		(connected z14 z13 w)
		(connected z19 z20 e)
		(connected z20 z19 w)
		(connected z21 z22 e)
		(connected z22 z21 w)
		(connected z22 z23 e)
		(connected z23 z22 w)
		(connected z23 z24 e)
		(connected z24 z23 w)
		(connected z1 z6 s)
		(connected z6 z1 n)
		(connected z6 z11 s)
		(connected z11 z6 n)
		(connected z11 z16 s)
		(connected z16 z11 n)
		(connected z2 z7 s)
		(connected z7 z2 n)
		(connected z7 z12 s)
		(connected z12 z7 n)
		(connected z12 z17 s)
		(connected z17 z12 n)
		(connected z17 z22 s)
		(connected z22 z17 n)
		(connected z3 z8 s)
		(connected z8 z3 n)
		(connected z8 z13 s)
		(connected z13 z8 n)
		(connected z13 z18 s)
		(connected z18 z13 n)
		(connected z4 z9 s)
		(connected z9 z4 n)
		(connected z9 z14 s)
		(connected z14 z9 n)
		(connected z14 z19 s)
		(connected z19 z14 n)
		(connected z5 z10 s)
		(connected z10 z5 n)
		(connected z10 z15 s)
		(connected z15 z10 n)
		(connected z20 z25 s)
		(connected z25 z20 n)

		(= (distance z1 Z2) 1)
		(= (distance Z2 z1) 1)
		(= (distance Z2 Z3) 1)
		(= (distance Z3 Z2) 1)
		(= (distance Z3 z4) 1)
		(= (distance z4 Z3) 1)
		(= (distance z4 z5) 1)
		(= (distance z5 z4) 1)
		(= (distance z12 z13) 1)
		(= (distance z13 z12) 1)
		(= (distance z13 z14) 1)
		(= (distance z14 z13) 1)
		(= (distance z19 z20) 1)
		(= (distance z20 z19) 1)
		(= (distance z21 z22) 1)
		(= (distance z22 z21) 1)
		(= (distance z22 z23) 1)
		(= (distance z23 z22) 1)
		(= (distance z23 z24) 1)
		(= (distance z24 z23) 1)
		(= (distance z1 z6) 1)
		(= (distance z6 z1) 1)
		(= (distance z6 z11) 1)
		(= (distance z11 z6) 1)
		(= (distance z11 z16) 1)
		(= (distance z16 z11) 1)
		(= (distance z2 z7) 1)
		(= (distance z7 z2) 1)
		(= (distance z7 z12) 1)
		(= (distance z12 z7) 1)
		(= (distance z12 z17) 1)
		(= (distance z17 z12) 1)
		(= (distance z17 z22) 1)
		(= (distance z22 z17) 1)
		(= (distance z3 z8) 1)
		(= (distance z8 z3) 1)
		(= (distance z8 z13) 1)
		(= (distance z13 z8) 1)
		(= (distance z13 z18) 1)
		(= (distance z18 z13) 1)
		(= (distance z4 z9) 1)
		(= (distance z9 z4) 1)
		(= (distance z9 z14) 1)
		(= (distance z14 z9) 1)
		(= (distance z14 z19) 1)
		(= (distance z19 z14) 1)
		(= (distance z5 z10) 1)
		(= (distance z10 z5) 1)
		(= (distance z10 z15) 1)
		(= (distance z15 z10) 1)
		(= (distance z20 z25) 1)
		(= (distance z25 z20) 1)
		(= (total-cost) 0)

		(zone-type z1 bosque)
		(zone-type Z3 piedra)
		(zone-type z5 agua)
		(zone-type Z2 piedra)
		(zone-type z4 piedra)
		(zone-type z13 piedra)
		(zone-type z12 piedra)
		(zone-type z14 piedra)
		(zone-type z20 piedra)
		(zone-type z19 piedra)
		(zone-type z22 arena)
		(zone-type z24 piedra)
		(zone-type z21 bosque)
		(zone-type z23 arena)
		(zone-type z1 bosque)
		(zone-type z6 piedra)
		(zone-type z11 agua)
		(zone-type z16 bosque)
		(zone-type z17 piedra)
		(zone-type z22 bosque)
		(zone-type z12 piedra)
		(zone-type z7 piedra)
		(zone-type z2 piedra)
		(zone-type z18 piedra)
		(zone-type z3 arena)
		(zone-type z13 piedra)
		(zone-type z8 piedra)
		(zone-type z4 piedra)
		(zone-type z19 piedra)
		(zone-type z9 piedra)
		(zone-type z14 piedra)
		(zone-type z15 agua)
		(zone-type z10 arena)
		(zone-type z5 arena)
		(zone-type z20 piedra)
		(zone-type z25 arena)

		(is-bikini bikini1)
		(is-sneakers zapatilla1)
		(is-bikini bikini2)

		(= (total-points player1) 0)
		(= (total-points dealer) 0)
		(at algoritmo1 z1)
		(at bikini1 z1)
		(at oscar2 z4)
		(at zapatilla1 z5)
		(player-looking player1 n)
		(backpack-empty player1)
		(at player1 z13)
		(at bikini2 z14)
		(at rosa1 z20)
		(at oscar1 z20)
		(at principe1 z24)
		(at manzana3 z6)
		(at manzana1 z16)
		(player-looking player2 n)
		(backpack-empty player2)
		(at player2 z2)
		(at oro2 z8)
		(player-looking dealer n)
		(backpack-empty dealer)
		(at dealer z4)
		(at oro1 z9)
		(at manzana2 z10)
		(at oro3 z20)
		(at bruja1 z25)

		(= (points principe1 algoritmo1) 5)
		(= (points principe1 oscar2) 1)
		(= (points principe1 rosa1) 3)
		(= (points principe1 oscar1) 1)
		(= (points principe1 manzana3) 4)
		(= (points principe1 manzana1) 4)
		(= (points principe1 oro2) 10)
		(= (points principe1 oro1) 10)
		(= (points principe1 manzana2) 4)
		(= (points principe1 oro3) 10)
		(= (points bruja1 algoritmo1) 1)
		(= (points bruja1 oscar2) 4)
		(= (points bruja1 rosa1) 5)
		(= (points bruja1 oscar1) 4)
		(= (points bruja1 manzana3) 10)
		(= (points bruja1 manzana1) 10)
		(= (points bruja1 oro2) 3)
		(= (points bruja1 oro1) 3)
		(= (points bruja1 manzana2) 10)
		(= (points bruja1 oro3) 3)

		(= (pocket-size bruja1) 5)
		(= (pocket bruja1) 0)
		(= (pocket-size principe1) 10)
		(= (pocket principe1) 0)

	)

	(:goal (and
			(has-obj bruja1)
			(has-obj principe1)
			(>= (+ (total-points player1) (total-points dealer) ) 31)
			(>= (total-points player1) 20)
			(>= (total-points dealer) 2)

		)
	)

	(:metric minimize (total-cost))
)
