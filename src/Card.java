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
			this.rank = rank;
			this.index = rank + suit * 13;
		}
	}
	public Card(int index)
	{
		this.index = index;
		this.suit = (index - 1) / 13;
		this.rank = this.index - this.suit * 13;
	}
	@Override
	public String toString()
	{
		String s = new String();
		
		if(suit == 0 && rank == 0)
			return "JOKER";
		
		switch(suit)
		{
		case Constant.DIAMOND:
			s = "\u2666";
			break;
		case Constant.CLUB:
			s = "\u2663";
			break;
		case Constant.HEART:
			s = "\u2665";
			break;
		case Constant.SPADE:
			s = "\u2660";
			break;
		}
		return s + this.rank;
	}
	
	public boolean isJoker()
	{
		return suit == 0 && rank == 0;
	}
	
	public int getSuit()
    {
        return suit;
    }
    public int getRank()
    {
        return rank;
    }
    public int getValue()
    {
        if (rank == 0 && suit == 0)
        {
            return 16;
        }
        return (rank < 3) ? rank + 13 : rank;
    }
    public int getIndex()
    {
    	return index;
    }
    public boolean hasSameRank(Card c)
    {
        return (this.rank == c.rank) ? true : false;
    }
    public boolean hasSameSuit(Card c)
    {
        return (this.suit == c.suit) ? true : false;
    }
	public int compareTo(Card c)
	{
		//return (this.index > c.index)? 1: -1;
        return (this.getValue() > c.getValue()) ? 1 : -1;
	}
}




