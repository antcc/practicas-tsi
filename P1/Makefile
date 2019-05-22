all: practica1.zip

%.pdf: %.md
	pandoc -o $@ $<

practica1.zip: memoria.pdf fuentes/*
	zip -9 $@ -r $^
