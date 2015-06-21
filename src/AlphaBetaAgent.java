import java.util.LinkedList;

public class AlphaBetaAgent extends MMTSAgent
{
	public AlphaBetaAgent(Player player, int depth, double timeLimit)
	{
		super(player, depth, timeLimit);
		this.type = Constant.AlphaBetaAgent;
	}
	
	public searchReturn runMaxNode(Node n, int depth, searchReturn alpha, searchReturn beta, int target)
	{
		LinkedList<Movement> ll = n.gs.genMove(n.gs.next);
		int numMove = ll.size();
		
		// System.out.println(numMove + " " + depth);
		// termination condition 
		if(depth == 0 || numMove == 0 || n.gs.remainPlayer <= 1 || isTimesUp())
		{
			return new searchReturn(evaluation(n), target - depth);
		}

		searchReturn m = alpha;
		
		for(int i = 0; i < ll.size(); i++)
		{
			GameState childGs = new GameState(n.gs);
			childGs.doMove(n.gs.next, ll.get(i));
			Node childNode = new Node(ll.get(i), childGs);
			
			searchReturn t;
			if(childGs.next != player.index)
				t = runMinNode(childNode, depth - 1, m, beta, target);
			else
				t = runMaxNode(childNode, depth - 1, m, beta, target);
			
			if(target == depth)
				scoreList[i].update(t);
			
			if(t.score > m.score)
				m = t;
			
			if(m.score >= beta.score)
				return m;
		}
			
		return m;
	}
	
	public searchReturn runMinNode(Node n, int depth, searchReturn alpha, searchReturn beta, int target)
	{
		LinkedList<Movement> ll = n.gs.genMove(n.gs.next);
		int numMove = ll.size();
		
		// System.out.println(numMove + " " + depth);
		// termination condition 
		if(depth == 0 || numMove == 0 || n.gs.remainPlayer <= 1 || isTimesUp())
		{
			return new searchReturn(evaluation(n), target - depth);
		}
		
		searchReturn m = beta;
		
		for(int i = 0; i < ll.size(); i++)
		{
			GameState childGs = new GameState(n.gs);
			childGs.doMove(n.gs.next, ll.get(i));
			Node childNode = new Node(ll.get(i), childGs);
			
			searchReturn t;
			if(childGs.next != player.index)
				t = runMinNode(childNode, depth - 1, alpha, m, target);
			else
				t = runMaxNode(childNode, depth - 1, alpha, m, target);
			
			if(t.score < m.score)
				m = t;
			
			if(m.score <= alpha.score)
				return m;
		}
			
		return m;
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
		
		searchReturn alpha = new searchReturn();
		searchReturn beta = new searchReturn();
		
		alpha.score = -Double.MAX_VALUE;
		alpha.depth = -1;
		beta.score = Double.MAX_VALUE;
		beta.depth = -1;
		
		while(!isTimesUp() && curDepth <= NUM_DEPTH)
		{
			Node n = new Node(null, gs);
			runMaxNode(n, curDepth, alpha, beta, curDepth);
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
}








