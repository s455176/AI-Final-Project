import javax.swing.*;
import java.awt.*;

public class Card implements Constant, Comparable<Card>
{
	// attribute
	private int suit;
	private int rank;
	private int index;
	private Image graphic;
	private ImageIcon ii;
	
	// method 
	public Card()
	{}
	public Card(int suit, int rank)
	{
		this.suit = suit;
		this.rank = rank - 1;
		this.index = rank * 13 + suit + 1;
		ii = new ImageIcon(this.getClass().getResource(Card.filePrefix +  
				this.index + ".gif"));
		graphic = ii.getImage();
	}
	public Card(int index)
	{
		this.index = index + 1;
		this.rank = index % 13;
		this.suit = index / 13;
		ii = new ImageIcon(this.getClass().getResource(Card.filePrefix + 
				this.index + ".gif"));
		graphic = ii.getImage();
	}
	public String toString()
	{
		String s = new String();
		switch(suit)
		{
		case DIAMOND:
			s = "DIAMOND";
			break;
		case CLUB:
			s = "CLUB";
			break;
		case HEART:
			s = "HEART";
			break;
		case SPADE:
			s = "SPADE";
			break;
		}
		return "Suit: " + s +  ", " + "Rank: " + (this.rank + 1);
	}
	public Image getImage()
	{
		return graphic;
	}
	public ImageIcon getIcon()
	{
		return ii;
	}
	public int compareTo(Card c)
	{
		return (this.index > c.index)? 1: 0;
	}
	public int getIndex()
	{
		return index;
	}
	
	// unit test 
	public static void main(String[] args)
	{
		Card c = new Card(1, Card.DIAMOND);
		System.out.println(c);
	}
}




