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
		int iteration = 10;
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
			Simulation simulation = new Simulation();
			simulation.game.runInSimulation();
			for(int i = 0; i < Constant.numPlayer; i++)
			{
				playerStatStatic[i][iter] = simulation.game.playerStat[i];
				// total
				total[i].update(simulation.game.total[i].getTime(), simulation.game.total[i].getCount());
				// thinking
				thinking[i].update(simulation.game.thinking[i].getTime(), simulation.game.thinking[i].getCount());
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
			System.out.println("player " + i);
			System.out.println("total: ");
			System.out.println(total[i].getTime() + " \t" + total[i].getCount() + " \t" + total[i].mean());
			System.out.println("thinking: ");
			System.out.println(thinking[i].getTime() + " \t" + thinking[i].getCount() + " \t" + thinking[i].mean());
		}
	}
}












