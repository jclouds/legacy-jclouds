:abort
   CALL :findProcess %INSTANCE_NAME%
   if defined _pid (
      echo stopping %INSTANCE_NAME%
      TASKKILL /F /T /PID %FOUND_PID% >NUL
   )
   schtasks /end /tn %INSTANCE_NAME% >NUL 2>NUL
   schtasks /delete /tn %INSTANCE_NAME% /F >NUL 2>NUL
   exit /b 0
