@echo off
set PATH=
set JAVA_HOME=
set PATH=
set RUNTIME=
GOTO FUNCTION_END
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
:abort
   echo aborting: %EXCEPTION%
   exit /b 1
:default
   set RUNTIME=Moo
   exit /b 0
:FUNCTION_END
set PATH=c:\windows\;C:\windows\system32;c:\windows\system32\wbem
if not "%1" == "start" if not "%1" == "stop" if not "%1" == "status" (
   set EXCEPTION=bad argument: %1 not in start stop status
   goto abort
)
goto CASE_%1
:CASE_start
   call :default
   if errorlevel 1 goto abort
   echo start %RUNTIME%
   GOTO END_SWITCH
:CASE_stop
   call :default
   if errorlevel 1 goto abort
   echo stop %RUNTIME%
   GOTO END_SWITCH
:CASE_status
   echo hello world >>%TEMP%\%USERNAME%\scripttest\temp.txt
   echo the following should be []: [%RUNTIME%]
   GOTO END_SWITCH
:END_SWITCH
exit /b 0
