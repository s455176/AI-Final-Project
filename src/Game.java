import javax.swing.*;

import java.awt.event.*;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.util.Arrays;

public class Game extends JPanel implements ActionListener
{
	// attribute
	private Timer timer;
	private Player[] players; // player 0 is the human player
	private Deck deck;
	private GUIResource gui;
	private KeyController keyController;
	private Movement showMove;
	private boolean isGameEnd, isRoundEnd, isStartGame;
	public boolean[] isPlayerPassed;
	
	// for player 0
	private boolean[] choose;
	private int numChoose;
	private boolean player0Fin;
	
	// B01902018
	public PlayerGameState gameState;
	
	// method 
	/**
	 * Constructor of the game
	 */
	public Game()
	{
		gui = new GUIResource(this);
		setFocusable(true);
		setLayout(null);
		keyController = new KeyController();
		
		players = new Player[Constant.numPlayer];
		isPlayerPassed = new boolean[Constant.numPlayer];
		deck = new Deck();
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			players[i] = new Player(this, i);
			isPlayerPassed[i] = false;
		}
		choose = new boolean[Constant.numMaxHandCard];
		numChoose = 0;
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			choose[i] = false;
		}
		player0Fin = false;
		showMove = null;
		isGameEnd = false;
		isRoundEnd = false;
		isStartGame = true;
		timer = new Timer(10, this);
        timer.start();
		
		// B01902018
		gameState = new PlayerGameState();
	}
	public void reset()
	{
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			players[i].reset();
			isPlayerPassed[i] = false;
		}
		numChoose = 0;
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			choose[i] = false;
		}
		player0Fin = false;
		removeShowCards();
		isGameEnd = false;
		isStartGame = true;
		isRoundEnd = false;
		deck.shuffle();
	}
	
	
	public boolean getIsStartGame()
	{
		return isStartGame;
	}
	
	/**
	 * deal the cards to 4 players, and also need to draw the cards on the JPanel
	 */
	public int deal()
	{
		int turn = 0;
		int startingPlayer = -1;
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			Card c = deck.getNext();
			if(c.getIndex() == 42)
				startingPlayer = turn;
			players[turn].getCard(c);
			turn = (turn + 1) % 4;
		}
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			players[i].sortHandCard();
		}
		
		// show player 0' s cards on the window
		for(int i = 0; i < players[0].numHandCards; i++)
		{
			int index = players[0].hand[i].index;
			choose[i] = false;
			gui.setPlayerAndPos(index, 0, i);
			gui.setCardLocation(index, Constant.playerCardLocationX[i], Constant.playerCardLocationY[0]);
			add(gui.cardResource[index], 0);
			gui.enableMouseListener(index);
		}
		// show player 1~3 's back cards on the window
		for(int i = 1; i <= 3; i++)
		{
			drawBackCards(i, true);
		}
		// all players are not pass at first
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			remove(gui.passLabel[i]);
		}
		
		if(startingPlayer == -1)
			SystemFunc.throwException("cannot find CLUB 3 when dealing cards");
		
		return startingPlayer;
	}
	/**
	 * label the clicked card as chosen by the player and move up in the hand 
	 * 
	 * @param player which player is the human player that clicks the mouse, actually the player 0
	 * @param position which card in the hand is clicked
	 * @param index the index of the card (0~52, and 0 is the Joker)
	 * @return check whether the clicking player is player 0
	 */
	public boolean playerClickCard(int player, int position, int index)
	{
		// actually for player 0
		if(player == 0)
		{
			choose[position] = !choose[position];
			numChoose = choose[position]? numChoose + 1: numChoose - 1;
			int yLocation = choose[position]? 1: 0;
			gui.setCardLocation(index, 
					Constant.playerCardLocationX[position], Constant.playerCardLocationY[yLocation]);
			return true;
		}
		else
			return false;
	}
	/**
	 * help human player construct the Movement and call the doMove just like an agent will do 
	 * 
	 * @param player which player is the human player that press the Enter, actually the player 0
	 * @return check whether the clicking player is player 0
	 */
	public boolean playerPressedEnter(int player)
	{
		// actually for player 0
		if(player == 0 && numChoose > 0 && numChoose <= Constant.maxMovementCard)
		{
			// construct Movement from chosen card
			Card[] chosenCard = new Card[numChoose];
			int j = 0;
			for(int i = 0; i < Constant.numMaxHandCard; i++)
			{
				if(choose[i])
				{
					chosenCard[j++] = players[player].hand[i];
				}
			}
			Movement move = new Movement(chosenCard);
			// cannot do the illegal move
			if(!Rule.isLegalMove(move, showMove, false, isStartGame))
			{
				return false;
			}
			// reset the is chosen boolean array if the move is legal and the move will be played
			numChoose = 0;
			for(int i = 0; i < Constant.numMaxHandCard; i++)
			{
				choose[i] = false;
			}
			System.out.println(move);
			// players[0].doMove();
			players[player].doMove(move);
			// SystemFunc.sleep(1000);
			return true;
		}
		else
			return false;
	}
	/**
	 * not done yet
	 * 
	 * @param player which player is the human player that press the Pass, actually the player 0
	 * @return check whether the clicking player is player 0
	 */
	public boolean playerPressedPass(int player)
	{
		if(player == 0)
		{
			Card[] pass = new Card[0];
			Movement passMovement = new Movement(pass);
			// cannot do the illegal move
			if(!Rule.isLegalMove(passMovement, showMove, false, isStartGame))
			{
				return false;
			}
			players[player].doMove(passMovement);
			return true;
		}
		else
			return false;
	}
	/**
	 * show the chosen card on the middle of the window, and if the player is not human player, 
	 * it should decrease the covered cards in hand
	 * 
	 * @param move the movement done by the player 
	 * @param playerIndex the index of the player who do the movements
	 */
	public void doMove(Movement move, int playerIndex)
	{
		if(!Rule.isLegalMove(move, showMove, false, isStartGame))
		{
			SystemFunc.throwException("Illegal Move by player " + playerIndex);
		}
		// move with 0 cards means player do the "pass" movement
		if(move.type == Constant.PASS) 
		{
			// draw passed label of the correspondent player
			drawPlayerPass(playerIndex);
			isPlayerPassed[playerIndex] = true;
		}
		else
		{
			if(playerIndex != 0)
			{
				// draw the back of cards
				drawBackCards(playerIndex, false);
			}
			// draw the cards show in the middle
			drawShowCards(move);
			resetPass();
		}
		// repaint();
	}
	private void drawPlayerPass(int playerIndex)
	{
		System.out.println("player pass");
		add(gui.passLabel[playerIndex], 0);
	}
	
	private void removeShowCards()
	{
		if(showMove != null)
		{
			for(int i = 0; i < showMove.numCards; i++)
			{
				Card c = showMove.cards[i];
				remove(gui.cardResource[c.index]);
				showMove.cards[i] = null;
			}
			showMove.numCards = 0;
			showMove = null;
		}
	}
	
	/**
	 * show the movement card on the middle of the window, called by game.doMove()
	 * 
	 * @param move the movement done by the player 
	 */
	private void drawShowCards(Movement move)
	{
		if(showMove != null)
		{
			removeShowCards();
		}
		int count = 0;
		showMove = move;
		for(int i = 0; i < move.numCards; i++)
		{
			count++;
			Card c = showMove.cards[i];
			gui.setCardLocation(c.index, 
					Constant.showLocationX0 + count * Constant.showLocationDX / (showMove.numCards + 1), 
					Constant.showLocationY);
			gui.disableMouseListener(c.index);
			add(gui.cardResource[c.index], 0);
			// System.out.println(c);
		}
	}
	/**
	 * draw the cards in the player' s hand, called by called by game.doMove()
	 * 
	 * @param playerIndex the index of player whose cards are drawn
	 * @param isInit whether is called when dealing the card
	 */
	private void drawBackCards(int playerIndex, boolean isInit)
	{
		int index = playerIndex - 1;
		if(isInit)
		{
			int width = (playerIndex % 2 == 0)? Constant.cardWidth: Constant.cardHeight;
			int height = (playerIndex % 2 == 0)? Constant.cardHeight: Constant.cardWidth;
			for(int i = 0; i < Constant.numMaxHandCard; i++)
			{
				if(i < players[playerIndex].numHandCards)
				{
					int x = (playerIndex % 2 == 0)? 
							Constant.playerCardBack[index][0] + (i + 1) * Constant.playerCardBack[index][1] / players[playerIndex].numHandCards:
							Constant.playerCardBack[index][2];
					int y = (playerIndex % 2 == 1)? 
							Constant.playerCardBack[index][0] + (i + 1) * Constant.playerCardBack[index][1] / players[playerIndex].numHandCards:
							Constant.playerCardBack[index][2];
					gui.cardBack[index][i].setBounds(x, y, width, height);
					add(gui.cardBack[index][i], 0);
				}
			}
		}
		else
		{
			for(int i = players[playerIndex].numHandCards; i < Constant.numMaxHandCard; i++)
			{
				remove(gui.cardBack[index][i]);
			}
		}
	}
	
	/**
	 * start playing the game begin from the player whose index is "turn"
	 */
	public void run()
	{
		int turn = deal();
		while(!isGameEnd)
		{
			turn = runRound(turn);
			System.out.println("========Round end");
			// remove the show cards in the middle
			removeShowCards();
			// repaint();
			SystemFunc.sleep(2000);
			if(numRemainPlayer() == 1)
			{
				isGameEnd = true;
				break;
			}
			if(turn == -1)
			{
				SystemFunc.throwException("Error: turn == -1");
			}
		}
		System.out.println("========Game end");
	}
	/**
	 * wait the human player to choose the cards and press the key
	 */
	public void waitPlayer0()
	{
		// System.out.println("Sleep");
		SystemFunc.sleep(1000);
	}

	public void resetPass()
	{
		// remove all the pass label
		// set all player pass boolean to false
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			remove(gui.passLabel[i]);
			isPlayerPassed[i] = false;
		}
	}
	private int numRemainPlayer()
	{
		int count = 0;
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			if(!players[i].isFinish())
				count++;
		}
		return count;
	}
	
	
	public Movement getShowMove()
	{
		return showMove;
	}
	
	private int findNextPlayerWithHandCards(int cur)
	{
		int nextStartTurn = -1;
		int i = (cur + 1) % Constant.numPlayer;
		while(i != cur)
		{
			if(!players[i].isFinish())
			{
				nextStartTurn = i;
				break;
			}
			i = (i + 1) % Constant.numPlayer;
		}
		return nextStartTurn;
	}
	
	public int runRound(int initTurn)
	{
		// init the round
		int passCount = 0;
		int turn = initTurn; /*note that the player who is the first player in the round is not allowed to pass*/
		int nextRoundStart = -1;
		isRoundEnd = false;
		
		// start the round 
		while(!isRoundEnd)
		{
			if(players[turn].isFinish())
			{
				turn = (turn + 1) % Constant.numPlayer;
				continue;
			}
			// all other players pass
			if(passCount == numRemainPlayer() - 1)
			{
				isRoundEnd = true;
				nextRoundStart = turn;
				break;
			}
			// player decide a movement
			System.out.println("turn " + turn + ", isGameStart: " + isStartGame);
			if(turn == 0)
			{
				addKeyListener(keyController);
				while(!player0Fin)
				{
					waitPlayer0();
				}
				removeKeyListener(keyController);
				player0Fin = false;
			}
			else
			{
				players[turn].takeTurn();
			}
			// after player do a moment
			isStartGame = false;
			if(!isPlayerPassed[turn] && players[turn].isFinish())
			{
				// a player has no hand cards after do a non-pass movement (ie, this player finishes)
				isRoundEnd = true;
				nextRoundStart = findNextPlayerWithHandCards(turn);
				break;
			}
			if(isPlayerPassed[turn])
				passCount++;
			else
				passCount = 0;
			turn = (turn + 1) % Constant.numPlayer;
		}
		return nextRoundStart;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
        repaint();  
    }
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(gui.background, 0, 0, Constant.width, Constant.height, this);
		// System.out.println("paint");
	}
	private class KeyController extends KeyAdapter
	{
		boolean isEnter;
		boolean isPass;
		public KeyController()
		{
			super();
			isEnter = false;
			isPass = false;
		}
		@Override
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER && !isEnter)
			{
				players[0].genLegalMove(showMove);
				isEnter = true;
				player0Fin = playerPressedEnter(0);
			}
			if(key == KeyEvent.VK_P && !isPass)
			{
				isPass = true;
				player0Fin = playerPressedPass(0);
			}
		}
		@Override
		public void keyReleased(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER && isEnter)
			{
				isEnter = false;
			}
			else if(key == KeyEvent.VK_P && isPass)
			{
				isPass = false;
			}
		}
	}
}















