
<div class="container">
<img src="https://orig00.deviantart.net/1793/f/2018/033/f/7/coreengine_logo_by_pokemutt-dc202tg.png" width=700px />
  </div>
# CoreEngine <br>
A tiny java game engine. This was made specifically for a couple of projects of mine to facilitate my understanding
of how game engines work. <br>

# <h2>Overview</h2>
<ul>
  <li>State based</li>
  <li>2D game engine features</li>
  <li>No dependencies</li>
  <li>Small and simple</li>
  <li>Clean and easy state management</li>
  <li>Not much implemented yet as of now, it is very basic at its current state.</li>
</ul>

# <h2>Building with CoreEngine</h2>
To set up CoreEngine, you must use the GameBox class that is incharge of everything related to the engine itself.
The first line of code that should be at the start of your app would be:
```
GameBox.InitGame(NAMEOFYOURGAME, widthOfScreen, heightOfScreen);
```

Next, since CoreEngine is statebased, you must register which classes will be serving as your game states. A game state is basically
a screen in the game, examples of this is "the main menu", or "Options screen", or "GameWorld". To register game states, you 
must first have a class inheriting from:

```
public abstract class GameState implements InputListener{
...
}
```

An example class would be the following. To keep my gamestate id's organized, I have an enumerator named GameStates
that keep track of all of my id's. 

```
public class WorldState extends GameState{

  @Override
  public void Render(Graphics2D g) {
    g.setColor(Color.WHITE);
    g.drawString("Hello World!", 10, 10);
  }

  @Override
  public Enum<?> getStateID() {
    return GameStates.PLAY;
  }
}
```

To register our gamestate, you must use the StateHandler given by our GameBox.
The statehandler keeps track of what states are going to be displayed/updated, among another set of things.
The following registers a new gamestate. Additionally, you can tell the statemanager that we are going to
enter into this particular gamestate:

```
GameBox.registerState(new WorldState());
GameBox.enterState(GameStates.PLAY);
```

Finally, you call the following method to start the gameloop and show the game window to the player:
```
GameBox.StartGame();
```

To summarize everything, an usual setup of CoreEngine would be the following:
```
import me.zmsky.core.GameBox;
import me.zmsky.rpg.gamestates.GameStates;
import me.zmsky.rpg.gamestates.WorldState;

public class Main {	
  public static void main(String[] args){
    GameBox.InitGame("Game",800,600);
    
    //This is a gamestate. In this example, this state is the main menu for the game.
    GameBox.getStateHandler().registerState(new MainMenu());
    //For commodity, I have all gamestate id's in a enum to easily change
    //between them.
    GameBox.getStateHandler().enterState(GameStates.PLAY);

    //After setting everything up, we start the game.
    GameBox.StartGame();
  }
}
```

