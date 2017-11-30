import java.awt.*;
import java.lang.Exception.*;

/**  Derive your agents from this class.  */

public class Player
{
    /**  You MUST call "super()" in the first line of your constructor.  Do NOT rely on the values
    of Parent, ID, or the thread name in the constructor - due to a limitation in Java they're
    not set until immediately after creation. */
    public Player()
    {
        super();
        CalledSuper = true;
        Run = true;
    }

    /**  This function may be overloaded by your classes to allow you to draw custom players.
    Use gc as your graphics constant and draw only in the rectangle provided.  The 
    rectangle has the upper X,Y coordinates and the width and height of the area which
    the player should draw to.  */

    public void Draw(Graphics gc, Rectangle ClipRect)
    {
        if(Parent.GetTeamID(this)==World.EAST)
            gc.setColor(Color.blue);
        else
            gc.setColor(Color.red);  
        gc.fillRect(ClipRect.x, ClipRect.y, ClipRect.width, ClipRect.height);
        gc.setColor(Color.black);
        gc.drawRect(ClipRect.x, ClipRect.y, ClipRect.width-1, ClipRect.height-1);
    }

    /**  You'll probably never need to override this - it just erases a square. */
    public void Erase(Graphics gc, Rectangle ClipRect)
    {
        /*  Bug fix:  Remember the "-1" when you're checking if something is
        on the edge.  It's like zero based arrays in C */
        if (GetLocation().y == 0 || GetLocation().y == (FieldY()-1))
            gc.setColor(World.SIDECOLOR);
        else if (GetLocation().x == 0 || GetLocation().x == (FieldX()-1))
            gc.setColor(World.GOALCOLOR);
        else
            gc.setColor(World.FIELDCOLOR);
        gc.fillRect(ClipRect.x, ClipRect.y, ClipRect.width, ClipRect.height);
        gc.drawRect(ClipRect.x, ClipRect.y, ClipRect.width-1, ClipRect.height-1);
    }

    /**  Over ride this function to give the class unique behavior (i.e. make it do something
    intelligent).  */

    public int getAction()
    {
        return BALL;
    }

    /**  This is called each time your team scores a point. */
    public void WonPoint() { }

    /**  This is called each time your team is scored against. */

    public void LostPoint() { }

    /**  This is called at the start of every point. */
    public void InitializePoint() { }

    /**  This is called at the end of every game */
    public void GameOver()
    {
    }

    /**  This is called when the team is first loaded.  Unlike in the constructor, it is safe to
    use Parent et al. */
    public void InitializeGame() { }

    /**  This function is used to remind programmers that they must call super() in the
    first line of their contstructor.  When the teams are being constructed, it is used
    to confirm that Player's constructor was called to handle behind the scenes
    initialization.  I doubt that it's foolproof - it's merely a reminder*/
    public final boolean CalledPlayer()
    {
        return CalledSuper;
    }

    public final void SetID(int id)
    {
        ID = id;
    }

    /**  Returns the distance to the ball in squares.  */
    public final int GetBallDistance()
    {
        return Parent.GetBallDistance(this);
    }

    public final void SetParent(World parent)
    {
        Parent = parent;
    }

    /**  Returns the contents of the square in Direction */
    public final int Look(int Direction)
    {
        return Parent.Look(this, Direction);
    }

    /**  Returns the X and Y coordinates of the player.  */
    public final Point GetLocation()
    {
        //  We need to lie to people on the West team about where they are.
        return Parent.GetCorrectedPlayerLocation(this);
    }

    /**  Returns the width of the field in squares */
    public final int FieldX()
    {
        return World.XSQUARES;
    }

    /**  Returns the height of the field in squares */
    public final int FieldY()
    {
        return World.YSQUARES;
    }

    /**  Returns which team a given player is on */
    public final int GetTeam()
    {
        return Parent.GetTeamID(this);
    }

    /**  Returns the direction the player must move to get to the ball. */
    public final int GetBallDirection()
    {
        return Parent.GetBallDirection(this);
    }

    /**  Returns the direction the player must go to get to the Nth closest opponent.  1 is the
    closest and 4 is the furthest opponent.  */
    public final int GetOpponentDirection(int n)
    {
        return Parent.GetOpponentDirection(this, n);
    }

    /**  Get's the distance to the Nth opponent.  See GetOpponentDirection. */
    public final int GetOpponentDistance(int n)
    {
        return Parent.GetOpponentDistance(this, n);
    }

    /** Get's the distance to the nth nearest teammate */
    public final int GetTeammateDistance(int n)
    {
        //return Parent.GetTeammateDistance(this, n);
        return 1;
    }

    /** Think about it. */
    public final int GetTeammateDirection(int n)
    {
        //return Parent.GetTeammateDirection(this, n);
        return NORTH;
    }

    private World Parent;
    protected int ID;
    private boolean CalledSuper;
    protected boolean Run;

    //  These are copied verbatim from World just to avoid having to refer to them
    //  as World.EAST/etc.  I'm a lazy, lazy man...  <g>
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
}



