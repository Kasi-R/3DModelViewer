JAVAC=javac
JAR=jar

JAVA_NAME=3DModelViewer.jar

SRC=javaclasses
MFST=MANIFEST.MF

all:
	@echo "	Syntax for the 3DModelViewer by Kasi makefile:"
	@echo "  	  For distributions: make file"
	@echo "  	  To clean build files: make clean"	
	
file:
	@echo "Compiling .java files..."
	@$(JAVAC) $(SRC)/*.java
	@echo "Successfully compiled .java files!"
	@echo "Compressing .class files..."
	@$(JAR) cfm $(JAVA_NAME) $(MFST) $(SRC)/*.class
	@echo "Successfully Compressed .class files!"
	@echo Check ${CURDIR}/ for .jar file
	
clean:
	@echo "Cleaning source files..."
	@rm -rf $(SRC)/*.class
	@echo "Directory cleaned!"