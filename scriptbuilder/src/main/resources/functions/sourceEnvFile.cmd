:sourceEnvFile
   set ENV_FILE=%1
   shift
   if not defined ENV_FILE (
      set EXCEPTION=sourceEnvFile requires a parameter of the file to source
      exit /b 1
   )
   call %ENV_FILE%
   if errorlevel 1 (
      set EXCEPTION=Please append 'exit /b 0' to the end of '%ENV_FILE%'
      exit /b 1
   )
   exit /b 0
