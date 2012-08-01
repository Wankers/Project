@ECHO ############################################
@ECHO ########## Building all files ... ##########
@ECHO ########## Execute from ./trunk   ##########
@ECHO ############################################


@cd chatserver
@call mvn clean install
@copy build\ae-chatserver.zip  ..\zips
@cd ..

@cd gameserver
@call mvn clean install
@copy build\ae-gameserver.zip  ..\zips
@cd ..

@cd loginserver
@call mvn clean install
@copy build\ae-loginserver.zip  ..\zips
@cd ..

@ECHO ############################################
@ECHO ################# Completed ################
@ECHO ############################################

@PAUSE