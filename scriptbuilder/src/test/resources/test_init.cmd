@echo off
set PATH=
set JAVA_HOME=
set PATH=
GOTO FUNCTION_END
:abort
   echo aborting: %EXCEPTION%
   exit /b 1
:default
   set INSTANCE_NAME=mkebsboot
set INSTANCE_HOME=/mnt/tmp
set LOG_DIR=/mnt/tmp
   exit /b 0
:mkebsboot
   set TMP_DIR=/mnt/tmp
   exit /b 0
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
:forget
   SETLOCAL
   set FOUND_PID=
   set NEXT_MINUTE=
   set INSTANCE_NAME=%1
   shift
   set SCRIPT=%1
   shift
   set LOG_DIR=%1
   shift
   CALL :findProcess %INSTANCE_NAME%
   if defined FOUND_PID (
      echo %INSTANCE_NAME% already running pid [%FOUND_PID%]
   ) else (
      CALL :nextMinute
      set _DATE=%DATE:~4%
      set CMD=schtasks /create /sd %_DATE% /tn %INSTANCE_NAME% /ru System /tr "cmd /c title %INSTANCE_NAME%&%SCRIPT% >%LOG_DIR%\stdout.log 2>%LOG_DIR%\stderr.log" /sc:once /st %NEXT_MINUTE%
      echo %INSTANCE_NAME% will start at %NEXT_MINUTE%
      set SECONDS=%TIME:~6,2%
      set /a SECOND=60-SECONDS
      %CMD% >NUL
      ping -n %SECONDS% 127.0.0.1 > NUL 2>&1
      CALL :findProcess %INSTANCE_NAME%
      if not defined FOUND_PID (
         set EXCEPTION=%INSTANCE_NAME% did not start
         abort
      )
   ) 
   exit /b 0
:FUNCTION_END
set PATH=c:\windows\;C:\windows\system32;c:\windows\system32\wbem
if not "%1" == "init" if not "%1" == "status" if not "%1" == "stop" if not "%1" == "start" if not "%1" == "tail" if not "%1" == "tailerr" if not "%1" == "run" (
   set EXCEPTION=bad argument: %1 not in init status stop start tail tailerr run
   goto abort
)
goto CASE_%1
:CASE_init
   call :default
   if errorlevel 1 goto abort
   call :mkebsboot
   if errorlevel 1 goto abort
   md %INSTANCE_HOME%
   del %INSTANCE_HOME%\mkebsboot.cmd 2>NUL
   echo @echo off>>%INSTANCE_HOME%\mkebsboot.cmd
   echo title mkebsboot>>%INSTANCE_HOME%\mkebsboot.cmd
   echo set PATH=c:\windows\;C:\windows\system32;c:\windows\system32\wbem>>%INSTANCE_HOME%\mkebsboot.cmd
   echo set INSTANCE_NAME=mkebsboot>>%INSTANCE_HOME%\mkebsboot.cmd
   echo set TMP_DIR=%TMP_DIR%>>%INSTANCE_HOME%\mkebsboot.cmd
   echo set INSTANCE_NAME=%INSTANCE_NAME%>>%INSTANCE_HOME%\mkebsboot.cmd
   echo set INSTANCE_HOME=%INSTANCE_HOME%>>%INSTANCE_HOME%\mkebsboot.cmd
   echo set LOG_DIR=%LOG_DIR%>>%INSTANCE_HOME%\mkebsboot.cmd
   echo cd /d %%INSTANCE_HOME%%>>%INSTANCE_HOME%\mkebsboot.cmd
   echo find />>%INSTANCE_HOME%\mkebsboot.cmd
   echo exit /b 0 >>%INSTANCE_HOME%\mkebsboot.cmd
   GOTO END_SWITCH
:CASE_status
   call :default
   if errorlevel 1 goto abort
   call :findPid %INSTANCE_NAME%
   if errorlevel 1 goto abort
   echo [%FOUND_PID%]
   GOTO END_SWITCH
:CASE_stop
   call :default
   if errorlevel 1 goto abort
   call :findPid %INSTANCE_NAME%
   if errorlevel 1 goto abort
   if defined FOUND_PID (
      TASKKILL /F /T /PID %FOUND_PID% >NUL
   )
   GOTO END_SWITCH
:CASE_start
   call :default
   if errorlevel 1 goto abort
   call :forget %INSTANCE_NAME% %INSTANCE_HOME%\%INSTANCE_NAME%.cmd %LOG_DIR%
   if errorlevel 1 goto abort
   GOTO END_SWITCH
:CASE_tail
   call :default
   if errorlevel 1 goto abort
   tail %LOG_DIR%\stdout.logGOTO END_SWITCH
:CASE_tailerr
   call :default
   if errorlevel 1 goto abort
   tail %LOG_DIR%\stderr.logGOTO END_SWITCH
:CASE_run
   call :default
   if errorlevel 1 goto abort
   %INSTANCE_HOME%\%INSTANCE_NAME%.cmdGOTO END_SWITCH
:END_SWITCH
exit /b 0
