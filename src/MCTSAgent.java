import java.util.LinkedList;
import java.util.Random;


public class MCTSAgent extends Agent
{
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
		
		SimulatedGame sg = new SimulatedGame(gs);
		
		int[] result = sg.startSimulate();
		
		for(int i = 0; i < result.length; i++)
			System.out.println(result[i] + " ");
		
		System.out.println();
		System.out.println("=================================================");
		
		// the following just temp for gen new random movement 
		LinkedList<Movement> ll = Player.genLegalMove(player.getGameShowMove(), player.hand, player.getIsRevo(), player.getIsStartGame());
		int numElement = ll.size();
		int index = rn.nextInt(numElement);
		System.out.println(numElement + " " + index);
		return ll.get(index);
	}
}
