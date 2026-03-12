@echo off
echo ========================================
echo Generando instalador EXE de PRIORIZA
echo ========================================
echo.

echo Paso 1: Limpiando proyecto...
call mvn clean

echo.
echo Paso 2: Compilando proyecto...
call mvn compile

echo.
echo Paso 3: Generando JAR con dependencias...
call mvn assembly:single

echo.
echo Paso 4: Creando imagen de runtime con jlink...
call mvn javafx:jlink

echo.
echo Paso 5: Generando instalador EXE...
"C:\Users\Usuario\.jdks\openjdk-24.0.1\bin\jpackage.exe" --type exe ^
         --input target/ ^
         --name PRIORIZA ^
         --main-jar prioriza-1.0-SNAPSHOT-jar-with-dependencies.jar ^
         --main-class com.prioriza.MainApp ^
         --runtime-image target/prioriza-runtime ^
         --icon src/main/resources/img/prioriza-icono.ico ^
         --vendor "Ricardo Hernández" ^
         --version 1.0 ^
         --win-shortcut ^
         --win-menu ^
         --win-dir-chooser ^
         --description "Gestor Inteligente de Tareas con Motor Heurístico"

echo.
echo ========================================
echo Proceso completado!
echo Busca PRIORIZA-1.0.exe en la carpeta actual
echo ========================================
pause