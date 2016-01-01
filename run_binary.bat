@echo off
title FrostBite 3 Tools Console
mode con: cols=130 lines=20> nul
echo DO NOT CLOSE THIS WINDOW, ITS REQUIRED!
echo JavaFX does only works on Java 8+
java -Xmx2048m -jar binary_build.jar
echo Press any key to EXIT!
pause >nul