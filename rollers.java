import java.awt.*;

public class rollers extends Player {
    static final int CONTROL_TIME = 13;
    static final int WINGSPAN = 8;
    static final int Lead = 1;
    static final int NorthWing = 2;
    static final int SouthWing = 3;
    static final int Rear = 4;

    static int cycle;
    static int haveBall;
    static int leader;
    static int plx[]; // keeps track of the x coord of each player
    static int ply[]; // keeps track of the y coord of each player
    static int roles[]; // tracks the role (Lead, NWing, SWing, Rear) of each player
    static int ball[]; // tells if each player has or doesn't have the ball (value is either 0 or 1)
    static int balld[]; // each player has its owd distance to the ball
    static int synchro[];

    //initialize game
    public void InitializeGame () {
      cycle = -1;
      haveBall = 0; // tells whether team has ball?
      plx = new int[4];
      ply = new int[4];
      roles = new int[4];
      ball = new int[4];
      balld = new int[4];
      synchro = new int[4];
    }

    public void InitializePoint () {
      int i;
      leader = 0;
      haveBall = 0;
      for (i=0; i < 4; i++) {
        plx[i] = 0;
        ply[i] = 0;
        synchro[i] = 0;
      }
      roles[0] = Lead;
      roles[1] = NorthWing;
      roles[2] = SouthWing;
      roles[3] = Rear;
    }
    
    public int Player1() {

      int action = WEST;

      /* Mark where I am */
      plx[0] = GetLocation().x;
      ply[0] = GetLocation().y;
      ball[0] = HaveBall(0);
      balld[0] = GetBallDistance();

      /* Generate orders */
      Behave(); // gives the appropriate roles for each player

      for (int i = 0; i < 4; i++)
	    synchro[i] = 1;

      //checks for the role of player 1
      switch (roles[0]) {
      case Lead: action =  Lead();
        break;
      case NorthWing: action =  NorthWing();
	    break;
      case SouthWing: action =  SouthWing();
	    break;
      case Rear: action =  Rear();
	    break;
      }
      return action;
    }

    public int Player2() {
      /* Mark where I am */
      int action = WEST;

      plx[1] = GetLocation().x;
      ply[1] = GetLocation().y;
      ball[1] = HaveBall(1);
      balld[1] = GetBallDistance();
      synchro[1] = 0;

      switch (roles[1]) {
          case Lead: action =  Lead();
            break;
          case NorthWing: action =  NorthWing();
            break;
          case SouthWing: action =  SouthWing();
            break;
          case Rear: action =  Rear();
            break;
          }
      return action;
    }

    public int Player3() {

      int action = WEST;

      /* Mark where I am */
      plx[2] = GetLocation().x;
      ply[2] = GetLocation().y;
      ball[2] = HaveBall(2);
      balld[2] = GetBallDistance();

      synchro[2] = 0;
      switch (roles[2]) {
          case Lead: action =  Lead();
            break;
          case NorthWing: action =  NorthWing();
            break;
          case SouthWing: action =  SouthWing();
            break;
          case Rear: action =  Rear();
            break;
          }
      return action;
    }

    public int Player4() {
      int action = WEST;
      /* Mark where I am */
      plx[3] = GetLocation().x;
      ply[3] = GetLocation().y;
      ball[3] = HaveBall(3);
      balld[3] = GetBallDistance();
      synchro[3] = 0;

      switch (roles[3]) {
          case Lead: action =  Lead();
            break;
          case NorthWing: action =  NorthWing();
            break;
          case SouthWing: action =  SouthWing();
            break;
          case Rear: action =  Rear();
            break;
          }
      return action;
    }

    public void WonPoint () {};
    public void LostPoint () {};
    public void GameOver () {};

    // checks if the player has the ball (I think same code from ZombiePlus)
    public int HaveBall(int id) {
      int BallDir = GetBallDirection();
      if ((GetBallDistance() == 1) && 
	  ((BallDir == EAST) || (BallDir == NORTHEAST)
	   || (BallDir == SOUTHEAST) || (BallDir == NORTH)
	   || (BallDir == SOUTH))) {
	haveBall = CONTROL_TIME;
	return 1;
      }
      return 0;
    }
				       
    // Takes care of switching the roles for each player depending on their distance to the ball
    public void Behave () {

      int newl = 0;
      int i;
      cycle = (cycle + 1) % 4;
      if (cycle == 0) { //if cycle == 0, then team doesn't have ball?
          haveBall--;
      }

      /* Whoever has the ball gets to be the leader */
      for ( i = 0; i < 4; i++) {
	    if (balld[i] < balld[newl])
	    newl = i;
	    if (ball[i] == 1) {  // if the player's distance to the ball is less than everyon else's and has ball
          Regroup(i); // give a new role for each position
          i = 5;
        }
          }
      if (i == 4) /* No one was on the ball, pick the closest guy */
        Regroup(newl);
      /*if (haveBall <= 0) {
        leader = 0;
      } */
    }

    // reassigns roles
    public void Regroup (int newLead) {
      int i, score, good;
      good = 0;   /* Make Java happy */
      for (i=0; i<4; i++) {
          roles[i] = 0;
      }
      
      leader = newLead;
      roles[leader] = Lead;
      
      /* southernmost unassigned player is south wing */
      score = -1;
      for (i=0; i<4; i++) {
        if (roles[i] == 0) {
          if (ply[i] > score) {
                score = ply[i];
                good = i;
          }
        }
      }
      roles[good] = SouthWing;
      
      /* northernmost unassigned player is north wing */
      score = 10000;
      for (i=0; i<4; i++) {
        if (roles[i] == 0) {
          if (ply[i] < score) {
                score = ply[i];
                good = i;
          }
        }
      }
      roles[good] = NorthWing;
      
      /* easternmost unassigned player is rear */
      score = -1;
      for (i=0; i<4; i++) {
        if (roles[i] == 0) {
          if (plx[i] > score) {
                score = plx[i];
                good = i;
          }
        }
      }
      roles[good] = Rear;
    }


    // WHAT EACH ROLES DO:
    public int Lead () {
      int i, numPlayersAbove;
      int x = GetLocation().x;
      int y = GetLocation().y;

      /* If lead is east of the ball and an opponent is around, get the ball
	 out of here */
      if ((GetOpponentDistance(1) < 2) && 
	  ((Look(SOUTHWEST) == BALL) || (Look(WEST) == BALL) || (Look(NORTHWEST) == BALL)))
	    return KICK;
      
      /* Calculate the number of players above you, positive for above and negative for below */
      numPlayersAbove = 0;
      for (i=0; i<4; i++) {
	    if ((y < FieldY() - 4) && (ply[i] < y)) {
	        numPlayersAbove++;
	    }
	    if ((y > 5) && (ply[i] > y)) {
	        numPlayersAbove--;
	    }
      }
      
      /* If the BAll is in Look(SW) */
      if (Look(SOUTHWEST) == BALL) {
        // if ball is far away from goal and if there is at least one player above the leader, kick it in SW direction
        if ((x > 3 * FieldX() / 4) || (numPlayersAbove > 0)) {
          return KICK;
        }
        // if not, go South, now in Look(West) condition
        return SOUTH;
      }
      
      /* If the ball is Look(N) */
      if (Look(NORTH) == BALL) {
        // if ball is far away from goal and there is no player above the leader, kick it in N direction
        // NOTE: THIS IS WRONG, SHOULD RETURN NORTHEAST (MAYBE)
        if ((x > 3 * FieldX() / 4) || (numPlayersAbove < 0)) {
          return KICK;
        }
        // if ball is close to goal and there is at least one player above the leader, bump the ball up north
	    return NORTH;
      }

      // if the ball is Look(W)
      if (Look(WEST) == BALL) {
        // if all three other players are above the leader, bump the ball up north
        if (numPlayersAbove >= 3) {
          return NORTH;
        }
        // if all three other players are below the leader, the leader moves south
        // NOTE: THIS IS WRONG, SHOULD RETURN NORTHWEST (MAYBE)
        if (numPlayersAbove <= -3) {
          return SOUTH;
        }

	    // if there is at least one player above and at least one player below the leader
        // then there is support on both sides, kick the ball west (toward goal)
	    return KICK;
      }

      // HELLO: ANOTHER LOOK(NORTH) FUNCTION BUT FOR OPPONENTS INSTEAD (MAKE NO SENSE)
      // if ball is look(N)
      if (Look(NORTH) == BALL) {

        // if there is an opponent both in the west and northwest directions, kick the ball up north
        // NOTE: THIS WRONG, OUR IF CLAUSE SHOULD INCLUDE CONDITIONS FOR BOTH BALL & OPPONENT RATHER THAN
        // SEPRATE THEM LIKE THIS
        if (Look(WEST) == OPPONENT && Look(NORTHWEST) == OPPONENT) {
          return KICK;
        }

        // if the ball is near our goal (defending goal), and there is an opponent either in the West
        // or in the northwest, then kick it up north
        // NOTE: not sure if should kick the ball up north??
        if ((x > 3 * FieldX() / 4) &&
            (Look(WEST) == OPPONENT || Look(NORTHWEST) == OPPONENT)) {
          return KICK;
        }

        // if there is at least one player above and there is nothing in the northeast direction
        // go northeast
        if ((numPlayersAbove >= 0) && Look(NORTHEAST) == EMPTY) {
          return(NORTHEAST);
        }

        // if none of the condition above, the leader move east, so that the new condition is Look(NW)
        // NOTE: THIS MAKES NO SENSE BECAUSE THERE IS NO LOOK(NW) CONDITION
        return(EAST);
      }

      // if the ball is look(NE)
      if (Look(NORTHEAST) == BALL) {
        // if there is nothing in the north direction but there is an opponent in the northwest direction
        // move up north
        if (Look(NORTH) == EMPTY && (Look(NORTHWEST) == OPPONENT)) {
          return(NORTH);
        }
        // if not, move east so that the new condition is now Look(North)
        return(EAST);
      }

      // if the ball is look(E)
      if (Look(EAST) == BALL) {
        // if there is at least one player above the leader and there is nothing in northeast
        // then go to the northeast, condition now becomes Look(South)
        if ((numPlayersAbove > 0) && Look(NORTHEAST) == EMPTY) {
          return(NORTHEAST);
        }
        // if there is no player above the leader and there is nothing in the southeast
        // then go to southeast, condition now becomes Look(North)
        if ((numPlayersAbove < 0) && Look(SOUTHEAST) == EMPTY) {
          return(SOUTHEAST);
        }

        // if there is an opponent in the west direction, bump into the opponent by moving west
        // QUESTION: DOES THIS MOVE THE OPPONENT BACK OR DOES THIS MEAN THAT THERE IS BASICALLY NO MOVEMENT
        if (Look(WEST) == OPPONENT) {
          return(WEST);
        }

        // if there is nothing in the north, then move north
        if (Look(NORTH) == EMPTY) {
          return(NORTH);
        }

        // if none of the condition above is satisfied, then move south
        return(SOUTH);
      }

      // if ball is look(SE)
      if (Look(SOUTHEAST) == BALL) {
        // if there is nothing in the south and an opponent in the southwest
        // then move south to block the opponent
        if (Look(SOUTH) == EMPTY && (Look(SOUTHWEST) == OPPONENT)) {
          return(SOUTH);
        }
        // if none of the condition above, then move east, and new condition becomes look(S)
        return(EAST);
      }

      // if ball is look(S)
      if (Look(SOUTH) == BALL) {
        // if there is an opponent in the west and the southwest, then kick the ball south
        if (Look(WEST) == OPPONENT && (Look(SOUTHWEST) == OPPONENT)) {
          return(KICK);
        }

        // if the ball is near the defending goal and there is opponent in either the south
        // or the southwest, then kick the ball south
        if ((x > 3 * FieldX() / 4) &&
            (Look(WEST) == OPPONENT || Look(SOUTHWEST) == OPPONENT)) {
          return(KICK);
        }

        // if there is at least one player above the leader and there is nothing in the southeast
        // move to southeast, condition now becomes look(W)
        if ((numPlayersAbove >= 0) && Look(SOUTHEAST) == EMPTY) {
          return(SOUTHEAST);
        }

        // if none of the condition is satisfied, go to east, condition now becomes look(S)
        return(EAST);
      }
      
      // if none of the condition above, just move towards the ball
      return(GetBallDirection());
    }

    public int NorthWing () {
      int x;
      int y;
      int ew = -1;
      int ns = -1;
      x = GetLocation().x;
      y = GetLocation().y;

      /* If near the ball, act like a leader */
      if (GetBallDistance() < 2) {
        return(Lead());
      }

      /* Try to get into position */
      if (Look(NORTH) == EMPTY && (y > ply[leader] - WINGSPAN)) {
        ns = NORTH;
      }
      if (Look(SOUTH) == EMPTY && (y < ply[leader] - WINGSPAN)) {
        ns = SOUTH;
      }
      if ((x < plx[leader]) && (y == ply[leader])) {
        ns = SOUTH;
      }
      if (Look(WEST) == EMPTY && (x > plx[leader] + WINGSPAN)) {
        ew = WEST;
      }
      if (Look(EAST) == EMPTY && (x < plx[leader] + WINGSPAN)) {
        ew = EAST;
      }

      if ((ew == EAST) && (ns == NORTH)) {
        return(NORTHEAST);
      }
      if ((ew == EAST) && (ns == SOUTH)) {
        return(SOUTHEAST);
      }
      if ((ew == WEST) && (ns == NORTH)) {
        return(NORTHWEST);
      }
      if ((ew == WEST) && (ns == SOUTH)) {
        return(SOUTHWEST);
      }
      if (ew == EAST) {
        return(EAST);
      }
      if (ew == WEST) {
        return(WEST);
      }
      if (ns == NORTH) {
        return(NORTH);
      }
      if (ns == SOUTH) {
        return(SOUTH);
      }

      return(GetBallDirection());
    }

    public int SouthWing () {
      int x;
      int y;
      int ew = -1;
      int ns = -1;
      x = GetLocation().x;
      y = GetLocation().y;

      /* If near the ball, act like a leader */
      if (GetBallDistance() < 2) {
        return(Lead());
      }

      /* Try to get into position */
      if (Look(NORTH) == EMPTY && (y > ply[leader] + WINGSPAN)) {
        ns = NORTH;
      }
      if (Look(SOUTH) == EMPTY && (y < ply[leader] + WINGSPAN)) {
        ns = SOUTH;
      }
      if ((x < plx[leader]) && (y == ply[leader])) {
        ns = NORTH;
      }
      if (Look(WEST) == EMPTY && (x > plx[leader] + WINGSPAN)) {
        ew = WEST;
      }
      if (Look(EAST) == EMPTY && (x < plx[leader] + WINGSPAN)) {
        ew = EAST;
      }

      if ((ew == EAST) && (ns == NORTH)) {
        return(NORTHEAST);
      }
      if ((ew == EAST) && (ns == SOUTH)) {
        return(SOUTHEAST);
      }
      if ((ew == WEST) && (ns == NORTH)) {
        return(NORTHWEST);
      }
      if ((ew == WEST) && (ns == SOUTH)) {
        return(SOUTHWEST);
      }
      if (ew == EAST) {
        return(EAST);
      }
      if (ew == WEST) {
        return(WEST);
      }
      if (ns == NORTH) {
        return(NORTH);
      }
      if (ns == SOUTH) {
        return(SOUTH);
      }

      return(GetBallDirection());
    }

    public int Rear() {
      return Lead();
    }

    public int getAction() {
      switch(ID)
        {
        case 1:
          return Player1();
        case 2:
          return Player2();
        case 3:
          return Player3();
        case 4:
          return Player4();
        }
        return BALL;
      }
    }
 



