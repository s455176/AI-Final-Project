import java.util.LinkedList;
import java.util.Random;


public class MCTSAgent extends Agent
{
	private static int simulateNum = 20;
	private static int iteration = 10;
	private Random rn;
	
	public MCTSAgent(Player player)
	{
		this.player = player;
		rn = new Random();
	}
	
	public Movement decideMove()
	{
//		GameState gs = new GameState(player.hand, player.getGameShowMove(), player.getIsRevo(), player.getIs11Revo(), player.index,
//				player.getGameHistory(), player.index, player.getIsStartGame(), player.getPassCount(), 
//				player.getNumRemainPlayer(), player.getPlayerNumCards(), player.getIsRevoBeforeRound());		
//		
//		double sum = 0;
//		long startTime = System.currentTimeMillis();
//		for(int i = 0; i < simulateNum; i++)
//		{
//			SimulatedGame sg = new SimulatedGame(gs);
//			int[] result = sg.startSimulate();
//			sum += (double)result[player.index];
//		}
//		long stopTime = System.currentTimeMillis();
//		long elapsedTime = stopTime - startTime;
//		System.out.println("time: " + (double)elapsedTime * 0.001 + ", result: " + sum / simulateNum);
//		
//		
//		// the following just temp for gen new random movement 
//		LinkedList<Movement> ll = Player.genLegalMove(player.getGameShowMove(), player.hand, player.getIsRevo(), player.getIsStartGame());
//		int numElement = ll.size();
//		int index = rn.nextInt(numElement);
//		System.out.println(numElement + " " + index);
//		return ll.get(index);
		
		LinkedList<Movement> ll = Player.genLegalMove(player.getGameShowMove(), player.hand, player.getIsRevo(), player.getIsStartGame()); 
		int numElement = ll.size();
		
		// if only one move left, then just return the move
		if(numElement == 1)
			return ll.get(0);

//		double[] score = new double[numElement];
		
		GameState gs = new GameState(player.hand, player.getGameShowMove(), player.getIsRevo(), player.getIs11Revo(), player.index,
				player.getGameHistory(), player.index, player.getIsStartGame(), player.getPassCount(), 
				player.getNumRemainPlayer(), player.getPlayerNumCards(), player.getIsRevoBeforeRound());
		
		// System.out.println("showMove: " + gs.showMove + " " + player.getGameShowMove());
		
		Node root = new Node(null, gs);
		int count = 0;
		
		while(count < iteration)
		{
			// System.out.println(count);
			runMCTS(root);
			count++;
		}
		
		if(root.children.length == 0)
			SystemFunc.throwException("No move can be chose in MCTSAGent");
		
		
		double best_score = root.children[0].score[player.index] / root.children[0].count;
		int best_child = 0;
		System.out.println(root.children[0].move + " " + best_score);
		for(int i = 1; i < root.children.length; i++)
		{
			double s = root.children[i].score[player.index] / root.children[i].count;
			if(best_score > s)
			{
				best_score = s;
				best_child = i;
			}
			System.out.println(root.children[i].move + " " + s);
		}
		
		return root.children[best_child].move;
	}
	
	public void runMCTS(Node startNode)
	{
		// is leaf node
		if(startNode.children == null)
		{
			// EXPANSION
			int numMove = startNode.expand();
			
			// no legal move
			if(numMove <= 0) return ;
			
			// SIMULATION
			for(int i = 0; i < startNode.children.length; i++)
			{
				startNode.children[i].simulate();
				
				// BACK PROPORGATION
				for(int j = 0; j < Constant.numPlayer; j++)
					startNode.score[j] += startNode.children[i].score[j];
				
				startNode.count += startNode.children[i].count;
			}
		}
		else
		{
			// startNode.cildren is not null but no legal move
			if(startNode.children.length == 0) return ;
			// SELECTION
			int selectionIndex = startNode.gs.next;
			double best_score = startNode.children[0].score[selectionIndex] / startNode.children[0].count;
			int best_child = 0;
			for(int i = 1; i < startNode.children.length; i++)
			{
				double s = startNode.children[i].score[selectionIndex] / startNode.children[i].count;
				if(best_score > s)
				{
					best_score = s;
					best_child = i;
				}
			}
			for(int i = 0; i < Constant.numPlayer; i++)
				startNode.score[i] -= startNode.children[best_child].score[i];
			
			startNode.count -= startNode.children[best_child].count;
			
			// recursive call
			runMCTS(startNode.children[best_child]);
			
			// BACK PROPORGATION
			for(int i = 0; i < Constant.numPlayer; i++)
				startNode.score[i] += startNode.children[best_child].score[i];
			
			startNode.count += startNode.children[best_child].count;
		}
	}
	
	private class Node
	{
		public Movement move;
		public double[] score;
		public double count;
		public Node[] children;
		public GameState gs;
		
		public Node(Movement move, GameState gs)
		{
			this.move = move;
			this.score = new double[Constant.numPlayer];
			this.count = 0.0000000001;
			this.children = null;
			this.gs = new GameState(gs);
		}
		public int expand()
		{
			if(children != null)
				SystemFunc.throwException("multiple times expansion");
			
			// game finish 
			if(gs.remainPlayer <= 1)
			{
				children = new Node[0];
				return 0;
			}
			else
			{
				LinkedList<Movement> ll = gs.genMove(gs.next);
				children = new Node[ll.size()];
				for(int i = 0; i < ll.size(); i++)
				{
					GameState childGs = new GameState(gs);
					childGs.doMove(gs.next, ll.get(i));
					// System.out.println(childGs);
					children[i] = new Node(ll.get(i), childGs);
				}
//				System.out.println(ll.size() + " " + gs.showMove);
//				System.out.println(ll);
				return ll.size();
			}
		}
		public void simulate()
		{
			for(int i = 0; i < simulateNum; i++)
			{
				SimulatedGame sg = new SimulatedGame(gs);
				int[] result = sg.startSimulate();
				for(int j = 0; j < Constant.numPlayer; j++)
					score[j] += (double)result[j];
			}
			count += simulateNum;
		}
	}
}



















