import java.util.Random;
import java.util.*;

public class TestAgent extends Agent
{
	// private Random rn;
	
	public TestAgent(Player player)
	{
		this.player = player;
		this.type = Constant.RandomAgent;
		// rn = new Random();
	}
	/**
	 * Return a Movement decided by the agent base on the current situation
	 * 
	 * @param none
	 * @return Movement the decided movement
	 */
	public Movement decideMove()
	{
		// int num = player.numHandCards;
		// int count = (num > Constant.maxMovementCard)? rn.nextInt(Constant.maxMovementCard + 1):num;
//		int count = 0;
//		Card[] c = new Card[count];
//		int nextStart = start + count;
//		int j = 0;
//		for(int i = start; i < start + count; i++)
//		{
//			c[j] = player.hand[i];
//			j++;
//		}
//		start = nextStart;
//		return new Movement(c);
		LinkedList<Movement> ll = Player.genLegalMove(player.getGameShowMove(), player.hand, player.getIsRevo(), player.getIsStartGame());
		int numElement = ll.size();
		int index = Constant.rn.nextInt(numElement);
		System.out.println(numElement + " " + index);
		return ll.get(index);
	}
}
