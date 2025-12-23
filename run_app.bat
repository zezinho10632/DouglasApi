@echo off
set "JAVA_HOME=D:\Java\jdk-17"
echo Iniciando a aplicacao Douglas API...
echo Usando JDK em: %JAVA_HOME%
call gradlew.bat bootRun
pause
