
/**
 * @author (Grace, Quyen, Kevin)
 */
public class LlamasWithHats extends Player {

    static final int CONTROL_TIME = 13;
    static final int WINGSPAN = 8;

    // initialize haveBall to 0
    static int haveBall = 0;

    // two states stored as int, 1 for LEAD, 2 for SUPPORT
    static final int LEAD = 1;
    static final int SUPPORT = 2;
    static final int REAR = 3;
    static final int NORTHWING = 4;


    static int player_x[];       // keeps track of the x coord of each player
    static int player_y[];       // keeps track of the y coord of each player
    static int roles[];          // keep track of each player's role
    static int dist_to_ball[];   // keep track of distance from player to ball
    static int direct_to_ball[]; // keep track of direction from player to ball
    static int player_look[][];  // keep track of the cells adjacent to the player
    static int have_ball[];      // keep track of whether the player has the ball

    // initialize the game and the above variables
    public void InitializeGame () {
        
        player_x = new int[4];
        player_y = new int[4];
        roles = new int[4];
        dist_to_ball = new int[4];
        direct_to_ball = new int[4];
        player_look = new int[4][8];
        have_ball = new int[4];

    } // end InitializeGame

    // intialize the points
    public void InitializePoint () {

        // initialize every player's position to (0,0)
        for (int i = 0; i < 4; i++) {
            player_x[i] = 0;
            player_y[i] = 0;

        }
        
        // intialize every player's role
        roles[0] = LEAD;
        roles[1] = SUPPORT;
        roles[2] = SUPPORT;
        roles[3] = SUPPORT;
    
    } // end InitializePoint

    // function for player1
    public int Player1() {
        
        // initialize the initial action to WEST
        int action = WEST;
        
        // call the behave function to find each player's role
        Behave();
        
        // get position, distance to ball, have ball, and direction to ball for player 1
        player_x[0] = GetLocation().x;
        player_y[0] = GetLocation().y;
        dist_to_ball[0] = GetBallDistance();
        have_ball[0] = HaveBall(0);
        direct_to_ball[0] = GetBallDirection();
        
        // checks for the role of player 1
        switch (roles[0]) {
            case LEAD: action = Lead();
                break;
            case SUPPORT: action = Defensive();
                break;
            case NORTHWING: action = NorthWing();
                break;
            case REAR: action = Rear();
                break;
        }
        
        return action;
        
    } // end Player1()
    
    // function for player2
    public int Player2() {
        
        // initialize the initial action to WEST
        int action = WEST;
        
        // get position, distance to ball, have ball, and direction to ball for player 1
        player_x[1] = GetLocation().x;
        player_y[1] = GetLocation().y;
        dist_to_ball[1] = GetBallDistance();
        have_ball[1] = HaveBall(1);
        direct_to_ball[1] = GetBallDirection();
        
        // checks for the role of player 2
        switch (roles[1]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  Defensive();
                break;
            case NORTHWING: action = NorthWing();
                break;
            case REAR: action = Rear();
                break;
        }
        
        return action;
        
    } // end Player2()
    
    // function for player3
    public int Player3() {
        
        // initialize the initial action to WEST
        int action = WEST;
        
        // get position, distance to ball, have ball, and direction to ball for player 1
        player_x[2] = GetLocation().x;
        player_y[2] = GetLocation().y;
        dist_to_ball[2] = GetBallDistance();
        have_ball[2] = HaveBall(2);
        direct_to_ball[2] = GetBallDirection();
        
        // checks for the role of player 3
        switch (roles[2]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  Defensive();
                break;
            case NORTHWING: action = NorthWing();
                break;
            case REAR: action = Rear();
                break;
        }
        
        return action;
        
    } // end Player3()
    
    // function for player4
    public int Player4() {
        
        // initialize the initial action to WEST
        int action = WEST;
        
        // get position, distance to ball, have ball, and direction to ball for player 1
        player_x[3] = GetLocation().x;
        player_y[3] = GetLocation().y;
        dist_to_ball[3] = GetBallDistance();
        have_ball[3] = HaveBall(3);
        direct_to_ball[3] = GetBallDirection();
        
        // checks for the role of player 4
        switch (roles[3]) {
            case LEAD: action =  Lead();
                break;
            case SUPPORT: action =  Defensive();
                break;
            case NORTHWING: action = NorthWing();
                break;
            case REAR: action = Rear();
                break;
        }
        
        return action;
        
    } // end Player4()

    public void WonPoint () {};
    public void LostPoint () {};
    public void GameOver () {};

    // checks if the player has the ball
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
    } // end HaveBall

    // takes care of switching the roles for each player depending on their distance to the ball
    public void Behave () {
        
        // initialize newLead to 0
        int newLead = 0;

        // loop over all the player
        for(int i = 0; i < 4 ; i++){
            // if distance from the player to ball is 1
            if(dist_to_ball[i] == 1) {
                // prioritize players who are adjacent to the ball and in the NW or SW directions as leader
                if(direct_to_ball[i] == NORTHWEST || direct_to_ball[i] == SOUTHWEST){
                    newLead = i;
                    break;
                }
            }
            // else choose leader as the closest player to ball
            else if(dist_to_ball[i] < dist_to_ball[newLead]){
                newLead = i;
           }
        }
        
        // call regroup, which assign the proper role to each player
        Regroup(newLead);

    } // end Behave

    // function to reassign the role now that their indices are known
    public void Regroup (int newLead) {
        
        // initialize all roles to 0; this means that if roles[i] == 0, it is unassigned
        for(int i = 0; i < 4; i++){
            roles[i] = 0;
        }
        
        // set the new leader to player with index newLead
        if(have_ball[newLead] == 1){
            roles[newLead] = LEAD;
        }
        else {
            roles[newLead] = SUPPORT;
        }
        
        int rear = 0;
        int north = 0;
        int bottomY = 10000;
        
        for(int i = 0; i < 4; i++){
            if(dist_to_ball[i] > dist_to_ball[rear]){
                rear = i;
            }
            // if role unassigned, players are automatically supporters
            if(roles[i] == 0){
                roles[i] = SUPPORT;
                bottomY = player_y[i];
                north = i;
            }
        }
        
        
        
        for(int i = 0; i < 4; i++){
            // if role unassigned, players are automatically supporters
            if(roles[i] == 0){
                roles[i] = SUPPORT;
            }
        }
        
        // furthest away from ball becomes rear
        roles[rear] = REAR;
        roles[north] = NORTHWING;
        
        
    } // end Regroup
 

    /////////////////////////////////////////////////////////////////////////

    public int Rear(){

        // get the direction of the ball
        int ballDir = GetBallDirection();
        
        // get the y coordinate of the player
        int player_y = GetLocation().y;

        // if y coordinate of player is less that 5
        // move south
        if (player_y < 5){
            return SOUTH;
        }
        
        // if y coordinate of player is more than 75
        // move north
        else if (player_y > 70){
            if (Look(NORTH) == EMPTY) {
                return NORTH;
            }
            else if (Look(NORTHEAST) == EMPTY) {
                return NORTHEAST;
            }
            return PLAYER;
        }
        
        // if the distance to ball is more than 8
        // act like a defensive player
        else if (GetBallDistance() > 8) {
            return Defensive();
        }
        
        // if none of the condition is satisfied
        else{
            
            // initialize the number of opponents above the player to 0
            int numOpponentsAbove = 0;
            
            for (int i = 0; i < 4; i++){
                
                // get the opponent direction
                int opponentDirection = GetOpponentDirection(i);
                
                // if the opponent is in the north or northwest direction
                if (opponentDirection == NORTH || opponentDirection == NORTHWEST) {
                    // increment the number of opponents above player
                    numOpponentsAbove++;
                }
                
                // if the opponent is in the south or southwest directoion
                else if (opponentDirection == SOUTH || opponentDirection == SOUTHWEST) {
                    // decrement the number of opponents below player
                    numOpponentsAbove--;
                }
            }
            
            // if there are more opponents above than below, go north
            if(numOpponentsAbove > 1){
                return SOUTH;
            }
            
            // if there are more opponents below than above, go north
            else if (numOpponentsAbove < -1){
                return NORTH;
            }
            
            // if there is an equal number of players above and below
            else if (numOpponentsAbove == 0) {
                return Defensive();
            }
            
            // else just follow the ball's direction
            return ballDir;
        }

    } // end Rear
    
    public int NorthWing(){
        /*int player_y = GetLocation().y;
        if (player_y > 15 * FieldY() / 16){
            if (Look(NORTH) == EMPTY) {
                return NORTH;
            }
            else if (Look(NORTHEAST) == EMPTY) {
                return NORTHEAST;
            }
        }*/
        return Defensive();
    }

    // function to establish Defensive behavior
    public int Defensive(){
        
        // get the direction to ball
        int ballDir = GetBallDirection();
        
        // get the distance to ball
        int ballDist = GetBallDistance();
        
        // get the x_coordinate of the player
        int player_x = GetLocation().x;
        
        // if the player is near the goal, and the ball is in the NW, W, SW direction
        if (player_x < 9 && (ballDir == NORTHWEST || ballDir == WEST || ballDir == SOUTHWEST)
        && ballDist == 1) {
            return KICK;
        }

        // if the ball is in the north direction, if northeast is empty, go to northeast, then north
        // if northeast is not empty, just go north
        else if (ballDir == NORTH){
            if (Look(NORTHEAST) == EMPTY){
                return NORTHEAST;
            }
            return NORTH;
        }

        // if the ball is in the northeast direction, if northeast is empty, go to northeast, then north
        // if northeast is not empty, just go north
        else if (ballDir == NORTHEAST){
            if (Look(NORTHEAST) == EMPTY){
                return NORTHEAST;
            }
            return NORTH;
        }

        // if the ball is in the east direction, if east is empty, go to east, then northeast
        // if east is not empty, just go northeast
        else if (ballDir == EAST) {
            if (Look(EAST) == EMPTY) {
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
        // if southeast is not empty, just go east
        else if (ballDir == SOUTH) {
            if (Look(SOUTHEAST) == EMPTY) {
                return SOUTHEAST;
            }
            return EAST;
        }

        // if the ball is in the southeast direction, if southeast is empty, go southeast, then south
        // if southeast is not empty, then just go south --> the point is to get behind the ball
        else if (ballDir == SOUTHWEST) {
            if (Look(SOUTH) == EMPTY) {
                return SOUTH;
            }
            return SOUTHEAST;
        }

        // if the ball is in the west direction, and if there is a teammate in the west direction,
        // then go to northwest; if northwest is also blocked, then go to southwest; otherwise just go west
        else if (ballDir == WEST) {
            if(Look(WEST) == TEAMMATE) {
                if(Look(NORTH) == EMPTY) {
                    return NORTH;
                }
                else if(Look(SOUTH) == EMPTY) {
                    return SOUTH;
                }
            }
            return WEST;
        }
        
        // if the ball is in the northwest direction, and if northeast is empty, go to northeast, then north
        // if northeast is not empty, just go north
        else if (ballDir == NORTHWEST) {
            if (Look(NORTH) == EMPTY) {
                return NORTH;
            }
            return NORTHEAST;
        }
        
        else {
            // if none of the condition is satisfied, just followed the ball
            return ballDir;
        }
    } // end Defensive

    // function to establish Lead behavior
    public int Lead () {

        // get the ball's direction
        int ballDir = GetBallDirection();
        
        // get the y_coordinate of the player
        int player_y = GetLocation().y;

        // if there is a ball in the south direction
        if (Look(NORTH) == BALL) {
            
            // if the opponent are generally faraway
            if (GetOpponentDistance(1) > 4) {
                
                // if adjacent southeast cell is empty, go southeast
                if (Look(NORTHEAST) == EMPTY) {
                    return NORTHEAST;
                } else {
                    return EAST;
                }
            }
            
            // if there is an opponent in the southwest and west direction, but
            // no supporter in the southeast direction
            else if (Look(NORTHWEST) == OPPONENT || Look(WEST) == OPPONENT &&
                     Look(NORTHEAST) != TEAMMATE) {
                if (player_y > 4) {
                    return KICK;
                } else {
                    return NORTH;
                }
            }
            
            // if there is nothing in the southeast adjacent cell
            else if (Look(NORTHEAST) == EMPTY) {
                return NORTHEAST;
            }
            
            // if there is nothing in the east adjacent cell
            else if (Look(EAST) == EMPTY) {
                return EAST;
            }
            
            // if none of the condition above is satisfied
            else {
                if (Look(NORTHWEST) == EMPTY) {
                    return NORTHWEST;
                } else {
                    return WEST;
                }
            }
        }

        // if there is a ball in the northeast direction
        else if(Look(NORTHEAST) == BALL){
            
            // if there is nothing in the east adjacent cell, then go east
            if(Look(EAST) == EMPTY){
                return EAST;
            }
            // if not, then if there is nothing in the north adjacent cell, then go north
            // else go southeast
            else{
                if(Look(NORTH) == EMPTY){
                    return NORTH;
                }
                return SOUTHEAST;
            }
        }

        // if there is a ball in the east direction
        else if(Look(EAST) == BALL){
            
            // if there is nothing in the southeast adjacent cell, then go southeast
            if(Look(SOUTHEAST) == EMPTY) {
                return SOUTHEAST;
            }
            // if there is nothing in the northeast adjacent cell, then go northeast
            else if(Look(NORTHEAST) == EMPTY) {
                return NORTHEAST;
            }
            // if there is nothing in the north adjacent cell, then go north
            else if (Look(NORTH) == EMPTY) {
                return NORTH;
            }
            // if there is nothing in the south adjacent cell, then go south
            // otherwise go west
            else {
                if(Look(SOUTH) == EMPTY) {
                    return SOUTH;
                }
                return WEST;
            }
        }

        // if there is a ball in the southeast direction
        else if(Look(SOUTHEAST) == BALL) {
            
            // if there is nothing in the east adjacent cell, then go east
            if(Look(EAST) == EMPTY) {
                return EAST;
            }
            
            // if there is nothing in the south adjacent cell, then go south
            else if(Look(SOUTH) == EMPTY) {
                return SOUTH;
            }
            
            // if there is nothing in the northeast adjacent cell, then go northeast
            // otherwise go southwest (CHANGED)
            else {
                if(Look(SOUTHWEST) == EMPTY) {
                    return SOUTHWEST;
                }
                return NORTH;
            }
        }

        // if there is a ball in the south direction
        else if (Look(SOUTH) == BALL) {
            
            // if the opponent are generally faraway
            if (GetOpponentDistance(1) > 4) {
                
                // if adjacent southeast cell is empty, go southeast
                if (Look(SOUTHEAST) == EMPTY) {
                    return SOUTHEAST;
                } else {
                    return EAST;
                }
            
            }
            
            // if there is an opponent in the southwest and west direction, but
            // no supporter in the southeast direction
            else if (Look(SOUTHWEST) == OPPONENT || Look(WEST) == OPPONENT &&
                     Look(SOUTHEAST) != TEAMMATE) {
                if (player_y < 30) {
                    return KICK;
                } else {
                    return SOUTH;
                }
            }
            
            // if there is nothing in the southeast adjacent cell
            else if (Look(SOUTHEAST) == EMPTY) {
                return SOUTHEAST;
            }
            
            // if there is nothing in the east adjacent cell
            else if (Look(EAST) == EMPTY) {
                return EAST;
            }
            
            // if none of the condition above is satisfied
            else {
                if (Look(SOUTHWEST) == EMPTY) {
                    return SOUTHWEST;
                } else {
                    return WEST;
                }
            }
        }

        // if there is a ball in the southwest direction, kick
        else if (Look(SOUTHWEST) == BALL) {
            return KICK;
        }
        
        // if there is a ball in the west direction, kick
        else if (Look(WEST) == BALL){
            return  KICK;
        }
        
        // if there is a ball in the southwest direction, kick
        else if (Look(NORTHWEST) == BALL){
            return KICK;
        }
        
        // otherwise follow the ball
        else {
            return GetBallDirection();
        }

    } // end Lead

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
    } // end getAction

}

