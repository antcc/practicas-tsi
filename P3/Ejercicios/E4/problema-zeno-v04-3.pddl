(define (problem zeno-3) (:domain zeno-travel)
(:customization
(= :time-format "%d/%m/%Y %H:%M:%S")
(= :time-horizon-relative 2500)
(= :time-start "05/06/2007 08:00:00")
(= :time-unit :hours))

(:objects
    p1 p2 p3 p4 p5 p6 p7 p8 p9 p10 p11 p12 p13 p14 p15 p16 p17 p18 p19 p20 p21 - person
    almeria barcelona bilbao cadiz cordoba gibraltar granada huelva jaen madrid malaga sevilla - city
    a1 a2 - aircraft
)
(:init
    (at p1 almeria)
    (at p2 barcelona)
    (at p3 bilbao)
    (at p4 cordoba)
    (at p5 granada)
    (at p6 granada)
    (at p7 huelva)
    (at p8 jaen)
    (at p9 madrid)
    (at p10 malaga)
    (at p11 sevilla)
    (at p12 almeria)
    (at p13 barcelona)
    (at p14 bilbao)
    (at p15 bilbao)
    (at p16 gibraltar)
    (at p17 granada)
    (at p18 huelva)
    (at p19 jaen)
    (at p20 madrid)
    (at p21 madrid)

    (at a1 granada)
    (at a2 malaga)

    (= (fuel a1) 1140)
    (= (slow-speed a1) 10)
    (= (fast-speed a1) 20)
    (= (slow-burn a1) 1)
    (= (fast-burn a1) 2)
    (= (capacity a1) 1140)
    (= (refuel-rate a1) 1)
    (= (people a1) 0)
    (= (max-people a1) 4)
    (= (fuel-limit a1) 20000)
    (= (total-fuel-used a1) 0)
    (= (time-consumed a1) 0)
    (= (time-limit a1) 10000)

    (= (boarding-time) 1)
    (= (debarking-time) 1)

    (= (fuel a2) 1140)
    (= (slow-speed a2) 10)
    (= (fast-speed a2) 20)
    (= (slow-burn a2) 1)
    (= (fast-burn a2) 2)
    (= (capacity a2) 1140)
    (= (refuel-rate a2) 1)
    (= (people a2) 0)
    (= (max-people a2) 5)
    (= (fuel-limit a2) 20000)
    (= (total-fuel-used a2) 0)
    (= (time-consumed a2) 0)
    (= (time-limit a2) 10000)

    (destino p19 almeria)
    (destino p20 barcelona)
    (destino p21 barcelona)
    (destino p16 bilbao)
    (destino p18 cordoba)
    (destino p14 madrid)
    (destino p12 malaga)
    (destino p10 sevilla)
    (destino p9 almeria)
    (destino p17 gibraltar)
    (destino p15 granada)
    (destino p11 huelva)
    (destino p13 jaen)
    (destino p7 barcelona)
    (destino p8 bilbao)
    (destino p6 cordoba)
    (destino p5 gibraltar)
    (destino p3 granada)
    (destino p4 huelva)
    (destino p2 jaen)
    (destino p1 madrid)


    ; DISTANCES ----------------------------------------------------------------
        ; DISTANCES ----------------------------------------------------------------
            ; DISTANCES ----------------------------------------------------------------
                ; DISTANCES ----------------------------------------------------------------
                    ; DISTANCES ----------------------------------------------------------------

    (= (distance almeria barcelona) 809)
    (= (distance almeria bilbao) 958)
    (= (distance almeria cadiz) 463)
    (= (distance almeria cordoba) 316)
    (= (distance almeria gibraltar) 339)
    (= (distance almeria granada) 162)
    (= (distance almeria huelva) 505)
    (= (distance almeria jaen) 220)
    (= (distance almeria madrid) 547)
    (= (distance almeria malaga) 207)
    (= (distance almeria sevilla) 410)

    (= (distance barcelona bilbao) 620)
    (= (distance barcelona cadiz) 1284)
    (= (distance barcelona cordoba) 908)
    (= (distance barcelona gibraltar) 1124)
    (= (distance barcelona granada) 868)
    (= (distance barcelona huelva) 1140)
    (= (distance barcelona jaen) 804)
    (= (distance barcelona madrid) 621)
    (= (distance barcelona malaga) 997)
    (= (distance barcelona sevilla) 1046)
    (= (distance barcelona almeria) 809)

    (= (distance bilbao cadiz) 1058)
    (= (distance bilbao cordoba) 796)
    (= (distance bilbao gibraltar) 1110)
    (= (distance bilbao granada) 829)
    (= (distance bilbao huelva) 939)
    (= (distance bilbao jaen) 730)
    (= (distance bilbao madrid) 395)
    (= (distance bilbao malaga) 939)
    (= (distance bilbao sevilla) 933)
    (= (distance bilbao almeria) 958)
    (= (distance bilbao barcelona) 620)

    (= (distance cadiz cordoba) 261)
    (= (distance cadiz gibraltar) 124)
    (= (distance cadiz granada) 296)
    (= (distance cadiz huelva) 214)
    (= (distance cadiz jaen) 330)
    (= (distance cadiz madrid) 654)
    (= (distance cadiz malaga) 240)
    (= (distance cadiz sevilla) 126)
    (= (distance cadiz almeria) 463)
    (= (distance cadiz barcelona) 1284)
    (= (distance cadiz bilbao) 1058)

    (= (distance cordoba gibraltar) 294)
    (= (distance cordoba granada) 160)
    (= (distance cordoba huelva) 241)
    (= (distance cordoba jaen) 108)
    (= (distance cordoba madrid) 396)
    (= (distance cordoba malaga) 165)
    (= (distance cordoba sevilla) 143)
    (= (distance cordoba almeria) 316)
    (= (distance cordoba barcelona) 908)
    (= (distance cordoba bilbao) 796)
    (= (distance cordoba cadiz) 261)

    (= (distance gibraltar granada) 255)
    (= (distance gibraltar huelva) 289)
    (= (distance gibraltar jaen) 335)
    (= (distance gibraltar madrid) 662)
    (= (distance gibraltar malaga) 134)
    (= (distance gibraltar sevilla) 201)
    (= (distance gibraltar almeria) 339)
    (= (distance gibraltar barcelona) 1124)
    (= (distance gibraltar bilbao) 1110)
    (= (distance gibraltar cadiz) 124)
    (= (distance gibraltar cordoba) 294)

    (= (distance granada huelva) 346)
    (= (distance granada jaen) 93)
    (= (distance granada madrid) 421)
    (= (distance granada malaga) 125)
    (= (distance granada sevilla) 252)
    (= (distance granada almeria) 162)
    (= (distance granada barcelona) 868)
    (= (distance granada bilbao) 829)
    (= (distance granada cadiz) 296)
    (= (distance granada cordoba) 261)
    (= (distance granada gibraltar) 255)

    (= (distance huelva jaen) 347)
    (= (distance huelva madrid) 591)
    (= (distance huelva malaga) 301)
    (= (distance huelva sevilla) 95)
    (= (distance huelva almeria) 505)
    (= (distance huelva barcelona) 1140)
    (= (distance huelva bilbao) 939)
    (= (distance huelva cadiz) 214)
    (= (distance huelva cordoba) 241)
    (= (distance huelva gibraltar) 289)
    (= (distance huelva granada) 346)

    (= (distance jaen madrid) 335)
    (= (distance jaen malaga) 203)
    (= (distance jaen sevilla) 246)
    (= (distance jaen almeria) 220)
    (= (distance jaen barcelona) 804)
    (= (distance jaen bilbao) 730)
    (= (distance jaen cadiz) 330)
    (= (distance jaen cordoba) 108)
    (= (distance jaen gibraltar) 335)
    (= (distance jaen granada) 93)
    (= (distance jaen huelva) 347)

    (= (distance madrid malaga) 532)
    (= (distance madrid sevilla) 534)
    (= (distance madrid almeria) 547)
    (= (distance madrid barcelona) 621)
    (= (distance madrid bilbao) 395)
    (= (distance madrid cadiz) 654)
    (= (distance madrid cordoba) 396)
    (= (distance madrid gibraltar) 662)
    (= (distance madrid granada) 421)
    (= (distance madrid huelva) 591)
    (= (distance madrid jaen) 335)

    (= (distance malaga sevilla) 209)
    (= (distance malaga almeria) 207)
    (= (distance malaga barcelona) 997)
    (= (distance malaga bilbao) 939)
    (= (distance malaga cadiz) 240)
    (= (distance malaga cordoba) 165)
    (= (distance malaga gibraltar) 134)
    (= (distance malaga granada) 125)
    (= (distance malaga huelva) 301)
    (= (distance malaga jaen) 203)
    (= (distance malaga madrid) 532)

    (= (distance sevilla almeria) 410)
    (= (distance sevilla barcelona) 1046)
    (= (distance sevilla bilbao) 933)
    (= (distance sevilla cadiz) 126)
    (= (distance sevilla cordoba) 143)
    (= (distance sevilla gibraltar) 201)
    (= (distance sevilla granada) 252)
    (= (distance sevilla huelva) 95)
    (= (distance sevilla jaen) 246)
    (= (distance sevilla madrid) 534)
    (= (distance sevilla malaga) 209)
 )


(:tasks-goal
   :tasks(
   (transport-person)
   )
  )
)