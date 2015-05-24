import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class Card extends JLabel implements Comparable<Card>
{
	// attribute
	public int suit;
	public int rank;
	public int index;
	
	// method 
	public Card() {}
	public Card(int suit, int rank)
	{
		if(suit == 0 && rank == 0)
		{
			this.suit = 0;
			this.rank = 0;
			this.index = 0;
		}
		else
		{
			this.suit = suit;
			this.rank = rank - 1;
			this.index = rank * 13 + suit + 1;
		}
	}
	public Card(int index)
	{
		this.index = index;
		this.rank = index % 13;
		this.suit = index / 13;
	}
	public String toString()
	{
		String s = new String();
		switch(suit)
		{
		case Constant.DIAMOND:
			s = "DIAMOND";
			break;
		case Constant.CLUB:
			s = "CLUB";
			break;
		case Constant.HEART:
			s = "HEART";
			break;
		case Constant.SPADE:
			s = "SPADE";
			break;
		}
		return "Suit: " + s +  ", " + "Rank: " + (this.rank + 1);
	}
	public int compareTo(Card c)
	{
		return (this.index > c.index)? 1: -1;
	}
}




