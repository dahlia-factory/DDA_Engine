JC = javac
JVM = java
LIB_DIR = lib
CP = ".:$(LIB_DIR)/*"

MAIN = DDARaycaster
SRC = DDARaycaster.java

all: compile run

compile:
	$(JC) -cp $(CP) $(SRC)

run:
	$(JVM) -cp $(CP) $(MAIN)

clean:
	rm -f *.class
