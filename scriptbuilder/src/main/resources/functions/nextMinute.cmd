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
