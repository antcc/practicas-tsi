(define 
(problem ej1-test2)

	(:domain ej1-domain)

	(:objects
		n s e w - orient
		z1 z2 z3 z4 z5 z6 z7 z8 z9 z10 z11 z12 z13 z14 z15 z16 z17 z18 z19 z20 z21 z22 z23 z24 z25 - zone
		algoritmo1 - obj
		leonardo1 - npc
		oscar1 - obj
		player1 - player
		manzana1 - obj
		princesa1 - npc
		principe1 - npc
		bruja1 - npc
		rosa1 - obj
		profesor1 - npc
		oro1 - obj
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
		(connected z20 z25 s)
		(connected z25 z20 n)

		(at algoritmo1 z1)
		(at leonardo1 Z3)
		(at oscar1 z5)
		(player-looking n)
		(at player1 z14)
		(at manzana1 z20)
		(at princesa1 z21)
		(at principe1 z24)
		(at bruja1 z16)
		(at rosa1 z8)
		(at profesor1 z10)
		(at oro1 z25)

	)

	(:goal (and
			(has-obj princesa1)
			(has-obj principe1)
			(has-obj profesor1)
			(has-obj bruja1)
			(has-obj leonardo1)
		)
	)
)
