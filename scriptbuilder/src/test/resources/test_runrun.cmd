md %TEMP%\%USERNAME%\scripttest
del %TEMP%\%USERNAME%\scripttest\yahooprod.cmd 2>NUL
echo @echo off>>%TEMP%\%USERNAME%\scripttest\yahooprod.cmd
echo title yahooprod>>%TEMP%\%USERNAME%\scripttest\yahooprod.cmd
echo set INSTANCE_NAME=yahooprod>>%TEMP%\%USERNAME%\scripttest\yahooprod.cmd
echo set JAVA_HOME=%JAVA_HOME%>>%TEMP%\%USERNAME%\scripttest\yahooprod.cmd
echo cd /d %TEMP%\%USERNAME%\scripttest>>%TEMP%\%USERNAME%\scripttest\yahooprod.cmd
echo echo hello>>%TEMP%\%USERNAME%\scripttest\yahooprod.cmd
echo echo %%JAVA_HOME%%\bin\java -DinstanceName=%%INSTANCE_NAME%% myServer.Main>>%TEMP%\%USERNAME%\scripttest\yahooprod.cmd
echo exit /b 0 >>%TEMP%\%USERNAME%\scripttest\yahooprod.cmd
