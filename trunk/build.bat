@ECHO off
SET MODE=clean package
SET TITLE=Build
TITLE Aion Extreme 3.0 - %TITLE% Panel
:MENU
CLS
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                    Aion Extreme 3.0 Project - %TITLE% Panel                  ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Build Login server                           6 - Build mode       ^|
ECHO   ^|    2 - Build Game server                            7 - Install mode     ^|
ECHO   ^|    3 - Build Chat server                            8 - Debug mode       ^|
ECHO   ^|    4 - Build Commons                                9 - Quit             ^|
ECHO   ^|    5 - Build All                                                         ^|
ECHO   ^|                                                                          ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO    For normal and clean compilation, use first  7 - Install mode 
ECHO    and then choice your Server ! Example: 7 and then 2 for Gameserver.
ECHO.
:ENTER
SET /P OPTION=Type your option and press ENTER: 
IF %OPTION% == 1 (
CLS
TITLE Aion Extreme 3.0 - %TITLE%ing Login server
CD loginserver
call mvn %MODE%
pause
)
IF %OPTION% == 2 (
CLS
TITLE Aion Extreme 3.0 - %TITLE%ing Game Server
CD gameserver
call mvn %MODE%
pause
)
IF %OPTION% == 3 (
CLS
TITLE Aion Extreme 3.0 - %TITLE%ing Chat Server
CD chatserver
call mvn %MODE%
pause
)
IF %OPTION% == 4 (
CLS
TITLE Aion Extreme 3.0 - %TITLE%ing Commons Server
call mvn install:install-file -Dinternal -Dfile=commons/libs/javac-1.6.jar -DgroupId=local.com.sun.tools -DartifactId=javac -Dversion=1.6 -Dpackaging=jar
CD commons
call mvn %MODE%
pause
)
IF %OPTION% == 5 (
CLS
TITLE Aion Extreme 3.0 - %TITLE%ing All Project
CD loginserver
call mvn %MODE%
CD ../gameserver
call mvn %MODE%
CD ../chatserver
call mvn %MODE%
CD ../commons
call mvn %MODE%
pause
)
IF %OPTION% == 6 (
GOTO :MENU
)
IF %OPTION% == 7 (
CLS
SET MODE=clean install
SET TITLE=Install
TITLE Aion Extreme 3.0 - Install Panel
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                    Aion Extreme 3.0 Project - Install Panel                ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Install Login server                        6 - Build mode        ^|
ECHO   ^|    2 - Install Game server                         7 - Install mode      ^|
ECHO   ^|    3 - Install Chat server                         8 - Debug mode        ^|
ECHO   ^|    4 - Install Commons                             9 - Quit              ^|
ECHO   ^|    5 - Install All                                                       ^|
ECHO   ^|                                                                          ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
GOTO :ENTER
)
IF %OPTION% == 8 (
CLS
SET MODE=clean package -e -X
SET TITLE=Debug
TITLE Aion Extreme 3.0 - Debug Panel
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                    Aion Extreme 3.0 Project - Debug Panel                  ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Debug Login server                          6 - Build mode        ^|
ECHO   ^|    2 - Debug Game server                           7 - Install mode      ^|
ECHO   ^|    3 - Debug Chat server                           8 - Debug mode        ^|
ECHO   ^|    4 - Debug Commons                               9 - Quit              ^|
ECHO   ^|    5 - Debug All                                                         ^|
ECHO   ^|                                                                          ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
GOTO :ENTER
)
IF %OPTION% == 9 (
EXIT
)
IF %OPTION% GEQ 10 (
GOTO :MENU
)
