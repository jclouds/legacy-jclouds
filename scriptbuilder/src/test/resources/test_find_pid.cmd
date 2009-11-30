@echo off
set PATH=
set JAVA_HOME=
set PATH=
GOTO FUNCTION_END
:abort
   echo aborting: %EXCEPTION%
   exit 1
:findPid
   set FOUND_PID=
   SETLOCAL
   set _pid=
   set _expression=%1
   shift
   set FIND_PROCESS=wmic process where (CommandLine like "%_expression%%%") get ProcessId
   for /f "usebackq skip=1" %%a in (`cmd /c "%FIND_PROCESS% 2>NUL"`) do (
      if not defined _proc (
         set _pid=%%a
         goto :done
      )
   )
   :done
   ENDLOCAL&SET FOUND_PID=%_pid%
   exit /b 0
:FUNCTION_END
set PATH=c:\windows\;C:\windows\system32
call :findPid "%*"
if errorlevel 1 goto abort
echo %FOUND_PID%
exit 0
