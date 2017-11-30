/**  A *very* stupid agent. */
import java.awt.*;
import java.lang.Exception.*;

public class ZombiePlus extends Player
  {
  public ZombiePlus()
    {
    super();
    }
  
  /**  Over ride this function to give the class unique behavior (i.e. make it do something
        intelligent).  */
  public int getAction()
    {
	int BallDirection = GetBallDirection();
	int BallDistance = GetBallDistance();
	if (BallDistance == 1)
	    if (BallDirection == EAST || BallDirection == SOUTHEAST || BallDirection == NORTHEAST)
		return ((BallDirection + 1) % 8);
	return BALL;
    }
  }
