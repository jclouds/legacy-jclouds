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

   ENDLOCAL&SET NEXT_MINUTE=%HOURS%:%MINUTES%:00
   exit /b 0
