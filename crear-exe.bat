@echo off
echo ========================================
echo  Generador de instalador para Prioriza
echo ========================================
echo.

echo [1/4] Limpiando compilaciones anteriores...
call mvn clean
if %errorlevel% neq 0 (
    echo ERROR: Fallo al limpiar el proyecto.
    pause
    exit /b %errorlevel%
)

echo.
echo [2/4] Empaquetando el JAR...
call mvn package
if %errorlevel% neq 0 (
    echo ERROR: Fallo al empaquetar el JAR.
    pause
    exit /b %errorlevel%
)

echo.
echo [3/4] Generando el instalador EXE con jpackage...

:: Busca automáticamente el JAR generado (toma el primero que encuentre)
for /f "delims=" %%i in ('dir target\*.jar /b /od') do set JAR_NAME=%%i
if "%JAR_NAME%"=="" (
    echo ERROR: No se encontró ningún archivo JAR en target\
    pause
    exit /b 1
)
echo Usando JAR: %JAR_NAME%

jpackage --type exe ^
         --input target\ ^
         --main-jar %JAR_NAME% ^
         --main-class com.prioriza.MainApp ^
         --name "Prioriza" ^
         --icon src\main\resources\img\prioriza-icono.ico ^
         --app-version "1.0" ^
         --vendor "RicardoHernandez" ^
         --dest target\installer ^
         --win-shortcut ^
         --win-menu

if %errorlevel% neq 0 (
    echo ERROR: Fallo al generar el instalador con jpackage.
    pause
    exit /b %errorlevel%
)

echo.
echo ========================================
echo   Proceso completado con éxito
echo ========================================
echo Busca tu instalador en: target\installer\Prioriza.exe
echo.
pause