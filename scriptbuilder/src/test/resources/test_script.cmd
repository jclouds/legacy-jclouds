@echo off
set PATH=
set JAVA_HOME=
set PATH=
set PATH=c:\windows\;C:\windows\system32
set JAVA_HOME=/apps/jdk1.6
goto CASE%1
:CASE_start
   echo started
   GOTO END_SWITCH
:CASE_stop
   echo stopped
   GOTO END_SWITCH
:END_SWITCH
