import java.util.LinkedList;

public class AlphaBetaAgent extends MMTSAgent
{
	public AlphaBetaAgent(Player player, int depth)
	{
		super(player, depth);
		this.type = Constant.AlphaBetaAgent;
	}
	
	public double runMaxNode(Node n, int depth, double alpha, double beta)
	{
		LinkedList<Movement> ll = n.gs.genMove(n.gs.next);
		int numMove = ll.size();
		
		// System.out.println(numMove + " " + depth);
		// termination condition 
		if(depth == 0 || numMove == 0 || n.gs.remainPlayer <= 1)
			return evaluation(n);

		double m = alpha;
		
		for(int i = 0; i < ll.size(); i++)
		{
			GameState childGs = new GameState(n.gs);
			childGs.doMove(n.gs.next, ll.get(i));
			Node childNode = new Node(ll.get(i), childGs);
			
			double t;
			if(childGs.next != player.index)
				t = runMinNode(childNode, depth - 1, m, beta);
			else
				t = runMaxNode(childNode, depth - 1, m, beta);
			
			if(t > m)
				m = t;
			
			if(m >= beta)
				return m;
		}
			
		return m;
	}
	
	public double runMinNode(Node n, int depth, double alpha, double beta)
	{
		LinkedList<Movement> ll = n.gs.genMove(n.gs.next);
		int numMove = ll.size();
		
		// System.out.println(numMove + " " + depth);
		// termination condition 
		if(depth == 0 || numMove == 0 || n.gs.remainPlayer <= 1)
			return evaluation(n);
		
		double m = beta;
		
		for(int i = 0; i < ll.size(); i++)
		{
			GameState childGs = new GameState(n.gs);
			childGs.doMove(n.gs.next, ll.get(i));
			Node childNode = new Node(ll.get(i), childGs);
			
			double t;
			if(childGs.next != player.index)
				t = runMinNode(childNode, depth - 1, alpha, m);
			else
				t = runMaxNode(childNode, depth - 1, alpha, m);
			
			if(t < m)
				m = t;
			
			if(m <= alpha)
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
		
		double alpha = -Double.MAX_VALUE;
		double beta = Double.MAX_VALUE;
		
		GameState childGs = new GameState(gs);
		childGs.doMove(gs.next, ll.get(0));
		Node childNode = new Node(ll.get(0), childGs);
		
		if(childGs.next != player.index)
			alpha = runMinNode(childNode, NUM_DEPTH, alpha, beta);
		else
			alpha = runMaxNode(childNode, NUM_DEPTH, alpha, beta);

		int bestIndex = 0;
		for(int i = 1; i < numElement; i++)
		{
			childGs = new GameState(gs);
			childGs.doMove(gs.next, ll.get(i));
			childNode = new Node(ll.get(i), childGs);
			
			double score;
			if(childGs.next != player.index)
				score = runMinNode(childNode, NUM_DEPTH, alpha, beta);
			else
				score = runMaxNode(childNode, NUM_DEPTH, alpha, beta);
			
			if(score > alpha)
			{
				alpha = score;
				bestIndex = i;
			}
		}
		
		return ll.get(bestIndex);
	}
}








