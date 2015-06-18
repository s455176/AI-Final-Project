
public abstract class Agent
{
	protected Player player;
	public int type;
	
	// just used for simulation record testing data
	public simulationRecordData record;
	
	public abstract Movement decideMove();
}

class simulationRecordData
{
	public int curNumLegalMove;
	public boolean isjokerShown;
	public simulationRecordData(int num, boolean joker)
	{
		this.curNumLegalMove = num;
		this.isjokerShown = joker;
	}
}