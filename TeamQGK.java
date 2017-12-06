
/**
 * @author (Grace, Quyen, Kevin)
 */
public class TeamQGK extends Player {

    static final int CONTROL_TIME = 13;
    static final int WINGSPAN = 8;

    static int haveBall = 0;

    static final int LEAD = 1;
    static final int SUPPORT = 2;
    static final int REAR = 3;


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

        Behave();

        // My IFO
        player_x[0] = GetLocation().x;
        player_y[0] = GetLocation().y;
        dist_to_ball[0] = GetBallDistance();
        have_ball[0] = HaveBall(0);
        direct_to_ball[0] = GetBallDirection();

        //checks for the role of player 1
        switch (roles[0]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  defensive();
                break;
            case REAR: action = Rear();
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
        direct_to_ball[1] = GetBallDirection();

        switch (roles[1]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  defensive();
                break;
            case REAR: action = Rear();
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
        direct_to_ball[2] = GetBallDirection();

        switch (roles[2]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  defensive();
                break;
            case REAR: action = Rear();
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

        direct_to_ball[3] = GetBallDirection();


        switch (roles[3]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  defensive();
                break;
            case REAR: action = Rear();
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
            else if(dist_to_ball[i] < dist_to_ball[newLead]){
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
        if(have_ball[newLead] == 1){
            roles[newLead] = LEAD;
        }
        else {
            roles[newLead] = SUPPORT;
        }

        int maxIndex = 0;
        for(int i = 0; i < 4; i++){

            if(dist_to_ball[i] > dist_to_ball[maxIndex]){
                maxIndex = i;
            }
            //if role unassigned
            if(roles[i] == 0){
                roles[i] = SUPPORT;
            }
        }
        //if(have_ball[newLead] != 1){
            roles[maxIndex] = REAR;
        //}

    }

    /////////////////////////////////////////////////////////////////////////
    public int Rear(){

        int numOpponentsAbove = 0;
        for(int i = 0; i < 4; i++){
            int opponentDirection = GetOpponentDirection(i);
            if(opponentDirection == NORTH || opponentDirection == NORTHWEST){
                numOpponentsAbove++;
            }
            else if(opponentDirection == SOUTH || opponentDirection == SOUTHWEST){
                numOpponentsAbove--;
            }
        }

        if(GetBallDistance() > 7) {
            return defensive();
        }

        else if(numOpponentsAbove > 1){
            return SOUTH;
        }
        else if(numOpponentsAbove < -1 ){
            return NORTH;
        }

        return defensive();
    }


    public int defensive(){
        int ballDir = GetBallDirection();

        // if the ball is in the north direction, if northeast is empty, go to northeast, then north
        // if northeast is not empty, just go north
        if(ballDir == NORTH){
            if(Look(NORTHEAST) == EMPTY){
                return NORTHEAST;
            }
            return NORTH;
        }

        // if the ball is in the northeast direction, if northeast is empty, go to northeast, then north
        // if northeast is not empty, just go north
        else if(ballDir == NORTHEAST){
            if(Look(NORTHEAST) == EMPTY){
                return NORTHEAST;
            }
            return NORTH;
        }

        // if the ball is in the east direction, if east is empty, go to east, then northeast
        // if east is not empty, just go northeast
        else if(ballDir == EAST){
            if(Look(EAST) == EMPTY){
                return EAST;
            }
            return NORTHEAST;
        }

        // if the ball is in the southeast direction, if southeast is empty, go southeast, then south
        // if southeast is not empty, just go south
        else if (ballDir == SOUTHEAST) {
            if (Look(SOUTHEAST) == EMPTY) {
                return SOUTHEAST;
            }
            return SOUTH;
        }

        // if the ball is in the south direction, if southeast is empty, go southeast, then east
        // if southeast is not empty, just go east  --> this is a little wonky, but i want the new
        // condition becomes southwest...
        else if (ballDir == SOUTH) {
            if (Look(SOUTHEAST) == EMPTY) {
                return SOUTHEAST;
            }
            return EAST;
        }

        // if the ball is in the southwest direction, if southeast is empty, go southeast, then south
        // if southeast is not empty, then just go south --> the point is to get behind the ball
        else if (ballDir== SOUTHWEST) {
            if (Look(SOUTHEAST) == EMPTY) {
                return SOUTHEAST;
            }
            return SOUTH;
        }

        // if the ball is in the west direction, just go west
        else if (ballDir == WEST) {
            if(Look(WEST) == TEAMMATE){
                if(Look(NORTHWEST) == EMPTY){
                    return NORTHWEST;
                }
                else if(Look(SOUTHWEST) == EMPTY){
                    return SOUTHWEST;
                }
            }
            return WEST;
        }

        else if (ballDir == NORTHWEST) {
            if (Look(NORTHEAST) == EMPTY) {
                return NORTH;
            }
            return NORTH;
        }


        return ballDir;

    }

    // WHAT EACH ROLES DO:
    public int Lead () {
        int player_y = GetLocation().y;

        if(Look(NORTH) == BALL){
            if(Look(WEST) == OPPONENT || Look(NORTHWEST) == OPPONENT) {
                if(player_y > 5){
                    return KICK;
                }
                return NORTH;
            }
            // if the closest oppenent's distance is less than 3
            else if(GetOpponentDistance(1) < 3){
                return NORTH;
            }
            return NORTHEAST;
        }

        // If ball is North East
        else if(Look(NORTHEAST) == BALL){
            if(Look(EAST) == EMPTY){
                return EAST;
            }
            else{
                if(Look(NORTH) == EMPTY){
                    return NORTH;
                }

                return SOUTHEAST;

            }
        }

        //if ball is in East
        else if(Look(EAST) == BALL){
            if(Look(SOUTHEAST) == EMPTY){
                return SOUTHEAST;
            }
            else if( Look(NORTHEAST) == EMPTY){
                return NORTHEAST;
            }
            else {
                if(Look(SOUTH) == EMPTY) {
                    return SOUTH;
                }

                return WEST;

            }
        }

        // if ball in Sout East
        else if(Look(SOUTHEAST) == BALL){
            if(Look(EAST) == EMPTY){
                return EAST;
            }
            else if(Look(SOUTH) == EMPTY){
                return SOUTH;
            }
            else {
                if(Look(NORTH) == EMPTY){
                    return NORTH;
                }
                return SOUTHWEST;
            }
        }

        else if(Look(SOUTH) == BALL){
            if(Look(WEST) == OPPONENT || Look(SOUTHWEST) == OPPONENT){
                if(player_y < 30){
                    return KICK;
                }
                return SOUTH;
            }

            else  if(GetOpponentDistance(1) < 3){
                return SOUTH;
            }
            return SOUTHEAST;
        }

        else if(Look(SOUTHWEST) == BALL){
            return KICK;
        }
        else if(Look(WEST) == BALL){
            return  KICK;
        }
        else if(Look(NORTHWEST) == BALL){
            return KICK;
        }
        else {
            return GetBallDirection();
        }




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

    // team doesn't have ball

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

/* QUYEN'S NOTE: HOW TO FOLLOW BALL

My algorithm for how to follow the ball efficiently for supporters. These are designed to block the ball from 
opponents or cutting off as they are trying to get the ball
 
 // if the ball is in the north direction, if northeast is empty, go to northeast, then north
 // if northeast is not empty, just go north
 if direct_to_ball == NORTH {
 if Look(NORTHEAST) == EMPTY {
 return NORTHEAST;
 }
 return NORTH;
 }
 
 // if the ball is in the northeast direction, if northeast is empty, go to northeast, then north
 // if northeast is not empty, just go north
 else if direct_to_ball == NORTHEAST {
 if Look(NORTHEAST) == EMPTY {
 return NORTHEAST;
 }
 return NORTH;
 }
 
 // if the ball is in the east direction, if east is empty, go to east, then northeast
 // if east is not empty, just go northeast
 else if direct_to_ball == EAST {
 if Look(EAST) == EMPTY {
 return EAST;
 }
 return NORTHEAST;
 }
 
 // if the ball is in the southeast direction, if southeast is empty, go southeast, then south
 // if southeast is not empty, just go south
 else if direct_to_ball == SOUTHEAST {
 if Look(SOUTHEAST) == EMPTY {
 return SOUTHEAST;
 }
 return SOUTH;
 }
 
 // if the ball is in the south direction, if southeast is empty, go southeast, then east
 // if southeast is not empty, just go east  --> this is a little wonky, but i want the new
 // condition becomes southwest...
 else if direct_to_ball == SOUTH {
 if Look(SOUTHEAST) == EMPTY {
 return SOUTHEAST;
 }
 return EAST;
 }
 
 // if the ball is in the southwest direction, if southeast is empty, go southeast, then south
 // if southeast is not empty, then just go south --> the point is to get behind the ball
 else if direct_to_ball == SOUTHWEST {
 if Look(SOUTHEAST) == EMPTY {
 return SOUTHEAST;
 }
 return SOUTH;
 }
 
 // if the ball is in the west direction, just go west
 else if direct_to_ball == WEST {
 return WEST;
 }
 
 // if the ball is in the northwest direction, go northeast, then north
 // if northeast is not empty, then just go north
 else if direct_to_ball == NORTHWEST {
 if Look(NORTHEAST) {
 return NORTHEAST;
 }
 return NORTH;
 }
 
 // if none of the case above, just go towards the ball in the ball's direction
 else {
 return direct_to_ball;
 }
 */

/* MORE NOTES FROM QUYEN: Strategy for leader when it has a ball
 
 if Look(NORTH) == BALL {
    // case 1: if there is immediate danger; i.e. if opponent is in the west or southwest directions
    if Look(WEST) == OPPONENT or Look(NORTHWEST) == OPPONENT {
        // if the player is low enough on the board, just kick the ball upwards (north)
        if playerY > Y_THRESHOLD {
            return KICK;
        }
        // if the player is not low enough on the board, nudge the ball upwards (north)
        else {
            return NORTH;
        }
    }
    // case 2: if opponents are not adjacent to the ball but pretty are pretty near
    // nudge the ball upwards (north)
    else if dist_to_opponent < OPPONENT_THRESHOLD {
        return NORTH;
    }
    // if none of the case above, go northeast to get behind the ball
    else {
        return NORTHEAST;
    }
 }
*/


