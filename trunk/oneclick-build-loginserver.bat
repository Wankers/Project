@ECHO ############################################
@ECHO ########## Building Loginserver   ##########
@ECHO ########## Execute from ./trunk   ##########
@ECHO ############################################

@cd loginserver
@call mvn clean install
@copy build\ae-loginserver.zip ..\zips
@cd ..

@ECHO ############################################
@ECHO ################# Completed ################
@ECHO ############################################

@PAUSE