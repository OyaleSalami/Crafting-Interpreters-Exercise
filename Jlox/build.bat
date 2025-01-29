@echo off
echo "Compiling all the java files in this directory"

echo "Building Tools"
cd "tools/"
javac -d "../bin/" *.java

echo "Generating Expressions"
cd "../"
cd "bin/"
java com.craftinginterpreters.tools.GenerateAst "../src"

echo "Building Lox"
cd "../src"
javac -d "../bin/" *.java

echo "Done! or not..."
pause