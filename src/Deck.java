import java.io.PrintStream;
import java.util.Random;

public class Deck
{
	// attribute
	private Card[] cards  = new Card[Constant.MAX_NUM_CARD];
	// private Random rn = new Random();
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
	@Override
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
		Constant.rn.setSeed(System.currentTimeMillis());
		for(int i = 0; i < Constant.MAX_NUM_CARD; i++)
		{
			Card hold = cards[i];
			int shuf = Constant.rn.nextInt(Constant.MAX_NUM_CARD);
			cards[i] = cards[shuf];
			cards[shuf] = hold;
		}
	}
	/**
	 * get the next card in the deck, or say in the Card[]
	 * 
	 * @return Card the next card in the deck, if there is no card in the deck, an exception will be thrown
	 */
	public Card getNext()

	{
		if(position < Constant.MAX_NUM_CARD)
		{
			return cards[position++];
		}
		else
		{
			SystemFunc.throwException("No cards availible");
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
