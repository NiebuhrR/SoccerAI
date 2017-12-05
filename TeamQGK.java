
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
        int hasBall = 0;

        for(int i = 0; i < 4 ; i++){
            if(dist_to_ball[i] == 1) { //1 is not the final number here

                // want to prioritize NE and SE players to be lead
                if(direct_to_ball[i] == NORTHEAST || direct_to_ball[i] == SOUTHEAST){
                    newLead = i;
                    hasBall = 1;
                    break;
                }
            }
            else if(direct_to_ball[i] < direct_to_ball[newLead]){

                if(dist_to_ball[i] == 1){
                    hasBall = 1;
                }

                newLead = i;
            }
        }
        Regroup(newLead, hasBall);

    }

    // reassigns roles
    public void Regroup (int newLead, int hasBall) {
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


/*
Leader

CASE 1: Leader doesn’t have ball
    * want to move towards ball
    * return GetBallDirection()

CASE 2: Leader has ball

    a) an opponent is blocking us from kicking the ball (in any direction)

        if(opponentDir == ballDir && opponentDist[i] == 2) {
            * want to be a blocker, so opponent can't kick from their
            * want other team member to rescue us: let support move toward ball
            * leader stays the same, closest support acts like the leader
        }

    b) if none of the opponents are blocking us from kicking the ball
    
        i. if at least one opponent is really close to us but we're not block:
            (meaning opponent can easily steal ball)
            * idk what to do?
            * maybe, just kick the ball using ballDir?

        ii. if(GetOpponentDistance(1) > 3){ //checks for closet opponent
            * check where we have the most opponents (N or S)

                if(numOppAbove >= 2) { //at least 2 above
                    * want to kick to southward (S or SW)
                    if (ballDir == S or SW ){ Kick } //if leader is N or NE of ball, getBallDir == S or SW

                    if (ballDir == W){ // if leader is East of ball
                        * want to move N so I can kick SW
                        * tell support that leader is passing SW so they can go towards that area
                    }

                }
                if(numOppAbove <= -2) { //at least 2 below
                    * want to kick to southward (N or NW)
                    if (ballDir from Leader == N or NW ){ Kick } //if leader is S or SE of ball, getBallDir == N or NW
                    if (ballDir from Leader == W){
                        * want to move S so I can kick NW
                        * tell support that leader is passing NW so they can go towards that area
                    }
                }
        }


SUPPORT

Case 1: team has ball
    * if(dist_to_ball == 1) { act like a leader with ball; }
    * if leader is about to pass, start moving to where the pass will be (would need a new "passing_N/S" state)

Case 2: team doesn't have bal;
    * want to move towards ball
    * return GetBallDirection()

ADDITION TO CODE:
- behave keeps track if leader has ball because IDK how to check that in the Lead() function since we
    don't know which player is the leader

*/
