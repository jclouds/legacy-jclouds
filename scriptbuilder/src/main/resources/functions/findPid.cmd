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
