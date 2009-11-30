@echo off

goto END_FUNCTIONS
:abortFunction
    echo Aborting:  %EXCEPTION%.
    exit /b 1
    
:sourceEnv
    set ENV_FILE=%1
    shift
    if not defined ENV_FILE (
        set EXCEPTION=Internal error.  Called sourceEnv with no file param
        exit /b 1
    )
    call %ENV_FILE%
    if errorlevel 1 (
        set EXCEPTION=Please end your '%ENV_FILE%' file with the command 'exit /b 0' to enable this script to detect syntax errors.
        exit /b 1
    )
    exit /b 0

:END_FUNCTIONS

if exist "%APPENV_SETTINGS_FILE%" (
    call :sourceEnv "%APPENV_SETTINGS_FILE%"
    if errorlevel 1 goto abortFunction
)
