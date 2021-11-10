/**Sean Devine Minesweeper game V9 (improved final version)
  * Friday May, 31 2019
  * The game consists of an adjustable square grid. The game starts with every square covered and the user left clicks on 
  * any square to start the game. When a user left clicks a square, the square is either blank, contains a mine on it 
  * or has one or more mines next to it (including corners); the first click will not contain a mine. If the user right
  * clicks on a blank square, the program will automatically clear all the blank squares around it and the squares 
  * touching the outermost cleared blank squares (squares with numbers on them). If the user left clicks on a square
  * with a mine on it , the game ends, all the mines are displayed and the user loses. If a square
  * has any amount of mines next to it, the square will display a number that says that amount. The objective of the 
  * game is to clear every square that does not have a mine on it. The user also has the option to use flags; by right
  * clicking on a square, the program will put a flag on the square marking that it has a mine and the user will not be
  * able to accidentally left click on that square. The program will not stop you from placing a flag if it is wrong, 
  * and the flag can be removed by right clicking on the square containing the flag again. The program also gives a 
  * user a set number of flags equal to the amount of mines on the grid and each time a flag is used the number goes 
  * down. Also the game displays a timer counting how many seconds the user has been playing since they clicked their
  * first square, the timer will not stop the player, but used for player to time themselves.
  */

import hsa_ufa.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


class UseMouseProgram implements MouseListener, MouseMotionListener, ActionListener, KeyListener{
  
  //creates variable for all images used
  public Image pic1;
  public Image pic2;
  public Image pic3;
  public Image pic4;
  public Image pic5;
  public Image pic6;
  public Image pic7;
  public Image pic8;
  public Image picMine;
  public Image picFlag;
  public Image background;
  public Image options;
  public Image optionsHighlighted;
  public Image death;
  public Image win;
  public Image numberExample;
  public Image flagExample;
  
  
  //declares all global variables and arrays
  Console c;
  Timer timer;
  int sec;
  int delaySec = 0;
  int playerKeyCode;
  int oldX;
  int oldY;
  int imageShift = 10;
  int tilesClickedTotal;
  int totalBombs = 30;
  int trueArraySize = 252;
  int xArrayLength = 18;
  int yArrayLength = 14;
  int gridlineLength = 4;
  int squareSize = 36;
  char playerKeyChar;
  ArrayList<Character> keyList;
  boolean start = true; 
  boolean alive = true;
  boolean clearingTiles;
  boolean stillTiles;
  boolean justStarting;
  boolean dying = false;
  boolean doDeathDelay=false;
  boolean doWinDelay=false;
  boolean justWon=false;
  String difficulty = "Easy";
  String gridSize = "Medium";
  int[] tileStatus = new int[567];
  int[] bombTiles = new int[92];
  int totalFlags = 30;
  boolean[] isFlag = new boolean[567];
  boolean[] isTilePressed = new boolean[567];
  
  //declares global boolean variables to identify the part of the program that the user is viewing
  boolean onMenuScreen = true;
  boolean onGameScreen = false;
  boolean onTempScreen = false;
  boolean onOptionsScreen = false;
  boolean onInstructionsScreen = false;
  boolean onPage1 = false;
  boolean onPage2 = false;
  boolean onPage3 = false;
  
  
  
  //Custom fonts and colours
  Font timerFont = new Font("Arial", Font.PLAIN, 45);
  Font menuHead = new Font("Verdana", Font.PLAIN, 90);
  Font menuSubtitle = new Font("Verdana", Font.PLAIN, 45);
  Font instructionSubtitle = new Font ("Calibri",Font.PLAIN,27);
  Font instruction = new Font("Verdana", Font.PLAIN, 20);
  Font winOrLose = new Font ("Verdana", Font.PLAIN, 80);
  Color coolGrey = new Color(204, 204, 204);
  Color darkGrey = new Color(115, 115, 115);
  Color mediumGrey = new Color(191, 191, 191);
  Color lightGrey = new Color(242, 242, 242);
  Color babyBlue = new Color(12,164,255);
  
  Toolkit tk = Toolkit.getDefaultToolkit ();
  
  public UseMouseProgram(){
    
    //initializes all image variables
    pic1= tk.getImage ("minesweeper1.png");
    pic2= tk.getImage ("minesweeper2.png");
    pic3= tk.getImage ("minesweeper3.png");
    pic4= tk.getImage ("minesweeper4.png");
    pic5= tk.getImage ("minesweeper5.png");
    pic6= tk.getImage ("minesweeper6.png");
    pic7= tk.getImage ("minesweeper7.png");
    pic8= tk.getImage ("minesweeper8.png");
    picMine= tk.getImage ("mine.png");
    picFlag= tk.getImage ("flag.png");
    background = tk.getImage("background.jpg");
    options = tk.getImage("options.png");
    optionsHighlighted = tk.getImage("optionsHighlighted.png");
    death = tk.getImage("death.jpg");
    win = tk.getImage("win.jpg");
    numberExample = tk.getImage("numberExample.png");
    flagExample = tk.getImage("flagExample.png");
    
    
    
    keyList = new ArrayList<Character>();
    c =  new Console(708,674);
    sec = 0;
    timer = new Timer(1000, this);
    c.addMouseListener(this);
    c.addMouseMotionListener(this);
    c.addKeyListener(this);
  }
  
  //starts program
  public static void main(String[] args){
    UseMouseProgram prog = new UseMouseProgram();
    prog.startGame(); 
  }
  
  
  public void startGame(){
    
    //user is on menu screen, it is printed using method
    if (onMenuScreen)
      printMenuScreen();
    
    //user is on game screen, it is printed using method
    if (onGameScreen)
      printStartScreen();
    
  }
  
  
  public void mouseExited(MouseEvent e){}
  public void mouseEntered(MouseEvent e){ }
  public void mouseClicked(MouseEvent e){
    
    //if user is in game and alive:
    if (onGameScreen && alive){
      
      //program checks if user clicks in game coordinates
      if ((30 <= e.getX() && e.getX() <= 677) && (165 <= e.getY() && e.getY() <= 669)){
        
        //makes tile not get instantly pressed upon user starting game
        if (!justStarting || (justStarting && (e.getX() != oldX || e.getY() != oldY ))) {
          int clickArrayIndex = coordsToArrayIndex(e.getX(), e.getY());
          justStarting = false;
          
          //if user right clicks program places or removes flag
          if (SwingUtilities.isRightMouseButton(e))
            doFlags(clickArrayIndex);
          
          //if user left clicks program clears tile where user clicked
          if (SwingUtilities.isLeftMouseButton(e))
            doPressTile(clickArrayIndex);
        }
      }        
      
    }
  }  
  public void mouseReleased(MouseEvent e){}
  
  
  //detects when mouse is pressed
  public void mousePressed(MouseEvent e){
    
    //when user is on the menu screen:
    if (onMenuScreen){
      
      //if user presses start game button: 
      if (e.getX() >= 210 && e.getX() <= 498 && e.getY() >= 225 && e.getY() <= 305){
        
        //program is on game screen now and not menu screen. 
        onMenuScreen = false;
        onGameScreen = true;
        
        //program sets all variables to be the status necessary to start the game
        
        resetVariables();
        oldX = e.getX();
        oldY = e.getY();
        
        
        //program loads game
        startGame();
      }
      
      //if user presses instructions button, program prints instruction screen
      if (e.getX() >= 210 && e.getX() <= 498 && e.getY() >= 345 && e.getY() <= 425){
        
        //user is on instruction screen now and not menu
        onMenuScreen = false;
        onTempScreen = true;
        
        printInstructionScreen();
        
      }
      //if user clicks on quit button, program shuts down
      if (e.getX() >= 210 && e.getX() <= 498 && e.getY() >= 465 && e.getY() <= 545){
        
        c.close();
      }
      
      if (e.getX() >= 508 && e.getX() <= 588 && e.getY() >= 465 && e.getY() <= 545){
        
        onMenuScreen = false;
        
        onOptionsScreen = true;
        
        printOptionsScreen();
      }
      
    }
    
    //if a user is on a temporary screen:
    if (onTempScreen){
      
      //if user clicks on back to menu button program opens main menu
      if (e.getX() >= 140 && e.getX() <= 568 && e.getY() >= 601 && e.getY() <= 662){
        
        //user is now on main menu not temporary screen
        onTempScreen = false;
        onInstructionsScreen = false;
        onMenuScreen = true;
        
        startGame();
      }
      
      
    }
    
    //if user is on the instruction screen:
    if (onInstructionsScreen){
      
      //if user isn't on page 1 and clicks on page one, program goes to page 1
        if (!onPage1 && e.getX() >= 325 && e.getX() <= 338 && e.getY() >= 542 && e.getY() <= 596){
          
          onPage1 = true;
          onPage2 = false;
          onPage3 = false;
          
          
          printInstructionsPage1();
        }
        
        //if user isn't on page 2 and clicks on page one, program goes to page 2
        if (!onPage2 && e.getX() >= 351 && e.getX() <= 364 && e.getY() >= 542 && e.getY() <= 596){
          
          onPage1 = false;
          onPage2 = true;
          onPage3 = false;
          
          printInstructionsPage2();
        }
        
        //if user isn't on page 3 and clicks on page one, program goes to page 3
        if (!onPage3 && e.getX() >= 377 && e.getX() <= 390 && e.getY() >= 542 && e.getY() <= 596){
          
          onPage1 = false;
          onPage2 = false;
          onPage3 = true;
          
          printInstructionsPage3();
        }
        
      }
      
    
    //if the user is on the option screen:
    if (onOptionsScreen){
      
      //if user changes grid size program adjusts variables to do so
      if (e.getX() >= 210 && e.getX() <= 498 && e.getY() >= 225 && e.getY() <= 305){
        
        if (gridSize.equals("Medium")){
          
          gridSize = "Large";
          
          c.setColor(Color.white);
          c.fillRect(210,200,288,80);
          
          c.setColor(Color.black);
          c.drawString(gridSize,220,255);
          
          imageShift = 4;
          trueArraySize = 567;
          xArrayLength = 27;
          yArrayLength = 21;
          gridlineLength = 4;
          squareSize = 24;
          
          findTotalBombs();      
          totalFlags = totalBombs;
        }
        
        else if (gridSize.equals("Large")){
          
          gridSize = "Small";
          
          c.setColor(Color.white);
          c.fillRect(210,200,288,80);
          
          c.setColor(Color.black);
          c.drawString(gridSize,220,255);
          
          imageShift = 28;
          trueArraySize = 63;
          xArrayLength = 9;
          yArrayLength = 7;
          gridlineLength = 4;
          squareSize = 72;
          
          
          findTotalBombs();
          totalFlags = totalBombs;
        }
        
        else if (gridSize.equals("Small")){
          
          gridSize = "Medium";
          
          c.setColor(Color.white);
          c.fillRect(210,200,288,80);
          
          c.setColor(Color.black);
          c.drawString(gridSize,220,255);
          
          imageShift = 10;
          trueArraySize = 252;
          xArrayLength = 18;
          yArrayLength = 14;
          gridlineLength = 4;
          squareSize = 36;
          
          findTotalBombs();
          totalFlags = totalBombs;
          
        }
        
        
        
      }
      
      
      //if user changes difficulty, program adjusts variables to do so
      if (e.getX() >= 210 && e.getX() <= 498 && e.getY() >= 345 && e.getY() <= 425){
        
        
        if (difficulty.equals("Normal")){
          
          difficulty = "Hard";
          
          c.setColor(Color.white);
          c.fillRect(210,320,288,80);
          
          c.setColor(Color.black);
          c.drawString(difficulty,220,375);
          
          findTotalBombs();
          
        }
        
        else if (difficulty.equals("Hard")){
          
          difficulty = "Easy";
          
          c.setColor(Color.white);
          c.fillRect(210,320,288,80);
          
          c.setColor(Color.black);
          c.drawString(difficulty,220,375);
          
          
          findTotalBombs();
        }
        
        else if (difficulty.equals("Easy")){
          
          difficulty = "Normal";
          
          c.setColor(Color.white);
          c.fillRect(210,320,288,80);
          
          c.setColor(Color.black);
          c.drawString(difficulty,220,375);
          
          findTotalBombs();
          
        }
        
        
      }
      
      //if user clicks back to menu program sends user to main menu
      if (e.getX() >= 140 && e.getX() <= 568 && e.getY() >= 601 && e.getY() <= 662){
        
        //user is now on main menu not temporary screen
        onOptionsScreen = false;
        onMenuScreen = true;
        
        startGame();
      }
      
    }
    
  }
  
  
  public void mouseMoved(MouseEvent e){
    c.setCursor(15, 5);
    
    //when mouse is hovering over button on the menu screen, program makes button light up and go back to normal if user is no longer hovering over square
    if (onMenuScreen){
      

      
      if (e.getX() >= 210 && e.getX() <= 498 && e.getY() >= 225 && e.getY() <= 305){
        c.setColor(Color.red);
        c.drawString("Play",300,255);
      }
      if (e.getX() <= 210 || e.getX() >= 498 || e.getY() <= 225 || e.getY() >= 305){
        c.setColor(Color.black);
        c.drawString("Play",300,255);
      }
      
      if (e.getX() >= 210 && e.getX() <= 498 && e.getY() >= 345 && e.getY() <= 425){
        c.setColor(Color.red);
        c.drawString("Instructions",220,375);
      }
      if (e.getX() <= 210 || e.getX() >= 498 || e.getY() <= 345 || e.getY() >= 425){
        c.setColor(Color.black);
        c.drawString("Instructions",220,375);
      }
      
      if (e.getX() >= 210 && e.getX() <= 498 && e.getY() >= 465 && e.getY() <= 545){
        c.setColor(Color.red);
        c.drawString("Quit",300,495);
      }
      if (e.getX() <= 210 || e.getX() >= 498 || e.getY() <= 465 || e.getY() >= 545){
        c.setColor(Color.black);
        c.drawString("Quit",300,495);
      }
      if (e.getX() >= 508 && e.getX() <= 588 && e.getY() >= 465 && e.getY() <= 545){
        tk.prepareImage(optionsHighlighted,-1,-1,null);
        c.drawImage (optionsHighlighted,518,440);
      }
      
      if (e.getX() <= 508 || e.getX() >= 588 || e.getY() <= 465 || e.getY() >= 545){
        tk.prepareImage(options,-1,-1,null);
        c.drawImage (options,518,440);
      }
    }
    if (onTempScreen || onOptionsScreen) {
      if (e.getX() >= 140 && e.getX() <= 568 && e.getY() >= 601 && e.getY() <= 662){
        c.setFont(menuSubtitle);
        c.setColor(Color.red);
        c.drawString("Back to Main Menu",140,624);
      }
      
      if (e.getX() <= 140 || e.getX() >= 568 || e.getY() <= 601 || e.getY() >= 662){
        c.setFont(menuSubtitle);
        c.setColor(Color.black);
        c.drawString("Back to Main Menu",140,624);
      }
      
    }
    
    //if the user is on the instrucion screen, program makes page number highlighted when user hovers over it
    if (onInstructionsScreen){
      
        
        if (!onPage1 && e.getX() >= 325 && e.getX() <= 338 && e.getY() >= 542 && e.getY() <= 596){
          
          c.setFont(instructionSubtitle);
          c.setColor(Color.red);
          c.drawString("1",325,544);

        }
        
        else if(!onPage1){
          
          c.setFont(instructionSubtitle);
          c.setColor(Color.black);
          c.drawString("1",325,544);
        
        }
        
        if (!onPage2 && e.getX() >= 351 && e.getX() <= 364 && e.getY() >= 542 && e.getY() <= 596){
          
          c.setFont(instructionSubtitle);
          c.setColor(Color.red);
          c.drawString("2",351,544);

        }
        
        else if(!onPage2){
          
          c.setFont(instructionSubtitle);
          c.setColor(Color.black);
          c.drawString("2",351,544);
        
        }
        
        if (!onPage3 && e.getX() >= 377 && e.getX() <= 390 && e.getY() >= 542 && e.getY() <= 596){
          
          c.setFont(instructionSubtitle);
          c.setColor(Color.red);
          c.drawString("3",377,544);

        }
        
        else if(!onPage3){
          
          c.setFont(instructionSubtitle);
          c.setColor(Color.black);
          c.drawString("3",377,544);
        
        }
        
      }
    
    //when mouse is hovering over button on the options screen, program makes button light up and go back to normal if user is no longer hovering over square
    if(onOptionsScreen){
      
      if (e.getX() >= 210 && e.getX() <= 498 && e.getY() >= 225 && e.getY() <= 305){
        c.setColor(Color.red);
        c.drawString(gridSize,220,255);
      }
      if (e.getX() <= 210 || e.getX() >= 498 || e.getY() <= 225 || e.getY() >= 305){
        c.setColor(Color.black);
        c.drawString(gridSize,220,255);
      }
      
      if (e.getX() >= 210 && e.getX() <= 498 && e.getY() >= 345 && e.getY() <= 425){
        
        c.setColor(Color.white);
        c.fillRect(210,320,288,80);
        
        c.setColor(Color.red);
        c.drawString(difficulty,220,375);
        
      }
      
      if (e.getX() <= 210 || e.getX() >= 498 || e.getY() <= 345 || e.getY() >= 425){
        
        c.setColor(Color.white);
        c.fillRect(210,320,288,80);
        
        c.setColor(Color.black);
        c.drawString(difficulty,220,375);
        
      }
      
      
    }
  }
  public void mouseDragged(MouseEvent e){}
  
  //timer method
  public void actionPerformed(ActionEvent e){
    
    c.setCursor(20, 15);
    
    //if user is alive program prints timer in top right corner
    if(alive){
      sec = sec + 1;
      
      c.setFont(timerFont);
      
      c.setColor(Color.black);
      c.fillRect(40,40,94,60);
      
      c.setColor(Color.red);
      String val;
      
      if (sec%60 < 10)
        val = "0";
      else 
        val = "";
      
      String timer = sec/60 +  ":" + val + sec%60;
      
      //timer stops showing time if user has played for more than 9 minutes and 59 seconds
      if (sec >= 599)
        timer = "9:59";
      
      c.drawString(timer, 40,83);
      
    }
    //if user won hame lets user see there cleared field for 2 seconds
    if(doWinDelay){
      
      delaySec = delaySec+1;
      
      if (delaySec == 2){
        doWinDelay = false;
        delaySec=0;
        launchWinScreen();
      }
    }
    
    
    //sets delay when user clicks on mine so they can see cleared field then launches lose screen
    if(doDeathDelay){
      delaySec = delaySec + 1;
      
      if (delaySec==2){
        doDeathDelay = false;
        delaySec=0;
        launchDeathScreen();
        
      }
      
    }
  }
  
  public synchronized void keyReleased(KeyEvent e){}
  public synchronized void keyPressed(KeyEvent e){}  
  public void keyTyped(KeyEvent e){}
  
  //prints all components of menu screen
  public void printMenuScreen() {
    
    
    //prints menu background image
    tk.prepareImage (background,-1,-1,null);
    c.drawImage (background,0,0);
    
    //prints minesweeper (title)
    c.setFont(menuHead);
    c.setColor(Color.white);
    c.drawString("Minesweeper",55,85);
    
    //prints authors name
    c.setFont(menuSubtitle);
    c.drawString("Made by Sean Devine",65,140);
    
    //prints play, instructions, options and quit buttons
    c.fillRect(210,200,288,80);
    
    c.fillRect(210,320,288,80);
    
    c.fillRect(210,440,288,80);
    
    c.fillRect(518,440,80,80);
    
    c.setColor(Color.black);
    
    c.drawRect(210,200,288,80);
    
    c.drawRect(210,320,288,80);
    
    c.drawRect(210,440,288,80);
    
    c.drawRect(517,439,81,81);
    
    tk.prepareImage(options,-1,-1,null);
    c.drawImage (options,518,440);
    
    c.drawString("Play",300,255);
    
    c.drawString("Instructions",220,375);
    
    c.drawString("Quit",300,495);
    
  }
  //prints instruction screen
  public void printInstructionScreen(){
    
    onInstructionsScreen = true;
    
    //prints background image
    tk.prepareImage (background,-1,-1,null);
    c.drawImage (background,0,0);
    
    //prints blank box to make text visable
    c.setColor(Color.white);
    c.fillRect(30,30,648,614);
    c.setColor(Color.black);
    c.drawRect(30,30,648,614);
    
    //instructions screen starts on page 1
    printInstructionsPage1();
    
    
    //prints back to main menu button
    c.setFont(menuSubtitle);
    c.drawString("Back to Main Menu",140,624);
    
    
    
  }
  
  public void printInstructionsPage1(){
     
    //resets background
    c.setColor(Color.white);
    c.fillRect(31,31,646,520);
  
    //prints "Basics" subtitle
    c.setColor(babyBlue);
    c.setFont(instructionSubtitle);
    c.drawString("Basics",45,65);
    
    //prints "Basics" instructions
    c.setColor(Color.black);
    c.setFont(instruction);
    c.drawString("-The game consists of an adjustable square grid. The game",45,100);
    c.drawString(" starts with every square covered and the user left clicks",45,120);
    c.drawString(" on any square to start the game.",45,140);
    c.drawString("-When a user left clicks a square, the square is either blank,",45,165);
    c.drawString(" contains a mine on it or has one or more mines next to it",45,185);
    c.drawString(" (number tile).",45,205);
    c.drawString("-If the user left clicks on a square with a mine on it, the",45,230);
    c.drawString(" game ends, all the mines are displayed and the user loses.",45,250);
    
    //prints an example of number tiles
    tk.prepareImage(numberExample,-1,-1,null);
    c.drawImage (numberExample,546,290);
    
    //prints "Number Tiles" subtitle
    c.setColor(babyBlue);
    c.setFont(instructionSubtitle);
    c.drawString("Number Tiles",45,310);
    
    //prints "Number Tiles" instructions
    c.setColor(Color.black);
    c.setFont(instruction);
    c.drawString("-If a square has any amount of mines next to it, ",45,355);
    c.drawString(" the square will display a number that says that",45,375);
    c.drawString(" amount.",45,395);
    
    
    //prints bottom page numbers
    c.setFont(instructionSubtitle);
    c.setColor(Color.red);
    c.drawString("1",325,544);
    c.setColor(Color.black);
    c.drawString("2  3",351,544);
    
  }
  
  public void printInstructionsPage2(){
    
    //resets background
    c.setColor(Color.white);
    c.fillRect(31,31,646,520);
    
    //prints an example of a flag
    tk.prepareImage(flagExample,-1,-1,null);
    c.drawImage (flagExample,546,65);
  
    //prints "Flags" subtitle
    c.setColor(babyBlue);
    c.setFont(instructionSubtitle);
    c.drawString("Flags",45,65);

    //prints "Flags" instructions
    c.setColor(Color.black);
    c.setFont(instruction);
         
    c.drawString("-By right clicking on an unclicked square, the",45,100);
    c.drawString(" program will put a flag on the square marking",45,120);
    c.drawString(" that it has a mine and the user will not be ",45,140);
    c.drawString(" able to accidentally left click on that square",45,160);
    c.drawString("-The program will not stop you from placing a",45,185);
    c.drawString(" flag if it is wrong and the flag can be removed by right",45,205);
    c.drawString(" clicking on the square containing the flag again.",45,225);
    c.drawString("-The program also gives a user a set number of flags equal to",45,250);
    c.drawString(" the amount of mines on the grid and each time a flag is used",45,270);
    c.drawString(" the number goes down.",45,290);
    
    //prints "Objective" subtitle
    c.setColor(babyBlue);
    c.setFont(instructionSubtitle);
    c.drawString("Objective",45,350);
    
    //prints "Objective" instructions
    c.setColor(Color.black);
    c.setFont(instruction);
    
    c.drawString("-The objective of the game is to clear every square that does",45,395);
    c.drawString(" not have a mine on it",45,415);
    
     //prints bottom page numbers
    c.setFont(instructionSubtitle);
    c.setColor(Color.red);
    c.drawString("2",351,544);
    c.setColor(Color.black);
    c.drawString("1",325,544);
    c.drawString("3",377,544);
    
  }
  
  public void printInstructionsPage3(){
  
    //resets background
    c.setColor(Color.white);
    c.fillRect(31,31,646,520);
    
    //prints "Things to Know" subtitle
    c.setColor(babyBlue);
    c.setFont(instructionSubtitle);
    c.drawString("Things to Know",45,65);
    
    //prints "Things to Know" instructions
    c.setColor(Color.black);
    c.setFont(instruction);
    
    c.drawString("-Your first click will not contain a mine.",45,100);
    c.drawString("-If you right click on a blank square, the program will",45,125);
    c.drawString(" clear all the blank squares around it and the squares ",45,145);
    c.drawString(" touching the outermost cleared blank squares",45,165);
    c.drawString(" (numbers tiles).",45,185);
    c.drawString("-The game displays a timer counting how many seconds",45,210);
    c.drawString(" the user has been playing since they clicked their first",45,230);
    c.drawString(" square, the timer will not stop the player, but used for",45,250);
    c.drawString(" player to time themselves.",45,270);
    
    
    //prints bottom page numbers
    c.setFont(instructionSubtitle);
    c.setColor(Color.red);
    c.drawString("3",377,544);
    c.setColor(Color.black);
    c.drawString("1  2",325,544);
    
  }
  
  public void printOptionsScreen(){
    
    //prints menu background image
    tk.prepareImage (background,-1,-1,null);
    c.drawImage (background,0,0);
    
    //prints options
    c.setFont(menuHead);
    c.setColor(Color.white);
    c.drawString("Options",180,85);
    
    
    //prints gridsize, difficulty and back to main menu buttons
    c.fillRect(210,200,288,80);
    
    c.fillRect(210,320,288,80);
    
    c.fillRect(130,580,470,55);
    
    c.setFont(menuSubtitle);
    
    
    c.drawString("grid size:",0,255);
    c.drawString("difficulty:",0,375); 
    
    
    c.setColor(Color.black);
    
    
    c.drawRect(210,200,288,80);
    
    c.drawRect(210,320,288,80);
    
    c.drawRect(130,580,470,55);
    
    c.drawString(gridSize,220,255);
    
    c.drawString(difficulty,220,375);
    
    c.drawString("Back to Main Menu",140,624);
    
    
    
    
  }
  //prints game background
  public void printStartScreen(){
    
    //prints console background
    c.setColor(coolGrey);
    c.fillRect(0, 0, 708, 674);
    
    c.setColor(lightGrey);
    
    c.fillRect(0,0, 708,4);
    
    c.setColor(darkGrey);
    
    c.fillRect(704,0,4,674);
    
    
    //prints features background
    c.setColor(coolGrey);
    c.fillRect(30,30,648,80);
    
    c.setColor(darkGrey);
    c.fillRect(28,28,652,4);
    
    c.fillRect(28,28,4,84);
    
    c.setColor(lightGrey);
    c.fillRect(28,110,652,4);
    
    c.fillRect(680,28,4,86);
    
    
    c.setColor(Color.black);
    
    //prints timer on left
    c.fillRect(40,40,94,60);
    c.setColor(Color.red);
    c.setFont(timerFont);
    c.drawString("0:00", 40,83);
    c.setColor(Color.black);
    
    //prints middle smile background
    
    c.setColor(coolGrey);
    c.fillRect(324,40,60,60);
    
    c.setColor(lightGrey);
    c.fillRect(324,40,60,4);
    c.fillRect(324,40,4,60);
    
    c.setColor(darkGrey);
    c.fillRect(324,96,60,4);
    c.fillRect(380,40,4,60);
    
    c.setColor(Color.black);
    
    //prints number of flags on right
    c.fillRect(572,40,94,60);
    
    c.setColor(Color.red);
    c.drawString(totalBombs+"", 580,83);
    
    c.setColor(coolGrey);
    
    //prints grid square background
    c.fillRect(30,140,648, 504);
    
    c.setColor(darkGrey);
    
    c.fillRect(26,136,656,4);
    c.fillRect(26,136,4,512);
    
    c.setColor(lightGrey);
    
    c.fillRect(26,645,656,4);
    
    c.fillRect(680,136,4,512);
    
    //prints middle smile
    c.setColor(Color.yellow);
    c.fillOval(330,46,48,48);
    c.setColor(Color.black);
    
    c.fillRect(342,58,5,5);
    c.fillRect(362,58,5,5);
    c.drawArc(342,58,24,24,195,150);
    
    
    
    //fills x grid lines
    c.setColor(Color.red); 
    for (int i = 0; i < yArrayLength; i++){
      
      c.setColor(lightGrey);
      
      c.fillRect(30,140+squareSize*i,648,gridlineLength);
      
      c.setColor(darkGrey);
      
      c.fillRect(30,140+squareSize*i+squareSize-gridlineLength,648,gridlineLength);
      
      
      
    }
    
    //fills y grid lines
    for (int i = 0; i < xArrayLength; i++){
      
      c.setColor(lightGrey);
      c.fillRect(30+squareSize*i,140,gridlineLength,504);
      
      c.setColor(darkGrey);
      c.fillRect(30+squareSize*i+squareSize-gridlineLength,140,gridlineLength,504);
      
    }
    
  }
  //finds total number of mines to generate for given grid size and difficulty
  public void findTotalBombs(){
    
    if (difficulty.equals("Normal"))
      totalBombs = trueArraySize/8-1;
    
    else if (difficulty.equals("Hard"))
      totalBombs = trueArraySize/6-2;
    
    
    else if (difficulty.equals("Easy"))
      totalBombs = trueArraySize/12 -1;
    
  }
  
  
  //resets variables for replaying game
  public void resetVariables(){
    
    Arrays.fill(isTilePressed,false);
    Arrays.fill(isFlag,false);
    Arrays.fill(tileStatus,0);
    Arrays.fill(bombTiles,0);
    justStarting = true;
    start = true;
    totalFlags = totalBombs;
    tilesClickedTotal=0;
    sec=0;
    alive=true;
    justWon=false;
    
  }
  
  //converts coordinates into game tile array index
  public int coordsToArrayIndex (int xCoord, int yCoord){
    
    return (((xCoord-30)/squareSize) + (xArrayLength) * ((yCoord-165)/squareSize));
    
  }
  
  //converts game array index into console X coordinate
  public int arrayIndexToXCoord(int arrayIndex){
    
    return ((arrayIndex%(xArrayLength)) * squareSize  + 30);
  }
  
  //converts game array index into console Y coordinate
  public int arrayIndexToYCoord(int arrayIndex){
    
    return (arrayIndex/(xArrayLength) * squareSize +140);
  }
  
  //converts click X coordinate to X coordinate  that is at the top left corner  of a grid square
  public int topCornerX(int xCoord){
    
    return (xCoord-30)/squareSize * squareSize +30;
    
  }
  
  //converts click Y coordinate to Y coordinate  that is at the top left corner  of a grid square
  public int topCornerY(int yCoord){
    
    return (yCoord-165)/squareSize * squareSize +140;
    
  }
  //generates level upon first click whhile user is playing game
  public void generateLevel(int tileClicked){
    
    boolean realMine;  
    
    //generates 30 mines in grid
    for (int i = 0; i < totalBombs; i++){
      
      //picks random coordinate for bombs
      bombTiles[i] = (int) (trueArraySize*Math.random());
      
      realMine = true;
      
      //ensures user has space around the first tile they click and never hits a bomb upon clearing their first tile
      if (bombTiles[i] == tileClicked)
        i--;
      else if (bombTiles[i] == tileClicked+1)
        i--;
      else if (bombTiles[i] == tileClicked-1)
        i--;
      else if (bombTiles[i] == tileClicked+(xArrayLength-1))
        i--;
      else if (bombTiles[i] == tileClicked+(xArrayLength))
        i--;
      else if (bombTiles[i] == tileClicked+(xArrayLength+1))
        i--;
      else if (bombTiles[i] == tileClicked-(xArrayLength-1))
        i--;
      else if (bombTiles[i] == tileClicked-(xArrayLength))
        i--;
      else if (bombTiles[i] == tileClicked-(xArrayLength+1))
        i--;
      
      
      //ensures there are not mines on a space where another mine is
      else{
        
        if (i > 0){
          
          for (int k = i-1; k >= 0; k--)
          {
            
            if( bombTiles[i] == bombTiles[k]){
              
              i--;
              realMine = false;
              break;
              
            }
            
          }
          
        }      
        
        //if the mine is on a unique space, program sets status of grid array to have mine at generated coordinate and updates tiles around it so that they are displaing the correct number of mines they are hitting
        if (realMine){
          
          tileStatus[bombTiles[i]] = -1;
          
          updateTiles(i, bombTiles);
          
          
        }      
      }
    }
  }
  
  //updates tiles around mine during mine generation
  public void updateTiles(int i, int[] bombTiles){
    
    int tempIndex;
    
    tempIndex = bombTiles[i]+1;
    //squares next to tile
    if (inBounds(tempIndex,trueArraySize)){
      if ( (  tempIndex % xArrayLength != 0 )  && (tileStatus[tempIndex] != -1)) 
        
        tileStatus[tempIndex]++;
    }
    
    tempIndex = bombTiles[i]-1;
    
    if (inBounds(tempIndex,trueArraySize)){
      if (( tempIndex % xArrayLength != (xArrayLength-1 )) && (tileStatus[tempIndex] != -1))
        
        tileStatus[tempIndex]++;
    }
    
    //squares above tile
    tempIndex = bombTiles[i]-(xArrayLength);
    
    if (inBounds(tempIndex,trueArraySize)){
      if (bombTiles[i] > (xArrayLength-1) && (tileStatus[tempIndex] != -1))
        tileStatus[tempIndex]++;
    }
    tempIndex = bombTiles[i]-(xArrayLength+1);
    
    if (inBounds(tempIndex,trueArraySize)){
      if (bombTiles[i] > (xArrayLength-1) && (tileStatus[tempIndex] != -1) && ( tempIndex % xArrayLength <  bombTiles[i] % (xArrayLength) ))
        tileStatus[tempIndex]++;
    }
    tempIndex = bombTiles[i]-(xArrayLength-1);
    
    if (inBounds(tempIndex,trueArraySize)){
      if (bombTiles[i] > (xArrayLength-1) && (tileStatus[tempIndex] != -1) && ( tempIndex % xArrayLength >  bombTiles[i] % (xArrayLength) ))    
        tileStatus[tempIndex]++;
    }
    
    //squares below tile
    
    tempIndex = bombTiles[i]+(xArrayLength);
    
    if (inBounds(tempIndex,trueArraySize)){
      if (bombTiles[i] < (trueArraySize - xArrayLength) && (tileStatus[tempIndex] != -1))
        tileStatus[tempIndex]++;
    }
    
    tempIndex = bombTiles[i]+(xArrayLength+1);
    if (inBounds(tempIndex,trueArraySize)){
      
      if (bombTiles[i] < (trueArraySize - xArrayLength) && (tileStatus[tempIndex] != -1) && ( tempIndex % xArrayLength >  bombTiles[i] % (xArrayLength) ))
        tileStatus[tempIndex]++;
      
    }
    
    tempIndex = bombTiles[i]+(xArrayLength-1);
    if (inBounds(tempIndex,trueArraySize)){
      
      if (bombTiles[i] < (trueArraySize - xArrayLength) && (tileStatus[tempIndex] != -1) && ( tempIndex % xArrayLength <  bombTiles[i] % (xArrayLength) ))
        tileStatus[tempIndex]++;
      
    }
    
    
  }
  
  //checks that given index is in bounds of given array
  public boolean inBounds (int index, int arraySize){
    
    return (index >= 0 && index < arraySize); 
    
  }
  
  //prints or deletes flag
  public void doFlags(int arrayIndex){
    
    //if tile doesent have a flag and hasnt been pressed, program places flag on tile and subtracts flag from total amount displayed at top right
    if (isFlag[arrayIndex] == false && !isTilePressed[arrayIndex]){
      
      isFlag[arrayIndex] = true;
      totalFlags--;
      c.setColor(Color.black);
      c.fillRect(572,40,94,60);
      c.setColor(Color.red);
      c.setFont(timerFont);
      c.drawString(totalFlags+"", 580,83);
      
      tk.prepareImage (picFlag,-1,-1,null);
      c.drawImage(picFlag,arrayIndexToXCoord(arrayIndex) + imageShift ,arrayIndexToYCoord(arrayIndex)+ imageShift);
      
      
    }
    //if tile has flag and hasnt been pressed, program removes on tile and adds flag to total amount displayed at top right
    else if(isFlag[arrayIndex] == true && !isTilePressed[arrayIndex]){
      
      isFlag[arrayIndex] = false;
      totalFlags++;
      
      c.setColor(Color.black);
      c.fillRect(572,40,94,60);
      c.setColor(Color.red);
      c.drawString(totalFlags+"", 580,83);
      
      
      c.setColor(coolGrey);
      
      c.fillRect(arrayIndexToXCoord(arrayIndex) + gridlineLength,arrayIndexToYCoord(arrayIndex) +gridlineLength ,squareSize-8,squareSize-8);
      
      
    }
  }
  
  //when user left clicks on tile:
  public void doPressTile(int arrayIndex){
    
    
    //if it is user first click: starts timer, saves that user is no longer starting, and generates level around where they clicked and sets their status to alive
    if (start){
      
      timer.start();
      start = false; 
      generateLevel(arrayIndex);
      alive = true;
    }
    
    c.setCursor(10, 5);
    
    //if user clicks on tile that hasn't been pressed before and doesn't have a flag:
    if(!isTilePressed[arrayIndex] && !isFlag[arrayIndex] && alive){
      
      
      
      //checks if they clicked on a mine and ends game and sets their status to not alive if they do
      if (tileStatus[arrayIndex] == -1){
        //clears entire grid
        endGame();
        
        
        alive = false;
      }
      
      //if user doesn't click on mine, program clears tile
      else{
        
        printTile(arrayIndex);
        
        
      }
      
    }
    
    
  }
  
  //printing tile prints black square and tiles status
  public void printTile (int arrayIndex){
    
    printSquare(arrayIndex);
    printStatus(arrayIndex);
    
  }
  
  //prints empty square where user clicked if tile has not been pressed before
  public void printSquare (int arrayIndex){
    
    
    if (isTilePressed[arrayIndex] == false){
      
      int xCoord = arrayIndexToXCoord(arrayIndex);
      int yCoord = arrayIndexToYCoord(arrayIndex);
      
      c.setColor(mediumGrey);
      
      c.fillRect(xCoord + 1,yCoord+1,squareSize-1,squareSize-1);
      
      c.setColor(darkGrey);
      c.fillRect(xCoord,yCoord,squareSize,1);      
      c.fillRect(xCoord,yCoord,1,squareSize);
      
      isTilePressed[arrayIndex] = true ;
      
      //fixes bug making sure game doesnt make user win when program clears grid upon users death
      if(dying == false){
        
        tilesClickedTotal++;
        
        //if user has clicked all tiles, program makes user win and updates theirs status to no longer alive (becuase they won and are no longer playing)
        if (tilesClickedTotal == (trueArraySize-totalBombs)){
          justWon=true;
          doWinDelay=true;
          alive = false;  
        }
      }
    }
  }
  
  //prints status of square by checking the status of square in tile status array
  public void printStatus(int arrayIndex){
    
    //fixes tile printing on win screen bug
    if(!justWon){
      
      //if the tile is blank and program is not in process of clearing blank tiless, program clears all tiles that aren't mines around a blank tile
      if (tileStatus[arrayIndex] == 0 && clearingTiles==false){
        spaceClearing(arrayIndex);
      }
      
      if (tileStatus[arrayIndex] == 1){
        tk.prepareImage (pic1,-1,-1,null);
        c.drawImage (pic1,arrayIndexToXCoord(arrayIndex)+ imageShift,arrayIndexToYCoord(arrayIndex)+ imageShift);  
      }    
      
      if (tileStatus[arrayIndex] == 2){
        tk.prepareImage (pic2,-1,-1,null);
        c.drawImage (pic2,arrayIndexToXCoord(arrayIndex)+ imageShift,arrayIndexToYCoord(arrayIndex)+ imageShift);
      }
      
      if (tileStatus[arrayIndex] == 3){
        tk.prepareImage (pic3,-1,-1,null);
        c.drawImage (pic3,arrayIndexToXCoord(arrayIndex)+ imageShift,arrayIndexToYCoord(arrayIndex)+ imageShift);
      }
      
      if (tileStatus[arrayIndex] == 4){
        tk.prepareImage (pic4,-1,-1,null);
        c.drawImage (pic4,arrayIndexToXCoord(arrayIndex)+ imageShift,arrayIndexToYCoord(arrayIndex)+ imageShift);
      }
      
      if (tileStatus[arrayIndex] == 5){
        tk.prepareImage (pic5,-1,-1,null);
        c.drawImage (pic5,arrayIndexToXCoord(arrayIndex)+ imageShift,arrayIndexToYCoord(arrayIndex)+ imageShift) ;
      }
      
      if (tileStatus[arrayIndex] == 6){
        tk.prepareImage (pic6,-1,-1,null);
        c.drawImage (pic6,arrayIndexToXCoord(arrayIndex)+ imageShift,arrayIndexToYCoord(arrayIndex)+ imageShift);
      }
      
      if (tileStatus[arrayIndex] == 7){
        tk.prepareImage (pic7,-1,-1,null);
        c.drawImage (pic7,arrayIndexToXCoord(arrayIndex)+ imageShift,arrayIndexToYCoord(arrayIndex)+ imageShift);
      }
      
      if (tileStatus[arrayIndex] == 8){
        tk.prepareImage (pic8,-1,-1,null);
        c.drawImage (pic8,arrayIndexToXCoord(arrayIndex)+ imageShift,arrayIndexToYCoord(arrayIndex)+ imageShift);
      }
      
      if (tileStatus[arrayIndex] == -1){
        tk.prepareImage (picMine,-1,-1,null);
        c.drawImage (picMine,arrayIndexToXCoord(arrayIndex)+ imageShift,arrayIndexToYCoord(arrayIndex)+ imageShift);
      }
    }
  }
  
  //program checks all clicked blank squares on grid and prints tiles around them; continues untill all clicked blank tiles have all their squares around them cleared
  public void spaceClearing (int arrayIndex){
    
    do{
      
      stillTiles= false;
      
      for (int i = 0; i < trueArraySize; i++){
        
        if (tileStatus[i] == 0 && isTilePressed[i]== true){
          
          checkAround(i);
          
        }
        
        
      }
      
      
    }while(stillTiles==true);
  }
  
  
  //clears all squares around a blank tile and updates the status of squares; if not tiles are cleared, space clearing method stops
  public void checkAround (int tileIndex) {
    
    int tempIndex;
    
    clearingTiles = true;
    
    //squares next to tile
    tempIndex = tileIndex + 1;
    
    if (inBounds(tempIndex,trueArraySize)){
      if (    tempIndex % (xArrayLength) != 0  && (isTilePressed[tempIndex] == false)){
        printTile(tempIndex);
        isTilePressed[tempIndex]= true;
        stillTiles = true;
      }
    }
    
    tempIndex = tileIndex - 1;
    
    if (inBounds(tempIndex,trueArraySize)){
      if ( (  ( tempIndex ) % (xArrayLength) != (xArrayLength-1) )  && (isTilePressed[tempIndex] == false)){
        printTile(tempIndex);
        isTilePressed[tempIndex]= true;
        stillTiles = true;
      }
    }
    
    //squares above tile
    tempIndex = tileIndex - (xArrayLength);
    
    if (inBounds(tempIndex,trueArraySize)){
      if  (  ( tileIndex > (xArrayLength-1) )  && (isTilePressed[tempIndex] == false)){
        printTile(tempIndex);
        isTilePressed[tempIndex]= true;
        stillTiles = true;
      }
    }
    tempIndex = tileIndex -(xArrayLength+1);
    
    if (inBounds(tempIndex,trueArraySize)){
      if (   ( tileIndex > (xArrayLength-1) )  && (tempIndex % (xArrayLength) != (xArrayLength-1) )  && (isTilePressed[tempIndex] == false)){
        printTile(tempIndex);
        isTilePressed[tempIndex]= true;
        stillTiles = true;
      }
    }
    tempIndex = tileIndex -(xArrayLength-1);
    
    if (inBounds(tempIndex,trueArraySize)){
      if (   ( tileIndex > (xArrayLength-1) )  && (( tempIndex ) % (xArrayLength) != 0 )  && (isTilePressed[tempIndex] == false)){
        printTile(tempIndex);
        isTilePressed[tempIndex]= true;
        stillTiles = true;
      }
    }
    
    //squares below tile
    tempIndex = tileIndex +(xArrayLength);
    
    if (inBounds(tempIndex,trueArraySize)){
      if  (  ( tileIndex < (trueArraySize - xArrayLength) )  && (isTilePressed[tempIndex] == false)){
        printTile(tempIndex);
        isTilePressed[tempIndex]= true;
        stillTiles = true;
      }
    }
    tempIndex = tileIndex +(xArrayLength+1);
    
    if (inBounds(tempIndex,trueArraySize)){     
      if ( ( tileIndex < (trueArraySize - xArrayLength) )  && (  ( tempIndex ) % (xArrayLength) != 0 )  && (isTilePressed[tempIndex] == false)){
        printTile(tempIndex);
        isTilePressed[tempIndex]= true;
        stillTiles = true;
      }
    }
    tempIndex = tileIndex +(xArrayLength-1);
    
    if (inBounds(tempIndex,trueArraySize)){
      if ( ( tileIndex < (trueArraySize - xArrayLength) )  && (  ( tempIndex ) % (xArrayLength) != (xArrayLength-1) )  && (isTilePressed[tempIndex] == false)){
        printTile(tempIndex);
        isTilePressed[tempIndex]= true;
        stillTiles = true;
        
      }    
    }
    
    
    
    clearingTiles = false;
  }
  
  //when user dies program clears grid, displays it for 2 seconds then sends user to death screen
  public void endGame (){
    
    c.setColor(Color.yellow);
    c.fillOval(330,46,48,48);
    c.setColor(Color.black);
    
    c.fillRect(342,58,5,5);
    c.fillRect(362,58,5,5);
    c.drawArc(342,75,24,24,15,150);
    
    
    
    for (int i = 0; i < trueArraySize; i++){
      
      dying = true;
      printTile(i);    
      alive = false;
      
    }
    dying = false;
    doDeathDelay = true;
    
  }
  
  //program prints death screen
  public void launchDeathScreen() {
    
    //program prints background
    tk.prepareImage (background,-1,-1,null);
    c.drawImage (background,0,0);
    
    //program prints blank white sheet around background
    c.setColor(Color.white);
    c.fillRect(30,30,648,614);
    
    c.setColor(Color.black);
    c.drawRect(30,30,648,614);
    
    c.setFont(winOrLose);
    
    //program prints YOU LOSE
    c.setColor(Color.red);
    c.drawString("YOU LOSE!",145,125); 
    
    tk.prepareImage (death,-1,-1,null);
    c.drawImage(death, 104, 180);
    
    //program prints back to main menu button
    c.setColor(Color.black);
    c.setFont(menuSubtitle);
    c.drawString("Back to Main Menu",140,624);
    
    
    //program is no longer on a game screen and now on a temporary (die) screen
    onGameScreen = false;
    onTempScreen = true;
    
  }
  
  //when user wins program launches win screen
  public void launchWinScreen() {
    
    //program prints background
    tk.prepareImage (background,-1,-1,null);
    c.drawImage (background,0,0);
    
    //program prints blank white sheet around background
    c.setColor(Color.white);
    c.fillRect(30,30,648,614);
    
    c.setColor(Color.black);
    c.drawRect(30,30,648,614);
    
    c.setFont(winOrLose);
    
    //program prints YOU WIN
    c.setColor(Color.red);
    c.drawString("YOU WIN!",148,125); 
    
    tk.prepareImage (win,-1,-1,null);
    c.drawImage(win, 104, 180);
    
    
    //prints users time taken to win
    String val;
    
    if (sec%60 < 10)
      val = "0";
    else 
      val = "";
    
    String timer = sec/60 +  ":" + val + sec%60;
    
    c.setFont(menuSubtitle);
    
    c.drawString("Your time: " + timer,152,450 );
    
    //program prints back to main menu button
    c.setColor(Color.black);
    
    c.drawString("Back to Main Menu",140,624);
    
    //program is no longer on a game screen and now on a temporary (win) screen
    
    onGameScreen = false;
    onTempScreen = true;
    
  }
  
  
}