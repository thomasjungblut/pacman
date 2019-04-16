Pacman
======

Simple 2D pacman in Java to test several AI things (search/planning/q-learning/machine learning). 
Note that my maze generation is a bit exotic, so it doesn't look like a real pacman ;-)

AI
--

Pacman (or QLearningAgent) learns an optimal pacman game policy over dozens of games and time.
While the smartest ghost will chase Pacman 20% of his turn times by constantly computing the shortest path (using A-Star).

Build
-----

It requires my graph library which needs to be installed independently:

[tjungblut-graph](https://github.com/thomasjungblut/tjungblut-graph "Thomas' nifty graph lib")

Maven and Java8 to compile.

To compile, simply run

> mvn clean install package

This will create a runnable client under the target folder.

Video
-----

[Youtube Video of Pacman QLearning](https://youtu.be/yEjf4vAJUjI "Youtube Video of Pacman QLearning")
