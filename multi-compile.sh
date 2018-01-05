#!/bin/bash
set -e

echo "--- COMPILATION & PACKAGING ---"

#Java executable for standard Linux environment
export JAVAC_EXE=javac
export JAR_EXE=jar
#Java executable for MinGW environment
#export JAVAC_EXE=/c/jdk9/bin/javac.exe
#export JAR_EXE=/c/jdk9/bin/jar.exe

echo " > creating clean directories"
rm -rf classes
mkdir classes
rm -rf mods
mkdir mods

echo " > multi-compiling modules"
# spark is required as an automatic module, so copy it to mods
cp libs/spark-core-* mods/spark.core.jar
$JAVAC_EXE \
	--module-path mods \
	--module-source-path "./*/src/main/java" \
	-d classes \
	--module monitor

# Since they aren't "required" in any module-info.java file, these two modules
# won't be captured by compiling the monitor module above. Therefore, they must
# be built separately.
$JAVAC_EXE --module-path mods --module-source-path "./*/src/main/java" -d classes --module monitor.observer.alpha
$JAVAC_EXE --module-path mods --module-source-path "./*/src/main/java" -d classes --module monitor.observer.beta

echo " > packaging modules"
$JAR_EXE --create \
	--file mods/monitor.observer.jar \
	-C classes/monitor.observer .
$JAR_EXE --create \
	--file mods/monitor.observer.alpha.jar \
	-C classes/monitor.observer.alpha .
$JAR_EXE --create \
	--file mods/monitor.observer.beta.jar \
	-C classes/monitor.observer.beta .
$JAR_EXE --create \
	--file mods/monitor.statistics.jar \
	-C classes/monitor.statistics .
$JAR_EXE --create \
	--file mods/monitor.persistence.jar \
	-C classes/monitor.persistence .
$JAR_EXE --create \
	--file mods/monitor.rest.jar \
	-C classes/monitor.rest .
$JAR_EXE --create \
	--file mods/monitor.jar \
	--main-class monitor.Main \
	-C classes/monitor .

# monitor.observer.zero = plain JAR. Since it requires other modules, we build it last so that it can refer to the JARs created above.
echo " > building monitor.observer.zero"
$JAVAC_EXE \
	--class-path 'mods/*' \
	-d classes/monitor.observer.zero \
	$(find monitor.observer.zero -name '*.java')
cp -r monitor.observer.zero/src/main/resources/META-INF classes/monitor.observer.zero
$JAR_EXE --create \
	--file mods/monitor.observer.zero.jar \
	-C classes/monitor.observer.zero .
