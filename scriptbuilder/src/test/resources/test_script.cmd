@echo off
set PATH=
set JAVA_HOME=
set PATH=
GOTO FUNCTION_END
:abort
   echo aborting: %EXCEPTION%
   exit /b 1
:default
   set RUNTIME=Moo
   exit /b 0
:FUNCTION_END
set PATH=c:\windows\;C:\windows\system32;c:\windows\system32\wbem
goto CASE%1
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
   echo status ... the following should be empty, as we haven't sourced the variable"%RUNTIME%"
   GOTO END_SWITCH
:END_SWITCH
exit /b 0
