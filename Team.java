import java.awt.*;

public class Team
  {
  public Team(World parent, int side)
    {
    int counter;
    int PlayerX = 0;  //  This is fixed later, but the compiler doesn't realize it and claims it may be uninitialized
    int PlayerY = 13;
    Player current;
    String ThreadName = new String();
    
    //  Let's hang onto our creation info:
    Parent = parent;
    Side = side;

    //  We need to do some side specific initialization
    if(Side==World.EAST)
      {
      ThreadName = "EAST_TEAM_";
      PlayerX = 50;
      }
    else if(Side==World.WEST)
      {
      ThreadName = "WEST_TEAM_";
      PlayerX = 30;
      }
    else
      {  //  No, I don't think that this will ever happen, but I've got a cold and can't think right now
      System.out.println("Critical error in Team::Team:");
      System.out.println("  Invalid Side was passed to constructor");
      System.exit(1);
      }
      
    //  Allocate array of pointers (I think - I'm new at this.  (notice I said "pointer"))
    Players = new PlayerWrapper[4];
    
    //  Allocate each player and each wrapper
    for(counter=0; counter<4; counter++)
      {
      /*  An unfortunate result of the dynamic linking scheme that I'm using is that the constructor
          can't take arguements.  I haven't found a way to assign thread groups yet anywhere
          other than the constructor.  C'est la vie.  At least the dynamic linking works...  */
      current = new Player();
      current.SetParent(Parent);
      current.SetID(counter+1);
      //current.setName(ThreadName + (counter+1));
      if(!current.CalledPlayer())  //  This will probably just crash because the boolean is not initialized
        System.out.println("A player on " + ThreadName + " did not call Player::Player");
      else
        {
        current.InitializeGame();
        current.InitializePoint();
        Players[counter] = new PlayerWrapper(current, PlayerX,  PlayerY, ThreadName + (counter+1));
        }
      PlayerY+= 5;
      }
    }  // end of Team::Team
  
  public Team(World parent, int side, Player players[])
    {
    int counter;
    int PlayerX = 0;  //  This is fixed later, but the compiler doesn't realize it and claims it may be uninitialized
    int PlayerY = 13;
    Player current;
    String ThreadName = new String();
    
    //  Let's hang onto our creation info:
    Parent = parent;
    Side = side;
    
    //  We need to do some side specific initialization
    if(Side==World.EAST)
      {
      ThreadName = "EAST_TEAM_";
      PlayerX = 50;
      }
    else if(Side==World.WEST)
      {
      ThreadName = "WEST_TEAM_";
      PlayerX = 30;
      }
    else
      {  //  No, I don't think that this will ever happen, but I've got a cold and can't think right now
      System.out.println("Critical error in Team::Team:");
      System.out.println("  Invalid Side was passed to constructor");
      System.exit(1);
      }
      
    //  Allocate array of pointers (I think - I'm new at this.  (notice I said "pointer"))
    Players = new PlayerWrapper[4];
    
    //  Allocate each wrapper
    for(counter=0; counter<4; counter++)
      {
      /*  An unfortunate result of the dynamic linking scheme that I'm using is that the constructor
          can't take arguements.  I haven't found a way to assign thread groups yet anywhere
          other than the constructor.  C'est la vie.  At least it works...  */
      players[counter].SetParent(Parent);
      players[counter].SetID(counter+1);
      //players[counter].setName(ThreadName + (counter+1));
      if(!players[counter].CalledPlayer())  //  This will probably just crash because the boolean is not initialized
        System.out.println("A player on " + ThreadName + " did not call Player::Player");
      else
        {
        Players[counter] = new PlayerWrapper(players[counter], PlayerX,  PlayerY, ThreadName + (counter+1));
        players[counter].InitializeGame();
        players[counter].InitializePoint();
        }
      PlayerY+= 5;
      }
    }  // end of Team::Team (2nd version)

  /**  Places the teams back in their starting positions.  Does not affect their threads at all. */
  public void ResetTeam()
    {
    int counter;
    int PlayerX = 0;
    int PlayerY;

    if(Side==World.EAST)
      PlayerX = 50;
    else if(Side==World.WEST)
      PlayerX = 30;

    PlayerY = 13;
    for(counter=0; counter<4; counter++)
      {
      Players[counter].X = PlayerX;
      Players[counter].Y = PlayerY;
      PlayerY+= 5;
      }
    }  //  End of Team::ResetTeams
  
  /**  Returns true if the specified player is a member of the team.  */    
  public boolean IsAMember(Player player)
    {
    //  I've been told that it's poor style to return from a function other than at its end.
    //  I think it makes things easier to follow, but it's also a pain in the butt for early
    //  termination of searches and I'm on vacation right now.
    int counter;
    for(counter=0; counter<4; counter++)
      if(Players[counter].Client==player)
        return true;
     return false;
    }

  public Point GetPlayerLocation(Player client)
    {
    int counter;
    for(counter=0; counter<4; counter++)
      if(Players[counter].Client==client)
        return new Point(Players[counter].X, Players[counter].Y);
    return null;
    }
 
  public Point GetPlayerLocation(int ID)
    {
    if(ID>0 && ID<5)
      return new Point(Players[ID-1].X, Players[ID-1].Y);
    return null;
    }   
  
  Player GetPlayer(int Number)
    {
    if(Number>0 && Number<5)
      return Players[Number-1].Client;
    else
      return null;
    }
  
  /**  Returns a number from 1 to 4 identifying the player at a given position.  It returns 0
        if there's noone there.  */
  public int IsPlayerAt(Point target)
    {
    int counter;
    for(counter=0; counter<4; counter++)
      if(Players[counter].X==target.x && Players[counter].Y==target.y)
        return counter+1;
    return 0;
    }

  public void TranslatePlayer(Player target, Point Delta)
    {
    int counter;
    for(counter=0; counter<4; counter++)
      {
      if(Players[counter].Client==target)
        {
        Players[counter].X += Delta.x;
	if (Players[counter].X < 0) Players[counter].X = 0;
	if (Players[counter].X >= Parent.XSQUARES) Players[counter].X = Parent.XSQUARES-1; 
        Players[counter].Y += Delta.y;
	if (Players[counter].Y < 0) Players[counter].Y = 0;
	if (Players[counter].Y >= Parent.YSQUARES) Players[counter].Y = Parent.YSQUARES-1; 
        break;
        }
      }
    }
  
  public int GetSide()
    {
    return Side;
    }          
  
  public void InitializePoint()
    {
    int counter;
    for(counter=0; counter<4; counter++)
      Players[counter].Client.InitializePoint();
    }

  public void WonPoint()
    {
    int counter;
    for(counter=0; counter<4; counter++)
      Players[counter].Client.WonPoint();
    }

  public void LostPoint()
    {
    int counter;
    for(counter=0; counter<4; counter++)
      Players[counter].Client.LostPoint();
    }

  public void GameOver()
    {
    int counter;
    for(counter=0; counter<4; counter++)
      Players[counter].Client.GameOver();
    }
  
  private PlayerWrapper Players[];
	private int Side;
  private World Parent;
  } 
  
/**  Players should not be able to access their own X and Y coordinates or other
      similar data.  This class provides storage for that information in a place where they
      can't access it.  On a similar note, this class shouldn't be accessable from anywhere
      else.  */
      
class PlayerWrapper
  {
  protected PlayerWrapper(Player client, int x, int y, String threadname)
    {
    Client = client;
    ThreadName = new String(threadname);
    X = x;
    Y = y;
    }
    
  protected Player Client;
  protected int X;
  protected int Y;
  protected String ThreadName;
  }







