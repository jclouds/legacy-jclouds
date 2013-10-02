REM
REM Licensed to the Apache Software Foundation (ASF) under one or more
REM contributor license agreements.  See the NOTICE file distributed with
REM this work for additional information regarding copyright ownership.
REM The ASF licenses this file to You under the Apache License, Version 2.0
REM (the "License"); you may not use this file except in compliance with
REM the License.  You may obtain a copy of the License at
REM
REM     http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.
REM
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
