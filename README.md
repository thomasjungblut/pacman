pacman
======

Simple 2D pacman in Java to test several AI things (search/planning/q-learning/machine learning). 
Note that my maze generation is a bit exotic, so it doesn't look like a real pacman ;-)


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
