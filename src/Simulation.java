import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.*;

import java.io.*;
import java.util.Arrays;

public class Simulation 
{
	public Game game;
	public JFrame frame;
	
	public PrintWriter gameLog;
	public int iteration;
	public playerAttr p0, p1, p2, p3;
	
	public Simulation(String filename, int iter,  
			playerAttr p0, playerAttr p1, playerAttr p2, playerAttr p3) throws IOException
	{
		// game = new Game(p0, p1, p2, p3);
		this.p0 = p0; this.p1 = p1; this.p2 = p2; this.p3 = p3;
		gameLog = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
		this.iteration = iter;
	}
	
	public void setGame() throws IOException
	{
		frame = new JFrame();
		game = new Game(p0, p1, p2, p3);
		frame.add(game);
		frame.setSize(Constant.width, Constant.height);
		frame.setTitle("AI_FINAL");
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	public void endGame()
	{
		frame.setVisible(false);
		frame.remove(game);
		frame.dispose();
		game = null;
		frame = null;
	}
	
	public void runSimlation() throws IOException
	{
		gameLog.println("player0: " + p0);
		gameLog.println("player1: " + p1);
		gameLog.println("player2: " + p2);
		gameLog.println("player3: " + p3);
		
		// int iteration = 10;
		int[][] playerStatStatic = new int[Constant.numPlayer][iteration];
		
		timeMeasure[] total = new timeMeasure[Constant.numPlayer];
		timeMeasure[] thinking = new timeMeasure[Constant.numPlayer];
		
		for(int i = 0; i < Constant.numPlayer; i++)
			for(int j = 0; j < iteration; j++)
				playerStatStatic[i][j] = 0;
		
		for(int i = 0; i < Constant.numPlayer; i++)
		{
			total[i] = new timeMeasure();
			thinking[i] = new timeMeasure();
		}
		
		for(int iter = 0; iter < iteration; iter++)
		{
			System.out.println("===========================Iter: " + iter + "===========================");
			this.setGame();
			this.game.runInSimulation();
			for(int i = 0; i < Constant.numPlayer; i++)
			{
				playerStatStatic[i][iter] = this.game.playerStat[i];
				// total
				total[i].update(this.game.total[i].getTime(), this.game.total[i].getCount());
				// thinking
				thinking[i].update(this.game.thinking[i].getTime(), this.game.thinking[i].getCount());
			}
			this.endGame();
		}
		
		// result
		int[][] result = new int[Constant.numPlayer][4]; // player, status
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				result[i][j] = 0;
		
		for(int i = 0; i < iteration; i++)
		{
			for(int j = 0;j < Constant.numPlayer; j++)
			{
				gameLog.print("player " + j + ": " + playerStatStatic[j][i] + " ");
				result[j][playerStatStatic[j][i] - 1]++;
			}
			gameLog.println();
		}
		gameLog.println("-----result-----");
		gameLog.println("ID\t 1st\t 2nd\t 3rd\t 4th\t");
		for(int i = 0; i < 4; i++)
		{
			gameLog.print("player " + i + ":\t");
			for(int j = 0; j < 4; j++)
			{
				gameLog.print(result[i][j] + "\t");
			}
			gameLog.println();
		}
		gameLog.println("time per move:");
		for(int i = 0; i < 4; i++)
		{
			gameLog.println("player " + i);
			gameLog.println("total: ");
			gameLog.println(total[i].getTime() + " \t" + total[i].getCount() + " \t" + total[i].mean());
			gameLog.println("thinking: ");
			gameLog.println(thinking[i].getTime() + " \t" + thinking[i].getCount() + " \t" + thinking[i].mean());
		}
		gameLog.println("-------------------------------------------------");
		gameLog.flush();
	}
	
	public static void main(String[] args) throws IOException
	{
		Simulation s1 = new Simulation("AlphaBetaWithZero_result.txt", 5, 
											new playerAttr(Constant.AlphaBetaWithZeroAgent, new int[]{5}), 
											new playerAttr(Constant.RandomAgent, new int[]{}), 
											new playerAttr(Constant.RandomAgent, new int[]{}), 
											new playerAttr(Constant.RandomAgent, new int[]{}));
		s1.runSimlation();
//		Simulation s2 = new Simulation("MMTS_result.txt", 5, new playerAttr(Constant.MMTSAgent, new int[]{5}), 
//				  									  		 new playerAttr(Constant.RandomAgent, new int[]{}), 
//				  									  		 new playerAttr(Constant.RandomAgent, new int[]{}), 
//				  									  		 new playerAttr(Constant.RandomAgent, new int[]{}));
//		s2.runSimlation();
	}
}












