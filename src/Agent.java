
public abstract class Agent
{
	protected Player player;
	public int type;
	public double timeLimit = 10;
	public long startTime;
	
	// just used for simulation record testing data
	public simulationRecordData record;
	
	public abstract Movement decideMove();
	
	public boolean isTimesUp()
	{
		if(timeLimit == -1)
			return false;
		else
			return (double)(System.currentTimeMillis() - startTime) * 0.001 > timeLimit;
	}
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