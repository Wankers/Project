@ECHO ############################################
@ECHO ########## Cleaning all files ... ##########
@ECHO ########## Execute from ./trunk/  ##########
@ECHO ############################################

@cd chatserver
@call rmdir /S /Q bin
@cd ..

@cd loginserver
@call rmdir /S /Q bin

@cd ..

@cd gameserver
@call rmdir /S /Q bin

@cd ..

@cd commons
@call rmdir /S /Q bin

@ECHO ############################################
@ECHO ################# Completed ################
@ECHO ############################################

@PAUSE