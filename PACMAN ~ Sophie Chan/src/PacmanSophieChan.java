/*
 * Pac-Man
 * ~ Sophie Chan 2020
 * Classic pacman game: pacman should avoid the ghosts, which will eat pacman if they collide.
 * Pacman should eat pellets to obtain a higher score. If a power pellet is eaten, ghosts will 
 * go into frightened mode, and pacman will be able to eat the ghosts to obtain more points.
 * Fruits are also to be eaten for points. Pacman will have a total of three lives per game.
 */

import java.awt.event.*;
import java.io.File;
import java.awt.*;

import javax.swing.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

@SuppressWarnings("serial") //funky warning, just suppress it. It's not gonna do anything.
public class PacmanSophieChan extends JPanel implements Runnable, KeyListener, MouseListener{
	
	//self explanatory variables
	Image background, backgroundblue, backgroundwhite, ready, gameover, pressm, paused, cherry, strawberry;
	Image title, credits, controls, icon, smile;
	Image pacman, savedpacman, pacmandie, pacmanleft;
	static Image pacmanright;
	Image pacmanup, pacmandown, pacmanwhole, dot, powerpellet, frightened, frightenedghost, frightenedghost2;
	Image blinky, blinkyleft, blinkyright, blinkyup, blinkydown, pinky, pinkyleft, pinkyright, pinkyup, pinkydown, inky, inkyleft, inkyright, inkyup, inkydown, clyde, clydeleft, clyderight, clydeup, clydedown, currentghost;
	Image [] ghostScore = new Image [5];
	Image [] numbers = new Image [10];
	static ImageIcon startbutton, controlsbutton, creditsbutton;
	static JButton startButton, controlsButton, creditsButton;
	static boolean changeScreen;
	Clip backgroundmusic;
	int FPS = 60;
	Thread thread;
	int screenWidth = 467;
	int screenHeight = 600;
	Rectangle player = new Rectangle(216, 423, 40, 40); //player
	Rectangle playerEat = new Rectangle(229, 435, 16, 16); //player core that can eat pellet if touched
	Rectangle playerFieldX = new Rectangle(211, 420, 52, 46); //field surrounding player to check for turns
	Rectangle playerFieldY = new Rectangle(213, 417, 48, 52); //field surrounding player to check for turns
	Rectangle blinkyRect = new Rectangle(216, 220, 40, 40); //red ghost
	Rectangle blinkyFieldX = new Rectangle(211, 217, 52, 46); //red ghost field surrounding
	Rectangle blinkyFieldY = new Rectangle(213, 214, 48, 52); //red ghost field surrounding
	Rectangle blinkyEat = new Rectangle(229, 232, 16, 16); //red ghost core for eating/being eaten by player
	Rectangle pinkyRect = new Rectangle(215, 270, 40, 40); //pink ghost
	Rectangle pinkyFieldX = new Rectangle(210, 267, 52, 46); //pink ghost field surrounding
	Rectangle pinkyFieldY = new Rectangle(212, 264, 48, 52); //pink ghost field surrounding
	Rectangle pinkyEat = new Rectangle(228, 282, 16, 16); //pink ghost core for eating/being eaten by player
	Rectangle inkyRect = new Rectangle(186, 270, 40, 40); //blue ghost
	Rectangle inkyFieldX = new Rectangle(181, 267, 52, 46); //blue ghost field surrounding
	Rectangle inkyFieldY = new Rectangle(183, 264, 48, 52); //blue ghost field surrounding
	Rectangle inkyEat = new Rectangle(199, 282, 16, 16); //blue ghost core for eating/being eaten by player
	Rectangle clydeRect = new Rectangle(244, 270, 40, 40); //orange ghost
	Rectangle clydeFieldX = new Rectangle(238, 267, 52, 46); //orange ghost field surrounding
	Rectangle clydeFieldY = new Rectangle(240, 264, 48, 52); //orange ghost field surrounding
	Rectangle clydeEat = new Rectangle(256, 282, 16, 16); //orange ghost core for eating/being eaten by player
	Rectangle fruit = new Rectangle(215, 320, 40, 40); 	//cherry or strawberry
	Rectangle fruitEat = new Rectangle(228, 332, 16, 16); //fruit core for being eaten by player
	boolean up, down, left, right, upturn, downturn, leftturn, rightturn;	//pacman moving and turn collision checks
	boolean upBlinky, downBlinky, leftBlinky, rightBlinky, upturnBlinky, downturnBlinky, leftturnBlinky, rightturnBlinky; //ghost moving and turn collision checks
	boolean upPinky, downPinky, leftPinky, rightPinky, upturnPinky, downturnPinky, leftturnPinky, rightturnPinky;
	boolean upInky, downInky, leftInky, rightInky, upturnInky, downturnInky, leftturnInky, rightturnInky;
	boolean upClyde, downClyde, leftClyde, rightClyde, upturnClyde, downturnClyde, leftturnClyde, rightturnClyde;
	boolean outPinky = false, outInky = false, outClyde = false; //keeps track of whether ghosts are at home base or out
	boolean returnBlinky = false, returnPinky = false, returnInky = false, returnClyde = false; //keeps track of whether ghosts are returning from frightened
	boolean frightenedMode = false, frightenedBlinky = false, frightenedPinky = false, frightenedInky = false, frightenedClyde = false;
	boolean eatenBlinky = false, eatenPinky = false, eatenInky = false, eatenClyde = false;
	boolean sameLife = true;
	String lastDirection = "", lastDirectionBlinky = "", lastDirectionPinky = "", lastDirectionInky = "", lastDirectionClyde = "";
	String savedTurn = "";
	int level = 1;
	int speed = 2;
	int blinkyspeed = 1120-10*level, pinkyspeed = 1120-10*level, inkyspeed = 1120-10*level, clydespeed = 1120-10*level; //divided by FPS
	int countdown, pause; 		//the game will not run unless these are both set to 0
	boolean pauseMode = false;	//only changed by pressing p on keyboard
	int score = 0;
	int highscore = 0;
	int pelletsEaten = 0;
	int ghostsEaten = 0;
	int lives = 3;
	int livesUsedTotal = 0; //total lives used in a single run of the program
	int pacGraphicsCounter = 0; //for opening and closing mouth
	int backgroundCounter = 0; //for background flashing
	int pelletCounter = 0; //for power pellet flashing
	int frightenedCounter = 0; //for frightened ghost flashing and timing
	int pressmCounter = 0; //for press m instruction to flash
	int pausedCounter = 0; //for paused text to flash
	boolean pressmCheck = false, pauseCheck = false;
	boolean revertGhost = false;
	static String currentScreen = "mainmenu";
	boolean gameOver = false;
	boolean fruitOut = false, fruitEaten = false;
	
	boolean checkdownPac = false;	//checking pacman turns
	boolean checkupPac = false;
	boolean checkrightPac = false;
	boolean checkleftPac = false;
	
	boolean bColUp = true;		//checking for blinky collisions (true = no collision)
	boolean bColDown = true;
	boolean bColLeft = true;
	boolean bColRight = true;
	boolean pColUp = true;		//checking for pinky collisions (true = no collision)
	boolean pColDown = true;
	boolean pColLeft = true;
	boolean pColRight = true;
	boolean iColUp = true;		//checking for inky collisions (true = no collision)
	boolean iColDown = true;
	boolean iColLeft = true;
	boolean iColRight = true;
	boolean cColUp = true;		//checking for clyde collisions (true = no collision)
	boolean cColDown = true;
	boolean cColLeft = true;
	boolean cColRight = true;
	
	int[][] originalMap = {		//serves as reset; never gets changed
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 1, 0, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 5, 0, 1, 1, 1, 1, 0, 0, 5, 0, 1, 1, 1, 1, 1, 1, 0, 0, 5, 0, 1, 0, 0, 5, 0, 1, 1, 1, 1, 1, 1, 0, 0, 5, 0, 1, 1, 1, 1, 0, 0, 5, 0, 1},
			{1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1},
			{1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, -1, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 9, 0, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 6, 0, 1, 1, 1, 1, 0, 0, 6, 0, 1, 0, 6, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 6, 1, 0, 0, 6, 0, 1, 1, 1, 1, 0, 0, 6, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 1, 0, 7, 0, 7, 0, 7, 0, 7, 0, 1, 0, 0, 7, 0, 7, 0, 7, 0, 7, 1, 0, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 7, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 7, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 7, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 7, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 7, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 7, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 7, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 7, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 7, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 7, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 7, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 7, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 3, 0, 3, 0, 3, 0, 7, 0, 1, 0, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 1, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 7, 0, 1},
			{1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1},
			{1, 0, 0, 6, 0, 0, 0, 0, 1, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 1, 0, 0, 0, 0, 0, 6, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, -1, 0, 9, 0, 0, 1, 0, 0, 5, 0, 5, 0, 9, 0, 9, 0, 9, 0, 5, 0, 0, 0, 0, 5, 0, 5, 0, 9, 0, 5, 0, 5, 0, 5, 0, 1, 0, 0, 9, 0, 0, -1, 0, 1},
			{1, 0, 0, 0, 0, 3, 0, 0, 1, 0, 0, 7, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 7, 0, 1, 0, 0, 3, 0, 0, 0, 0, 1},
			{1, 1, 1, 1, 0, 4, 0, 0, 1, 0, 0, 8, 0, 1, 0, 4, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 4, 0, 0, 1, 0, 0, 8, 0, 1, 0, 0, 4, 0, 1, 1, 1, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 8, 0, 4, 0, 8, 0, 8, 0, 8, 0, 1, 0, 4, 0, 8, 0, 8, 0, 8, 0, 1, 0, 0, 8, 0, 8, 0, 4, 0, 0, 1, 0, 0, 8, 0, 8, 0, 8, 4, 0, 0, 8, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 8, 0, 1, 0, 0, 8, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 1},
			{1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1},
			{1, 0, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 0, 0, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 8, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
		};
	
	int[][] map = new int [originalMap.length] [originalMap[0].length];	//map that gets updated
	Rectangle [][] walls = new Rectangle [map.length] [map[0].length]; //array storing rectangles for collision purposes
	Rectangle [][] pellets = new Rectangle [map.length] [map[0].length]; //array storing dots for point purposes
	Rectangle [][] powerpellets = new Rectangle [map.length] [map[0].length]; //array storing power pellets
	int numRows = map.length;
	int numCols = map[0].length;
	int tileWidth = 10;
	int tileHeight = 10;
	
	// Description: Constructor: initializations and starting the threads
	// Parameters: n/a, Return: n/a
	public PacmanSophieChan() {
		//sets up JPanel
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true);
		
		//starting the thread
		thread = new Thread(this);
		thread.start();
		//starting ghost threads
		blinkyThread blinkyThread = new blinkyThread();
		blinkyThread.start();
		pinkyThread pinkyThread = new pinkyThread();
		pinkyThread.start();
		inkyThread inkyThread = new inkyThread();
		inkyThread.start();
		clydeThread clydeThread = new clydeThread();
		clydeThread.start();
		
		//set up the map that is constantly updated
		for (int row = 0; row < map.length; row++) {
			for (int col = 0; col < map[row].length; col++) {
				map[row][col] = originalMap[row][col];
			}
		}
		
		//load the sounds
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("backgroundmusic.wav"));
			backgroundmusic = AudioSystem.getClip();
			backgroundmusic.open(sound);
			backgroundmusic.setFramePosition (0); //play sound file again from beginning
			backgroundmusic.loop(Clip.LOOP_CONTINUOUSLY);
		} 
		catch (Exception e) {
		}
		
		//load the images
		backgroundblue = Toolkit.getDefaultToolkit().createImage("background.png");
		backgroundwhite = Toolkit.getDefaultToolkit().createImage("backgroundwhite.png");
		ready = Toolkit.getDefaultToolkit().createImage("ready.png");
		gameover = Toolkit.getDefaultToolkit().createImage("gameover.png");
		pressm = Toolkit.getDefaultToolkit().createImage("pressm.png");
		paused = Toolkit.getDefaultToolkit().createImage("paused.png");
		pacmanleft = Toolkit.getDefaultToolkit().createImage("pacmanleft.png");
		pacmanright = Toolkit.getDefaultToolkit().createImage("pacmanright.png");
		pacmanup = Toolkit.getDefaultToolkit().createImage("pacmanup.png");
		pacmandown = Toolkit.getDefaultToolkit().createImage("pacmandown.png");
		dot = Toolkit.getDefaultToolkit().createImage("dot.png");
		powerpellet = Toolkit.getDefaultToolkit().createImage("powerpellet.png");
		pacmanwhole = Toolkit.getDefaultToolkit().createImage("pacmanwhole.png");
		blinkyleft = Toolkit.getDefaultToolkit().createImage("blinkyleft.png");
		blinkyright = Toolkit.getDefaultToolkit().createImage("blinkyright.png");
		blinkydown = Toolkit.getDefaultToolkit().createImage("blinkydown.png");
		blinkyup = Toolkit.getDefaultToolkit().createImage("blinkyup.png");
		pinkyleft = Toolkit.getDefaultToolkit().createImage("pinkyleft.png");
		pinkyright = Toolkit.getDefaultToolkit().createImage("pinkyright.png");
		pinkydown = Toolkit.getDefaultToolkit().createImage("pinkydown.png");
		pinkyup = Toolkit.getDefaultToolkit().createImage("pinkyup.png");
		inkyleft = Toolkit.getDefaultToolkit().createImage("inkyleft.png");
		inkyright = Toolkit.getDefaultToolkit().createImage("inkyright.png");
		inkydown = Toolkit.getDefaultToolkit().createImage("inkydown.png");
		inkyup = Toolkit.getDefaultToolkit().createImage("inkyup.png");
		clydeleft = Toolkit.getDefaultToolkit().createImage("clydeleft.png");
		clyderight = Toolkit.getDefaultToolkit().createImage("clyderight.png");
		clydedown = Toolkit.getDefaultToolkit().createImage("clydedown.png");
		clydeup = Toolkit.getDefaultToolkit().createImage("clydeup.png");
		frightenedghost = Toolkit.getDefaultToolkit().createImage("frightenedghost.png");
		frightenedghost2 = Toolkit.getDefaultToolkit().createImage("frightenedghost2.png");
		ghostScore[0] = Toolkit.getDefaultToolkit().createImage("200.png");
		ghostScore[1] = Toolkit.getDefaultToolkit().createImage("400.png");
		ghostScore[2] = Toolkit.getDefaultToolkit().createImage("800.png");
		ghostScore[3] = Toolkit.getDefaultToolkit().createImage("1600.png");
		ghostScore[4] = Toolkit.getDefaultToolkit().createImage("1600.png");
		cherry = Toolkit.getDefaultToolkit().createImage("cherry.png");
		strawberry = Toolkit.getDefaultToolkit().createImage("strawberry.png");
		credits = Toolkit.getDefaultToolkit().createImage("credits.png");
		smile = Toolkit.getDefaultToolkit().createImage("smile.png");
		controls = Toolkit.getDefaultToolkit().createImage("controls.png");
		title = Toolkit.getDefaultToolkit().createImage("title.png");
		icon = Toolkit.getDefaultToolkit().createImage("icon.png");
		startbutton = new ImageIcon ("startbutton.png");
		controlsbutton = new ImageIcon ("controlsbutton.png");
		creditsbutton = new ImageIcon ("creditsbutton.png");
	
		background = backgroundblue;
		pacman = pacmanwhole;
		blinky = blinkyleft;
		pinky = pinkydown;
		inky = inkyup;
		clyde = clydeup;
		
		//load the number images for the score
		for (int i = 0; i<10; i++) {
			numbers[i] = Toolkit.getDefaultToolkit().createImage(i+".png");
		}
		
		//add a tracker for all of the images necessary when program first runs
		MediaTracker tracker = new MediaTracker (this);
		tracker.addImage (background, 0);
		tracker.addImage (pacman, 1);
		tracker.addImage (blinky, 2);
		tracker.addImage (pinky, 3);
		tracker.addImage (inky, 4);
		tracker.addImage (clyde, 5);
		tracker.addImage (dot, 6);
		tracker.addImage (powerpellet, 7);
		tracker.addImage (credits, 8);
		tracker.addImage (controls, 9);
		tracker.addImage (title, 10);
		tracker.addImage (startbutton.getImage(), 11);
		tracker.addImage (controlsbutton.getImage(), 12);
		tracker.addImage (creditsbutton.getImage(), 13);
		tracker.addImage (blinkyright, 14);
		tracker.addImage (blinkyup, 15);
		tracker.addImage (blinkydown, 16);
		tracker.addImage (smile, 17);
		for (int i = 0; i<10; i++) {
			tracker.addImage (numbers[i], 18+i);
		}
		//wait until all of the images are loaded
		try
		{
		    tracker.waitForAll ();
		}
		catch (InterruptedException e)
		{
		}
	
	}	//Constructor
	
	// Description: the continuously running method of the main thread (controlling pacman and general game)
	// Parameters: n/a, Return: n/a
	@Override
	public void run() {
		initialize();
		while(true) {
		while(currentScreen.equals("game")) {	//while the player is on the game screen
		newGame();
		while(!gameOver) {	//while it is not game over
			reset();
			while(sameLife) {	//while they are in the same life
				//main game loop
				update();
				updatePositions();
				resetColTurnVariables();
				this.repaint();
				try {
					Thread.sleep(1000/FPS);		//allows program to run at FPS frames per second
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		}
		String s = "update";		//to allow the loop to update
		s = s.toUpperCase();
		}
	}
	
	public void initialize() {
		//setups before the game starts running
	}
	
	// Description: continuous update that checks everything for collisions, moving, modes, lives, levels, etc
	// Parameters: n/a, Return: n/a
	public void update() {
		move();
		keepInBound(player);
		checkGhostCollision();
		for(int row = 0; row < walls.length; row++) {
			for (int col = 0; col<walls[0].length; col++) {
				checkWallCollision(walls[row][col], player);	
			}
		}
		if ((lastDirection.equals("down") || lastDirection.equals("up"))){
			for(int row = 0; row < walls.length; row++) {
				for (int col = 0; col<walls[0].length; col++) {
					checkTurnX(walls[row][col], playerFieldX);	
				}
			}
		}
		else if ((lastDirection.equals("left") || lastDirection.equals("right"))){
			for(int row = 0; row < walls.length; row++) {
				for (int col = 0; col<walls[0].length; col++) {
					checkTurnY(walls[row][col], playerFieldY);	
				}
			}
		}
		checkDownP();
		checkUpP();
		checkLeftP();
		checkRightP();
		if (pelletsEaten > 100 && pelletsEaten < 135 && !fruitEaten) {	//fruit is out between 100-135 pellets eaten
			fruitOut = true;
		}
		else fruitOut = false;
		for(int i = 0; i < pellets.length; i++) {
			for (int x = 0; x<pellets[0].length; x++) {
				checkPoints(pellets[i][x], i, x, powerpellets[i][x]);	
			}
		}
		if (pelletsEaten == 197) {	//if all the pellets have been eaten, reset certain variables
			pelletsEaten = 0;
			sameLife = false;
			backgroundChange();
			reset();
			for (int row = 0; row < map.length; row++) {
				for (int col = 0; col < map[row].length; col++) {
					map[row][col] = originalMap[row][col];
				}
			}
			repaint();
			sameLife = true;
			level++;					//update level and ghost speeds
			blinkyspeed = 1120-10*level;
			pinkyspeed = 1120-10*level;
			inkyspeed = 1120-10*level;
			clydespeed = 1120-10*level;
		}
		if (frightenedMode == false) {	//resets individual ghost modes when frightened mode is false
			frightenedBlinky = false;
			frightenedPinky = false;
			frightenedInky = false;
			frightenedClyde = false;
		}else {
			frightenedGhosts();
		}
	}
	
	// Description: Paint Component: draws everything
	// Parameters: Graphics g, Return: n/a
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		Graphics2D g2 = (Graphics2D) g;	
		setBackground(new Color(0, 0, 0));
		
		if (currentScreen.equals("game")) {
		//pacman background
		g.drawImage(background, 0, 0, screenWidth, screenHeight, this);
		
		//draw walls and pellets
		for(int row = 0; row < numRows; row++) {
			for(int col = 0; col < numCols; col++) {
				int x = col * tileWidth;
				int y = row * tileHeight+50;
				if(map[row][col] == 1) {											//wall placements
					walls[row][col] = new Rectangle(x, y, tileWidth, tileHeight);
				}
				else if(map[row][col] == 5) {										//generic dot placements
					g.drawImage(dot, (x-5), (y-5), tileWidth, tileHeight, this);
					pellets[row][col] = new Rectangle((x-5), (y-5), tileWidth, tileHeight);
				}else if(map[row][col] == 6) {										//dots that need individual formatting (y lower than 5)
					g.drawImage(dot, (x-5), (y), tileWidth, tileHeight, this);
					pellets[row][col] = new Rectangle((x-5), (y), tileWidth, tileHeight);
				}else if(map[row][col] == 7) {										//dots that need individual formatting (y lower than 6)
					g.drawImage(dot, (x-5), (y+3), tileWidth, tileHeight, this);
					pellets[row][col] = new Rectangle((x-5), (y+3), tileWidth, tileHeight);
				}else if(map[row][col] == 8) {										//dots that need individual formatting (y lower than 7)
					g.drawImage(dot, (x-5), (y+10), tileWidth, tileHeight, this);
					pellets[row][col] = new Rectangle((x-5), (y+10), tileWidth, tileHeight);
				}else if(map[row][col] == 9) {										//dots that need individual formatting (5 but more right)
					g.drawImage(dot, (x-1), (y-5), tileWidth, tileHeight, this);
					pellets[row][col] = new Rectangle((x-1), (y-5), tileWidth, tileHeight);
				}else if(map[row][col] == 2) {										//dots that need individual formatting (6 but more right)
					g.drawImage(dot, (x-1), (y), tileWidth, tileHeight, this);
					pellets[row][col] = new Rectangle((x-1), (y), tileWidth, tileHeight);
				}else if(map[row][col] == 3) {										//dots that need individual formatting (7 but more right)
					g.drawImage(dot, (x-1), (y+3), tileWidth, tileHeight, this);
					pellets[row][col] = new Rectangle((x-1), (y+3), tileWidth, tileHeight);
				}else if(map[row][col] == 4) {										//dots that need individual formatting (8 but more right)
					g.drawImage(dot, (x-1), (y+10), tileWidth, tileHeight, this);
					pellets[row][col] = new Rectangle((x-1), (y+10), tileWidth, tileHeight);
				}else if(map[row][col] == -1) {
					if (pelletCounter < 60 && gameOver == false && countdown == 0 && pause == 0 && !pauseMode) {	//power pellets (flash during normal gameplay)
						g.drawImage(powerpellet, (x-8), (y-8), tileWidth+6, tileHeight+6, this);
					}else if (pelletCounter > 120) {
						pelletCounter = 0;
					}
					pelletCounter++;
					powerpellets[row][col] = new Rectangle((x-8), (y-8), tileWidth, tileHeight);
				}
			}
		}
		//draws fruit
		if (fruitOut && !fruitEaten && !gameOver && sameLife && countdown == 0) {
			if (level%2 == 0) g.drawImage(strawberry, fruit.x+7, fruit.y+7, 25, 25, this);
			else g.drawImage(cherry, fruit.x+7, fruit.y+7, 25, 25, this);
		}
		//draw pacman and ghosts in updated position	
		g.drawImage(clyde, clydeRect.x+7, clydeRect.y+7, 25, 25, this);
		g.drawImage(inky, inkyRect.x+7, inkyRect.y+7, 25, 25, this);
		g.drawImage(pinky, pinkyRect.x+7, pinkyRect.y+7, 25, 25, this);
		g.drawImage(blinky, blinkyRect.x+7, blinkyRect.y+7, 25, 25, this);	
		g.drawImage(pacman, player.x+7, player.y+7, 25, 25, this);
		
		//draws ready text
		if (countdown>0 && !pauseMode) {		//count down to starting the game
			countdown--;
			g.drawImage(ready, 175, 326, 119, 30, this);	
		}
		
		//draw cherry and strawberry on bottom right of screen
		g.drawImage(cherry, 423, 567, 26, 26, this);
		if (level > 1) {
			g.drawImage(strawberry, 393, 567, 26, 26, this);
		}
		
		//draw pause text if game is paused
		if (pauseMode) {
			if (pausedCounter % 80 == 0) {
				pauseCheck = true;
			}else if (pausedCounter % 40 == 0){
				pauseCheck = false;
			}	
			if (pauseCheck) g.drawImage(paused, 173, 328, 125, 25, this);
			pausedCounter++;
		}
		//draw game over text if no more lives
		if (lives == 0) {		//if no lives left, show game over
			g.drawImage(gameover, 160, 326, 158, 30, this);	
		}
		
		//to display the score using the images
		Image [] digitsScore = new Image[7];
		Image [] digitsHighScore = new Image[7];
		int remainder;
		int number = score;
		int x = 0;
		while (number>0) {	//determines images required to display score
			remainder = number%10;
			digitsScore[x] = numbers[remainder];
			number = number/10;
			x++;
		}
		//draws score on top left
		for (int i = 0; i<7; i++) {
			if (digitsScore[i] == null) digitsScore[i] = numbers[0];
			g.drawImage(digitsScore[i], 123-18*i, 20, 20, 25, this);
		}
		number = highscore;
		x = 0;
		while (number>0) { //determines images required to display high score
			remainder = number%10;
			digitsHighScore[x] = numbers[remainder];
			number = number/10;
			x++;
		}
		//draws high score on top right
		for (int i = 0; i<7; i++) {
			if (digitsHighScore[i] == null) digitsHighScore[i] = numbers[0];
			g.drawImage(digitsHighScore[i], 423-18*i, 20, 20, 25, this);	
		}
	//graphics for screens other than the game screen		
	}else if (currentScreen.equals("credits")) { 	//credits screen
		g.drawImage(credits, 0, 0, screenWidth, screenHeight, this);
		g.drawImage(smile, 385, 509, 30, 12, this);
		delay(1000/FPS);
		repaint();
	}else if (currentScreen.equals("controls")) {	//controls screen
		g.drawImage(controls, 0, 0, screenWidth, screenHeight, this);
		delay(1000/FPS);
		repaint();
	}else if (currentScreen.equals("mainmenu")) {	//main menu screen
		g.drawImage(title, 15, 75, 450, 116, this);
		g.drawImage(icon, 83, 200, 300, 62, this);
		delay(1000/FPS);
		repaint();
	}
		//draw press m to return in certain conditions
		if (currentScreen.equals("game")&&lives == 0 || currentScreen.equals("credits") || currentScreen.equals("controls")) {		//show press m on other pages or if game over
			if (pressmCounter % 80 == 0) {
				pressmCheck = true;
			}else if (pressmCounter % 40 == 0){
				pressmCheck = false;
			}
			if (pressmCheck) g.drawImage(pressm, 17, 535, 424, 23, this);
			pressmCounter++;
		//draw pacman on bottom left of screen showing lives remaining
		}else if (currentScreen.equals("game")&&lives > 1) {	//show how many lives left
			g.drawImage(pacmanleft, 23, 570, 20, 20, this);	
			if (lives > 2) {
				g.drawImage(pacmanleft, 50, 570, 20, 20, this);
			}
		}
		
		//set button visibility according to the screen
		if (!currentScreen.equals("mainmenu")) { 	//setting visible here gives error; moved to keyPressed
			startButton.setVisible(false);
			controlsButton.setVisible(false);
			creditsButton.setVisible(false);
		}
		
		//if screen is changed from another screen to game screen
		if (changeScreen) {		//(to avoid making newGame() static)
			newGame();
			changeScreen = false;
		}
		
	} 	//Paint Component

	@Override
	public void keyTyped(KeyEvent e) {
	}

	// Description: allows player to control the game with the keyboard
	// Parameters: KeyEvent e, Return: n/a
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (currentScreen == "game") {
			//controlling pacman with WASD or arrow keys
			if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {			
				right = false;
				up = false;
				down = false;
				left = true;
			}else if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
				left = false;
				up = false;
				down = false;
				right = true;
			}else if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
				down = false;
				right = false;
				left = false;
				up = true;
			}else if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
				up = false;
				right = false;
				left = false;
				down = true;
			//pausing the game
			}if (key == KeyEvent.VK_P) {
				if (pauseMode) {
					pauseMode = false;
				}
				else{
					pauseMode = true;
				}
			}
		//changing screens
		}if (key == KeyEvent.VK_M) {
			currentScreen = "mainmenu";
			gameOver = true;
			startButton.setVisible(true);
			controlsButton.setVisible(true);
			creditsButton.setVisible(true);
		}else if (key == KeyEvent.VK_N) {
			currentScreen = "game";
			newGame();
		}else if (key == KeyEvent.VK_X) {
			System.exit(0);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {	
	}

	@Override
	public void mousePressed(MouseEvent e) {	
	}

	@Override
	public void mouseReleased(MouseEvent e) {	
	}

	@Override
	public void mouseEntered(MouseEvent e) {		
	}

	@Override
	public void mouseExited(MouseEvent e) {		
	}
	
	// Description: changes the appearance of pacman periodically from open to closed mouth while the game is running
	// Parameters: n/a, Return: n/a
	public void pacmanChange() {
		if (countdown==0 && pause==0 && !pauseMode) {
		if (pacGraphicsCounter%2==0 && pacGraphicsCounter<6) {		//pacman is made to look like it is opening and closing its mouth
			if (left) {
				savedpacman = pacmanleft;
			}else if (right) {
				savedpacman = pacmanright;
			}else if (up) {
				savedpacman = pacmanup;
			}else if (down) {
				savedpacman = pacmandown;
			}
			pacman = pacmanwhole;
		} else if (pacGraphicsCounter == 6) {
			pacman = savedpacman;
		}
		pacGraphicsCounter++;
		if (pacGraphicsCounter>12) {
			pacGraphicsCounter = 0;
		}
		}
	}
	
	// Description: makes the background flash when all the pellets are eaten
	// Parameters: n/a, Return: n/a
	public void backgroundChange() {
		MediaTracker tracker = new MediaTracker (this);
		tracker.addImage (backgroundblue, 0);
		tracker.addImage (backgroundwhite, 1);
		try
		{
		    tracker.waitForAll ();
		}
		catch (InterruptedException e)
		{
		}
		while(backgroundCounter<24000000) { 		//background flashes if all the pellets are eaten
			if (backgroundCounter%4000000==0 && backgroundCounter>=8000000) {		
				background = backgroundblue;
			}else if (backgroundCounter%2000000==0 && backgroundCounter>=8000000) {
				background = backgroundwhite;
			}
			backgroundCounter++;
			String s = "delay";
			s = s.toUpperCase();
			repaint();
		}
		background = backgroundblue;
		backgroundCounter = 0;
	}
	
	// Description: allows pacman to move using the predetermined directions
	// Parameters: n/a, Return: n/a
	void move() {	
		if (countdown == 0 && pause == 0 && !pauseMode) {		//runs while the game has started and is not paused
		if(left) {
			if (leftturn == true || lastDirection.equals("left")) {
				player.x -= speed;
				if (!(lastDirection.equals("left"))){
					pacman = pacmanleft;
				}
				lastDirection = "left";
			}else {
				continueDirection();
				savedTurn = "left";
			}
		}
		else if(right) {
			if (rightturn == true || lastDirection.equals("right")) {
				player.x += speed;
				if (!(lastDirection.equals("right"))){
					pacman = pacmanright;
				}
				lastDirection = "right";
			}else {
				continueDirection();
				savedTurn = "right";
			}
		}
		else if(up) {
			if (upturn == true || lastDirection.equals("up")) {
				player.y -= speed;
				if (!(lastDirection.equals("up"))){
					pacman = pacmanup;
				}
				lastDirection = "up";
			}else {
				continueDirection();
				savedTurn = "up";
			}
		}
		else if(down) {
			if (downturn == true || lastDirection.equals("down")) {
				player.y += speed;
				if (!(lastDirection.equals("down"))){
					pacman = pacmandown;
				}
				lastDirection = "down";
			}else {
				continueDirection();
				savedTurn = "down";
			}
		}
		playerFieldX.x = player.x - 6;		//update pacman's testing fields based on pacman's new coordinates
		playerFieldY.x = player.x - 4;
		playerFieldX.y = player.y - 3;
		playerFieldY.y = player.y - 6;
		playerEat.x = player.x + 12;
		playerEat.y = player.y + 12;
		upturn = true;					//reset the turns to available (will turn false if unavailable)
		downturn = true;
		leftturn = true;
		rightturn = true;
		}
		pacmanChange();		//call for pacman graphics change
	}
	
	// Description: if rectangle being passed in exits the screen, make it appear on other side of screen
	// Parameters: the rectangle being checked (can be pacman or one of the ghosts), Return: n/a
	void keepInBound(Rectangle check) {
		if(check.x < 0)									//if exit on right of screen, come back on left, and vice versa
			check.x = screenWidth - check.width;
		else if(check.x > screenWidth - check.width)
			check.x = 0;
		
		if(check.y < 0)
			check.y = 0;
		else if(check.y > screenHeight - check.height)
			check.y = screenHeight - check.height;
	}

	// Description: checks whether the rectangle being passed in collides with a wall and stops it if it does
	// Parameters: the rectangle being checked (pacman or one of the ghosts) and a wall (all the walls from the map array will be passed in), Return: n/a
	void checkWallCollision(Rectangle wall, Rectangle check) {		//check collisions with actual characters (not fields)
		//check if rect touches walls
		if(wall != null && check.intersects(wall)) {	//avoid error: checks that there is a rectangle passed in
			//stop the rect from moving
			double left1 = check.getX();
			double right1 = check.getX() + check.getWidth();
			double top1 = check.getY();
			double bottom1 = check.getY() + check.getHeight();
			double left2 = wall.getX();
			double right2 = wall.getX() + wall.getWidth();
			double top2 = wall.getY();
			double bottom2 = wall.getY() + wall.getHeight();
			
			if(right1 > left2 && 
			   left1 < left2 && 
			   right1 - left2 < bottom1 - top2 && 
			   right1 - left2 < bottom2 - top1)
	        {
	            //rect collides from left side of the walls
				check.x = wall.x - check.width;
				if (check == blinkyRect) {
	        		bColRight = false;
	        	}else if (check == pinkyRect) {
	        		pColRight = false;
	        	}else if (check == inkyRect) {
	        		iColRight = false;
	        	}else if (check == clydeRect) {
	        		cColRight = false;
	        	}
	        }
	        else if(left1 < right2 &&
	        		right1 > right2 && 
	        		right2 - left1 < bottom1 - top2 && 
	        		right2 - left1 < bottom2 - top1)
	        {
	            //rect collides from right side of the walls
	        	check.x = wall.x + wall.width;
	        	if (check == blinkyRect) {
	        		bColLeft = false;
	        	}else if (check == pinkyRect) {
	        		pColLeft = false;
	        	}else if (check == inkyRect) {
	        		iColLeft = false;
	        	}else if (check == clydeRect) {
	        		cColLeft = false;
	        	}
	        }
	        else if(bottom1 > top2 && top1 < top2)
	        {
	            //rect collides from top side of the walls
	        	check.y = wall.y - check.height;
	        	if (check == blinkyRect) {
	        		bColDown = false;
	        	}else if (check == pinkyRect) {
	        		pColDown = false;
	        	}else if (check == inkyRect) {
	        		iColDown = false;
	        	}else if (check == clydeRect) {
	        		cColDown = false;
	        	}
	        }
	        else if(top1 < bottom2 && bottom1 > bottom2)
	        {
	            //rect collides from bottom side of the walls
	        	check.y = wall.y + wall.height;
	        	if (check == blinkyRect) {
	        		bColUp = false;
	        	}else if (check == pinkyRect) {
	        		pColUp = false;
	        	}else if (check == inkyRect) {
	        		iColUp = false;
	        	}else if (check == clydeRect) {
	        		cColUp = false;
	        	}
	        }
		}
	}
		
	// Description: allows pacman to continue in the previous direction when pacman cannot turn/is not turning
	// Parameters: n/a, Return: n/a
	void continueDirection () {			//pacman continues in the last direction
		if (lastDirection == "left") {
			right = false;
			up = false;
			down = false;
			left = true;
		}else if (lastDirection == "right") {
			left = false;
			up = false;
			down = false;
			right = true;
		}else if (lastDirection == "up") {
			right = false;
			left = false;
			down = false;
			up = true;
		}else if (lastDirection == "down") {
			right = false;
			left = false;
			up = false;
			down = true;
		}
	}
	
	// Description: checks whether the character being passed in is able to turn right or left based on whether it will almost immediately collide with a wall
	// Parameters: the rectangle being checked (pacman or one of the ghosts) and a wall (all the walls from the map array will be passed in), Return: n/a
	void checkTurnX(Rectangle wall, Rectangle checkingX) {		//checks for collision with the field (x field, checks for left/right turns)
		//check if rect touches walls
		if(wall != null && checkingX.intersects(wall)) {	//avoid error: checks that there is a rectangle passed in
			//stop the rect from moving
			double left1 = checkingX.getX();
			double right1 = checkingX.getX() + checkingX.getWidth();
			double top1 = checkingX.getY();
			double bottom1 = checkingX.getY() + checkingX.getHeight();
			double left2 = wall.getX();
			double right2 = wall.getX() + wall.getWidth();
			double top2 = wall.getY();
			double bottom2 = wall.getY() + wall.getHeight();

			if(right1 > left2 && 
					   left1 < left2 && 
					   right1 - left2 < bottom1 - top2 && 
					   right1 - left2 < bottom2 - top1)
			{
			            //rect collides from left side of the walls
				if (checkingX == playerFieldX) {
					rightturn = false;
					checkrightPac = true;
				}
				else if (checkingX == blinkyFieldX) {
					rightturnBlinky = false;
				}else if (checkingX == pinkyFieldX) {
					rightturnPinky = false;
				}else if (checkingX == inkyFieldX) {
					rightturnInky = false;
				}else if (checkingX == clydeFieldX) {
					rightturnClyde = false;
				}
			}
			else if(left1 < right2 &&
						right1 > right2 && 
			        	right2 - left1 < bottom1 - top2 && 
			        	right2 - left1 < bottom2 - top1)
			{
			            //rect collides from right side of the walls
			    if (checkingX == playerFieldX) {
			    	leftturn = false;
			    	checkleftPac = true;
			    }
			    else if (checkingX == blinkyFieldX) {
					leftturnBlinky = false;
			    }else if (checkingX == pinkyFieldX) {
			    	leftturnPinky = false;
			    }else if (checkingX == inkyFieldX) {
					leftturnInky = false;
				}else if (checkingX == clydeFieldX) {
					leftturnClyde = false;
				}
			 }
			else if(bottom1 > top2 && top1 < top2){
			            //rect collides from top side of the walls
			}
			else if(top1 < bottom2 && bottom1 > bottom2){
			            //rect collides from bottom side of the walls
			}
			
		}
	}
	
	// Description: checks whether the character being passed in is able to turn up or down based on whether it will almost immediately collide with a wall
	// Parameters: the rectangle being checked (pacman or one of the ghosts) and a wall (all the walls from the map array will be passed in), Return: n/a
	void checkTurnY(Rectangle wall, Rectangle checkingY) { //checks for collision with the field (y field, checks for up/down turns)
		//check if rect touches walls
		if(wall != null && checkingY.intersects(wall)) {	//avoid error: checks that there is a rectangle passed in
			//stop the rect from moving
			double left1 = checkingY.getX();
			double right1 = checkingY.getX() + checkingY.getWidth();
			double top1 = checkingY.getY();
			double bottom1 = checkingY.getY() + checkingY.getHeight();
			double left2 = wall.getX();
			double right2 = wall.getX() + wall.getWidth();
			double top2 = wall.getY();
			double bottom2 = wall.getY() + wall.getHeight();
			
			if(right1 > left2 && 
					   left1 < left2 && 
					   right1 - left2 < bottom1 - top2 && 
					   right1 - left2 < bottom2 - top1)
			{
			            //rect collides from left side of the walls
			}
			else if(left1 < right2 &&
						right1 > right2 && 
			        	right2 - left1 < bottom1 - top2 && 
			        	right2 - left1 < bottom2 - top1)
			{
			            //rect collides from right side of the walls
			 }
			else if(bottom1 > top2 && top1 < top2){
			            //rect collides from top side of the walls
				if (checkingY == playerFieldY) {
					downturn = false;
					checkdownPac = true;
				} 
				else if (checkingY == blinkyFieldY) {
					downturnBlinky = false;
				}else if (checkingY == pinkyFieldY) {
					downturnPinky = false;
				}else if (checkingY == inkyFieldY) {
					downturnInky = false;
				}else if (checkingY == clydeFieldY) {
					downturnClyde = false;
				}
			}else if(top1 < bottom2 && bottom1 > bottom2){
			            //rect collides from bottom side of the walls
				if (checkingY == playerFieldY) {
					upturn = false;
					checkupPac = true;
				} 
				else if (checkingY == blinkyFieldY) {
					upturnBlinky = false;
				}else if (checkingY == pinkyFieldY) {
					upturnPinky = false;
				}else if (checkingY == inkyFieldY) {
					upturnInky = false;
				}else if (checkingY == clydeFieldY) {
					upturnClyde = false;
				}
			}
		}
	}
	
	// Description: if a down turn has been saved and pacman is now able to turn down, make pacman turn down
	// Parameters: n/a, Return: n/a
	void checkDownP () {
		if (checkdownPac == false && savedTurn.equals("down")) {
			right = false;
			up = false;
			left = false;
			down = true;
			savedTurn = "";
		}else if (savedTurn.equals("down")){
			checkdownPac = false;
		}
	}
	// Description: if an up turn has been saved and pacman is now able to turn up, make pacman turn up
	// Parameters: n/a, Return: n/a
	void checkUpP () {
		if (checkupPac == false && savedTurn.equals("up")) {
			right = false;
			down = false;
			left = false;
			up = true;
			savedTurn = "";
		}else if (savedTurn.equals("up")){
			checkupPac = false;
		}
	}
	// Description: if a left turn has been saved and pacman is now able to turn left, make pacman turn left
	// Parameters: n/a, Return: n/a
	void checkLeftP() {
		if(checkleftPac == false && savedTurn.equals("left")) {
			left = true;
	     	right = false;
	     	up = false;
	     	down = false;
	     	savedTurn = "";
		}else if (savedTurn.equals("left")){
			checkleftPac = false;
		}
	}
	// Description: if a right turn has been saved and pacman is now able to turn right, make pacman turn right
	// Parameters: n/a, Return: n/a
	void checkRightP() {
		if(checkrightPac == false && savedTurn.equals("right")) {
			right = true;
	     	left = false;
	     	up = false;
	     	down = false;
	     	savedTurn = "";		
		}else if (savedTurn.equals("right")){
			checkrightPac = false;
		}
	}
	
	// Description: update the positions of all the characters based on the positions of the main rectangle of each respective character
	// Parameters: n/a, Return: n/a
	void updatePositions() {
		blinkyFieldX.x = blinkyRect.x - 6; 		//update fields based on blinky's current position
		blinkyFieldY.x = blinkyRect.x - 4;
		blinkyFieldX.y = blinkyRect.y - 3;
		blinkyFieldY.y = blinkyRect.y - 6;
		blinkyEat.x = blinkyRect.x + 12;
		blinkyEat.y = blinkyRect.y + 12;
		pinkyFieldX.x = pinkyRect.x - 6; 		//update fields based on pinky's current position
		pinkyFieldY.x = pinkyRect.x - 4;
		pinkyFieldX.y = pinkyRect.y - 3;
		pinkyFieldY.y = pinkyRect.y - 6;
		pinkyEat.x = pinkyRect.x + 12;
		pinkyEat.y = pinkyRect.y + 12;
		inkyFieldX.x = inkyRect.x - 6; 		//update fields based on inky's current position
		inkyFieldY.x = inkyRect.x - 4;
		inkyFieldX.y = inkyRect.y - 3;
		inkyFieldY.y = inkyRect.y - 6;
		inkyEat.x = inkyRect.x + 12;
		inkyEat.y = inkyRect.y + 12;
		clydeFieldX.x = clydeRect.x - 6; 		//update fields based on clyde's current position
		clydeFieldY.x = clydeRect.x - 4;
		clydeFieldX.y = clydeRect.y - 3;
		clydeFieldY.y = clydeRect.y - 6;
		clydeEat.x = clydeRect.x + 12;
		clydeEat.y = clydeRect.y + 12;
	}
	
	// Description: reset all the turning and collision variables for all the ghosts after the checks are done and a move is made
	// Parameters: n/a, Return: n/a
	void resetColTurnVariables() {
		upturnPinky = true;				//reset all the turn and collision variables for pinky
		downturnPinky = true;
		leftturnPinky = true;
		rightturnPinky = true;
		pColUp = true;
		pColDown = true;
		pColLeft = true;
		pColRight = true;
		upturnBlinky = true;				//reset all the turn and collision variables for blinky
		downturnBlinky = true;
		leftturnBlinky = true;
		rightturnBlinky = true;
		bColUp = true;
		bColDown = true;
		bColLeft = true;
		bColRight = true;
		upturnInky = true;				//reset all the turn and collision variables for inky
		downturnInky = true;
		leftturnInky = true;
		rightturnInky = true;
		iColUp = true;
		iColDown = true;
		iColLeft = true;
		iColRight = true;
		upturnClyde = true;				//reset all the turn and collision variables for clyde
		downturnClyde = true; 
		leftturnClyde = true;
		rightturnClyde = true;
		cColUp = true;
		cColDown = true;
		cColLeft = true;
		cColRight = true;
	}
	
	// Description: check whether pacman has eaten a pellet, power pellet, or fruit and update score/high score/mode accordingly
	// Parameters: n/a, Return: n/a
	void checkPoints(Rectangle pellet, int row, int col, Rectangle powerpellet) {
		//check if pacman has eaten a pellet and updates score accordingly
		if(pellet != null && playerEat.intersects(pellet)) {	//avoid error: checks that there is a rectangle passed in
				score += 10;
				pelletsEaten++;
				map[row][col] = 0;
				pellets[row][col] = null;
		//checks if pacman has eaten a power pellet and updates score/mode accordingly
		}else if(powerpellet != null && playerEat.intersects(powerpellet)) {	//avoid error: checks that there is a rectangle passed in
			score += 50;
			pelletsEaten++;
			map[row][col] = 0;
			powerpellets[row][col] = null;
			if (frightenedMode) {		//if ghosts are already in frightened mode, reset the timer for the mode
				frightenedCounter = 0;
				ghostsEaten = 0;
			}
			frightenedMode = true;
			frightenedBlinky = true;
			frightenedPinky = true;
			frightenedInky = true;
			frightenedClyde = true;
			blinkyspeed = 1510-5*level;	//make ghosts slower in frightened mode
			pinkyspeed = 1510-5*level;
			inkyspeed = 1510-5*level;
			clydespeed = 1510-5*level;
		//checks if pacman has eaten a fruit and updates score accordingly
		}else if(playerEat.intersects(fruitEat) && fruitOut && !fruitEaten) {	//checks whether fruit is out and updates to eaten if it is 
			if (level%2==0) score+=500;
			else score+=300;
			fruitOut = false;
			fruitEaten = true;
		}
		//checks if the score is larger than the high score and updates high score accordingly
		if (score > highscore) {
			highscore = score;
		}
	}
	
	// Description: checks whether pacman has collided with a ghost (in normal mode) and updates pacman's lives/makes pacman die accordingly, or allows pacman to eat a frightened ghost
	// Parameters: n/a, Return: n/a
	void checkGhostCollision() {		//check collisions with the eating fields of the characters (smaller)
		//check if rect touches walls
		if((playerEat.intersects(blinkyEat) && !frightenedBlinky || playerEat.intersects(pinkyEat) && !frightenedPinky || 
				playerEat.intersects(inkyEat) && !frightenedInky || playerEat.intersects(clydeEat) && !frightenedClyde )&& pause == 0 && !pauseMode) {
		sameLife = false;
		pacmandie = Toolkit.getDefaultToolkit().createImage("pacmandie.gif");	//loads gif again to allow it to play from beginning
		MediaTracker tracker = new MediaTracker (this);
		tracker.addImage (pacmanwhole, 0);
		tracker.addImage (pacmandie, 1);
		try
		{
		    tracker.waitForAll ();
		}
		catch (InterruptedException e)
		{
		}
		pacman = pacmanwhole;
		delay(500);
		pacman = pacmandie;
		blinky = null;	//make ghosts graphics disappear temporarily
		pinky = null; 
		inky = null;
		clyde = null;
			if (lives == 1) {	//if there was only one life left, game over
				gameOver = true;
				lives--;
				while (true) {
					repaint();
					delay(1000/FPS);
					if(!gameOver) break;
				}
			}else {	//if more than one life left, pause, then reset game
				try {
					repaint();
					blinkyThread.sleep(500);
					pinkyThread.sleep(500);
					inkyThread.sleep(500);
					clydeThread.sleep(500);
				} catch (InterruptedException e) {
				}
				updatePositions();
				reset();
				try {
					blinkyThread.sleep(500);
					pinkyThread.sleep(500);
					inkyThread.sleep(500);
					clydeThread.sleep(500);
				} catch (InterruptedException e) {
				}
				repaint();
				lives--;
			}
		//if the ghosts are in frightened mode and have not already been eaten, allow pacman to eat them if collision is detected
		}else if (playerEat.intersects(blinkyEat)&&!eatenBlinky || playerEat.intersects(pinkyEat)&&!eatenPinky 
				|| playerEat.intersects(inkyEat)&&!eatenInky || playerEat.intersects(clydeEat)&&!eatenClyde) {	//ghosts are in frightened mode
			score += 200*Math.pow(2, ghostsEaten);
			currentghost = ghostScore[ghostsEaten];
			if(playerEat.intersects(blinkyEat)) {		//updates mode of ghosts to eaten if they are eaten and make them return to home base
				returnBlinky = true;
				eatenBlinky = true;
			}else if (playerEat.intersects(pinkyEat)) {
				returnPinky = true;
				eatenPinky = true;
			}else if (playerEat.intersects(inkyEat)) {
				returnInky = true;
				eatenInky = true;
			}else if (playerEat.intersects(clydeEat)) {
				returnClyde = true;
				eatenClyde = true;
			}
			updatePositions();
		}
		
	}
	
	// Description: resets all the variables necessary for a reset of the game (not entirely new game: just if a life is lost or all pellets are eaten)
	// Parameters: n/a, Return: n/a
	void reset() {
		livesUsedTotal++;
		player.x = 216;			//reset pacman positions
		player.y = 423;
		playerEat.x = 229;
		playerEat.y = 435;
		MediaTracker tracker = new MediaTracker (this);
		tracker.addImage (pacmanwhole, 0);	//load graphics
		tracker.addImage (blinkyleft, 1);
		tracker.addImage (pinkydown, 2);
		tracker.addImage (inkyup, 3);
		tracker.addImage (clydeup, 4);
		try
		{
		    tracker.waitForAll ();
		}
		catch (InterruptedException e)
		{
		}
		blinkyRect.x = 216;	//reset ghost positions
		blinkyRect.y = 220;
		pinkyRect.x = 215;
		pinkyRect.y = 270;
		inkyRect.x = 186;
		inkyRect.y = 270;
		clydeRect.x = 244;
		clydeRect.y = 270;
		left = false;		//reset turns
		right = false;
		up = false;
		down = false;
		fruitEaten = false;		//reset other necessary variables
		frightenedMode = false;
		frightenedCounter = 0;
		returnBlinky = false;
		returnPinky = false;
		returnInky = false;
		returnClyde = false;
		pacman = pacmanwhole;	//reset graphics
		blinky = blinkyleft;
		pinky = pinkydown;
		inky = inkyup;
		clyde = clydeup;
		blinkyspeed = 1120-10*level;
		pinkyspeed = 1120-10*level;
		inkyspeed = 1120-10*level;
		clydespeed = 1120-10*level;
		ghostsEaten = 0;
		sameLife = true;
		countdown = 150;	//countdown to starting the game
		pause = 0;		//unpause
		pauseMode = false;
	}
	
	// Description: resets the variables necessary for an entirely new game
	// Parameters: n/a, Return: n/a
	void newGame() {
		gameOver = false;
		reset();
		updatePositions();
		sameLife = false;
		pelletsEaten = 0;
		score = 0;
		lives = 3;
		level = 1;
		for (int row = 0; row < map.length; row++) {
			for (int col = 0; col < map[row].length; col++) {
				map[row][col] = originalMap[row][col];
			}
		}
		sameLife = true;
		repaint();
	}
	
	// Description: allows for a delay in the program for a certain amount of time
	// Parameters: the duration of the delay in milliseconds, Return: n/a
	 private void delay (int milliSec)
	    {
		try
		{
		    Thread.sleep (milliSec);
		}
		catch (InterruptedException e)
		{
		}
	    }	
    

	 public class blinkyThread extends Thread		//thread for blinky
	{
		// Description: the continuously running method that controls blinky's actions
		// Parameters: n/a, Return: n/a
		public void run ()
		{
			delay(1500);
			while(true) {
			int blinkyModeCounter = 0;	//counter to determine if blinky is in scatter mode or chase mode
			while(true && !gameOver)
			{
				while (pause != 0 || pauseMode) {
					String s = "delay";		//to allow the loop to update
					s = s.toUpperCase();
				}
				while(sameLife && countdown == 0 && gameOver == false) {
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkWallCollision(walls[row][col], blinkyRect);	
						}
					}
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkTurnX(walls[row][col], blinkyFieldX);	
						}
					}
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkTurnY(walls[row][col], blinkyFieldY);	
						}
					}
					blinkyModeCounter = moveBlinky(blinkyModeCounter);
					keepInBound(blinkyRect);
					blinkyModeCounter++;
					try
					{
						sleep (blinkyspeed/FPS);
					}
					catch (Exception e)
					{
					}
					
					if(returnBlinky) {
						blinky = currentghost;
						pacman = null;
						pause = 100;
						while (pause > 0) {
							pause--;
							delay(1000/FPS);
						}
						while (pauseMode) {
							delay(1000/FPS);
						}
						ghostsEaten++;
						blinky = blinkydown;
						blinkyspeed = 1120-10*level;
						blinkyRect.x = 215;
						blinkyRect.y = 271;
						updatePositions();
						frightenedBlinky = false; 
						delay(1500);
						while (pauseMode) {
							delay(1000/FPS);
						}
						blinkyRect.x = 216;
						blinkyRect.y = 220;
						delay(1500);
						returnBlinky = false;
						eatenBlinky = false;
						moveBlinky(0);
					}
					while(pause != 0 || pauseMode) {
						delay(1000/FPS);
					}
				}
				String s = "delay";
				s.toUpperCase();		//to allow the loop to update
			}
			String s = "delay";
			s.toUpperCase();		//to allow the loop to update
			}
		}
	}
	
	// Description: allows blinky to follow the target (either the player or blinky's corner) depending on the current mode
	// Parameters: the counter running that determines whether blinky is in chase or scatter mode, Return: the updated counter
	int moveBlinky(int blinkyModeCounter) {		//move blinky
		leftBlinky = false;		//reset everything to false
		rightBlinky = false;
		downBlinky = false;
		upBlinky = false;
		
		Rectangle corner = new Rectangle(420, 60, 40, 40); 	//blinky's corner (top right)
		Rectangle target = player;
		
		if (frightenedBlinky) {			//set blinky's mode to frightened
			target = player;
		}else if(blinkyModeCounter < 1500) {			//set blinky's mode to scatter or chase
			target = player;	//chase
		}else if (blinkyModeCounter < 1850) {
			target = corner;	//scatter
		}else {
			blinkyModeCounter = 0;	//reset counter
		}
		
		if(frightenedBlinky) {	//if blinky is in frightened mode
			if((blinkyRect.getCenterY() - target.getCenterY() < 0) && !(lastDirectionBlinky.equals("down")) && (upturnBlinky) && bColUp) { //initial check which two directions blinky should head
				upBlinky = true;
			}else if ((blinkyRect.getCenterY() - target.getCenterY() > 0) && !(lastDirectionBlinky.equals("up")) && (downturnBlinky) && bColDown){
				downBlinky = true;
			}
			if (blinkyRect.getCenterX() - target.getCenterX() < 0 && !(lastDirectionBlinky.equals("right")) && (leftturnBlinky) && bColLeft) {	//initial check which two directions blinky should head
				leftBlinky = true;
			}else if (blinkyRect.getCenterX() - target.getCenterX() > 0 && !(lastDirectionBlinky.equals("left")) && (rightturnBlinky) && bColRight){
				rightBlinky = true;
			}
		}else {		//if blinky is not in frightened mode
			if((blinkyRect.getCenterY() - target.getCenterY() > 0) && !(lastDirectionBlinky.equals("down")) && (upturnBlinky) && bColUp) { //initial check which two directions blinky should head
				upBlinky = true;
			}else if ((blinkyRect.getCenterY() - target.getCenterY() < 0) && !(lastDirectionBlinky.equals("up")) && (downturnBlinky) && bColDown){
				downBlinky = true;
			}
			if (blinkyRect.getCenterX() - target.getCenterX() > 0 && !(lastDirectionBlinky.equals("right")) && (leftturnBlinky) && bColLeft) {	//initial check which two directions blinky should head
				leftBlinky = true;
			}else if (blinkyRect.getCenterX() - target.getCenterX() < 0 && !(lastDirectionBlinky.equals("left")) && (rightturnBlinky) && bColRight){
				rightBlinky = true;
			}
		}
		if (!(upBlinky || downBlinky || leftBlinky || rightBlinky)) {			//if none were made true, pick the only direction it is able to go in (regardless of good or bad)
			if (upturnBlinky && !(lastDirectionBlinky.equals("down")) && bColUp) {
				upBlinky = true;
			}else if (downturnBlinky && !(lastDirectionBlinky.equals("up")) && bColDown) {
				downBlinky = true;
			}else if (leftturnBlinky && !(lastDirectionBlinky.equals("right")) && bColLeft) {
				leftBlinky = true;
			}else if (rightturnBlinky && !(lastDirectionBlinky.equals("left")) && bColRight) {
				rightBlinky = true;
			}
		}else if (leftBlinky&&upBlinky || leftBlinky&&downBlinky || rightBlinky&&upBlinky || rightBlinky&&downBlinky) {		//if two were made true, pick the better option (one further away)
			if(!frightenedBlinky) {
				if (Math.abs(blinkyRect.getCenterY() - target.getCenterY()) > Math.abs(blinkyRect.getCenterX() - target.getCenterX())) {
					rightBlinky = false;
					leftBlinky = false;
				}else {
					upBlinky = false;
					downBlinky = false;
				}
			}else {	//not the best option for more variation in ghost paths in frightened mode
				if (Math.abs(blinkyRect.getCenterY() - target.getCenterY()) > Math.abs(blinkyRect.getCenterX() - target.getCenterX())) {
					upBlinky = false;
					downBlinky = false;
				}else {
					rightBlinky = false;
					leftBlinky = false;
				}
		}
		}
		if (bColDown&&lastDirectionBlinky.equals("down") && !rightBlinky && !leftBlinky) {		//otherwise, continue in current direction if there is no collision in that direction
			downBlinky = true;
			upBlinky = false;
		}else if (bColUp&&lastDirectionBlinky.equals("up") && !rightBlinky && !leftBlinky) {
			upBlinky = true;
			downBlinky = false;
		}else if (bColRight&&lastDirectionBlinky.equals("right") && !upBlinky && !downBlinky) {
			rightBlinky = true;
			leftBlinky = false;
		}else if (bColLeft&&lastDirectionBlinky.equals("left") && !upBlinky && !downBlinky) {
			leftBlinky = true;
			rightBlinky = false;
		}
		
		if(leftBlinky) {											//move blinky
				blinkyRect.x -= speed;
				if (!(lastDirectionBlinky.equals("left")) && !frightenedBlinky || revertGhost){
					blinky = blinkyleft;
				}
				lastDirectionBlinky = "left";
		}
		else if(rightBlinky) {
			blinkyRect.x += speed;
			if (!(lastDirectionBlinky.equals("right"))  && !frightenedBlinky || revertGhost){
				blinky = blinkyright;
			}
			lastDirectionBlinky = "right";
		}
		else if(upBlinky) {
			blinkyRect.y -= speed;
			if (!(lastDirectionBlinky.equals("up"))  && !frightenedBlinky || revertGhost){
				blinky = blinkyup;
			}
			lastDirectionBlinky = "up";
		}
		else if(downBlinky) {
			blinkyRect.y += speed;
			if (!(lastDirectionBlinky.equals("down"))  && !frightenedBlinky || revertGhost){
				blinky = blinkydown;
			}
			lastDirectionBlinky = "down";
		}
		
		return blinkyModeCounter;
	}
		
	
	public class pinkyThread extends Thread		//thread for pinky
	{
		// Description: the continuously running method that controls pinky's actions
		// Parameters: n/a, Return: n/a
		public void run ()
		{
			int currentLife;
			while(true) {
			int pinkyModeCounter = 0;	//counter to determine if pinky is in scatter mode or chase mode
			while(true && !gameOver) {
				while (pelletsEaten <= 6) {
					String s = "delay";		//to allow the loop to update
					s = s.toUpperCase();
				}
				currentLife = livesUsedTotal;
				delay(6000);
				while (pause != 0 || pauseMode) {
					String s = "delay";		//to allow the loop to update
					s = s.toUpperCase();
				}
				if (!gameOver && currentLife == livesUsedTotal) {
				pinkyRect.y = 220; 
				pinkyFieldX.x = 210;
				pinkyFieldY.x = 212;
				pinkyFieldX.y = 217; 
				pinkyFieldY.y = 213; //pink ghost
				if (outPinky == false) {
					delay(1500);
					outPinky = true;
				}
				while (sameLife && currentLife == livesUsedTotal && countdown == 0 && gameOver == false)
				{
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkWallCollision(walls[row][col], pinkyRect);	
						}
					}
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkTurnX(walls[row][col], pinkyFieldX);	
						}
					}
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkTurnY(walls[row][col], pinkyFieldY);	
						}
					}
					pinkyModeCounter = movePinky(pinkyModeCounter);
					keepInBound(pinkyRect);
					pinkyModeCounter++;
					try
					{
						sleep (pinkyspeed/FPS);
					}
					catch (Exception e)
					{
					}
					
					if(returnPinky) {
						pinky = currentghost;
						pacman = null;
						pause = 100;
						while (pause > 0) {
							pause--;
							delay(1000/FPS);
						}
						while (pauseMode) {
							delay(1000/FPS);
						}
						ghostsEaten++;
						pinky = pinkydown;
						pinkyspeed = 1120-10*level;
						pinkyRect.x = 215;
						pinkyRect.y = 270;
						updatePositions();
						frightenedPinky = false; 
						delay(2000);
						while (pauseMode) {
							delay(1000/FPS);
						}
						pinkyRect.x = 216;
						pinkyRect.y = 220;
						delay(1500);
						returnPinky = false;
						eatenPinky = false;
						movePinky(0);
					}
					outPinky = false;
					
					while(pause != 0 || pauseMode) {
					delay(1000/FPS);
					}
				}
				}
			}
			String s = "update";		//to allow the loop to update
			s = s.toUpperCase();
		}
		}
	}
	
		// Description: allows pinky to follow the target (either the player or pinky's corner) depending on the current mode
		// Parameters: the counter running that determines whether pinky is in chase or scatter mode, Return: the updated counter
		int movePinky(int pinkyModeCounter) {		//move pinky
		leftPinky = false;		//reset everything to false
		rightPinky = false;
		downPinky = false;
		upPinky = false;
		
		Rectangle corner = new Rectangle(10, 60, 40, 40); 	//pinky's corner (top left)
		Rectangle target = corner;
		
		if (frightenedPinky) {			//set pinky's mode to frightened
			target = player;
		}else if(pinkyModeCounter < 350) {			//set pinky's mode to scatter or chase
			target = corner;	//scatter
		}else if (pinkyModeCounter < 800) {
			target = player;	//chase
		}else {
			pinkyModeCounter = 0;	//reset counter
		}
		
		if(frightenedPinky) {	//if pinky is in frightened mode
			if((pinkyRect.getCenterY() - target.getCenterY() < 0) && !(lastDirectionPinky.equals("down")) && (upturnPinky) && pColUp) { //initial check which two directions pinky should head
				upPinky = true;
			}else if ((pinkyRect.getCenterY() - target.getCenterY() > 0) && !(lastDirectionPinky.equals("up")) && (downturnPinky) && pColDown){
				downPinky = true;
			}
			if (pinkyRect.getCenterX() - target.getCenterX() < 0 && !(lastDirectionPinky.equals("right")) && (leftturnPinky) && pColLeft) {	//initial check which two directions pinky should head
				leftPinky = true;
			}else if (pinkyRect.getCenterX() - target.getCenterX() > 0 && !(lastDirectionPinky.equals("left")) && (rightturnPinky) && pColRight){
				rightPinky = true;
			}
		}else {		//if pinky is not in frightened mode
			if((pinkyRect.getCenterY() - target.getCenterY() > 0) && !(lastDirectionPinky.equals("down")) && (upturnPinky) && pColUp) { //initial check which two directions pinky should head
				upPinky = true;
			}else if ((pinkyRect.getCenterY() - target.getCenterY() < 0) && !(lastDirectionPinky.equals("up")) && (downturnPinky) && pColDown){
				downPinky = true;
			}
			if (pinkyRect.getCenterX() - target.getCenterX() > 0 && !(lastDirectionPinky.equals("right")) && (leftturnPinky) && pColLeft) {	//initial check which two directions pinky should head
				leftPinky = true;
			}else if (pinkyRect.getCenterX() - target.getCenterX() < 0 && !(lastDirectionPinky.equals("left")) && (rightturnPinky) && pColRight){
				rightPinky = true;
			}
		}
		if (!(upPinky || downPinky || leftPinky || rightPinky)) {			//if none were made true, pick the only direction it is able to go in (regardless of good or bad)
			if (upturnPinky && !(lastDirectionPinky.equals("down")) && pColUp) {
				upPinky = true;
			}else if (downturnPinky && !(lastDirectionPinky.equals("up")) && pColDown) {
				downPinky = true;
			}else if (leftturnPinky && !(lastDirectionPinky.equals("right")) && pColLeft) {
				leftPinky = true;
			}else if (rightturnPinky && !(lastDirectionPinky.equals("left")) && pColRight) {
				rightPinky = true;
			}
		}else if (leftPinky&&upPinky || leftPinky&&downPinky || rightPinky&&upPinky || rightPinky&&downPinky) {		//if two were made true, pick the better option (one further away)
			if (Math.abs(pinkyRect.getCenterY() - target.getCenterY()) > Math.abs(pinkyRect.getCenterX() - target.getCenterX())) {
				rightPinky = false;
				leftPinky = false;
			}else {
				upPinky = false;
				downPinky = false;
			}
		}
		if (pColDown&&lastDirectionPinky.equals("down") && !rightPinky && !leftPinky) {		//otherwise, continue in current direction if there is no collision in that direction
			downPinky = true;
			upPinky = false;
		}else if (pColUp&&lastDirectionPinky.equals("up") && !rightPinky && !leftPinky) {
			upPinky = true;
			downPinky = false;
		}else if (pColRight&&lastDirectionPinky.equals("right") && !upPinky && !downPinky) {
			rightPinky = true;
			leftPinky = false;
		}else if (pColLeft&&lastDirectionPinky.equals("left") && !upPinky && !downPinky) {
			leftPinky = true;
			rightPinky = false;
		}		
		if(leftPinky) {											//move pinky
				pinkyRect.x -= speed;
				if (!(lastDirectionPinky.equals("left")) && !frightenedPinky || revertGhost){
					pinky = pinkyleft;
				}
				lastDirectionPinky = "left";
		}
		else if(rightPinky) {
			pinkyRect.x += speed;
			if (!(lastDirectionPinky.equals("right")) && !frightenedPinky || revertGhost){
				pinky = pinkyright;
			}
			lastDirectionPinky = "right";
		}
		else if(upPinky) {
			pinkyRect.y -= speed;
			if (!(lastDirectionPinky.equals("up")) && !frightenedPinky || revertGhost){
				pinky = pinkyup;
			}
			lastDirectionPinky = "up";
		}
		else if(downPinky) {
			pinkyRect.y += speed;
			if (!(lastDirectionPinky.equals("down")) && !frightenedPinky || revertGhost){
				pinky = pinkydown;
			}
			lastDirectionPinky = "down";
		}
		
		return pinkyModeCounter;
	}
	
	
	
	public class inkyThread extends Thread		//thread for inky
	{
		// Description: the continuously running method that controls inky's actions
		// Parameters: n/a, Return: n/a
		public void run ()
		{
			int currentLife;
			while(true) {
			int inkyModeCounter = 0;	//counter to determine if inky is in scatter mode or chase mode
			while(true && !gameOver) {
				while (pelletsEaten <= 30) {
					String s = "delay";		//to allow the loop to update
					s = s.toUpperCase();
				}
				currentLife = livesUsedTotal;
				delay(9000);
				while (pause != 0 || pauseMode) {
					String s = "delay";		//to allow the loop to update
					s = s.toUpperCase();
				}
				if (!gameOver && currentLife == livesUsedTotal) {
				inkyRect.x = 217;
				inkyRect.y = 220;
				inkyFieldX.x = 210;
				inkyFieldY.x = 212;
				inkyFieldX.y = 217; 
				inkyFieldY.y = 213; //blue ghost
				if (outInky == false) {
					delay(1500);
					outInky = true;
				}
				while (sameLife && currentLife == livesUsedTotal && countdown == 0 && gameOver == false)
				{
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkWallCollision(walls[row][col], inkyRect);	
						}
					}
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkTurnX(walls[row][col], inkyFieldX);	
						}
					}
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkTurnY(walls[row][col], inkyFieldY);	
						}
					}
					inkyModeCounter = moveInky(inkyModeCounter);
					keepInBound(inkyRect);
					inkyModeCounter++;
					try
					{
						sleep (inkyspeed/FPS);
					}
					catch (Exception e)
					{
					}
					
					if(returnInky) {
						inky = currentghost;
						pacman = null;
						pause = 100;
						while (pause > 0) {
							pause--;
							delay(1000/FPS);
						}
						while (pauseMode) {
							delay(1000/FPS);
						}
						ghostsEaten++;
						inky = inkyup;
						inkyspeed = 1120-10*level;
						inkyRect.x = 186;
						inkyRect.y = 270;
						updatePositions();
						frightenedInky = false;
						delay(2500);
						while (pauseMode) {
							delay(1000/FPS);
						}
						inkyRect.x = 216;
						inkyRect.y = 220;
						delay(1500);
						returnInky = false;
						eatenInky = false;
						moveInky(0);
					}
					outInky = false;
					
					while(pause != 0 || pauseMode) {
						delay(1000/FPS);
					}
				}
				}
			}
			String s = "update";		//to allow the loop to update
			s = s.toUpperCase();
		}
		}
	}
	
	// Description: allows inky to follow the target (either the player or inky's corner) depending on the current mode
	// Parameters: the counter running that determines whether inky is in chase or scatter mode, Return: the updated counter	
	int moveInky(int inkyModeCounter) {		//move inky
		leftInky = false;		//reset everything to false
		rightInky = false;
		downInky = false;
		upInky = false;
		
		Rectangle corner = new Rectangle(420, 520, 40, 40); 	//inky's corner (bottom right)
		Rectangle target = corner;
		
		if (frightenedInky) {			//set inky's mode to frightened
			target = player;
		}else if(inkyModeCounter < 1000) {			//set inky's mode to scatter or chase
			target = corner;	//scatter
		}else if (inkyModeCounter < 1250) {
			target = player;	//chase
		}else {
			inkyModeCounter = 0;	//reset counter
		}
		
		if(frightenedInky) {	//if inky is in frightened mode
			if((inkyRect.getCenterY() - target.getCenterY() < 0) && !(lastDirectionInky.equals("down")) && (upturnInky) && iColUp) { //initial check which two directions inky should head
				upInky = true;
			}else if ((inkyRect.getCenterY() - target.getCenterY() > 0) && !(lastDirectionInky.equals("up")) && (downturnInky) && iColDown){
				downInky = true;
			}
			if (inkyRect.getCenterX() - target.getCenterX() < 0 && !(lastDirectionInky.equals("right")) && (leftturnInky) && iColLeft) {	//initial check which two directions inky should head
				leftInky = true;
			}else if (inkyRect.getCenterX() - target.getCenterX() > 0 && !(lastDirectionInky.equals("left")) && (rightturnInky) && iColRight){
				rightInky = true;
			}
		}else {		//if inky is not in frightened mode
			if((inkyRect.getCenterY() - target.getCenterY() > 0) && !(lastDirectionInky.equals("down")) && (upturnInky) && iColUp) { //initial check which two directions inky should head
				upInky = true;
			}else if ((inkyRect.getCenterY() - target.getCenterY() < 0) && !(lastDirectionInky.equals("up")) && (downturnInky) && iColDown){
				downInky = true;
			}
			if (inkyRect.getCenterX() - target.getCenterX() > 0 && !(lastDirectionInky.equals("right")) && (leftturnInky) && iColLeft) {	//initial check which two directions inky should head
				leftInky = true;
			}else if (inkyRect.getCenterX() - target.getCenterX() < 0 && !(lastDirectionInky.equals("left")) && (rightturnInky) && iColRight){
				rightInky = true;
			}
		}
		if (!(upInky || downInky || leftInky || rightInky)) {			//if none were made true, pick the only direction it is able to go in (regardless of good or bad)
			if (upturnInky && !(lastDirectionInky.equals("down")) && iColUp) {
				upInky = true;
			}else if (downturnInky && !(lastDirectionInky.equals("up")) && iColDown) {
				downInky = true;
			}else if (leftturnInky && !(lastDirectionInky.equals("right")) && iColLeft) {
				leftInky = true;
			}else if (rightturnInky && !(lastDirectionInky.equals("left")) && iColRight) {
				rightInky = true;
			}
		}else if (leftInky&&upInky || leftInky&&downInky || rightInky&&upInky || rightInky&&downInky) {		//if two were made true, pick the better option (one further away)
			if (!frightenedInky) {
				if (Math.abs(inkyRect.getCenterY() - target.getCenterY()) > Math.abs(inkyRect.getCenterX() - target.getCenterX())) {
					rightInky = false;
					leftInky = false;
				}else {
					upInky = false;
					downInky = false;
				}
			}else {	//not the best option for more variation in ghost paths in frightened mode
				if (Math.abs(inkyRect.getCenterY() - target.getCenterY()) > Math.abs(inkyRect.getCenterX() - target.getCenterX())) {
					upInky = false;
					downInky = false;
				}else {
					rightInky = false;
					leftInky = false;
				}
			}
		}
		if (iColDown&&lastDirectionInky.equals("down") && !rightInky && !leftInky) {		//otherwise, continue in current direction if there is no collision in that direction
			downInky = true;
			upInky = false;
		}else if (iColUp&&lastDirectionInky.equals("up") && !rightInky && !leftInky) {
			upInky = true;
			downInky = false;
		}else if (iColRight&&lastDirectionInky.equals("right") && !upInky && !downInky) {
			rightInky = true;
			leftInky = false;
		}else if (iColLeft&&lastDirectionInky.equals("left") && !upInky && !downInky) {
			leftInky = true;
			rightInky = false;
		}		
		if(leftInky) {											//move inky
				inkyRect.x -= speed;
				if (!(lastDirectionInky.equals("left")) && !frightenedInky || revertGhost){
					inky = inkyleft;
				}
				lastDirectionInky = "left";
		}
		else if(rightInky) {
			inkyRect.x += speed;
			if (!(lastDirectionInky.equals("right")) && !frightenedInky || revertGhost){
				inky = inkyright;
			}
			lastDirectionInky = "right";
		}
		else if(upInky) {
			inkyRect.y -= speed;
			if (!(lastDirectionInky.equals("up")) && !frightenedInky || revertGhost){
				inky = inkyup;
			}
			lastDirectionInky = "up";
		}
		else if(downInky) {
			inkyRect.y += speed;
			if (!(lastDirectionInky.equals("down")) && !frightenedInky || revertGhost){
				inky = inkydown;
			}
			lastDirectionInky = "down";
		}
		
		return inkyModeCounter;
	}
	
	
	
	public class clydeThread extends Thread		//thread for clyde
	{
		// Description: the continuously running method that controls clyde's actions
		// Parameters: n/a, Return: n/a
		public void run ()
		{
			int currentLife;
			while(true) {
			int clydeModeCounter = 0;	//counter to determine if clyde is in scatter mode or chase mode
			while(true && !gameOver) {
				while (pelletsEaten <= 80) {
					String s = "delay";		//to allow the loop to update
					s = s.toUpperCase();
				}
				currentLife = livesUsedTotal;
				delay(11000);
				while (pause != 0 || pauseMode) {
					String s = "delay";		//to allow the loop to update
					s = s.toUpperCase();
				}
				if (!gameOver && currentLife == livesUsedTotal){
				clydeRect.x = 216;
				clydeRect.y = 220;
				clydeFieldX.x = 211;
				clydeFieldY.x = 213;
				clydeFieldX.y = 217; 
				clydeFieldY.y = 213; //orange ghost
				if (outClyde == false) {		//makes sure the delay only happens once
					delay(1500);
					outClyde = true;
				}
				while (sameLife && currentLife == livesUsedTotal && countdown == 0 && gameOver == false)
				{
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkWallCollision(walls[row][col], clydeRect);	
						}
					}
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkTurnX(walls[row][col], clydeFieldX);	
						}
					}
					for(int row = 0; row < walls.length; row++) {
						for (int col = 0; col<walls[0].length; col++) {
							checkTurnY(walls[row][col], clydeFieldY);	
						}
					}
					clydeModeCounter = moveClyde(clydeModeCounter);
					keepInBound(clydeRect);
					clydeModeCounter++;
					try
					{
						sleep (clydespeed/FPS);
					}
					catch (Exception e)
					{
					}
					
					if(returnClyde) {
						clyde = currentghost;
						pacman = null;
						pause = 100;
						while (pause > 0) {
							pause--;
							delay(1000/FPS);
						}
						while (pauseMode) {
							delay(1000/FPS);
						}
						ghostsEaten++;
						clyde = clydeup;
						clyde = clydeup;
						clydespeed = 1120-10*level;
						clydeRect.x = 244;
						clydeRect.y = 270;
						updatePositions();
						frightenedClyde = false;
						delay(3000);
						while (pauseMode) {
							delay(1000/FPS);
						}
						clydeRect.x = 216;
						clydeRect.y = 220;
						delay(1500);
						returnClyde = false;
						eatenClyde = false;
						moveClyde(0);
					}
					outClyde = false;
					
					while(pause != 0 || pauseMode) {
						delay(1000/FPS);
					}
				}
				}
			}
			String s = "update";		//to allow the loop to update
			s = s.toUpperCase();
		}
		}
	}
	
	// Description: allows clyde to follow the target (either the player or clyde's corner) depending on the current mode
	// Parameters: the counter running that determines whether clyde is in chase or scatter mode, Return: the updated counter
	int moveClyde(int clydeModeCounter) {		//move clyde
		leftClyde = false;		//reset everything to false
		rightClyde = false;
		downClyde = false;
		upClyde = false;
		
		Rectangle corner = new Rectangle(10, 520, 40, 40); 	//clyde's corner (bottom left)
		Rectangle target = player;
		
		if (frightenedClyde) {			//set blinky's mode to frightened
			target = player;
		}else if(clydeModeCounter < 500) {			//set clyde's mode to scatter or chase
			target = player;	//chase
		}else if (clydeModeCounter < 1350) {
			target = corner;	//scatter
		}else {
			clydeModeCounter = 0;	//reset counter
		}
		
		if(frightenedClyde) {	//if clyde is in frightened mode
			if((clydeRect.getCenterY() - target.getCenterY() < 0) && !(lastDirectionClyde.equals("down")) && (upturnClyde) && cColUp) { //initial check which two directions clyde should head
				upClyde = true;
			}else if ((clydeRect.getCenterY() - target.getCenterY() > 0) && !(lastDirectionClyde.equals("up")) && (downturnClyde) && cColDown){
				downClyde = true;
			}
			if (clydeRect.getCenterX() - target.getCenterX() < 0 && !(lastDirectionClyde.equals("right")) && (leftturnClyde) && cColLeft) {	//initial check which two directions clyde should head
				leftClyde = true;
			}else if (clydeRect.getCenterX() - target.getCenterX() > 0 && !(lastDirectionClyde.equals("left")) && (rightturnClyde) && cColRight){
				rightClyde = true;
			}
		}else {		//if clyde is not in frightened mode
			if((clydeRect.getCenterY() - target.getCenterY() > 0) && !(lastDirectionClyde.equals("down")) && (upturnClyde) && cColUp) { //initial check which two directions clyde should head
				upClyde = true;
			}else if ((clydeRect.getCenterY() - target.getCenterY() < 0) && !(lastDirectionClyde.equals("up")) && (downturnClyde) && cColDown){
				downClyde = true;
			}
			if (clydeRect.getCenterX() - target.getCenterX() > 0 && !(lastDirectionClyde.equals("right")) && (leftturnClyde) && cColLeft) {	//initial check which two directions clyde should head
				leftClyde = true;
			}else if (clydeRect.getCenterX() - target.getCenterX() < 0 && !(lastDirectionClyde.equals("left")) && (rightturnClyde) && cColRight){
				rightClyde = true;
			}
		}
		if (!(upClyde || downClyde || leftClyde || rightClyde)) {			//if none were made true, pick the only direction it is able to go in (regardless of good or bad)
			if (upturnClyde && !(lastDirectionClyde.equals("down")) && cColUp) {
				upClyde = true;
			}else if (downturnClyde && !(lastDirectionClyde.equals("up")) && cColDown) {
				downClyde = true;
			}else if (leftturnClyde && !(lastDirectionClyde.equals("right")) && cColLeft) {
				leftClyde = true;
			}else if (rightturnClyde && !(lastDirectionClyde.equals("left")) && cColRight) {
				rightClyde = true;
			}
		}else if (leftClyde&&upClyde || leftClyde&&downClyde || rightClyde&&upClyde || rightClyde&&downClyde) {		//if two were made true, pick the better option (one further away)
			if (Math.abs(clydeRect.getCenterY() - target.getCenterY()) > Math.abs(clydeRect.getCenterX() - target.getCenterX())) {
				rightClyde = false;
				leftClyde = false;
			}else {
				upClyde = false;
				downClyde = false;
			}
		}
		if (cColDown&&lastDirectionClyde.equals("down") && !rightClyde && !leftClyde) {		//otherwise, continue in current direction if there is no collision in that direction
			downClyde = true;
			upClyde = false;
		}else if (cColUp&&lastDirectionClyde.equals("up") && !rightClyde && !leftClyde) {
			upClyde = true;
			downClyde = false;
		}else if (cColRight&&lastDirectionClyde.equals("right") && !upClyde && !downClyde) {
			rightClyde = true;
			leftClyde = false;
		}else if (cColLeft&&lastDirectionClyde.equals("left") && !upClyde && !downClyde) {
			leftClyde = true;
			rightClyde = false;
		}		
		if(leftClyde) {											//move clyde
				clydeRect.x -= speed;
				if (!(lastDirectionClyde.equals("left")) && !frightenedClyde || revertGhost){
					clyde = clydeleft;
				}
				lastDirectionClyde = "left";
		}
		else if(rightClyde) {
			clydeRect.x += speed;
			if (!(lastDirectionClyde.equals("right")) && !frightenedClyde || revertGhost){
				clyde = clyderight;
			}
			lastDirectionClyde = "right";
		}
		else if(upClyde) {
			clydeRect.y -= speed;
			if (!(lastDirectionClyde.equals("up")) && !frightenedClyde || revertGhost){
				clyde = clydeup;
			}
			lastDirectionClyde = "up";
		}
		else if(downClyde) {
			clydeRect.y += speed;
			if (!(lastDirectionClyde.equals("down")) && !frightenedClyde){
				clyde = clydedown;
			}
			lastDirectionClyde = "down";
		}
		
		return clydeModeCounter;
	}
	
	// Description: keeps a counter to keep track of how long ghosts have been frightened, enforces flashes to warn of time running out, and reverts when it does
	// Parameters: n/a, Return: n/a	
	void frightenedGhosts() {
		if (frightenedCounter == 660) {	//reverts to normal mode if frightened mode counter max is reached
			frightenedCounter = 0;
			frightenedMode = false;
			ghostsEaten = 0;
			revertGhost = true;		
			moveBlinky(0);			//call all the ghost moving methods if they are out of home base to revert the ghost graphics uniformly
			movePinky(0);
			if (!outInky) {
				inky = inkyup;
			}else {
			moveInky(0);
			}
			if (!outClyde) {
				clyde = clydeup;
			}else {
			moveClyde(0);
			}
			revertGhost = false;			
		//ghosts that have not been eaten switch between the two frightened graphics
		}else if (frightenedCounter%40 == 0 && frightenedCounter > 500 && pause == 0 && !pauseMode) {	//flashing ghost graphics
			if (frightenedBlinky && !eatenBlinky) blinky = frightenedghost2;
			if (frightenedPinky && !eatenPinky) pinky = frightenedghost2;
			if (frightenedInky && !eatenInky) inky = frightenedghost2;
			if (frightenedClyde && !eatenClyde) clyde = frightenedghost2;
		}else if (frightenedCounter%20 == 0) {
			if (frightenedBlinky && !eatenBlinky) blinky = frightenedghost;
			if (frightenedPinky && !eatenPinky) pinky = frightenedghost;
			if (frightenedInky && !eatenInky) inky = frightenedghost;
			if (frightenedClyde && !eatenClyde) clyde = frightenedghost;
			if (pause != 0) frightenedCounter--;
		}
		frightenedCounter++;
	}
	
	
	// Description: Main Method: creates the panels, frames, and buttons, then adds them to each other
	// Parameters: String[] args, Return: n/a
	public static void main(String[] args) {
		
		//Creation of window
		//makes a brand new JFrame
		JFrame frame = new JFrame ("PAC-MAN ~ Sophie Chan");
		//makes a new copy of your "game" that is also a JPanel
		PacmanSophieChan myPanel = new PacmanSophieChan ();
		//adds JPanel to the frame so it can be seen
		frame.add(myPanel);
		//to allow keyboard input
		frame.addKeyListener(myPanel);
		//to allow mouse input
		frame.addMouseListener(myPanel);
		//to make frame visible
		frame.setVisible(true);
		//some weird method that must be run
		frame.pack();
		//places the frame in the middle of the screen
		frame.setLocationRelativeTo(null);
		//without this, the thread will keep running even when the window is closed!
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//don't want to resize the window because
		//it might mess up the graphics and collisions
		frame.setResizable(false);

		//main menu buttons
		myPanel.setLayout (new GridBagLayout());					//use grid bag layout
		GridBagConstraints gbc = new GridBagConstraints();
		startButton = new JButton (startbutton);		//create buttons
		controlsButton = new JButton (controlsbutton);
		creditsButton = new JButton (creditsbutton);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;	
		gbc.gridy = 0;	
		myPanel.add(Box.createRigidArea(new Dimension(0,175)), gbc);
		gbc.gridy = 1;
		myPanel.add(startButton, gbc);	
		gbc.gridy = 2;	
		myPanel.add(controlsButton, gbc);	
		gbc.gridy = 3;	
		myPanel.add(creditsButton, gbc);	
		startButton.setBorderPainted(false);	//get rid of the button backgrounds
		startButton.setFocusPainted(false);
		startButton.setContentAreaFilled(false);
		controlsButton.setBorderPainted(false);
		controlsButton.setFocusPainted(false);
		controlsButton.setContentAreaFilled(false);
		creditsButton.setBorderPainted(false);
		creditsButton.setFocusPainted(false);
		creditsButton.setContentAreaFilled(false);
		startButton.setFocusable(false);		//keep the focus on the key listener and not the buttons
		controlsButton.setFocusable(false);
		creditsButton.setFocusable(false);
		
		//action listeners for all the buttons on the main page: change current screen according to which is pressed
		startButton.addActionListener(new ActionListener()
		{
			// Description: changes the screen to game screen if start is pressed
			// Parameters: ActionEvent e, Return: n/a
		    public void actionPerformed(ActionEvent e)
		    {currentScreen = "game";
		    changeScreen = true;}
		});
		creditsButton.addActionListener(new ActionListener()
		{
			// Description: changes the screen to credits screen if credits is pressed
			// Parameters: ActionEvent e, Return: n/a
		    public void actionPerformed(ActionEvent e)
		    {currentScreen = "credits";}
		});
		controlsButton.addActionListener(new ActionListener()
		{
			// Description: changes the screen to controls screen if controls is pressed
			// Parameters: ActionEvent e, Return: n/a
		    public void actionPerformed(ActionEvent e)
		    {currentScreen = "controls";}
		});

		//set up the icon image (tracker not needed for the icon image)
		frame.setIconImage (pacmanright);
		
	}
}