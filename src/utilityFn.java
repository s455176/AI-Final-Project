import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
public class utilityFn extends Constant
{
	private int bedefeatvalue =new int[13+1];
	private int cardpoint =13;
	private int cardoccur3_13 =4;
	private int Score;
	private double[] Score= new int[13+1];
	private double[] P1= new int[13+1];
	private double[] P2= new int[13+1];
	private double[] P3= new int[13+1];
	private double[] P4= new int[13+1];
	private double[] P5= new int[13+1];
	private int[] CardsHistory = new int[13+1];  // cards in deck//
	private int[] cardoccur3_13 =new int[4+1];
	private int[] cardoccur1_2 =new int[4+1];
	private int[] history = new int[];  // cards in deck//
	private int[] hand = new int[];  // cards in hand//
	private int[] CardsHand = new int[13+1];  // cards in hand//
	cardoccur1_2[0]=0;
	cardoccur3_13[0]=0;
	CardHistory [0]=0;
	Cardhand [0]=0;
	bedefeatvalue[0]=0;
	
	for (int i = 1; i < bedefeatvalue.lehgth; i++)
	{
		
	
		if(i==8)
		{
		bedefeatvalue[i]=0;
		}
		else if (i==1)
		{
		bedefeatvalue[i]=1;		
		}
		else if (i==2)
		{
		bedefeatvalue[i]=0;		
		}
		else if (i==11)
		{
		bedefeatvalue[i]=8;		
		}
		bedefeatvalue[i]=15-i;
	}
	//計算所有已出牌組合
	for(i=0;i<history.lenth;i++)
	{
	if (history[i]>13)
		history[i]=history [i]%13;		
	}
	// calculate times of a particular number
	public static int countSpecifyNumberTimes(int specifyNumber, int[] array) {  
        int specifyCountTimes = 0;  
        for (int i = 0; i < array.length; i++) 
		{  
            if (array[i] == specifyNumber) 
			{  
                specifyCountTimes++;  
            }  
        }  
        return specifyCountTimes;  
    }  
	// calculate times of each number
	public static void countEveryNumberTimes1(int[] array) 
	{  
        for (int i = 1; i < cardpoint+1; i++) 
		{  
            int specifyCountTimes = countSpecifyNumberTimes(i, history);  
            CardHistory [i]= specifyCountTimes;
        }  
    }  
	
	public static void countEveryNumberTimes2(int[] array) 
	{  
        for (int i = 1; i < cardoccur3_13+1; i++) 
		{  
            int specifyCountTimes = countSpecifyNumberTimes(i, card);  
            cardoccur3_13 [i]= specifyCountTimes;
        }  
    }  
	//已出過的點數較大鐵支組合=cardoccur3_13 [4]
	//已出過的點數較大3條組合=cardoccur3_13 [3]
	//已出過的點數較大對子組合=cardoccur3_13 [2]
	//已出過的點數較大單張組合=cardoccur3_13 [1]
	
	//計算手中牌組合
	

	public static void countEveryNumberTimes(int[] array) 
	{  
        for (int i = 1; i < cardpoint+1; i++) 
		{  
            int specifyCountTimes = countSpecifyNumberTimes(i, hand);  
            CardHand [i]= specifyCountTimes
        }  
    }  
	for (int i = 3; i < CardHistory.length; i++) 
	{
		switch (CardHand[i])
		{
		case=1:
		       for(int j=4;j<5;j++)
			   {
				int specifyCountTimes = countSpecifyNumberTimes(j,CardHistory[i+1:CardHistory.length-1])+countSpecifyNumberTimes(j,CardHistory[1:2]);  
				cardoccur3_13 [1]=0;
				cardoccur3_13 [1]= specifyCountTimes+cardoccur3_13 [1];
			   }
			   P1[i]=(bedefeatvalue[i]-cardoccur3_13 [1])/bedefeatvalue[i];
				break;	
		case=2:
				for(int j=3;j<5;j++)
				{
				int specifyCountTimes = countSpecifyNumberTimes(j,CardHistory[i+1:CardHistory.length-1])+countSpecifyNumberTimes(j,CardHistory[1:2]);  
				cardoccur3_13 [2]=0;
				cardoccur3_13 [2]= specifyCountTimes+cardoccur3_13 [2];
				}
				P2[i]=(bedefeatvalue[i]-cardoccur3_13 [2])/bedefeatvalue[i];
				break;
		case=3:
				for(int j=2;j<5;j++)
				{
				int specifyCountTimes = countSpecifyNumberTimes(j,CardHistory[i+1:CardHistory.length-1])+countSpecifyNumberTimes(j,CardHistory[1:2]); 
				cardoccur3_13 [3]=0;
				cardoccur3_13 [3]= specifyCountTimes+cardoccur3_13 [3];
				}
				P3[i]=(bedefeatvalue[i]-cardoccur3_13 [3])/bedefeatvalue[i];
				break;
		case=4:
			for(int j=1;j<5;j++)
				{
				int specifyCountTimes = countSpecifyNumberTimes(j,CardHistory[i+1:CardHistory.length-1])+countSpecifyNumberTimes(j,CardHistory[1:2]); 
				cardoccur3_13 [4]= specifyCountTimes;
				}
				P4[i]=(bedefeatvalue[i]-cardoccur3_13 [4])/bedefeatvalue[i];
			break;
		}	
	}
	
	for (int i = 3; i < CardHistory.length-2; i++) 
	{
	if(CardHand[i]!=0 && CardHand[i+1]!=0 && CardHand[i+2-]!=0 )	
	P5[i]=(bedefeatvalue[i]-2-(cardoccur3_13 [1]+2+2)/bedefeatvalue[i];
	}

	for (int i = 1; i < 2; i++) 
	{
		switch (CardHand[i])
		{
		case=1:
		       for(int j=4;j<5;j++)
			   {
				int specifyCountTimes = countSpecifyNumberTimes(j,CardHistory[2])+countSpecifyNumberTimes(j,CardHistory[2]);  
				cardoccur1_2 [1]=0;
				cardoccur1_2 [1]= specifyCountTimes+cardoccur1_2 [1];
			   }
			   P1[1]=(bedefeatvalue[i]-cardoccur1_2 [1])/bedefeatvalue[i];
				break;	
		case=2:
				for(int j=3;j<5;j++)
				{
				int specifyCountTimes = countSpecifyNumberTimes(j,CardHistory[2])+countSpecifyNumberTimes(j,CardHistory[2]);   
				cardoccur1_2 [2]=0;
				cardoccur1_2 [2]= specifyCountTimes+cardoccur1_2 [2];
				}
				P2[1]=(bedefeatvalue[i]-cardoccur1_2 [2])/bedefeatvalue[i];
				break;
		case=3:
				for(int j=2;j<5;j++)
				{
				int specifyCountTimes = countSpecifyNumberTimes(j,CardHistory[2])+countSpecifyNumberTimes(j,CardHistory[2]); 
				cardoccur1_2 [3]=0;
				cardoccur1_2 [3]= specifyCountTimes+cardoccur1_2 [3];
				}
				P3[1]=(bedefeatvalue[i]-cardoccur1_2 [3])/bedefeatvalue[i];
				break;
		case=4:
			for(int j=1;j<5;j++)
				{
				int specifyCountTimes = countSpecifyNumberTimes(j,CardHistory[2])+countSpecifyNumberTimes(j,CardHistory[2]); 
				cardoccur1_2 [4]= specifyCountTimes;
				}
				P4[1]=(bedefeatvalue[i]-cardoccur1_2 [4])/bedefeatvalue[i];
			break;
		}
						
	}
	P1[2]=P2[2]=P3[2]=P4[2]=0
	P1[8]=P2[8]=P3[8]=P4[8]=0
	//被擊敗的狀態
	
	for(int i=; i < Score.length; i++)
	{
	Score[i]=(P1[i]*bedefeatvalue[i]+P2[i]*bedefeatvalue[i]+P3[i]*bedefeatvalue[i]+P4[i]*bedefeatvalue[i])
	Score=Score[i]++
	}
	
	
	
	//under normal state
	
	
		P1=(被擊敗值-已出過的點數較大鐵支組合)/被擊敗值
		P2=(被擊敗值-已出過的點數較大3條組合-已出過的點數較大鐵支組合)/被擊敗值
		P3=(被擊敗值-已出過的點數較大對子組合-已出過的點數較大鐵支組合)/被擊敗值
		P4=(被擊敗值-已出過的點數較大單張組合-已出過的點數較大對子組合-已出過的點數較大鐵支組合)/被擊敗值
		P5=(最小點數被擊敗值-2-(已出過的點數中點數較大鐵支組合+2+2))/被擊敗值
	
	
}
