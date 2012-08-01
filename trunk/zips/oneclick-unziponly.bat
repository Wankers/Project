@call unzip *.zip

@cd _local_configs
@call xcopy /y /e *.* ..
@cd ..