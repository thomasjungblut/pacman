Pacman
======

Simple 2D pacman in Java to test several AI things (search/planning/q-learning/machine learning). 
Note that my maze generation is a bit exotic, so it doesn't look like a real pacman ;-)

Project Goals
-------------

The pacman-client-java project is the project that I built a few years ago to do some AI algorithms. 
This project includes a GUI that can be started and will run an automated pacman game including several bots (2 ghosts, one pacman). 
Of course, there is also a possibility to configure a human player instead of any of those bots. 

pacman-server on the other hand should provide a client agnostic game execution on the server. 
A colleague of mine wanted a game server that can take multiple clients and provide a game that was easily able to play on a smartphone (even over bluetooth!). 

AI
--

Pacman (or QLearningAgent) learns an optimal pacman game policy over dozens of games and time.
While the smartest ghost will chase Pacman 20% of his turn times by constantly computing the shortest path (using A-Star).

Pacman On A Server (POAS) (Work in Progress)
--------------------------------------------

Pacman on the server provides a Thrift based RPC client interface that manages matchmaking, the game session itself and (obviously) the communication to the clients.
For now, it only is able to matchmake, create a pacman game and distribute it to the client. The client will then ping back to the server- once all clients are done, it will start the real game. 

The whole game execution is missing right now, as well as a real integration with a game client. 

Build
-----

It requires all my core libraries:

[tjungblut-math](https://github.com/thomasjungblut/tjungblut-math "Thomas' nifty math lib")

[tjungblut-graph](https://github.com/thomasjungblut/tjungblut-graph "Thomas' nifty graph lib")

Maven and Java8 to compile.

To compile both server and client, you can run

> mvn clean install package

This will create a runnable client and server fat-jar in the respective project subfolders under the target folder.

Video
-----

[Youtube Video of Pacman QLearning](http://www.youtube.com/watch?v=Byr-tgcKTYU "Youtube Video of Pacman QLearning")
