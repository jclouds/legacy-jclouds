@echo off

goto END_FUNCTIONS
:ABORT_SUB
    echo Aborting:  %EXCEPTION%.
    exit /b 1
    
:SOURCE_ENV
    set ENV_FILE=%1
    shift
    if not defined ENV_FILE (
        set EXCEPTION=Internal error.  Called SOURCE_ENV with no file param
        exit /b 1
    )
    call %SETTINGS_FILE%
    if errorlevel 1 (
        set EXCEPTION=Please end your '%SETTINGS_FILE%' file with the command 'exit /b 0' to enable this script to detect syntax errors.
        exit /b 1
    )
    exit /b 0

:END_FUNCTIONS

if exist "%APPENV_SETTINGS_FILE%" (
    call :SOURCE_SF "%APPENV_SETTINGS_FILE%"
    if errorlevel 1 goto ABORT_SUB
)
