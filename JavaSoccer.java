/**  This is the starter class for JavaSoccer.  It creates an instance of World.   I probably should
      have just put main() in World, but I always feel funny creating a class from within itself and
      this way I can create cool sounding classes like World without the user having to know
      about it when they type "java World" */

public class JavaSoccer
  {
  public static void main(String args[])
    {
	long t1;
	Thread thisThread = Thread.currentThread();
	World Universe = new World();
	while (!Universe.GameOn() && !Universe.Stepping()) {Thread.yield();}
	Universe.Go();
	while (true) {
	    if (Universe.GameOn()) {
		// slow things down
		t1 = System.currentTimeMillis();
		Universe.Go();
		//while (System.currentTimeMillis() - t1 < 50) {}
	    } else if (Universe.Stepping()) Universe.Go();
	    Thread.yield();
	}
     }
  }
