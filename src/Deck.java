import java.io.PrintStream;
import java.util.Random;

public class Deck
{
	// attribute
	private Card[] cards  = new Card[Constant.MAX_NUM_CARD];
	Random rn = new Random();
	private int position;
	
	// method
	public Deck()
	{
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			cards[i] = new Card(i);
		}
		this.shuffle();
		position = 0;
	}
	public String toString()
	{
		String s = new String();
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			s = s + cards[i] + "\n";
		}
		return s;
	}
	public void shuffle()
	{
		rn.setSeed(System.currentTimeMillis());
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			Card hold = cards[i];
			int shuf = rn.nextInt(Constant.MAX_NUM_CARD);
			cards[i] = cards[shuf];
			cards[shuf] = hold;
		}
	}
	public Card getNext()

	{
		if(position < Constant.MAX_NUM_CARD)
		{
			return cards[position++];
		}
		else
		{
			try
			{
				throw new Exception("No cards availible");
			}
			catch(Exception e)
			{
				e.printStackTrace(new PrintStream(System.err));
				System.exit(-1);
			}
			return new Card();
		}
	}
	public void reset()
	{
		this.shuffle();
		this.position = 0;
	}
	
	// unit test
	public static void main(String[] args)
	{
		Deck deck = new Deck();
		System.out.println(deck);
		deck.shuffle();
		System.out.println();
		System.out.println(deck);
	}
}
