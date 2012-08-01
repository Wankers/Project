@ECHO ############################################
@ECHO ########## Cleaning all files ... ##########
@ECHO ########## Execute from ./trunk/  ##########
@ECHO ############################################

@cd chatserver
@call mvn clean
@cd ..

@cd loginserver
@call mvn clean
@cd ..

@cd gameserver
@call mvn clean
cd..

@cd commons
@call mvn clean

@cd ..

@ECHO ############################################
@ECHO ################# Completed ################
@ECHO ############################################

@PAUSE