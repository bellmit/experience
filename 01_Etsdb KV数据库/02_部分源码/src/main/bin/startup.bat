echo off

title Tag Data Server

rem 设置延迟环境变量扩充，即感叹号间的值不会因跳出循环而为空值。
setlocal enabledelayedexpansion

cd..
set APP_HOME=%CD%

rem 再次进入bin目录　保持当前目录不变
rem cd %APP_HOME%/bin

rem java命令
set JAVA=%JAVA_HOME%\bin\java.exe

rem jvm参数
set OPTS=-server -Xms2048M -Xmx2048M -Xss128k -XX:+AggressiveOpts -XX:+UseParallelGC -XX:NewSize=64M

rem 库位置
set LIBPATH=%APP_HOME%\lib

rem 主类
set MAIN=com.excenergy.tagdataserv.Bootstrap

rem set CP=!CP!;%CLASSPATH%;%APP_HOME%\etc;.
set CP=%APP_HOME%\etc

rem 循环加载jar包
for /f %%i in ('dir /b %LIBPATH%\*.jar^|sort') do (
    set CP=!CP!;%LIBPATH%\%%i;
)

echo JAVA: %JAVA%
echo CLASSPATH: %CP%
echo.
%JAVA% %OPTS% -cp %CP% %MAIN%

pause