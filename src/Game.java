import javax.swing.*;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class Game extends JPanel
{
	// attribute
	private Player[] players; // player 0 is the human player
	private Deck deck;
	private GUIResource gui;
	private KeyController keyController;
	private Movement showMove;
	private boolean isEnd;
	private int turn;
	
	// for player 0
	private boolean[] choose;
	private int numChoose;
	private boolean player0Fin;
	
	// method 
	public Game()
	{
		gui = new GUIResource(this);
		setFocusable(true);
		setLayout(null);
		keyController = new KeyController();
		
		players = new Player[Constant.numPlayer];
		deck = new Deck();
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			players[i] = new Player(this, i);
		}
		choose = new boolean[Constant.numMaxHandCard];
		numChoose = 0;
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			choose[i] = false;
		}
		player0Fin = false;
		showMove = null;
		isEnd = false;
		turn = 0;
	}
	public void reset()
	{
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			players[i].reset();
		}
		numChoose = 0;
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			choose[i] = false;
		}
		player0Fin = false;
		showMove = null;
		isEnd = false;
		turn = 0;
		deck.shuffle();
	}
	public void deal()
	{
		int turn = 0;
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			Card c = deck.getNext();
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
	}
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
	public boolean playerPressedEnter(int player)
	{
		// actually for player 0
		if(player == 0 && numChoose > 0)
		{
			// construct Movement from chosen card
			Card[] chosenCard = new Card[numChoose];
			int j = 0;
			numChoose = 0;
			for(int i = 0; i < Constant.numMaxHandCard; i++)
			{
				if(choose[i])
				{
					choose[i] = false;
					chosenCard[j++] = players[player].hand[i];
				}
			}
			Movement move = new Movement(chosenCard);
			// players[0].doMove();
			players[player].doMove(move);
			return true;
		}
		else
			return false;
	}
	public boolean playerPressedPass(int player)
	{
		return true;
	}
	public void doMove(Movement move, int playerIndex)
	{
		if(playerIndex != 0)
		{
			// draw the back of cards
			drawBackCards(playerIndex, false);
		}
		// draw the cards show in the middle
		drawShowCards(move);
		repaint();
	}
	
	private void drawShowCards(Movement move)
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
			System.out.println(c);
		}
	}
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
	
	// start the game begin from the players[turn] 
	public void run()
	{
		deal();
		while(!isEnd)
		{
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
			turn = (turn + 1) % Constant.numPlayer;
		}
	}
	
	public void waitPlayer0()
	{
		// System.out.println("Sleep");
		try
		{
		    Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
		    e.printStackTrace();
		    System.exit(-1);
		}
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(gui.background, 0, 0, Constant.width, Constant.height, this);
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
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER && !isEnter)
			{
				isEnter = true;
				playerPressedEnter(0);
				player0Fin = true;
			}
			if(key == KeyEvent.VK_P && !isPass)
			{
				isPass = true;
				player0Fin = true;
			}
		}
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















