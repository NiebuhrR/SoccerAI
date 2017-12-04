
/**
 * @author (Grace, Quyen, Kevin)
 */
public class TeamQGK extends Player {
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

        // setting the new lead
        leader = newLead;
        roles[leader] = Lead;

      /* southernmost unassigned player is south wing */
        score = -1;
        for (i=0; i<4; i++) {
            if (roles[i] == 0) { // if player is unassigned
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

    /////////////////////////////////////////////////////////////////////////

    // WHAT EACH ROLES DO:
    public int Lead () {
        int numOpponetsAbove = 0;
        int x = GetLocation().x;
        int y = GetLocation().y;

        if(Look(WEST) == BALL){
            //case 1: if there are a lot of opponents above leader, go N to kick SW
            //case 2: if there are a lof of opponents below leader, go S to kick NW

            //check for the number of opponents above leader
            for( i = 0; i < 4; i++){
                int opponentDirection = GetOpponentDirection(i);
                if(opponentDirection == NORTH || opponentDirection == NORTHEAST || opponentDirection == NORTHWEST){
                    numOpponetsAbove++;
                }
                else if(opponentDirection == SOUTH || opponentDirection == SOUTHEAST || opponentDirection == SOUTHWEST){
                    numOpponetsAbove--;
                }
            }

            // atleast 1 opponent above
            if(numOpponetsAbove > 0){
                return NORTH;
            }

            // atleast 1 opponent above
            if(numOpponetsAbove <0){
                return SOUTH;
            }

            //Now, we

        }

    }

    public int NorthWing () {

    }

    public int SouthWing () {

    }

    public int Rear() {

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
