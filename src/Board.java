import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class Board extends JPanel
{
	// attribute
	private Deck decks;
	
	private Image background;
	private CardLabel[] cardLabel;
	private int[] choose;
	private int numChoose;
	
	private boolean isEnter;
	
	// method
	public Board()
	{
		ImageIcon ii = new ImageIcon(this.getClass().getResource("images/background.jpg"));
		background = ii.getImage();
		
		decks = new Deck();
		decks.shuffle();
		
		addKeyListener(new KeyController());

		setFocusable(true);
		setLayout(null);

		cardLabel = new CardLabel[13];
		choose = new int[13];
		numChoose = 0;
		for(int i = 12; i >= 0; i--)
		{
			cardLabel[i] = new CardLabel(decks.getNext());
			choose[i] = 0;
		}
		Arrays.sort(cardLabel);
		for(int i = 12; i >= 0; i--)
		{
			cardLabel[i].setPosition(i);
			add(cardLabel[i]);
			cardLabel[i].setBounds(Constant.playerCardLocationX[i], Constant.playerCardLocationY[0], 
					Constant.cardWidth, Constant.cardHeight);
		}
		isEnter = false;
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(background, 0, 0, Constant.width, Constant.height, this);
	}
	private class KeyController extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			int count = 0;
			if(key == KeyEvent.VK_ENTER && !isEnter)
			{
				isEnter = true;
				for(int i = 0; i < 13; i++)
				{
					if(choose[i] == 2)
					{
						remove(cardLabel[i]);
						cardLabel[i] = null;
						choose[i] = 3;
					}
				}
				for(int i = 0; i < 13; i++)
				{
					if(choose[i] == 1)
					{
						count++;
						cardLabel[i].setBounds(Constant.showLocationX0 + count * Constant.showLocationDX / (numChoose + 1), 
								Constant.showLocationY,  
			        			Constant.cardWidth, Constant.cardHeight);
						cardLabel[i].disableMouseListener();
						choose[i] = 2;
					}
				}
				numChoose = 0;
				repaint();
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
	private class CardLabel extends JLabel implements Comparable<CardLabel>
	{
		private int position;
		private Card card;
		private MouseController mouseController;
		
		public CardLabel(Card c)
		{
			super(c.getIcon());
			System.out.println(c);
			mouseController = new MouseController(); 
			addMouseListener(mouseController);
			this.position = -1;
			card = c;
		}
		public void setPosition(int position)
		{
			this.position = position;
		}
		private class MouseController extends MouseAdapter
		{
	        public void mouseClicked(MouseEvent e)
	        {
	        	choose[position] = (choose[position] + 1) % 2;
	        	numChoose = (choose[position] == 1)? numChoose + 1: numChoose - 1;
	        	cardLabel[position].setBounds(Constant.playerCardLocationX[position], 
	        			Constant.playerCardLocationY[choose[position]], 
	        			Constant.cardWidth, Constant.cardHeight);
	        }
	    }
		public void disableMouseListener()
		{
			removeMouseListener(mouseController);
		}
		public int compareTo(CardLabel c)
		{
			return (this.card.index > c.card.index)? 1: -1;
		}
	}
}








