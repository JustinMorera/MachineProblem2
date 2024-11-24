# Makefile
# A very simple makefile for compiling a Java program.  This file compiles
# the sim.java file, and relies on javac to compile all its  dependencies
# automatically.
#
# If you require any special options to be passed to javac, modify the
# CFLAGS variable.  You may want to comment out the DEBUG option before
# running your simulations.

JAVAC = javac
JAVA = java
CLASS_FILES = *.class
MAIN_CLASS = sim

# Change these for validation
PREDICTOR = bimodal
ARG_NUMS = 12
FILE = gcc
VAL = bimodal_2

# <simName> <B/M2/M1/K> <tracefile/N/M1> <tracefile/N> <M2>
ARGS = $(PREDICTOR) $(ARG_NUMS) traces/$(FILE)_trace.txt

# DEBUG = -g
CFLAGS = $(DEBUG) -deprecation

# default target
all: compile

# compile
compile:
	$(JAVAC) *.java

# Run main class with preset args
run: $(CLASS_FILES)
	$(JAVA) $(MAIN_CLASS) $(ARGS) > output.txt
	diff output.txt validation_runs/val_$(VAL).txt -y -w > diff.txt
		
sim:
	$(JAVAC) $(CFLAGS) sim.java

# type "make clean" to remove all your .class files
clean:
	-rm *.class

