@echo off

set CAFFEMARKDOWN_HOME=%~d0%~p0\..
set _CLASSPATH_=%CAFFEMARKDOWN_HOME%\lib\scala-library.jar;%CAFFEMARKDOWN_HOME%\lib\caffe-markdown.jar

java -classpath "%_CLASSPATH_%" "-Dstyles=%CAFFEMARKDOWN_HOME%" jp.cappuccino.tools.markdown.Main %*
