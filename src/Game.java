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
	private Movement showMove;
	
	// for player 0
	private Card[] player0Hand;
	private boolean[] choose;
	private int numChoose;
	
	// method 
	public Game()
	{
		gui = new GUIResource(this);
		setFocusable(true);
		setLayout(null);
		addKeyListener(new KeyController());
		
		players = new Player[Constant.numPlayer];
		deck = new Deck();
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			players[i] = new Player(this);
		}
		player0Hand = new Card[Constant.numMaxHandCard];
		choose = new boolean[Constant.numMaxHandCard];
		numChoose = 0;
		for(int i = 0; i < Constant.numMaxHandCard; i++)
		{
			choose[i] = false;
		}
		showMove = null;
		// deal();
	}
	public void deal()
	{
		int turn = 0;
		int count = 0;
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			Card c = deck.getNext();
			// record player0's cards to add them to JPanel, and open the mouseListener
			if(turn == 0)
			{
				player0Hand[count++] = c;
			}
			else
				players[turn].getCard(c);
			turn = (turn + 1) % 4;
		}
		Arrays.sort(player0Hand, 0, count);
		for(int i = 1; i < Constant.numPlayer; i++)
		{
			players[i].sortHandCard();
		}
		
		// show player0' s cards on the window
		for(int i = 0; i < count; i++)
		{
			int index = player0Hand[i].index;
			players[0].getCard(player0Hand[i]);
			choose[i] = false;
			gui.setPlayerAndPos(index, 0, i);
			gui.setCardLocation(index, Constant.playerCardLocationX[i], Constant.playerCardLocationY[0]);
			add(gui.cardResource[index], 0);
			gui.enableMouseListener(index);
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
					chosenCard[j++] = player0Hand[i];
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
	public void doMove(Movement move)
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
		}
		repaint();
	}
	
	// run the game
	public void run()
	{}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(gui.background, 0, 0, Constant.width, Constant.height, this);
	}
	private class KeyController extends KeyAdapter
	{
		boolean isEnter;
		public KeyController()
		{
			super();
			isEnter = false;
		}
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER && !isEnter)
			{
				isEnter = true;
				playerPressedEnter(0);
			}
		}
		public void keyReleased(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER && isEnter)
			{
				isEnter = false;
			}
		}
	}
}















