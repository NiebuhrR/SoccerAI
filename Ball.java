import java.lang.Exception.*;
import java.awt.*;

public class Ball
{
    public Ball(World parent)
    {
	Parent = parent;
	Y = (int)(Math.random()*((float)(World.YSQUARES - 20))) + 10;
	X = World.XSQUARES/2;    
    }
  
    public void RandomPlace()
    {
	Point Location = GetLocation();
	Parent.UI.Field.BallRequest(World.REQ_ERASEBALL);
	int x, y;
	//  Since the addition of "little" timeouts we have to worry
	//  a little more about where we place the ball
	x = World.XSQUARES/2;    
	do {
	    y = (int)(Math.random()*((float)(World.YSQUARES - 20))) + 10;
	    Location.move(x, y);
	} while (Parent.GetSquareContents(Location) != World.EMPTY);
	Impulse = 0;
	X = x;
	Y = y;
	Direction = World.NORTH;  // why not?
	Parent.UI.Field.BallRequest(World.REQ_DRAWBALL);
    }
    
    public void Kick(int direction)
    {
	if (direction % 2 == 0)
	    Impulse = World.KICKDISTANCE;
	else
	    Impulse = Math.round((float)World.KICKDISTANCE / (float)1.4421);
	Direction = direction;
    }

    /**  Notifies the parent if a goal was scored and resets the ball position. */
    private void TestScore()
    {
	if(X==0)
	    {
		RandomPlace();
		Parent.GoalScored(World.EAST);
	    }
	else if(X==(World.XSQUARES-1))
	    {
		RandomPlace();
		Parent.GoalScored(World.WEST);
	    }
    }
    
    public Point GetLocation()
    {
	return new Point(X, Y);
    }
  
    public void StopBall()
    {
	Impulse = 0;
    }
  
    public void Erase(Graphics gc, Rectangle ClipRect)
    {
	//  We need to check Y first 'cuz the side lines extend past
	//  the goal line.
	if(Y==0 || Y==(World.YSQUARES-1))
	    gc.setColor(World.SIDECOLOR);
	else if (X == 0 || X == (World.XSQUARES-1))
	    gc.setColor(World.GOALCOLOR);
	else
	    gc.setColor(World.FIELDCOLOR);
	gc.fillOval(ClipRect.x, ClipRect.y, ClipRect.width, ClipRect.height);
	gc.drawOval(ClipRect.x, ClipRect.y, ClipRect.width-1, ClipRect.height-1);
    }
  
    public void Draw(Graphics gc, Rectangle ClipRect)
    {
	gc.setColor(World.BALLCOLOR);
	gc.fillOval(ClipRect.x, ClipRect.y, ClipRect.width, ClipRect.height);
	gc.setColor(World.FRAMECOLOR);
	gc.drawOval(ClipRect.x, ClipRect.y, ClipRect.width-1, ClipRect.height-1);
    }
 
    /**  This function is called in response to the ball's requesting a BallAction from
	 World.  Never call it directly - the redraw won't be handled properly. */ 
    public void Translate(Point Delta)
    {
	X += Delta.x;
	Y += Delta.y;
    }
      
    public void run()
    {
	Point Delta;
	float MoveScale = 1;
	if(Impulse>0)
	    {
		if(!Parent.BallAction(Direction))
		    Impulse = 0; //  We just hit something
		else
		    Impulse--;  //  We moved - we've got a little less momentum now
		
		if(Direction==World.NORTHWEST || Direction==World.NORTHEAST || Direction==World.SOUTHWEST || Direction==World.SOUTHEAST)
		    MoveScale = (float)1.41421;
		else
		    MoveScale = 1;
	    }  // end if impulse > 0
	TestScore();
	
	/*  By setting MoveScale to zero we ensure that there is no lag between a kick and the
	    ball starting to move.  It'll get reset above once the ball starts moving. */
	MoveScale = 0;
    } //  end run()
        
    public boolean IsAt(Point target)
    {
	if(target.x==X && target.y == Y)
	    return true;
	return false;
    }   
  
    private World Parent;
    private int X;
    private int Y;
    /*  I'm sure I'm using "impulse" incorrectly, but hopefully a physics major will never see this
	and jump on me about it.  It's shorter to type than momentum.  
	Anyway, these tell how much further and in what direction
	the ball will travel.  */
    private int Impulse;
    private int Direction;
}





