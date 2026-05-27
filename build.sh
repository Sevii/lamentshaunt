#!/bin/bash

# Exit on any error
set -e

echo "=== Lament's Haunt Mod Builder ==="

# Define paths
STARSECTOR_JAVA="/Applications/Starsector.app/Contents/Resources/Java"
CLASSPATH="${STARSECTOR_JAVA}/starfarer.api.jar:${STARSECTOR_JAVA}/log4j-1.2.9.jar:${STARSECTOR_JAVA}/lwjgl.jar:${STARSECTOR_JAVA}/lwjgl_util.jar"
BIN_DIR="bin"
JARS_DIR="jars"
JAR_NAME="lamentshaunt.jar"

echo "Creating clean build directories..."
mkdir -p "${BIN_DIR}"
mkdir -p "${JARS_DIR}"

echo "Compiling Java files..."
javac -cp "${CLASSPATH}" --release 8 -d "${BIN_DIR}" \
  src/com/lamentshaunt/LamentsHauntModPlugin.java \
  src/com/lamentshaunt/world/LamentsHauntGen.java \
  src/com/lamentshaunt/world/LamentsHauntColonizationFixer.java

echo "Packaging class files into jar..."
jar cf "${JARS_DIR}/${JAR_NAME}" -C "${BIN_DIR}" .

echo "Cleaning up temporary compile files..."
rm -rf "${BIN_DIR}"

echo "Build successful! Created ${JARS_DIR}/${JAR_NAME}."
