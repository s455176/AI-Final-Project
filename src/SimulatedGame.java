
public class SimulatedGame
{
	private GameState gs;
	
	public SimulatedGame(GameState gamestate)
	{
		gs = new GameState(gamestate);
	}
	
	public Movement genOppoMove()
	{
		boolean[] history = gs.history;
		
		return new Movement();
	}
	
	public void startSimulate()
	{}
}
