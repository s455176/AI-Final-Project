import java.util.ArrayList;

public class MiniMaxNode
{
	// attribute
	private PlayerGameState playerGameState;
	private int value;
	private Movement move;
	private ArrayList<PlayerGameState> children;
	
	// method
	public MiniMaxNode(PlayerGameState playerGameState)
	{
		this.playerGameState = playerGameState;
		value = 0;
		children = new ArrayList<playerGameState>();
	}
	
	public PlayerGameState getPlayerGameState()
	{
		return playerGameState;
	}
	public int getValue()
	{
		return value;
	}
	public Movement getMove()
	{
		return move;
	}
	public ArrayList<PlayerGameState> getChildren()
	{
		return children;
	}
	
	public void addChildren(PlayerGameState playerGameState)
	{
		children.add(playerGameState);
	}
	public void setValue(int value)
	{
		this.value = value;
	}
	public void setMove(Movement move)
	{
		this.move = move;
	}
}