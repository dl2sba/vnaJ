@echo off

set JAVAC="C:\Program Files\Java\jdk-11.0.1\bin\javac"
set CP=./
set CP=%CP%;./vnaJ.Library.3.4.6.jar
set CP=%CP%;./AppleJavaExtensions.jar
set CP=%CP%;./commons-lang3-3.1.jar
set CP=%CP%;./commons-math3-3.6.1-tools.jar
set CP=%CP%;./commons-math3-3.6.1.jar
set CP=%CP%;./jcommon-1.0.17.jar
set CP=%CP%;./jdom-1.1.2.jar
set CP=%CP%;./jna-4.3.0.jar
set CP=%CP%;./poi-3.7-20101029.jar
set CP=%CP%;./purejavacomm-1.0.0.jar

echo Compiler =%JAVAC%
echo Classpath=%CP%

@echo on

%JAVAC% -classpath "%CP%" LibrarySampleRunner.java