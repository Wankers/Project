#!/bin/sh
#
# description: Starts and stops Open Aion services (Extended Version)
# author: Jake Sims
# author email: athen99s@yahoo.com
# last updated: 05/25/11
# Modified by: Kavinsky, zer0patches
# 08/
#


# Path to main Open Aion folder WITHOUT a trailing slash
RUNPATH="/home/elite_server/server_2.7"


# GameServer Launch Command
GAMEEXEC="java -Xms128m -Xmx8096m -Xbootclasspath/p:./libs/jsr166.jar -cp ./libs/*:ae-gameserver.jar gameserver.GameServer"

# LoginServer Launch Command
LOGINEXEC="java -Xms8m -Xmx32m -Xbootclasspath/p:./libs/jsr166.jar -cp ./libs/*:ae-loginserver.jar loginserver.LoginServer"

# ChatServer Launch Command
CHATEXEC="java -Xms128m -Xmx128m -ea -Xbootclasspath/p:./libs/jsr166.jar -cp ./libs/*:ae-chatserver.jar chatserver.ChatServer"


# DONT NO  EDIT AFTER HERE
GAME_PID=$(ps ux | awk '/gameserver/ && !/awk/ {print $2}')
LOGIN_PID=$(ps ux | awk '/loginserver/ && !/awk/ {print $2}')
CHAT_PID=$(ps ux | awk '/chatserver/ && !/awk/ {print $2}')

start() {
        if [ -z "$LOGIN_PID" ]; then
                echo -n "Starting Open Aion Login Server..."
                cd $RUNPATH/loginserver
                screen -A -m -d -S aion.login $LOGINEXEC
                echo "OK"
        else
                echo "Open Aion Login Server is already started..."
        fi
	
        if [ -z "$GAME_PID" ]; then
                echo -n "Starting Open Aion Game Server..."
                cd $RUNPATH/gameserver
                screen -A -m -d -S aion.game $GAMEEXEC
                echo "OK"
        else
                echo "Open Aion Game Server is already started..."
        fi
		
		if [ -z "$CHAT_PID" ]; then
                echo -n "Starting Open Aion Chat Server..."
                cd $RUNPATH/chatserver
                screen -A -m -d -S aion.chat $CHATEXEC
                echo "OK"
        else
                echo "Open Aion Chat Server is already started..."
        fi

        return 0
}

stop() {
        echo -n "Stoping Open Aion Services..."

        if [ ! -z "$LOGIN_PID" ]; then
                kill $LOGIN_PID
        fi

        if [ ! -z "$GAME_PID" ]; then
                kill $GAME_PID
        fi

        if [ ! -z "$CHAT_PID" ]; then
                kill $CHAT_PID
        fi
		
        echo "OK"

        return 0
}

start_login() {

	if [ -z "$LOGIN_PID" ]; then
                echo -n "Starting Open Aion Login Server..."
                cd $RUNPATH/loginserver
                screen -A -m -d -S aion.login $LOGINEXEC
                echo "OK"
        else
                echo "Open Aion Login Server is already started..."
        fi
	

	return 0

}

start_chat() {

	if [ -z "$CHAT_PID" ]; then
                echo -n "Starting Open Aion Chat Server..."
                cd $RUNPATH/chatserver
                screen -A -m -d -S aion.chat $CHATEXEC
                echo "OK"
        else
                echo "Open Aion Chat Server is already started..."
        fi

	return 0
}

start_game() {

	if [ -z "$GAME_PID" ]; then
                echo -n "Starting Open Aion Game Server..."
                cd $RUNPATH/gameserver
                screen -A -m -d -S aion.game $GAMEEXEC
                echo "OK"
        else
                echo "Open Aion Game Server is already started..."
        fi

	return 0

}

stop_login() {

	if [ ! -z "$LOGIN_PID" ]; then
                kill $LOGIN_PID
		echo "Stoping Aion login server..."
	else
		echo "Aion login server its not started"
        fi
	
	return 0

}

stop_chat() {

	if [ ! -z "$CHAT_PID" ]; then
                kill $CHAT_PID
		echo "Stoping Aion Chat server..."
	else
		echo "Aion Chat server its not started"
        fi
	
	return 0

}
stop_game() {

	if [ ! -z "$GAME_PID" ]; then
                kill $GAME_PID
        echo "Stoping Aion gameserver server..."
	else
		echo "Aion gameserver server its not started"
        fi
	
	

	return 0

}


	

status() {
	echo "[AION]============================"


	if [ ! -z "$LOGIN_PID" ]; then
             echo "[OK] Login Server Running..."
	else
		echo "[FAIL] Login Server is not running"
        fi

	if [ ! -z "$CHAT_PID" ]; then
             echo "[OK] Chat Server Running..."
	else
		echo "[FAIL] Chat Server is not running"
        fi
	
	if [ ! -z "$GAME_PID" ]; then
              echo "[OK] Game Server Running..."
	else
		echo "[FAIL] Game Server is not running"
        fi

	return 0


}

help() {

	echo "[OPEN AION] Help Section ================="
	echo "Author: Jake Sims | modified by: Kavinsky, zer0patches"
	echo "Commands: ====================================="
	echo "help 	= show this help text"
	echo "start 	= Starts all servers"
    echo "stop 	= Stops all servers"
	echo "start-login = Starts LoginServer Only"
	echo "start-chat  = Starts ChatServer Only"
	echo "start-game  = Starts GameServer Only"
	echo "stop-login  = Stop LoginServer Only"
	echo "stop-chat = Stop ChatServer Only"
	echo "stop-game = Stop GameServer Only"
	echo "status = Shows All servers status"

	return 0
}

case "$1" in
        start)
                start

                ;;
        stop)
                stop

                ;;

	start-login)
		start_login

		;;
	
	start-chat)
		start_chat
		;;

	start-game)
		start_game

		;;

	stop-login)
		stop_login

		;;
	stop-chat)
		stop_chat
		;;
	stop-game)
		stop_game
		;;

	help)
		help
		;;
	status)
		status
		;;

        *)
                echo $"Usage: $0 (start|stop|stop-login|stop-chat|stop-game|start-login|start-chat|start-game|status|help)"
                exit 1
esac

exit $?