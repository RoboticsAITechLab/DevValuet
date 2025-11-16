@echo off
echo Starting DevVault Pro X Application...

REM Set the main class
set MAIN_CLASS=com.devvault.frontend.DevVaultProXApplication

REM Set classpath to include all compiled classes and dependencies
set CLASSPATH=target\classes
for %%i in (target\libs\*.jar) do set CLASSPATH=!CLASSPATH!;%%i

REM Run the application
java -Djava.awt.headless=false ^
     -Dfile.encoding=UTF-8 ^
     --add-opens java.base/java.lang=ALL-UNNAMED ^
     --add-opens java.base/java.util=ALL-UNNAMED ^
     --add-opens javafx.base/com.sun.javafx.runtime=ALL-UNNAMED ^
     --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED ^
     --add-opens javafx.fxml/javafx.fxml=ALL-UNNAMED ^
     -cp "%CLASSPATH%" ^
     %MAIN_CLASS%

pause