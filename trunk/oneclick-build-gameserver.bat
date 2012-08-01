@ECHO ############################################
@ECHO ########## Building Gameserver   ##########
@ECHO ########## Execute from ./trunk   ##########
@ECHO ############################################

@cd gameserver
@call mvn clean install
@copy build\ae-gameserver.zip ..\zips
@cd ..

@ECHO ############################################
@ECHO ################# Completed ################
@ECHO ############################################

@PAUSE