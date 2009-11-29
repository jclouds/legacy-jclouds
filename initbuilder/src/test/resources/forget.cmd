@echo off
:: Nohup-like utility for windows written with zero dependencies on non-standard code.
::
:: Usage forget process_name fullpath arguments
::
:: Ex. forget tomcat c:\apps\tomcat start
::
:: Uses the schtasks command to launch whatever the command is in the next minute. 
:: If the process is already running, it is shutdown first.
::
:: Author Adrian Cole
::
GOTO FUNCTION_END

:ABORT
   echo aborting: %_result%
   exit /b 1

:findProcess
   SETLOCAL
   set _proc=
   set _pid=
   set _name=%1
   shift
   set FIND_PROCESS=wmic process where (name="cmd.exe" and CommandLine like "cmd /c title %_name%%%") get ProcessId
   for /f "usebackq skip=1" %%a in (`cmd /c "%FIND_PROCESS% 2>NUL"`) do (
      if not defined _proc (
         set _proc=%%a
         goto :done
      )
   )
   :done
   ENDLOCAL&SET _pid=%_proc%
   exit /b 0

:nextMinute
   SETLOCAL
   set HOURS=%TIME:~0,2%
   set MINUTES=%TIME:~3,2%

   set /a HOURS=%HOURS%
   set /a MINUTES+=1

   if %MINUTES% EQU 60 (set MINUTES=0&set /a HOURS+=1)
   if %HOURS% EQU 24 (set HOURS=0)

   if %HOURS% LSS 10 set HOURS=0%HOURS%
   if %MINUTES% LSS 10 set MINUTES=0%MINUTES%

   ENDLOCAL&SET _nextMinute=%HOURS%:%MINUTES%:00
   exit /b 0

:FUNCTION_END

SETLOCAL
set PID_TO_KILL=
set NEXT_MINUTE=
set NAME=%1
shift

CALL :findProcess %NAME%
if defined _pid (
   echo stopping %NAME%
   TASKKILL /F /T /PID %_pid% >NUL
)
schtasks /end /tn %NAME% >NUL 2>NUL
schtasks /delete /tn %NAME% /F >NUL 2>NUL

CALL :nextMinute
set NEXT_MINUTE=%_nextMinute%
set _DATE=%DATE:~4%
set CMD=schtasks /create /sd %_DATE% /tn %NAME% /ru System  /tr  "cmd /c title %NAME%&%1 %2 %3 %4 %5 %6 %7 %8 >c:\stdout.log 2>c:\stderr.log" /sc:once /st %NEXT_MINUTE%
echo %NAME% will start at %NEXT_MINUTE%
%CMD% >NUL
goto :eof
