@echo off
echo "--- COMPILATION & PACKAGING ---"

echo " > creating clean directories"
del /s /q classes
rmdir /s /q classes
mkdir classes
del /s /q mods
rmdir /s /q mods
mkdir mods

echo " > multi-compiling modules"
rem spark is required as an automatic module, so copy it to mods
copy libs\spark-core-*.jar mods\
javac --module-path mods --module-source-path "./*/src/main/java" -d classes --module monitor

rem Since they aren't "required" in any module-info.java file, these two modules
rem won't be captured by compiling the monitor module above. Therefore, they must
rem be built separately.
javac --module-path mods --module-source-path "./*/src/main/java" -d classes --module monitor.observer.alpha
javac --module-path mods --module-source-path "./*/src/main/java" -d classes --module monitor.observer.beta

echo " > packaging modules"
jar --create --file mods/monitor.observer.jar -C classes/monitor.observer .
jar --create --file mods/monitor.observer.alpha.jar -C classes/monitor.observer.alpha .
jar --create --file mods/monitor.observer.beta.jar -C classes/monitor.observer.beta .
jar --create --file mods/monitor.statistics.jar -C classes/monitor.statistics .
jar --create --file mods/monitor.persistence.jar -C classes/monitor.persistence .
jar --create --file mods/monitor.rest.jar -C classes/monitor.rest .
jar --create --file mods/monitor.jar --main-class monitor.Main -C classes/monitor .
