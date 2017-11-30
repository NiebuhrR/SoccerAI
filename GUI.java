import java.awt.*;
import java.awt.event.*;

public class GUI extends Frame
{
    public GUI(World parent)
    {
        //  We need a name
        super("JavaSoccer");
        Parent = parent;
        //  Handle events like window closings/resizing/etc.
        EventHook = new Hook(this);
        addWindowListener(EventHook);
        addComponentListener(EventHook);
        //  guestimate a good initial size - it can always be changed by the user.   
        setSize(600, 400);
        setBackground(World.WINDOWCOLOR);
        //  use a border layout - everything centers around the field
        setLayout(new BorderLayout());
        //  Make our playing field and score board
        Field = new PlayingField(this, Parent);
        ScoreBrd = new ScoreBoard(this);
        MyMenu = new SoccerMenu(this, EventHook);
        Stepper = new Button("Step");
        add(Stepper, "South");
        Stepper.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Step();
                }
            });
        setMenuBar(MyMenu);
        show();
        Field.Resized();
    }

    /**  Called by the EventHook when the frame window is resized.  Notifies all GUI components. */

    protected void Resized()
    {
        Field.Resized();
    }

    public void ShowVictory(int Side)
    {
        if(Side==World.EAST)
            System.out.println("East won!");
        else
            System.out.println("West won!");
    }

    public void LoadTeam(int Side)
    {
        String filename;
        FileDialog LoadDialog = new FileDialog(this, "Load Team...", FileDialog.LOAD);
        /*  An interesting fact about the show method of FileDialog is that it does not return until
        the user finishes with the modal dialog.  This is NOT the normal behavior for java
        dialog boxes, but I'm sure not complaining. */
        LoadDialog.show();
        filename = LoadDialog.getFile();
        if(filename!=null)
        {
            if(filename.endsWith(".class"))
            {
                filename = filename.substring(0, filename.length() - 6);
            }
            if(Parent.LoadTeam(filename, Side))
            {
                //  The load was successful
                if(Side==World.EAST)
                    ScoreBrd.SetEastName(filename);
                else
                    ScoreBrd.SetWestName(filename);
            }
        }
    }

    public void SlowDown()  
    {
        Parent.SlowDown();
    }

    public void SpeedUp()
    {
        Parent.SpeedUp();
    }

    public void Pause()
    {
        Parent.Pause();
    }

    public void Step()
    {
        Parent.Step();
    }

    public void About()
    {
        //  Maybe if I'm feeling ambitious I'll add a dialog box here.
        System.out.println();   
        System.out.println("JavaSoccer 1.0");
        System.out.println("Eric Chown");
        System.out.println("Doug Vail");
        System.out.println();
    }

    public void StartThreads()
    {
        if(Parent.EastTeam!=null && Parent.WestTeam!=null)
        {
            Parent.GoAhead();
        }
        else
        {
            System.out.println("Either one or both teams has not been loaded.");
        }
    }

    public void WindowClosing()
    {
        Parent.WindowClosing();
    }

    public void toggleGraphics()
    {
        Field.toggleGraphics();
    }

    private Hook EventHook;
    private World Parent;
    private SoccerMenu MyMenu;
    private Button Stepper;

    public PlayingField Field;
    public ScoreBoard ScoreBrd;
}

class PlayingField extends Canvas
{
    protected PlayingField(GUI parent, World universe)
    {
        Parent = parent;
        DrawGraphics = true;
        Universe = universe;
        setBackground(World.FIELDCOLOR);
        Parent.add("Center", this);
    }

    public void toggleGraphics()
    {
        DrawGraphics = !DrawGraphics;

        Graphics gc;
        Rectangle CRect;

        gc = getGraphics();

        gc.clearRect(0, 0, FieldWidth, FieldHeight);

        paint(gc);

        gc.dispose();
    }

    public void BallRequest(int req)
    {
        if(DrawGraphics)
        {
            Graphics gc;
            Rectangle CRect;
            Point BallLocation;

            gc = getGraphics();
            gc.setClip(0, 0, FieldWidth, FieldHeight);
            BallLocation = Universe.SoccerBall.GetLocation();
            CRect = GetClippingRect(BallLocation.x, BallLocation.y);
            gc.clipRect(CRect.x, CRect.y, CRect.width, CRect.height);
            if(req==World.REQ_DRAWBALL)
                Universe.SoccerBall.Draw(gc, CRect);
            else if(req==World.REQ_ERASEBALL)
                Universe.SoccerBall.Erase(gc, CRect);
            gc.dispose();
        }
    }

    public void PlayerRequest(int req, Player player, Team team)
    {
        if(DrawGraphics)
        {
            Graphics gc;
            Rectangle CRect;
            Point Location;

            gc = getGraphics();
            gc.setClip(0, 0, FieldWidth, FieldHeight);
            Location = team.GetPlayerLocation(player);
            CRect = GetClippingRect(Location.x, Location.y);
            gc.clipRect(CRect.x, CRect.y, CRect.width, CRect.height);
            if(req==World.REQ_DRAWPLAYER)
                player.Draw(gc, CRect);
            else if(req==World.REQ_ERASEPLAYER)
                player.Erase(gc, CRect);
            gc.dispose();
        }
    }

    public void paint(Graphics gc)
    {
        if(DrawGraphics)
        {
            int counter;
            Point PlayerLocation;
            Point BallLocation;
            Player Jock;
            Rectangle CRect;
            Parent.ScoreBrd.UpdateText();
            if(Universe.EastTeam!=null && Universe.WestTeam!=null)
            {
                for(counter=1; counter<5; counter++)
                {
                    //  First do the east side
                    //  Expand the clipping rectangle to allow drawing anywhere.  I'll shrink it for each player in a bit.
                    gc.setClip(0, 0, FieldWidth, FieldHeight);
                    PlayerLocation = Universe.EastTeam.GetPlayerLocation(counter);
                    Jock = Universe.EastTeam.GetPlayer(counter);
                    CRect = GetClippingRect(PlayerLocation.x, PlayerLocation.y);
                    //  We do NOT want players drawing off their own square.
                    gc.clipRect(CRect.x, CRect.y, CRect.width, CRect.height);
                    Jock.Draw(gc, CRect); 

                    //  Now let's draw the west team
                    //  Expand the clipping rectangle to allow drawing anywhere.  I'll shrink it for each player in a bit.
                    gc.setClip(0, 0, FieldWidth, FieldHeight);
                    PlayerLocation = Universe.WestTeam.GetPlayerLocation(counter);
                    Jock = Universe.WestTeam.GetPlayer(counter);
                    CRect = GetClippingRect(PlayerLocation.x, PlayerLocation.y);
                    //  We do NOT want players drawing off their own square.
                    gc.clipRect(CRect.x, CRect.y, CRect.width, CRect.height);
                    Jock.Draw(gc, CRect);   
                }
            } // end non-null teams

            //  Draw Sidelines and Goallines
            gc.setClip(0, 0, FieldWidth, FieldHeight);
            gc.setColor(World.GOALCOLOR);
            gc.fillRect(0, 0, SquareWidth, FieldHeight);
            gc.fillRect(FieldWidth - SquareWidth, 0, SquareWidth, FieldHeight);
            gc.setColor(World.SIDECOLOR);
            gc.fillRect(0, 0, FieldWidth, SquareHeight);
            gc.fillRect(0, FieldHeight - SquareHeight, FieldWidth, SquareHeight);

            //  Fill in the extra space below and to the right of the field
            gc.setClip(0, 0, getBounds().width, getBounds().height);
            gc.setColor(World.WINDOWCOLOR);
            gc.fillRect(FieldWidth, 0, getBounds().width, getBounds().height);
            gc.fillRect(0, FieldHeight, FieldWidth, getBounds().height);

            //  It's always nice to remember the ball:
            gc.setClip(0, 0, FieldWidth, FieldHeight);
            BallLocation = Universe.SoccerBall.GetLocation();
            CRect = GetClippingRect(BallLocation.x, BallLocation.y);
            gc.clipRect(CRect.x, CRect.y, CRect.width, CRect.height);
            Universe.SoccerBall.Draw(gc, CRect);
        }
    }    

    /** Returns the rectangle around a given space on the field grid in the form of:
    X coord,
    Y coord,
    Width,
    Height. */

    Rectangle GetClippingRect(int PlayerX, int PlayerY)
    {
        return new Rectangle(PlayerX*SquareWidth, PlayerY*SquareHeight, SquareWidth, SquareHeight);
    }

    /** Callback which gets called when the window is resized. */
    protected void Resized()
    {
        FieldWidth = getBounds().width;
        FieldHeight = getBounds().height;
        SquareWidth = FieldWidth/World.XSQUARES;
        SquareHeight = FieldHeight/World.YSQUARES;

        //  We need to correct for the round off error in the above division.
        FieldWidth = SquareWidth*World.XSQUARES;
        FieldHeight = SquareHeight*World.YSQUARES;
    }

    private GUI Parent;
    private World Universe;
    private int SquareWidth;
    private int SquareHeight;
    private int FieldWidth;
    private int FieldHeight;
    private boolean DrawGraphics;
}

class ScoreBoard extends Label
{
    protected ScoreBoard(GUI parent)
    {
        Parent = parent;
        setBackground(World.WINDOWCOLOR);
        setAlignment(Label.CENTER);
        Parent.add("North", this);
        EastName = "No team loaded";
        WestName = EastName;
        EastScore = 0;
        WestScore = 0;
        UpdateText();
    }

    public void UpdateText()
    {
        String NewText = new String();
        NewText = WestName + " " + Integer.toString(WestScore) + " vs " + EastName + " " + Integer.toString(EastScore);    
        setText(NewText);
    }

    protected void EastScored()
    {
        EastScore++;
        UpdateText();
    }

    protected void SetEastName(String name)
    {
        EastName = new String(name);
        UpdateText();
    }

    protected void SetWestName(String name)
    {
        WestName = new String(name);
        UpdateText();
    }

    protected void WestScored()
    {
        WestScore++;
        UpdateText();
    }

    protected void ResetScore()
    {
        EastScore = 0;
        WestScore = 0;
        UpdateText();
    }

    private GUI Parent;
    private String EastName;
    private String WestName;
    private int EastScore;
    private int WestScore; 
}

class Hook extends WindowAdapter implements ComponentListener, ActionListener
{
    Hook(GUI parent)
    {
        super();
        Parent = parent;
    }

    public void windowClosing(WindowEvent e)
    {
        Parent.WindowClosing();
        System.exit(0);
    }

    public void componentHidden(ComponentEvent e) { }

    public void componentMoved(ComponentEvent e) { }

    public void componentShown(ComponentEvent e) { }

    public void componentResized(ComponentEvent e)
    {
        Parent.Resized();
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("EXIT"))
            System.exit(0);
        else if(e.getActionCommand().equals("ABOUT"))
            Parent.About();
        else if(e.getActionCommand().equals("LOADEAST"))
            Parent.LoadTeam(World.EAST);
        else if(e.getActionCommand().equals("LOADWEST"))
            Parent.LoadTeam(World.WEST);
        else if(e.getActionCommand().equals("START"))
            Parent.StartThreads();
        else if (e.getActionCommand().equals("PAUSE"))
            Parent.Pause();
        else if (e.getActionCommand().equals("STEP"))
            Parent.Step();
        else if(e.getActionCommand().equals("FASTER"))
            Parent.SpeedUp();
        else if(e.getActionCommand().equals("SLOWER"))
            Parent.SlowDown();
        else if(e.getActionCommand().equals("TOGGLE"))
            Parent.toggleGraphics();
    }

    GUI Parent;
}

class SoccerMenu extends MenuBar
{
    public SoccerMenu(GUI parent, Hook hook)
    {
        super();
        Parent = parent;
        EventHook = hook;
        Menu TempMenu;
        MenuItem TempItem;

        //  Build our file menu
        TempMenu = add(new Menu("File"));

        TempItem = new MenuItem("Load East Team...");
        TempItem.addActionListener(EventHook);
        TempItem.setActionCommand("LOADEAST");
        TempMenu.add(TempItem);

        TempItem = new MenuItem("Load West Team...");
        TempItem.addActionListener(EventHook);
        TempItem.setActionCommand("LOADWEST");
        TempMenu.add(TempItem);

        TempMenu.addSeparator();   

        TempItem = new MenuItem("Exit");
        TempItem.addActionListener(EventHook);
        TempItem.setActionCommand("EXIT");
        TempMenu.add(TempItem);

        //  build our Game menu
        TempMenu = add(new Menu("Game"));

        TempItem = new MenuItem("Start");
        TempItem.addActionListener(EventHook);
        TempItem.setActionCommand("START");
        TempMenu.add(TempItem);

        TempItem = new MenuItem("Pause");
        TempItem.addActionListener(EventHook);
        TempItem.setActionCommand("PAUSE");
        TempMenu.add(TempItem);

        TempItem = new MenuItem("Step");
        TempItem.addActionListener(EventHook);
        TempItem.setActionCommand("STEP");
        TempMenu.add(TempItem);

        TempItem = new MenuItem("Faster");
        TempItem.addActionListener(EventHook);
        TempItem.setActionCommand("FASTER");
        TempMenu.add(TempItem);

        TempItem = new MenuItem("Slower");
        TempItem.addActionListener(EventHook);
        TempItem.setActionCommand("SLOWER");
        TempMenu.add(TempItem);

        TempItem = new MenuItem("Toggle Graphics");
        TempItem.addActionListener(EventHook);
        TempItem.setActionCommand("TOGGLE");
        TempMenu.add(TempItem);

        //  build our help menu
        TempMenu = add(new Menu("Help"));

        TempItem = new MenuItem("About...");
        TempItem.addActionListener(EventHook);
        TempItem.setActionCommand("ABOUT");
        TempMenu.add(TempItem);

    }

    GUI Parent;
    Hook EventHook;
}




