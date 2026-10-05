#!/bin/bash
# Script de compilación y ejecución para 2D Minecraft
# Requiere Java 17 (openjdk-17-jdk)
# Funciona en Chromebooks con Linux (Crostini)

set -e

echo "=== 2D Minecraft ==="
echo "Compilando..."

mkdir -p out
javac -d out src/*.java

echo "Compilación exitosa."
echo "Iniciando juego..."

java -cp out Main
