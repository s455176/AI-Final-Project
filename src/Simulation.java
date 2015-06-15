import java.io.IOException;

import javax.swing.*;

import java.io.*;
import java.util.Arrays;

public class Simulation extends JFrame
{
	public Game game;
	
	public Simulation() throws IOException
	{
		game = new Game();
		add(game);
		setSize(Constant.width, Constant.height);
		setTitle("AI_FINAL");
		setResizable(false);
		setVisible(true);
	}
	
	public static void main(String[] args) throws IOException
	{
		int iteration = 30;
		int[][] playerStatStatic = new int[Constant.numPlayer][iteration];
		long[] playerTimeStatic = new long[Constant.numPlayer];
		int[] playerMoveCountStatic = new int[Constant.numPlayer];
		
		for(int i = 0; i < Constant.numPlayer; i++)
			for(int j = 0; j < iteration; j++)
				playerStatStatic[i][j] = 0;
		
		Arrays.fill(playerTimeStatic, 0);
		Arrays.fill(playerMoveCountStatic, 0);
		
		for(int iter = 0; iter < iteration; iter++)
		{
			System.out.println("===========================Iter: " + iter + "===========================");
			Simulation simulation = new Simulation();
			simulation.game.runInSimulation();
			for(int i = 0; i < Constant.numPlayer; i++)
			{
				playerStatStatic[i][iter] = simulation.game.playerStat[i];
				playerTimeStatic[i] += simulation.game.totalTime[i];
				playerMoveCountStatic[i] += simulation.game.moveCount[i];
				System.out.println("player " + i + ": " + simulation.game.playerStat[i] + 
						", time per move: " + 0.001 * (double)simulation.game.totalTime[i] / (double)simulation.game.moveCount[i]);
			}
			simulation.setVisible(false);
			simulation.dispose();
			simulation = null;
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
				System.out.print("player " + j + ": " + playerStatStatic[j][i] + " ");
				result[j][playerStatStatic[j][i] - 1]++;
			}
			System.out.println();
		}
		System.out.println("-----result-----");
		System.out.println("ID\t 1st\t 2nd\t 3rd\t 4th\t");
		for(int i = 0; i < 4; i++)
		{
			System.out.print("player " + i + ":\t");
			for(int j = 0; j < 4; j++)
			{
				System.out.print(result[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println("time per move:");
		for(int i = 0; i < 4; i++)
		{
			System.out.println((double)playerTimeStatic[i] + " " + (double)playerMoveCountStatic[i] + " " + 
					0.001 * (double)playerTimeStatic[i] / (double)playerMoveCountStatic[i]);
		}
	}
}












