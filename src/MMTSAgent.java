import java.util.LinkedList;

public class MMTSAgent extends Agent
{
	public static int NUM_DEPTH = 5;
	
	public MMTSAgent(Player player)
	{
		this.player = player;
	}
	
	@Override
	public Movement decideMove()
	{
		LinkedList<Movement> ll = Player.genLegalMove(player.getGameShowMove(), player.hand, player.getIsRevo(), player.getIsStartGame()); 
		int numElement = ll.size();
		
		// if only one move left, then just return the move
		if(numElement == 1)
			return ll.get(0);
		
		GameState gs = new GameState(player.hand, player.getGameShowMove(), player.getIsRevo(), player.getIs11Revo(), player.index,
				player.getGameHistory(), player.index, player.getIsStartGame(), player.getPassCount(), 
				player.getNumRemainPlayer(), player.getPlayerNumCards(), player.getIsRevoBeforeRound());
		
		GameState childGs = new GameState(gs);
		childGs.doMove(gs.next, ll.get(0));
		Node childNode = new Node(ll.get(0), childGs);
		double maxScore = runMinNode(childNode, NUM_DEPTH);
		int maxIndex = 0;
		for(int i = 1; i < numElement; i++)
		{
			childGs = new GameState(gs);
			childGs.doMove(gs.next, ll.get(i));
			childNode = new Node(ll.get(i), childGs);
			double score = runMinNode(childNode, NUM_DEPTH);
			if(score > maxScore)
			{
				maxScore = score;
				maxIndex = i;
			}
		}
		
		return ll.get(maxIndex);
	}
	
	public double evaluation(Node n)
	{
		return 1.0;
	}
	
	public double runMaxNode(Node n, int depth)
	{
		LinkedList<Movement> ll = n.gs.genMove(n.gs.next);
		int numMove = ll.size();
		
		// termination condition 
		if(depth == 0 || numMove == 0 || n.gs.remainPlayer <= 1)
			return evaluation(n);

		double m = -Double.MAX_VALUE;
		
		for(int i = 0; i < ll.size(); i++)
		{
			GameState childGs = new GameState(n.gs);
			childGs.doMove(n.gs.next, ll.get(i));
			Node childNode = new Node(ll.get(i), childGs);
			
			double t;
			if(childGs.next != player.index)
				t = runMinNode(childNode, depth - 1);
			else
				t = runMaxNode(childNode, depth - 1);
			
			if(t > m)
				m = t;
		}
			
		return m;
	}
	
	public double runMinNode(Node n, int depth)
	{
		LinkedList<Movement> ll = n.gs.genMove(n.gs.next);
		int numMove = ll.size();
		
		// termination condition 
		if(depth == 0 || numMove == 0 || n.gs.remainPlayer <= 1)
			return evaluation(n);
		
		double m = Double.MAX_VALUE;
		
		for(int i = 0; i < ll.size(); i++)
		{
			GameState childGs = new GameState(n.gs);
			childGs.doMove(n.gs.next, ll.get(i));
			Node childNode = new Node(ll.get(i), childGs);
			
			double t;
			if(childGs.next != player.index)
				t = runMinNode(childNode, depth - 1);
			else
				t = runMaxNode(childNode, depth - 1);
			
			if(t < m)
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

