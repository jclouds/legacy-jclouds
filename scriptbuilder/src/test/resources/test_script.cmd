@echo off
set PATH=
set JAVA_HOME=
set PATH=
set RUNTIME=
GOTO FUNCTION_END
:abort
   echo aborting: %EXCEPTION%
   exit /b 1
:default
   set RUNTIME=Moo
   exit /b 0
:FUNCTION_END
set PATH=c:\windows\;C:\windows\system32;c:\windows\system32\wbem
if not "%1" == "start" if not "%1" == "stop" if not "%1" == "status" (
   set EXCEPTION=bad argument: %1 not in start stop status
   goto abort
)
goto CASE_%1
:CASE_start
   call :default
   if errorlevel 1 goto abort
   echo start %RUNTIME%
   GOTO END_SWITCH
:CASE_stop
   call :default
   if errorlevel 1 goto abort
   echo stop %RUNTIME%
   GOTO END_SWITCH
:CASE_status
   echo hello world >>%TEMP%\%USERNAME%\scripttest\temp.txt
   echo the following should be []: [%RUNTIME%]
   GOTO END_SWITCH
:END_SWITCH
exit /b 0
