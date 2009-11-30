@echo off
set PATH=
set JAVA_HOME=
set PATH=
GOTO FUNCTION_END
:abort
   echo aborting: %EXCEPTION%
   exit /b 1
:default
   set JAVA_HOME=/apps/jdk1.6
   exit /b 0
:FUNCTION_END
set PATH=c:\windows\;C:\windows\system32
goto CASE%1
:CASE_start
   echo started
   GOTO END_SWITCH
:CASE_stop
   echo stopped
   GOTO END_SWITCH
:END_SWITCH
