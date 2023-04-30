# Welcome to MazeRunner
1 December 2021
by enseri

## Instructions:
### How To Play:
Levels 1 - 5 are tutorial levels
Map Making:
INFO:
* One click will replace the tile with the tile corresponding with your current Zone
* To move onto a new zone once you are finished all you need to do is press enter
* Goombas/enemies can only go over blank tiles which does not include teleporters
* It is possible to make an impossible map, so make sure to try it out before sending it
* For tiles that don't need to have a second click to be registered you can drag the mouse to cover a larger space
 #### Zone 1: Borders
* When custom map selected you will automatically be set in this zone
* This is the outline of your maze the blocks/tiles the player will not be able to cross, this is essential for a good maze
* Be Careful with this block because it can make your map impossible for a player if misplaced during map creation
 #### Zone 2: Spawns
* This is the location where the player will spawn in your maze, essentially that start location of your map
* This block will only count as a checkpoint until the player sets another checkpoint
 #### Zone 3: Checkpoints
* This will be a new safe zone for the player so if he dies he will be brought back to this position
* If the player lands on this tile for the first time his lives will reset back to 3
 #### Zone 4: Goomba Traps
* One of the enemies in this game, if this enemy every comes in contact with the player the player will be set back
* To his spawn location after the enemy hits the player 3 times the whole map will be reset
* If you wish to change the direction the Goomba will face(upwards by default) click on the same tile and it will rotate -90 degrees
* If you keep clicking on it after 3 clicks after placement it will be deleted
 #### Zone 5: Pop Up Traps
* A trap that only appears every second.
* It also is invisible until it is active
 #### Zone 6: Door
* This is an immovable object, much like the Borders, except when a key is collected once the tile with the key
* corresponding with the door has been covered the door will open and the path will now be cleared
* When making the map click once for the first door and then the second click after will be the key linked to the door
 #### Zone 7: Teleporter
* This is a tile that will move the player from the specific tile with the teleporter to the second teleporter linked to it
* This tile can be used an infinite amount of times and the linkage applies both ways
* When making the map click once for the first teleporter and then the second click after will be the second teleporter
 #### Zone 8: Coins
* A collectable for the player that will be reflected in the level completion
 #### Zone 10: Goals
* This is the end zone for your map, once the tile is covered the map will change to the next set level, unless there isn't another level
 #### Zone 11: Begin Playing
## Playing:
* If you are in visual studio just go to the Game.java and run the program
* Follow the prompts in the console
* Once a map is made it will be added to the back of the list of maps, so you can always save your maps after a build
* To move your player just press W|A|S|D or ^|<|v|>
* The tutorial maps (levels 1 - 5) can help you learn all the different types of traps
* If you find any bugs contact my github @enseri
* Have Fun!