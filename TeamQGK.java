
/**
 * @author (Grace, Quyen, Kevin)
 */
public class TeamQGK extends Player {

    static final int CONTROL_TIME = 13;
    static final int WINGSPAN = 8;

    static int haveBall;

    static final int LEAD = 1;
    static final int SUPPORT = 2;


    static int player_x[]; // keeps track of the x coord of each player
    static int player_y[]; // keeps track of the y coord of each player
    static int roles[];
    static int dist_to_ball[];
    static int direct_to_ball[];
    static int player_look[][];
    static int have_ball[];


    //initialize game
    public void InitializeGame () {
        player_x = new int[4];
        player_y = new int[4];
        roles = new int[4];
        dist_to_ball = new int[4];
        direct_to_ball = new int[4];
        player_look = new int[4][8];
        have_ball = new int[4];

    }

    public void InitializePoint () {

        for (int i = 0; i < 4; i++) {
            player_x[i] = 0;
            player_y[i] = 0;

        }
        roles[0] = LEAD;
        roles[1] = SUPPORT;
        roles[2] = SUPPORT;
        roles[3] = SUPPORT;
    }

    public int Player1() {

        int action = WEST;

        // My IFO
        player_x[0] = GetLocation().x;
        player_y[0] = GetLocation().y;
        dist_to_ball[0] = GetBallDistance();
        have_ball[0] = HaveBall(0);

        //checks for the role of player 1
        switch (roles[0]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  Support();
                break;
        }
        return action;
    }

    public int Player2() {

        int action = WEST;

        //MY INFO
        player_x[1] = GetLocation().x;
        player_y[1] = GetLocation().y;
        dist_to_ball[1] = GetBallDistance();
        have_ball[1] = HaveBall(1);

        switch (roles[1]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  Support();
                break;
        }
        return action;
    }

    public int Player3() {

        int action = WEST;

        //MY INFO
        player_x[2] = GetLocation().x;
        player_y[2] = GetLocation().y;
        dist_to_ball[2] = GetBallDistance();
        have_ball[2] = HaveBall(2);

        switch (roles[2]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  Support();
                break;
        }
        return action;
    }

    public int Player4() {
        int action = WEST;

        //MY INFO
        player_x[3] = GetLocation().x;
        player_y[3] = GetLocation().y;
        dist_to_ball[3] = GetBallDistance();
        have_ball[3] = HaveBall(3);


        switch (roles[3]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  Support();
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
        int newLead = 0;

        for(int i = 0; i < 4 ; i++){
            if(dist_to_ball[i] == 1) { //1 is not the final number here

                // want to prioritize NE and SE players to be lead
                if(direct_to_ball[i] == NORTHEAST || direct_to_ball[i] == SOUTHEAST){
                    newLead = i;
                    break;
                }
            }
            else if(direct_to_ball[i] < direct_to_ball[newLead]){
                newLead = i;
            }
        }
        Regroup(newLead);

    }

    // reassigns roles
    public void Regroup (int newLead) {
        //initialize roles --> all unassigned
        for(int i = 0; i < 4; i++){
            roles[i] = 0;
        }

        //set new leader
        roles[newLead] = LEAD;

        for(int i = 0; i < 4; i++){
            //if role unassigned
            if(roles[i] == 0){
                roles[i] = SUPPORT;
            }
        }

    }

    /////////////////////////////////////////////////////////////////////////

    // WHAT EACH ROLES DO:
    public int Lead () {
        int numOpponetsAbove = 0;
        int x = GetLocation().x;
        int y = GetLocation().y;

        //case 1: if there are a lot of opponents above leader, go N to kick SW
        //case 2: if there are a lof of opponents below leader, go S to kick NW

        //check for the number of opponents above leader
        for(int i = 0; i < 4; i++){
            int opponentDirection = GetOpponentDirection(i);
            if(opponentDirection == NORTH || opponentDirection == NORTHEAST || opponentDirection == NORTHWEST){
                numOpponetsAbove++;
            }
            else if(opponentDirection == SOUTH || opponentDirection == SOUTHEAST || opponentDirection == SOUTHWEST){
                numOpponetsAbove--;
            }
        }

        if(Look(WEST) == BALL){
            // at least 1 opponent above
            if(numOpponetsAbove > 0){
                return NORTH;
            }

            // atleast 1 opponent above
            if(numOpponetsAbove <0){
                return SOUTH;
            }
        }

        if(Look(NORTHWEST) == BALL || Look(SOUTHWEST) == BALL){
            return KICK;
        }
        return(GetBallDirection());
    }

    public int Support () {

        return(GetBallDirection());
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
