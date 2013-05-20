@echo off
set PATH=
set JAVA_HOME=
set PATH=
GOTO FUNCTION_END
:abort
   echo aborting: %EXCEPTION%
   exit /b 1
:findPid
   set FOUND_PID=
   set _expression=%1
   shift
   set FIND_PROCESS=TASKLIST /FI "WINDOWTITLE eq %_expression%" /NH
   FOR /F "usebackq tokens=2 delims= " %%A IN (`cmd /c "%FIND_PROCESS% 2>NUL"`) DO (
      SET FOUND_PID=%%A
   )
   if defined FOUND_PID (
      exit /b 0
   ) else (
      set EXCEPTION=%_expression% not found
      exit /b 1
   )
:FUNCTION_END
set PATH=c:\windows\;C:\windows\system32;c:\windows\system32\wbem
call :findPid %*
if errorlevel 1 goto abort
if defined FOUND_PID (
   TASKKILL /F /T /PID %FOUND_PID% >NUL
)
exit /b 0
