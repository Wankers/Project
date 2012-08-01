@call unzip *.zip

@cd _local_configs
@call xcopy /y /e *.* ..
@cd ..

@cd chatserver
@start StartCS.bat
@cd ..

@cd loginserver
@start StartLS.bat
@cd ..

@cd gameserver
@start StartGS.bat
@cd ..

