import sys
import itertools
import os
import re

# Require that every character has an object in goal
goal_objects = True

# Helper function: trim string beginning in ':'
def trim(string):
    return string.split(":")[1]

# Check arguments
if len(sys.argv) != 3:
    sys.exit("Uso: generaproblema.py <mapa> <salida>")

# Print everything to PDDL file
sys.stdout = open(sys.argv[2], "w+")

# Initialize parts
define = "(define "
problem = "(problem "
domain = "\t(:domain "
objects = "\t(:objects\n\t\tn s e w - orient\n\t\t"
init = ["\t(:init\n", "", "", "", "", "", "", "", "", "", "", "", "", ""]
goal = "\n\t(:goal (and"

# Open file and split into list
map_file = open(sys.argv[1], "r")
content = list(filter(lambda x: x != '\n', map_file.readlines()))
content = list(map(lambda x: x.rstrip('\n'), content))
map_file.close()

# Get domain, problem and number of zones
domain_name = trim(content[0])
domain += domain_name + ")\n"
content.pop(0)
problem_name = trim(content[0])
problem += problem_name + ")\n"
content.pop(0);
num_zones = int(trim(content[0]))
content.pop(0);
num = int(domain_name[2])

# Get number of players (we don't need it)
num_players = 1
if num == 6:
    num_players = 2
elif num >= 7:
    num_players = int(trim(content[0]))
    content.pop(0)

# Ex 5: pocket
if num >= 5:
    m = re.match("bolsillo:\[(.*)\]", content[0])
    m = m.groups(0)[0]
    m = m.split(" ")
    content.pop(0)
    for npc in m:
        info = npc.split(":")
        init[6] += "\t\t(= (pocket-size " + info[0] + ") " + info[1] + ")\n"
        init[6] += "\t\t(= (pocket " + info[0] + ") 0)\n"

# Ex 4: points
score = 0
if num >= 4:
    m = re.match("puntos_totales:(\d+)", content[0])
    m = m.string.split(":")
    score = m[1]
    content.pop(0)

# Ex 6: points per player
goal6 = ""
if num >= 6:
    m = re.match("puntos_jugador:\[(.*)\]", content[0])
    m = m.groups(0)[0]
    m = m.split(" ")
    content.pop(0)
    for player in m:
        info = player.split(":")
        goal6 += "\t\t\t(>= (total-points " +  info[0] + ") " + info[1] + ")\n"
        init[4] += "\t\t(= (total-points " + info[0] + ") 0)\n"

# Define zone objects
for i in range(0, num_zones):
    objects += "z" + str(i+1)
    if i != num_zones - 1:
        objects += " "
objects += " - zone"

# Parse lines in content
characters = []
character_types = []
objs = []
objs_types = []
zones = []
players = []
for line in content:
    pattern = re.compile('([\w\d]*?)\[([\w\d\- ]*?)\](?:\[([\w\d]*?)\])?(=\d*=)*') # magic
    orientation = line[0]
    lista = re.findall(pattern, line)

    # Zones connected and distances between them
    for item in zip(lista, lista[1:]):
        if orientation == 'H':
            init[1] += "\t\t(connected " + item[0][0] + " " + item[1][0] + " e)\n"
            init[1] += "\t\t(connected " + item[1][0] + " " + item[0][0] + " w)\n"
        elif orientation == 'V':
            init[1] += "\t\t(connected " + item[0][0] + " " + item[1][0] + " s)\n"
            init[1] += "\t\t(connected " + item[1][0] + " " + item[0][0] + " n)\n"
        if num >= 2:
            init[2] += "\t\t(= (distance " + item[0][0] + " " + item[1][0] +") " + ''.join(c for c in item[0][3] if c.isdigit()) + ")\n"
            init[2] += "\t\t(= (distance " + item[1][0] + " " + item[0][0] +") " + ''.join(c for c in item[0][3] if c.isdigit()) + ")\n"

    # Zone types
    if num > 2:
        for item in list(set(lista)):
            init[3] += "\t\t(zone-type " + item[0] + " " + item[2].lower() + ")\n"

    # Characters and objects
    for item in lista:
        obj = item[1]
        if obj != '':
            b = obj.split(" ")
            for c in b:
                p = c.split('-');

                # Players
                if p[1] == "Player" or p[1] == "Dealer" or p[1] == "Picker":
                    aux = ""
                    if num >= 6:
                        aux = p[0] + " "
                    init[4] += "\t\t(player-looking " + aux + "n)\n"
                    if num >= 3:
                        init[4] += "\t\t(backpack-empty " + p[0] + ")\n"
                    if p[1] != "Picker":
                        players.append(p[0])
                    objects += "\n\t\t" + p[0] + " - " + p[1].lower()

                else:

                    # NPCs
                    if p[1] in ["Bruja", "Principe", "Princesa", "Leonardo", "Profesor"]:
                        characters.append(p[0])
                        character_types.append(p[1])
                        objects += "\n\t\t" + p[0] + " - npc"
                    else:

                        # Usable objects
                        if p[1] == "Zapatilla":
                            init[7] += "\t\t(is-sneakers " + p[0] + ")\n"
                            objects += "\n\t\t" + p[0] + " - obj"
                        elif p[1] == "Bikini":
                            init[7] += "\t\t(is-bikini " + p[0] + ")\n"
                            objects += "\n\t\t" + p[0] + " - obj"

                        # Non-usable objects
                        else:
                            objs.append(p[0])
                            objs_types.append(p[1])
                            if num >= 3:
                                objects += "\n\t\t" + p[0] + " - normal-obj"
                            else:
                                objects += "\n\t\t" + p[0] + " - obj"
                init[4] += "\t\t(at " + p[0] + " " + item[0] + ")\n"

# Dictionary of points
dict = {
    "Bruja": {"Oscar": "4", "Rosa": "5", "Manzana": "10", "Algoritmo": "1", "Oro": "3"},
    "Principe": {"Oscar": "1", "Rosa": "3", "Manzana": "4", "Algoritmo": "5", "Oro": "10"},
    "Princesa": {"Oscar": "5", "Rosa": "10", "Manzana": "1", "Algoritmo": "3", "Oro": "4"},
    "Leonardo": {"Oscar": "10", "Rosa": "1", "Manzana": "3", "Algoritmo": "4", "Oro": "5"},
    "Profesor": {"Oscar": "3", "Rosa": "4", "Manzana": "5", "Algoritmo": "10", "Oro": "1"}
}

# Initialize fluents
if num >= 2:
    init[2] += "\t\t(= (total-cost) 0)\n"
if num >= 4:
    if num <= 5:
        init[5] += "\t\t(= (total-points) 0)\n"
    for i in range(0, len(characters)):
        for j in range(0, len(objs)):
            index1 = character_types[i]
            index2 = objs_types[j]
            init[5] += "\t\t(= (points " + characters[i] + " " + objs[j] + ") " + dict[index1][index2] + ")\n"

# Zone types
if num >= 3:
    objects += "\n\t\tbosque arena piedra agua precipicio - type"

# Print problem
print(define)
print(problem)
print(domain)
print(objects)
print("\t)\n")
print(init[0])
print(init[1])
if num > 1:
    print(init[2])
if num > 2:
    print(init[3])
    print(init[7])
print(init[4])
if num > 3:
    print(init[5])
if num >= 5:
    print(init[6])
print("\t)")
print(goal)

# Goal for characters
if num <= 3 or goal_objects:
    characters = list(set(characters))
    for i in range(0, len(characters)):
        print("\t\t\t(has-obj " + characters[i] + ")")

# Goal for points
if num >= 4:
    if num <= 5:
        print("\t\t\t(>= (total-points) " + str(score) + ")")
    elif len(players) >= 2:
        print("\t\t\t(>= (+ ", end="")
        for player in players:
            print("(total-points " + player +") ", end="")
        print(") " + str(score) + ")")
if num >= 6:
    print(goal6)
print("\t\t)")
print("\t)")

# Minimize metric
if num >= 2:
    print("\n\t(:metric minimize (total-cost))")
print(")")
