
/*===========================================================================

	kickers.java

	Based upon a team by:
	Tucker Balch	

===========================================================================*/
public class kickers extends Player
{
    static int player_x[] = new int[5];
    static int player_y[] = new int[5];
    static boolean first_time = true;
    static int have_the_ball[] = new int[5];
    static int offensive = 0;
    
    public int getAction(int ID)
    {
	switch (ID) {
	case 1:
	    return player1(GetBallDirection(), GetLocation().x, GetLocation().y);
	case 2:
	    return player2(GetBallDirection(), GetLocation().x, GetLocation().y);
	case 3:
	    return player3(GetBallDirection(), GetLocation().x, GetLocation().y);
	default:
	    return player4(GetBallDirection(), GetLocation().x, GetLocation().y);
	}
    }


/*-----------------------------------------------------

	player1()

-----------------------------------------------------*/
    public int player1(int ball_direction, int x, int y)
    {
	int i;
	int my_id, partner_id;
	
	my_id = 1; partner_id = 2;
	
	player_x[my_id] = x;
	player_y[my_id] = y;
	
	if (first_time)
	    {
		first_time = false;
		for(i=0; i<=4; i++)
		    {
			player_x[i] = player_y[i] = 0;
			have_the_ball[i] = 0;
		    }
	    }
	
	for(i = 1; i <= 4; i++)
	    if (have_the_ball[i] == 1)
		offensive = 1;
	
	have_the_ball[my_id] = 0;
	if (Look(NORTH) == BALL) return(NORTHEAST);
	if (Look(NORTHEAST) == BALL) return(EAST);
	if (Look(EAST) == BALL) return(SOUTHEAST);
	if (Look(SOUTHEAST) == BALL) return(EAST);
	if (Look(SOUTH) == BALL) return(SOUTHEAST);
	if (Look(SOUTHWEST) == BALL) 
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	if (Look(WEST) == BALL)
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	if (Look(NORTHWEST) == BALL)
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	
	if (Look(NORTH) == BOUNDARY) return(ball_direction);
	
	if ((y >= (player_y[partner_id]-5))&&(ball_direction == NORTH)) return(NORTH);
	if ((y >= (player_y[partner_id]-5))&&(ball_direction == NORTHEAST)) return(NORTHEAST);
	if ((y >= (player_y[partner_id]-5))&&(ball_direction == EAST)) return(EAST);
	if ((y >= (player_y[partner_id]-5))&&(ball_direction == SOUTHEAST)) return(EAST);
	if ((y >= (player_y[partner_id]-5))&&(ball_direction == SOUTH)) return(PLAYER);
	if ((y >= (player_y[partner_id]-5))&&(ball_direction == SOUTHWEST)) return(SOUTHWEST);
	if ((y >= (player_y[partner_id]-5))&&(ball_direction == WEST)) return(WEST);
	if ((y >= (player_y[partner_id]-5))&&(ball_direction == NORTHWEST)) return(NORTHWEST);
	
	
	return(ball_direction);
    }
    
    /*-----------------------------------------------------
      
      player2()
      
      -----------------------------------------------------*/
    public int player2(int ball_direction, int x, int y)
    {
	int i;
	int my_id, partner_id;
	
	my_id = 2; partner_id = 3;
	
	player_x[my_id] = x;
	player_y[my_id] = y;

	have_the_ball[my_id] = 0;
	if (Look(NORTH) == BALL) return(NORTHEAST);
	if (Look(NORTHEAST) == BALL) return(EAST);
	if (Look(EAST) == BALL) return(SOUTHEAST);
	if (Look(SOUTHEAST) == BALL) return(EAST);
	if (Look(SOUTH) == BALL) return(SOUTHEAST);
	if (Look(SOUTHWEST) == BALL) 
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	if (Look(WEST) == BALL)
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	if (Look(NORTHWEST) == BALL)
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }

	if (Look(NORTH) == BOUNDARY) return(ball_direction);
	
	if (Look(NORTH) == BALL) return(NORTHEAST);
	if (Look(NORTHEAST) == BALL) return(EAST);
	if (Look(EAST) == BALL) return(SOUTHEAST);
	if (Look(SOUTHEAST) == BALL) return(EAST);
	if (Look(SOUTH) == BALL) return(SOUTHEAST);
	if (Look(SOUTHWEST) == BALL) return(KICK);
	if (Look(WEST) == BALL) return(KICK);
	if (Look(NORTHWEST) == BALL) return(KICK);
	
	return(ball_direction);
    }
    
    /*-----------------------------------------------------
      
      player3()
      
      -----------------------------------------------------*/
    public int player3(int ball_direction, int x, int y)
    {
	int i;
	int my_id, partner_id;
	
	my_id = 3; partner_id = 2;
	
	player_x[my_id] = x;
	player_y[my_id] = y;
	
	have_the_ball[my_id] = 0;
	if (Look(NORTH) == BALL) return(NORTHEAST);
	if (Look(NORTHEAST) == BALL) return(EAST);
	if (Look(EAST) == BALL) return(SOUTHEAST);
	if (Look(SOUTHEAST) == BALL) return(EAST);
	if (Look(SOUTH) == BALL) return(SOUTHEAST);
	if (Look(SOUTHWEST) == BALL) 
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	if (Look(WEST) == BALL)
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	if (Look(NORTHWEST) == BALL)
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	
	if (Look(NORTH) == BOUNDARY) return(ball_direction);
	
	if (Look(NORTH) == BALL) return(NORTHEAST);
	if (Look(NORTHEAST) == BALL) return(EAST);
	if (Look(EAST) == BALL) return(SOUTHEAST);
	if (Look(SOUTHEAST) == BALL) return(EAST);
	if (Look(SOUTH) == BALL) return(SOUTHEAST);
	if (Look(SOUTHWEST) == BALL) return(KICK);
	if (Look(WEST) == BALL) return(KICK);
	if (Look(NORTHWEST) == BALL) return(KICK);
	
	return(ball_direction);
    }

    /*-----------------------------------------------------
      
      player4()
      
      -----------------------------------------------------*/
    public int player4(int ball_direction, int x, int y)
    {
	int i;
	int my_id, partner_id;
	
	my_id = 4; partner_id = 3;
	
	player_x[my_id] = x;
	player_y[my_id] = y;
	
	have_the_ball[my_id] = 0;
	if (Look(NORTH) == BALL) return(NORTHEAST);
	if (Look(NORTHEAST) == BALL) return(EAST);
	if (Look(EAST) == BALL) return(SOUTHEAST);
	if (Look(SOUTHEAST) == BALL) return(EAST);
	if (Look(SOUTH) == BALL) return(SOUTHEAST);
	if (Look(SOUTHWEST) == BALL) 
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	if (Look(WEST) == BALL)
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	if (Look(NORTHWEST) == BALL)
	    {
		have_the_ball[my_id] = 1;
		return(KICK);
	    }
	
	if (Look(NORTH) == BOUNDARY) return(ball_direction);
	
	if (Look(NORTH) == BALL) return(NORTHEAST);
	if (Look(NORTHEAST) == BALL) return(EAST);
	if (Look(EAST) == BALL) return(SOUTHEAST);
	if (Look(SOUTHEAST) == BALL) return(EAST);
	if (Look(SOUTH) == BALL) return(SOUTHEAST);
	if (Look(SOUTHWEST) == BALL) return(KICK);
	if (Look(WEST) == BALL) return(KICK);
	if (Look(NORTHWEST) == BALL) return(KICK);
	
	if (Look(SOUTH) == BOUNDARY) return(ball_direction);
	
	/*printf("%d %d",y,player_y[2]);*/
	if ((y <= player_y[3]+5)&&(ball_direction == NORTH)) return(PLAYER);
	if ((y <= player_y[3]+5)&&(ball_direction == NORTHEAST)) return(EAST);
	if ((y <= player_y[3]+5)&&(ball_direction == EAST)) return(EAST);
	if ((y <= player_y[3]+5)&&(ball_direction == SOUTHEAST)) return(SOUTHEAST);
	if ((y <= player_y[3]+5)&&(ball_direction == SOUTH)) return(SOUTH);
	if ((y <= player_y[3]+5)&&(ball_direction == SOUTHWEST)) return(SOUTHWEST);
	if ((y <= player_y[3]+5)&&(ball_direction == WEST)) return(WEST);
	if ((y <= player_y[3]+5)&&(ball_direction == NORTHWEST)) return(NORTHWEST);
	
	return(ball_direction);
    }
    
    /*-----------------------------------------------------
      
      initialize_game()
      
      This function is called only once per
      game, before play begins.  You can leave it
      empty, or put code here to initialize your
      variables, etc.
      
      -----------------------------------------------------*/
    public void  InitializeGame()
    {
    }
    
    /*-----------------------------------------------------
      
      initialize_point()
      
      This function is called once per point, just
      before play begins for that point.
      You can leave it empty, or put code here to initialize 
      your variables, etc.
      
      -----------------------------------------------------*/
    public void  InitializePoint()
    {
    }
    
    /*-----------------------------------------------------
      
      lost_point()
      
      If your team loses a point, this function is
      called, otherwise, if you win the point, won_point()
      is called.  You can leave it empty, or put code here 
      for negative re-inforcement, etc.
      
      -----------------------------------------------------*/
    public void  LostPoint()
    {
    }

    /*-----------------------------------------------------
      
      won_point()
      
      If your team wins a point, this function is
      called, otherwise, if you lose the point, lost_point()
      is called.  You can leave it empty, or put code here 
      for positive re-inforcement, etc.
      
      -----------------------------------------------------*/
    public void  WonPoint()
    {
    }
    
    /*-----------------------------------------------------
      
      game_over()
      
      This function is called once at the end of
      the game.  You can leave it empty, or put code 
      here to save things to a file, etc.
      
      -----------------------------------------------------*/
    public void GameOver()
    {
    }
}
