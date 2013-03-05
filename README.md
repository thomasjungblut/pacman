pacman
======

Simple 2D pacman in Java to test several AI things (search/planning/q-learning/machine learning). 
Note that my maze generation is a bit exotic, so it doesn't look like a real pacman ;-)

Build
-----

It requires all my three core libraries:

[tjungblut-math](https://github.com/thomasjungblut/tjungblut-math "Thomas' nifty math lib")

[tjungblut-common](https://github.com/thomasjungblut/thomasjungblut-common "Thomas' nifty common lib")

[tjungblut-graph](https://github.com/thomasjungblut/tjungblut-graph "Thomas' nifty graph lib")

and Java7 to compile.

Current state (05.03.2013) is a fully functional pacman game engine with approximate q-learning.
You can see some early stages on my youtube channel here:

[Youtube Video of Pacman QLearning](http://www.youtube.com/watch?v=Byr-tgcKTYU "Youtube Video of Pacman QLearning")


State (02.03.2013) looks like this:

![Pacman 2][pcm2]

Added food and simple game mechanics like collisions with the ghosts and food exhaustion. 
Next milestone will add a q-learning pacman using a neural network.

State (21.12.2012) looks like this:

![Pacman 1][pcm1]

Basic human pacman movement works quite good. The red ghost is chasing the pacman's movement (THE STALKER!), the other guy is just running arround randomly.

[pcm1]: http://img16.imageshack.us/img16/2850/parkman.png "Pacman 1"
[pcm2]: http://img585.imageshack.us/img585/6615/pacman2s.png "Pacman 2"
