import java.util.LinkedList;
import java.util.Random;


public class MCTSAgent extends Agent
{
	private static int simulateNum = 100;
	private Random rn;
	
	public MCTSAgent(Player player)
	{
		this.player = player;
		rn = new Random();
	}
	
	public Movement decideMove()
	{
		GameState gs = new GameState(player.hand, player.getGameShowMove(), player.getIsRevo(), player.getIs11Revo(), player.index,
				player.getGameHistory(), player.index, player.getIsStartGame(), player.getPassCount(), 
				player.getNumRemainPlayer(), player.getPlayerNumCards(), player.getIsRevoBeforeRound());		
		
		double sum = 0;
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < simulateNum; i++)
		{
			SimulatedGame sg = new SimulatedGame(gs);
			int[] result = sg.startSimulate();
			sum += (double)result[player.index];
		}
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("time: " + (double)elapsedTime * 0.001 + ", result: " + sum / simulateNum);
		
		
		// the following just temp for gen new random movement 
		LinkedList<Movement> ll = Player.genLegalMove(player.getGameShowMove(), player.hand, player.getIsRevo(), player.getIsStartGame());
		int numElement = ll.size();
		int index = rn.nextInt(numElement);
		System.out.println(numElement + " " + index);
		return ll.get(index);
	}
}
