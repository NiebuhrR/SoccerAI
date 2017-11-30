/**  A *very* stupid agent. */
import java.awt.*;
import java.lang.Exception.*;

public class Zombie extends Player
  {
  public Zombie()
    {
    super();
    }
  
  /**  Over ride this function to give the class unique behavior (i.e. make it do something
        intelligent).  */
  public int getAction()
    {
	return BALL;
    }
  }
