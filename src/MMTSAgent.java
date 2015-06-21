import java.util.Arrays;
import java.util.LinkedList;

public class MMTSAgent extends Agent
{
	public int NUM_DEPTH = 3;
	public MoveScore[] scoreList;
	
	public MMTSAgent(Player player, int depth, double timeLimit)
	{
		this.player = player;
		this.NUM_DEPTH = depth;
		this.type = Constant.MMTSAgent;
		this.scoreList = null;
		this.startTime = -1;
		this.timeLimit = timeLimit;
	}
	
	@Override
	public Movement decideMove()
	{
		LinkedList<Movement> ll = Player.genLegalMove(player.getGameShowMove(), player.hand, player.getIsRevo(), player.getIsStartGame()); 
		int numElement = ll.size();
		
		GameState gs = new GameState(player.hand, player.getGameShowMove(), player.getIsRevo(), player.getIs11Revo(), player.index,
				player.getGameHistory(), player.index, player.getIsStartGame(), player.getPassCount(), 
				player.getNumRemainPlayer(), player.getPlayerNumCards(), player.getIsRevoBeforeRound());
		
		// set the record to record more data
		record = new simulationRecordData(numElement, gs.isJokerShown());
		
		// if only one move left, then just return the move
		if(numElement == 1)
			return ll.get(0);		
		
		// set the startTime in order to use the isTimesUp function
		startTime = System.currentTimeMillis();
		scoreList = new MoveScore[numElement];
		for(int i = 0; i < scoreList.length; i++)
			scoreList[i] = new MoveScore();
		
		int curDepth = 1;
		
		while(!isTimesUp() && curDepth <= NUM_DEPTH)
		{
			Node n = new Node(null, gs);
			runMaxNode(n, curDepth, curDepth);
			curDepth += 1;
		}
		
		startTime = -1;
		
		// choose the move with biggest score(need to modify to consider occur depth)
		int maxIndex = 0;
		double maxScore = scoreList[0].score;
		double minOccur = scoreList[0].occur;
		System.out.println(ll.get(0) + " " + scoreList[0].score + " " + scoreList[0].occur + " " + scoreList[0].depth);
		for(int i = 1; i < numElement; i++)
		{
			if((scoreList[i].score > maxScore) || (scoreList[i].score == maxScore && scoreList[i].occur < minOccur))
			{
				maxIndex = i;
				maxScore = scoreList[i].score;
				minOccur = scoreList[i].occur;
			}
			System.out.println(ll.get(i) + " " + scoreList[i].score + " " + scoreList[i].occur + " " + scoreList[i].depth);
		}
		
		return ll.get(maxIndex);
	}
	
	public double evaluation(Node n)
	{
		GameState copyGs = new GameState(n.gs);
		LinkedList<Movement> ll = Player.genLegalMove(null, copyGs.playerHand, copyGs.isRevo, copyGs.isStartGame);
		boolean[] history = copyGs.history;
		double[][] defeatVal;
		int[] valueCount = new int[13];
		
		Arrays.fill(valueCount, 0);
		
		for(int i = 0; i < copyGs.playerHand.length; i++)
			if(copyGs.playerHand[i] != null)
				history[copyGs.playerHand[i].getIndex()] = true;
		
		for(int i = 1; i < Constant.MAX_NUM_CARD; i++)
			if(!history[i])
			{
				int suit = (i - 1) / 13;
				int rank = i - suit * 13;
				int value = (rank < 3) ? rank + 13 : rank; // 3 ~ 15
				valueCount[value - 3]++;
			}
		
		defeatVal = new double[5][13];
		defeatVal[0][12] = 0; defeatVal[1][12] = 0; defeatVal[2][12] = 0; defeatVal[3][12] = 0; defeatVal[4][12] = 0;
		for(int i = 1; i < 5; i++)
			for(int j = 11; j >= 0; j--)
			{
				double addVal = (valueCount[j] >= i)? 1: 0;
				defeatVal[i][j] = defeatVal[i][j + 1] + addVal;
			}
		
		double total = 0.0;
		for(int i = 0; i < ll.size(); i++)
		{
			Movement m = ll.get(i);
			int type = m.type;
			
			// skip movement that is a single joker
			Card[] c = m.getCards();
			if(c.length == 1 && c[0].isJoker())
				continue;
			
			switch(type)
			{
			case Constant.SINGLE:
			case Constant.PAIR:
			case Constant.TRIPLE:
			case Constant.FOUR:
				if(!m.has8Cut)
					total += defeatVal[type][m.biggestValue - 3];
				break;
			case Constant.STRAIGHT3:
			case Constant.STRAIGHT4:
			case Constant.STRAIGHT5:
				if(!m.has8Cut)
					total += defeatVal[4][m.biggestValue - 3] * (type - 2);
				break;
			case Constant.PASS:
				break;
			default:
				SystemFunc.throwException("error occurs in evaluation function, no such move type");
			}
		}
		
		return -total;
	}
	
	public searchReturn runMaxNode(Node n, int depth, int target)
	{
//		System.out.println("maxNode" + " " + depth);
		LinkedList<Movement> ll = n.gs.genMove(n.gs.next);
		int numMove = ll.size();
		
		// termination condition 
		if(depth == 0 || numMove == 0 || n.gs.remainPlayer <= 1 || isTimesUp())
		{
			return new searchReturn(evaluation(n), target - depth);
		}

		searchReturn m = new searchReturn();
		
		m.score = -Double.MAX_VALUE;
		m.depth = -1;
		
		for(int i = 0; i < ll.size(); i++)
		{
			GameState childGs = new GameState(n.gs);
			childGs.doMove(n.gs.next, ll.get(i));
			Node childNode = new Node(ll.get(i), childGs);
			
			searchReturn t;
			if(childGs.next != player.index)
				t = runMinNode(childNode, depth - 1, target);
			else
				t = runMaxNode(childNode, depth - 1, target);
			
			if(target == depth)
				scoreList[i].update(t);
			
			if(t.score > m.score)
				m = t;
		}
			
		return m;
	}
	
	public searchReturn runMinNode(Node n, int depth, int target)
	{
//		System.out.println("minNode" + " " + depth);
		LinkedList<Movement> ll = n.gs.genMove(n.gs.next);
		int numMove = ll.size();
		
		// termination condition 
		if(depth == 0 || numMove == 0 || n.gs.remainPlayer <= 1 || isTimesUp())
		{
			return new searchReturn(evaluation(n), target - depth);
		}

		searchReturn m = new searchReturn();
		
		m.score = -Double.MAX_VALUE;
		m.depth = -1;
		
		for(int i = 0; i < ll.size(); i++)
		{
			GameState childGs = new GameState(n.gs);
			childGs.doMove(n.gs.next, ll.get(i));
			Node childNode = new Node(ll.get(i), childGs);
			
			searchReturn t;
			if(childGs.next != player.index)
				t = runMinNode(childNode, depth - 1, target);
			else
				t = runMaxNode(childNode, depth - 1, target);
			
			if(t.score < m.score)
				m = t;
		}
			
		return m;
	}

	protected class Node
	{
		public Movement move;
		public GameState gs;
		
		public Node(Movement move, GameState gs)
		{
			this.move = move;
			this.gs = new GameState(gs);
			
//			if(gs.remainPlayer <= 1)
//				children = new Node[0];
//			else
//			{
//				LinkedList<Movement> ll = this.gs.genMove(this.gs.next);
//				this.children = new Node[ll.size()];
//				for(int i = 0; i < ll.size(); i++)
//				{
//					GameState childGs = new GameState(this.gs);
//					childGs.doMove(this.gs.next, ll.get(i));
//					children[i] = new Node(ll.get(i), childGs);
//				}
//			}
		}
	}
}
class MoveScore
{
	public double score;
	public int depth;
	public int occur;
	
	public MoveScore()
	{
		this.score = -Double.MAX_VALUE;
		this.depth = -1;
		this.occur = -1;
	}
	
	public MoveScore(double score, int depth, int occur)
	{
		this.score = score;
		this.depth = depth;
		this.occur = occur;
	}
	
	// need modify to record the occur depth
	public void update(searchReturn sr)
	{
		boolean canUpdate = (sr.depth > this.depth) || (sr.depth == this.depth && sr.score > this.score);
		boolean updateOccur = canUpdate && sr.score != this.score;
		
		if(canUpdate)
		{
			this.score = sr.score;
			this.depth = sr.depth;
		}
		
		if(updateOccur)
			this.occur = sr.depth;
	}
}

class searchReturn
{
	public double score;
	public int depth;
	
	public searchReturn()
	{
		this.score = -Double.MAX_VALUE;
		this.depth = -1;
	}
	
	public searchReturn(double score, int depth)
	{
		this.score = score;
		this.depth = depth;
	}
}






