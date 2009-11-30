:validateEnvFile
   set ENV_FILE=%1
   shift
   if not defined ENV_FILE (
      set EXCEPTION=validateEnvFile requires a parameter of the file to source
      exit /b 1
   )
   if not exist "%ENV_FILE%" (
      set EXCEPTION=env file '%ENV_FILE%' does not exist
      exit /b 1
   )
   exit /b 0
