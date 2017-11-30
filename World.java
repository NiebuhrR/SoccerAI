
import java.awt.*;
import java.util.*;

public class World
{
    /**  Default constructor which creates the world, GUI, and other associated classes. */
    public World()
    {
        System.out.println("JavaSoccer 1.0");
        System.out.println("Eric Chown");
        System.out.println("Doug Vail");
        System.out.println();

        //  Allocate and initialize lookup table to convert directions to DX & DY
        DeltaLookupTable = new Point[8];
        DeltaLookupTable[NORTH] = new Point(0, -1);
        DeltaLookupTable[NORTHEAST] = new Point(1,-1);
        DeltaLookupTable[EAST] = new Point(1,0);
        DeltaLookupTable[SOUTHEAST] = new Point(1, 1);
        DeltaLookupTable[SOUTH] = new Point(0, 1);
        DeltaLookupTable[SOUTHWEST] = new Point(-1, 1);
        DeltaLookupTable[WEST] = new Point(-1, 0);
        DeltaLookupTable[NORTHWEST] = new Point(-1, -1);

        SoccerBall = new Ball(this);
        EastTeam = null;
        WestTeam = null;
        BaseRate = (float)1.0;

        //  All of the graphical stuff is encapsilated in GUI.  GUI passes tasks on to the canvas
        //  class or score board, but all requests are funneled through GUI.  Everything else
        //  talks to World which talks to GUI.
        UI = new GUI(this);

        // one tick for each player
        Ticker = new int[8];
        TimeoutTimer = 0;
        GameOn = false;
        Step = false;
        Priority = new int[8];
        Random r = new Random();
        int firstplayer = Math.abs(r.nextInt()) % 8;
        for (int i = 0; i < 8; i++) {
            Ticker[i] = 0;
            Priority[i] = (i + firstplayer) % 8;
        }

        DynLoader = new DynamicClassLoader();
        ResetScore();
    }

    public final void GoAhead() 
    {
        GameOn = true;
    }

    public void Pause()
    {
        GameOn = false;
        Step = false;
    }

    public void Step()
    {
        GameOn = false;
        Step = true;
    }

    public boolean GameOn()
    {
        return GameOn;
    }

    public boolean Stepping()
    {
        boolean s = Step;
        Step = false;
        return s;
    }

    /**  Monitors when players are allowed to go next.
     */
    public final void Go()
    {
        int i;
        if (TimeoutTimer > 20000) Timeout();
        if (TimeoutTimer % 2000 == 0 && TimeoutTimer > 0)
            LittleTimeout();
        TimeoutTimer++;
        for (int pri = 0; pri < 8; pri++) {
            // find the player with the current priority
            for (i = 0; Priority[i] != pri; i++) {}
            SoccerBall.run();
            if (Ticker[i] == 0) {
                // Its their turn get their action
                int action = 0, action_cost = 0;
                if (i % 2 == 0) {
                    action = EastPlayers[i / 2].getAction();
                    action_cost = PlayerAction(EastPlayers[i/2], EastTeam, action);
                }
                else {
                    action = WestPlayers[(i - 1) / 2].getAction();
                    action_cost = PlayerAction(WestPlayers[(i-1)/2], WestTeam, action);
                }
                Ticker[i] = action_cost;
            } 
            Ticker[i]--;
            if (i % 2 == 0)
                UI.Field.PlayerRequest(REQ_DRAWPLAYER, EastPlayers[i/2], EastTeam);
            else
                UI.Field.PlayerRequest(REQ_DRAWPLAYER, WestPlayers[(i -1)/2], WestTeam);
        }
        try        
        {
            Thread.sleep(10);
        } 
        catch(InterruptedException ex) 
        {
            Thread.currentThread().interrupt();
        }
    }

    public final void SpeedUp()
    {
        BaseRate -= 0.1;
    }

    public final void SlowDown()
    {
        BaseRate += 0.1;
    }

    /**  This loads a team from the filename it is passed.  Do not give it an extension on the 
    filename.  The extension ".class" is appended somewhere deep in the bowels of Sun's
    code.  And no, I didn't see that fact documented anywhere either.  */
    public final boolean LoadTeam(String filename, int Side)
    {
        Player TempPlayers[] = new Player[4];

        int counter; 
        boolean retval = true;
        if(Side==EAST)
            System.out.println("Loading " + filename + " in the East");
        else  //  I'm the only one who ever calls this, so don't bother testing for invalid values
            System.out.println("Loading " + filename + " in the West");

        for(counter=0; counter<4; counter++)
        {
            TempPlayers[counter] = DynLoader.LoadPlayer(filename);
            if(TempPlayers[counter]==null)
                retval = false;
        }

        if(retval) {
            if(Side==EAST) {
                if(EastTeam!=null)
                    for(counter=1; counter<5; counter++)
                        UI.Field.PlayerRequest(REQ_ERASEPLAYER, EastTeam.GetPlayer(counter), EastTeam);
                EastPlayers = TempPlayers;
                EastTeam = new Team(this, EAST, TempPlayers);
            }
            else {
                if(WestTeam!=null)
                    for(counter=1; counter<5; counter++)
                        UI.Field.PlayerRequest(REQ_ERASEPLAYER, WestTeam.GetPlayer(counter), WestTeam);
                WestPlayers = TempPlayers;
                WestTeam = new Team(this, WEST, TempPlayers);
            }
        }
        TimeoutTimer = 0;
        ResetScore();
        return retval;
    }

    /**  Returns either World.EAST or World.WEST depending on which team the player is on.
    Returns -1 on error.   */

    public final int GetTeamID(Player Unknown)
    {  
        if(Unknown!=null && EastTeam!=null && WestTeam!=null)
        {
            if(EastTeam.IsAMember(Unknown))
                return EAST;
            else if(WestTeam.IsAMember(Unknown))
                return WEST;
        }
        return -1;       
    }

    /**  Returns information about any of the eight squares around the player.   It returns
    OPPONENT, BALL, EMPTY, TEAMMATE, or BOUNDARY.  It takes N, NE, E, etc.  */

    public final int Look(Player CuriousGeorge, int Direction)
    {
        int contents;
        Point Delta = GetDelta(Direction);
        Team LookerTeam = GetTeam(CuriousGeorge);
        int side = GetTeamID(CuriousGeorge);
        Point Location = LookerTeam.GetPlayerLocation(CuriousGeorge);
        if(side==WEST)
            Delta.x = -Delta.x;
        if(Location!=null)
        {
            Location.x += Delta.x;
            Location.y += Delta.y;
            contents = GetSquareContents(Location);
            if(contents==EASTPLAYER)
            {
                if(side==EAST)
                    contents = TEAMMATE;
                else
                    contents = OPPONENT;
            }
            else if(contents==WESTPLAYER)
            {
                if(side==WEST)
                    contents = TEAMMATE;
                else
                    contents = OPPONENT;
            }
            else if (contents==GOALLINE)
                contents = EMPTY;
            else if (contents==SIDELINE)
                contents = EMPTY;
            return contents;
        }  //  end non-null location
        return -1;
    }

    /**  Returns the direction to the ball. */  
    public final int GetBallDirection(Player Client)
    {
        Team ClientTeam = GetTeam(Client);
        Point PlayerLocation = ClientTeam.GetPlayerLocation(Client);
        Point BallLocation = SoccerBall.GetLocation();
        int Side = GetTeamID(Client);
        return GetDirection(PlayerLocation, BallLocation, Side);
    }

    /**  Returns the direction to the ball reversed for the west team */
    public final int GetReverseDirection(Player Client)
    {
        int NormalDirection = GetBallDirection(Client);
        switch(NormalDirection) {
            case NORTH: return NORTH;
            case NORTHEAST: return NORTHWEST;
            case NORTHWEST: return NORTHEAST;
            case EAST: return WEST;
            case WEST: return EAST;
            case SOUTH: return SOUTH;
            case SOUTHEAST: return SOUTHWEST;
            case SOUTHWEST: return SOUTHEAST;
        }
        return WEST;  // won't ever happen
    }

    /**  Gets the distance to the ball. */
    public final int GetBallDistance(Player Client)
    {
        Team ClientTeam = GetTeam(Client);
        Point PlayerLocation = ClientTeam.GetPlayerLocation(Client);
        Point BallLocation = SoccerBall.GetLocation();
        return (int)Math.round(GetDistance(PlayerLocation, BallLocation));
    }

    /**  This function returns the direction to the nth closest opponent. */
    public final int GetOpponentDirection(Player Client, int nth)
    {
        if(nth>0 && nth<5)
        {
            Team ClientTeam = GetTeam(Client);
            Point PlayerLocation = ClientTeam.GetPlayerLocation(Client);
            Point OpponentLocation = GetOpponentLocation(nth, Client, ClientTeam, PlayerLocation);
            return GetDirection(PlayerLocation, OpponentLocation, ClientTeam.GetSide());
        }
        return -1;
    }

    /**  This allows players to determine where they are.  Coordinates are zero based with
    (0, 0) in the upper left hand corner of the field.  The field extends World.XSQUARES to
    the east and World.YSQUARES to the south.  The bottom right corner is:
    (XSQUARES - 1, YSQUARES - 1) 'cuz it's a zero based coordinate system. */

    public final Point GetCorrectedPlayerLocation(Player Client)
    {
        Point retval = GetPlayerLocation(Client);
        if(GetTeamID(Client)==WEST)
            retval.x = (XSQUARES - 1) - retval.x;
        return retval;
    }

    /**  This returns the player's true location on the field - it's not corrected for side... */
    public final Point GetPlayerLocation(Player Client)
    {
        Team ClientTeam = GetTeam(Client);
        return ClientTeam.GetPlayerLocation(Client);
    }

    /**  The original implementation gave players the ability to determine the distance to the
    closest opponent, the next closest opponent, and so forth.   I'd rather give them the
    distance to player one, player two, etc, just because it makes my life easier, but 
    they'd probably just end up seeing who was closest anyway...  So I left it alone; if you
    pass it 1, it will return the closest.  If you pass it 4, it will return the distance to the furthest. */

    public final int GetOpponentDistance(Player Client, int nth)
    {
        if(nth>0 && nth<5)
        {
            Team ClientTeam = GetTeam(Client);
            Point PlayerLocation = ClientTeam.GetPlayerLocation(Client);
            Point OpponentLocation = GetOpponentLocation(nth, Client, ClientTeam, PlayerLocation);
            return (int)Math.round(GetDistance(PlayerLocation, OpponentLocation));
        }
        return -1;
    }

    /**  I'm not proud of this function.  It finds the location of the nth clostest opponent to the
    client.  It takes so many arguements because the functions that call it need to find
    client team and player location anyway - no sense in doing it twice. */  
    private final Point GetOpponentLocation(int nth, Player Client, Team ClientTeam, Point PlayerLocation)
    {
        Point Location[] = new Point[4];
        float Distance[] = new float[4];
        float tempfloat;
        Point temppoint;
        int counter;
        int countertwo;
        Team OpponentTeam;
        if(ClientTeam==EastTeam)
            OpponentTeam = WestTeam;
        else if(ClientTeam==WestTeam)
            OpponentTeam = EastTeam;
        else
        {
            OpponentTeam = null;  //  make Mr. Compiler happy
            System.out.println("Invalid team in GetOpponentX");
            System.exit(1);
        }
        for(counter=0; counter<4; counter++)
        {
            Location[counter] = OpponentTeam.GetPlayerLocation(counter+1);
            Distance[counter] = GetDistance(PlayerLocation, Location[counter]);
        }

        //  It's a <cough> bubble sort.
        for(counter=0; counter<3; counter++)
            for(countertwo = counter + 1; countertwo<4; countertwo++)
            {
                if(Distance[counter]>Distance[countertwo])
                {
                    temppoint = Location[counter];
                    Location[counter] = Location[countertwo];
                    Location[countertwo] = temppoint;
                    tempfloat = Distance[counter];
                    Distance[counter] = Distance[countertwo];
                    Distance[countertwo] = tempfloat;
                }
            }
        return Location[nth - 1];   
    }

    /**  This is what the ball calls to query the world to see if a given move is valid. */
    public boolean BallAction(int Action)
    {
        boolean retval = true;
        Point Location = SoccerBall.GetLocation();
        Point Delta = GetDelta(Action);
        Location.x += Delta.x;
        Location.y += Delta.y;
        if((GetSquareContents(Location)==EMPTY) || (GetSquareContents(Location)==GOALLINE))
        {
            //  The only thing a ball will ever do is ask to move
            Nudging = 0;
            UI.Field.BallRequest(REQ_ERASEBALL);
            SoccerBall.Translate(GetDelta(Action));
            UI.Field.BallRequest(REQ_DRAWBALL);
        }
        else {
            Nudging++;
            UI.Field.BallRequest(REQ_DRAWBALL);
            retval = false;  //  we hit something
        }
        return retval;
    }

    /**  The ball calls this function to let the world know it's in a goal. */
    public final void GoalScored(int direction)
    {
        int counter;
        TimeoutTimer = 0;
        Cycles = 0;
        if(direction==EAST)
        {
            EastTeam.WonPoint();
            WestTeam.LostPoint();
            UI.ScoreBrd.EastScored();
            EastScore++;
        }
        else if(direction==WEST)
        {
            EastTeam.LostPoint();
            WestTeam.WonPoint();
            UI.ScoreBrd.WestScored();
            WestScore++;
        }
        //  Erase and then reset players and finally redraw them
        for(counter=1; counter<5; counter++)
        {
            UI.Field.PlayerRequest(REQ_ERASEPLAYER, EastTeam.GetPlayer(counter), EastTeam);
            UI.Field.PlayerRequest(REQ_ERASEPLAYER, WestTeam.GetPlayer(counter), WestTeam);
        }
        EastTeam.ResetTeam();
        WestTeam.ResetTeam();
        EastTeam.InitializePoint();
        WestTeam.InitializePoint();
        for(counter=1; counter<5; counter++)
        {
            UI.Field.PlayerRequest(REQ_DRAWPLAYER, EastTeam.GetPlayer(counter), EastTeam);
            UI.Field.PlayerRequest(REQ_DRAWPLAYER, WestTeam.GetPlayer(counter), WestTeam);
        }
        UI.ScoreBrd.UpdateText();
        TestForWin();
    }

    /**  This should never be called from anywhere but PlayerAction.  It is
    separate and synchronized to prevent two players from trying to modify
    the same variable at the same time.  Since this and BallAction are the
    only two methods that modify the world, they are the only two that
    need to be synchronized to prevent corrupting it.  Note:  It is still
    possible for clients to *read* corrupt data - if they read halfway through
    an update by the object that has the lock on the world.  Just think
    of the tiny amount of noise from that as another challenge for
    players to deal with.  (I don't want to synchronize everything for
    performance reasons) */

    private int PlayerAction(Player Actor, Team ActorTeam, int Action)
    {
        Point Location;
        Point NewLocation;
        Point Delta;
        Point BallDelta;    
        int contents;
        boolean PushedBall = false;
        boolean KickedBall = false;
        int retval = 0;
        float MoveScale;

        Cycles++;
        if(Action==BALL)
            Delta = GetDelta(GetBallDirection(Actor));
        else
            Delta = GetDelta(Action);  //  This is harmless for invalid values - it just returns 0,0

        if(ActorTeam==WestTeam)
        {
            //  Correct for reversal of sides
            Delta.x = -Delta.x;
        }
        else if(ActorTeam==null)
        {
            System.out.println("Invalid team trying to act");
            System.exit(1);
        }  

        Location = ActorTeam.GetPlayerLocation(Actor);

        switch(Action)
        {
            case PLAYER:
            // waiting
            retval = 1;
            break;
            case KICK:
            if(GetBallDistance(Actor)==1) {
                KickedBall = true;
                if (ActorTeam==WestTeam)
                    SoccerBall.Kick(GetReverseDirection(Actor));
                else
                    SoccerBall.Kick(GetBallDirection(Actor));
                retval = 4;
            }
            else
            // Swing and a miss!
                retval = 3;
            if(Nudging >= NUDGELIMIT)  // We keep trying, but can't kick
            {
                //  Try to shove ball in a random direction.
                //  See explanation below
                BallDelta = GetDelta((int)Math.round(Math.random()*((float)NORTHWEST)));
                NewLocation = SoccerBall.GetLocation();
                int count = 0;
                do {
                    NewLocation.x += BallDelta.x;
                    NewLocation.y += BallDelta.y;
                    count++;
                } while ((GetSquareContents(NewLocation) != EMPTY) && (count < 20));
                if(GetSquareContents(NewLocation)==EMPTY)
                {
                    KickedBall = true;
                    //  We can nudge the ball in a random direction and the player can move
                    Nudging = 0;
                    UI.Field.BallRequest(REQ_ERASEBALL);
                    SoccerBall.StopBall();
                    for (int i = 0; i < count; i++)
                        SoccerBall.Translate(BallDelta);
                    UI.Field.BallRequest(REQ_DRAWBALL);  
                }
            }  // nudging
            // ??? retval = false;
            break;

            default:
            //  Assume they're trying to move by default
            NewLocation = new Point(Location);
            NewLocation.x += Delta.x;
            NewLocation.y += Delta.y;
            contents = GetSquareContents(NewLocation);
            if((contents==EMPTY) || (contents == GOALLINE) || (contents == SIDELINE))
            {
                UI.Field.PlayerRequest(REQ_ERASEPLAYER, Actor, ActorTeam);
                ActorTeam.TranslatePlayer(Actor, Delta);
                UI.Field.PlayerRequest(REQ_DRAWPLAYER, Actor, ActorTeam);
                retval = Math.abs(Delta.x) + Math.abs(Delta.y) + 2;
            }        
            else if(contents==BALL)
            {
                //  Is there room to push the ball?
                NewLocation.x += Delta.x;
                NewLocation.y += Delta.y;
                if((GetSquareContents(NewLocation)==EMPTY) || (GetSquareContents(NewLocation)==GOALLINE))
                {
                    PushedBall = true;
                    UI.Field.BallRequest(REQ_ERASEBALL);
                    SoccerBall.StopBall();
                    SoccerBall.Translate(Delta);
                    UI.Field.BallRequest(REQ_DRAWBALL);
                    UI.Field.PlayerRequest(REQ_ERASEPLAYER, Actor, ActorTeam);
                    ActorTeam.TranslatePlayer(Actor, Delta);
                    UI.Field.PlayerRequest(REQ_DRAWPLAYER, Actor, ActorTeam);
                    retval = Math.abs(Delta.x) + Math.abs(Delta.y) + 3;
                }  //  end can push ball
                else {
                    if(Math.random()<=NUDGECHANCE)
                    {
                        //  Try to shove ball in a random direction.
                        //  I'm cheating.  I know that the directions are [0, 7]
                        //  This gets the DX and DY for a random direction in that range.
                        BallDelta = GetDelta((int)Math.round(Math.random()*((float)NORTHWEST)));
                        NewLocation = SoccerBall.GetLocation();
                        int count = 0;
                        do {
                            NewLocation.x += BallDelta.x;
                            NewLocation.y += BallDelta.y;
                            count++;
                        } while ((GetSquareContents(NewLocation) != EMPTY) && (count < 20));
                        if(GetSquareContents(NewLocation)==EMPTY)
                        {
                            //  We can nudge the ball in a random direction and the player can move
                            PushedBall = true;
                            UI.Field.BallRequest(REQ_ERASEBALL);
                            SoccerBall.StopBall();
                            for (int i = 0; i < count; i++)
                                SoccerBall.Translate(BallDelta);
                            UI.Field.BallRequest(REQ_DRAWBALL);  
                            UI.Field.PlayerRequest(REQ_ERASEPLAYER, Actor, ActorTeam);
                            ActorTeam.TranslatePlayer(Actor, Delta);
                            UI.Field.PlayerRequest(REQ_DRAWPLAYER, Actor, ActorTeam);
                            retval = 3;
                        }
                        else
                        // D'oh!
                            retval = 2;  
                    }  // end try to nudge ball

                    else
                        retval = 2;
                }}
            else
                retval = 2;
            break;  //  end default case
        }  // end of switch  

        return retval;    
    } // end private PlayerAction

    /**  Called by GUI when the window is closing - let's us notify the teams that
    they're about to go bye-bye */
    public void WindowClosing()
    {
        if(EastTeam!=null)
            EastTeam.GameOver();
        if(WestTeam!=null)
            WestTeam.GameOver();
    }

    /**  Returns the direction a player at origin would have to move in to get to Target.  Side is
    used to reverse things for the west team.  I've got to be making this more complicated
    than it needs to be. */
    private final int GetDirection(Point Origin, Point Target, int Side)
    {
        Point Delta = new Point(Origin.x -Target.x, Origin.y -Target.y);
        if(Side==WEST)
            Delta.x = -Delta.x;

        /*  We can divide things up into 4 quadrants by inspecting the signs of Delta.x and Delta.y
        + +  |   - +
        ------|------
        + -   |   - -

        This means that we can narrow the direction down to three choices right off the bat.    
        The shortest path toward the ball will either be along the major axis or the diagonal
        through the quadrant.  This narrows our choice down to two directions.  We can use the
        tangent (watching out for division by zero) to determine if the ball is closer to the diagonal
        path or the straight one.  If .41421356 < abs(tan) < 2.41421356 then the best approximation
        is along the diagonal path.  (Note that the two messy decimals are inverses - this let's us
        simplify things a little more later on so we only need to do one test after our div by 0 check).
        Those two messy decimals are the tangents of 22.5 and 67.5 degrees, BTW.
        Otherwise it's along the major axis.  I wonder if this is any
        faster than the brute force approach...  I'd only be testing 8 cases...
         */

        if(Delta.x>=0)
        {
            if(Delta.y>=0)  // DX>=0, DY>=0
            {
                if(Math.abs(Delta.x)>=Math.abs(Delta.y))  //  We're X major
                {
                    if(Math.abs(((float)Delta.y)/((float)Delta.x))>.41421356237)
                        return NORTHWEST;
                    else
                        return WEST;  
                }
                else  //  it's Y major
                {
                    //  Note that it's X/Y now instead of Y/X as above
                    //  Since the player can never be on the ball and we're always dividing by the
                    //  major axis, we'll never [in theory] divide by zero.
                    if(Math.abs(((float)Delta.x)/((float)Delta.y))>.41421356237)
                        return NORTHWEST;
                    else
                        return NORTH;  
                }
            }
            else // DX>=0, DY<0
            {
                if(Math.abs(Delta.x)>=Math.abs(Delta.y))  //  We're X major
                {
                    if(Math.abs(((float)Delta.y)/((float)Delta.x))>.41421356237)
                        return SOUTHWEST;
                    else
                        return WEST;  
                }
                else  //  it's Y major
                {
                    //  Note that it's X/Y now instead of Y/X as above
                    //  Since the player can never be on the ball and we're always dividing by the
                    //  major axis, we'll never [in theory] divide by zero.
                    if(Math.abs(((float)Delta.x)/((float)Delta.y))>.41421356237)
                        return SOUTHWEST;
                    else
                        return SOUTH;  
                }
            }  //  end DX>=0, DY<0
        } //  end DX>=0
        else  //  Delta.x<0; equals case handled with greater than
        {
            if(Delta.y>=0)  // DX<0, DY>=0
            {
                if(Math.abs(Delta.x)>=Math.abs(Delta.y))  //  We're X major
                {
                    if(Math.abs(((float)Delta.y)/((float)Delta.x))>.41421356237)
                        return NORTHEAST;
                    else
                        return EAST;  
                }
                else  //  it's Y major
                {
                    //  Note that it's X/Y now instead of Y/X as above
                    //  Since the player can never be on the ball and we're always dividing by the
                    //  major axis, we'll never [in theory] divide by zero.
                    if(Math.abs(((float)Delta.x)/((float)Delta.y))>.41421356237)
                        return NORTHEAST;
                    else
                        return NORTH;  
                }
            }
            else // DX<0, DY<0
            {
                if(Math.abs(Delta.x)>=Math.abs(Delta.y))  //  We're X major
                {
                    if(Math.abs(((float)Delta.y)/((float)Delta.x))>.41421356237)
                        return SOUTHEAST;
                    else
                        return EAST;  
                }
                else  //  it's Y major
                {
                    //  Note that it's X/Y now instead of Y/X as above
                    //  Since the player can never be on the ball and we're always dividing by the
                    //  major axis, we'll never [in theory] divide by zero.
                    if(Math.abs(((float)Delta.x)/((float)Delta.y))>.41421356237)
                        return SOUTHEAST;
                    else
                        return SOUTH;  
                }
            }  //  end DX<0, DY<0
        }  //  end DX<0
    }  //  End GetBallDirection

    /** Returns the team that a given player is on.   */

    public final Team GetTeam(Player target)
    {
        if(EastTeam!=null && WestTeam!=null)
        {
            if(EastTeam.IsAMember(target))
                return EastTeam;
            else if(WestTeam.IsAMember(target))
                return WestTeam;
        }
        return null;
    }

    private final float GetDistance(Point A, Point B)
    {
        return (float)Math.sqrt((A.x - B.x)*(A.x - B.x) + (A.y-B.y)*(A.y-B.y));
    }

    /**  Returns the contents of a square in the world.  It returns BOUNDARY, BALL, EASTPLAYER,
    WESTPLAYER, or EMPTY.  Look() needs to turn EASTPLAYER and WESTPLAYER
    into TEAMMATE and OPPONENT depending on the caller.  */

    public final int GetSquareContents(Point Target)
    {
        if(SoccerBall.IsAt(Target))
            return BALL;
        if(EastTeam.IsPlayerAt(Target)!=0)
            return EASTPLAYER;
        if(WestTeam.IsPlayerAt(Target)!=0)
            return WESTPLAYER;
        if(Target.y==0 || Target.y==(YSQUARES-1))
            return SIDELINE;
        if(Target.x==0 || Target.x==(XSQUARES-1))
            return GOALLINE;
        if(Target.x<0 || Target.y<0 || Target.x>=XSQUARES || Target.y>=YSQUARES)
            return BOUNDARY;
        return EMPTY;
    }

    //  This returns the change in X and Y coordinates corresponding to a given
    //  direction.
    private final Point GetDelta(int direction)
    {
        if(direction>=NORTH && direction<=NORTHWEST)
            return new Point(DeltaLookupTable[direction]);
        else
            return new Point(0, 0);    
    }

    private final void ResetScore()
    {
        EastScore = 0;
        WestScore = 0;
        UI.ScoreBrd.ResetScore();
    }

    private final void TestForWin()
    {
        if(EastScore==POINTSTOWIN)
        {
            if (!Training) {
                EastTeam.GameOver();
                WestTeam.GameOver();
                UI.ShowVictory(EAST);
            }
        }
        else if(WestScore==POINTSTOWIN)
        {
            if (!Training) {
                EastTeam.GameOver();
                WestTeam.GameOver();
                UI.ShowVictory(WEST);
            }
        }
    }  

    public final void Timeout()
    {
        int counter;
        TimeoutTimer = 0;
        if(EastTeam!=null && WestTeam!=null)
        {
            //  Erase and then reset players and finally redraw them
            for(counter=1; counter<5; counter++)
            {
                UI.Field.PlayerRequest(REQ_ERASEPLAYER, EastTeam.GetPlayer(counter), EastTeam);
                UI.Field.PlayerRequest(REQ_ERASEPLAYER, WestTeam.GetPlayer(counter), WestTeam);
            }
            Cycles = 0;
            EastTeam.ResetTeam();
            WestTeam.ResetTeam();
            SoccerBall.RandomPlace();  
            for(counter=1; counter<5; counter++)
            {
                UI.Field.PlayerRequest(REQ_DRAWPLAYER, EastTeam.GetPlayer(counter), EastTeam);
                UI.Field.PlayerRequest(REQ_DRAWPLAYER, WestTeam.GetPlayer(counter), WestTeam);
            }
        }
    }

    public void SetCycles(int c)
    {
        Cycles = c;
    }

    public int GetCycles()
    {
        return Cycles;
    }

    public final void LittleTimeout()
    {
        Cycles = 0;
        SoccerBall.RandomPlace();
        System.out.println("Timeout!");
    }

    /* Initially I had these tucked away as private variables, but since the players can't access
    the world, I can make these public to simpilfy communication between things like the GUI
    and the ball. */
    public GUI UI;
    public Team EastTeam;
    public Team WestTeam;
    public Player EastPlayers[];
    public Player WestPlayers[];
    public Ball SoccerBall;
    public int Nudging;
    public int Cycles;

    private DynamicClassLoader DynLoader;  
    private int TimeoutTimer;
    private Point DeltaLookupTable[];
    private int EastScore;
    private int WestScore;
    private float BaseRate;
    private int Ticker[];
    private int Priority[];
    private boolean GameOn;
    private boolean Step;
    private static boolean Training = true;

    //  Here's a huge list of constants.  
    //  You MUST leave the directions as 0-7 in order for the ball nudge routine to work
    //  correctly.  Also, I cut and paste this into the player class because it's ridiculous to
    //  always refer to World.NORTH et al when you're writing agents.
    static public final int NORTH = 0;
    static public final int NORTHEAST = 1;
    static public final int EAST = 2;
    static public final int SOUTHEAST = 3;
    static public final int SOUTH = 4;
    static public final int SOUTHWEST = 5;
    static public final int WEST = 6;
    static public final int NORTHWEST = 7;
    static public final int PLAYER = 8;
    static public final int BALL = 9;
    static public final int KICK = 10;
    static public final int BOUNDARY = 11;
    static public final int EASTPLAYER = 12;
    static public final int WESTPLAYER = 13;
    static public final int OPPONENT = 14;
    static public final int TEAMMATE = 15;
    static public final int EMPTY = 16;
    static public final int GOALLINE = 17;
    static public final int SIDELINE = 18;
    static public final int XSQUARES = 80;
    static public final int YSQUARES = 40;
    static public final int REQ_DRAWBALL = 1;
    static public final int REQ_ERASEBALL = 2;
    static public final int REQ_ERASEPLAYER = 3;
    static public final int REQ_DRAWPLAYER = 4;
    static public final int POINTSTOWIN = 15;
    //  chance the ball scoots off in a random direction when it's stuck
    static public final double NUDGECHANCE = 0.10; 
    // How many times can we kick the ball fruitlessly
    static public final int NUDGELIMIT = 10;
    //  milliseconds between the balls actions (how fast it moves)
    //  This should be about 1/10 the player speed acording to the original docs.
    static public final long BALLTICK = 30;
    //  milliseconds between player actions.
    static public final long  PLAYERTICK = 150;
    static public final int   KICKDISTANCE = 10;

    //  Penalties for kicking or moving the ball.
    //  Player actions take X_PENALTY times more time than normal when they
    //  are handeling the ball.
    static public final float KICK_PENALTY = (float)1.50;
    static public final float PUSH_PENALTY = (float)1.25;

    static public final long  TIMEOUT = 60000L;
    static public final int   CYCLELIMIT = 2500;
    static public final Color FIELDCOLOR = Color.green;
    static public final Color GOALCOLOR = Color.gray;
    static public final Color SIDECOLOR = Color.white;
    static public final Color FRAMECOLOR = Color.black;
    static public final Color BALLCOLOR = Color.white;
    static public final Color WINDOWCOLOR = Color.lightGray;
} 

/*  The constructor of ClassLoader is protected, which means I have to go through
this run around and derive my own class from it.  <put out> In a nutshell, this baby lets
me load classes from disk.  I can't believe I just refered to something as "baby".  <sigh> 
 */  

class DynamicClassLoader extends ClassLoader
{
    public DynamicClassLoader()
    {
        super();
    }

    //  filename is a file minus the extension.  They append .class later on.
    public final Player LoadPlayer(String filename)
    {
        Class PlayerClass;
        try
        {
            PlayerClass = loadClass(filename);
        }
        catch (ClassNotFoundException e)
        {
            PlayerClass = null;
            System.out.println(e);
        }
        if(PlayerClass!=null)
        {
            try
            {
                return (Player)PlayerClass.newInstance();
            }
            catch (IllegalAccessException e)
            {
                System.out.println(e);
            }
            catch(InstantiationException e)
            {
                System.out.println(e);
            }  //  They must have made that word up.
        }
        System.out.println("Unable to load " + filename);
        return null;
    }

    /*  The following aren't necessary under Win32, but need to be added to make this
    work with our linux machines. */
    public Class loadClass(String name) throws ClassNotFoundException
    {
        return loadClass(name, true);
    }

    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        Class retval = findSystemClass(name);
        if(resolve)
            resolveClass(retval);
        return retval;
    }
}

