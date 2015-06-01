import java.util.Random;


public class TestAgent extends Agent
{
	private Random rn;
	private int start;
	
	public TestAgent(Player player)
	{
		this.player = player;
		start = 0;
		rn = new Random();
	}
	/**
	 * Return a Movement decided by the agent base on the current situation
	 * 
	 * @param none
	 * @return Movement the decided movement
	 */
	public Movement decideMove()
	{
		int num = player.numHandCards;
		// int count = (num > Constant.maxMovementCard)? rn.nextInt(Constant.maxMovementCard + 1):num;
		int count = 0;
		Card[] c = new Card[count];
		int nextStart = start + count;
		int j = 0;
		for(int i = start; i < start + count; i++)
		{
			c[j] = player.hand[i];
			j++;
		}
		start = nextStart;
		return new Movement(c);
	}
}
