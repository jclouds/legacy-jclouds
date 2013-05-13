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
