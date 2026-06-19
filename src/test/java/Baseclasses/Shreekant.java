package Baseclasses;

import java.util.Scanner;

public class Shreekant { 
	public static void main(String[] args) {
		
//		Scanner sc=new Scanner(System.in);
//		System.out.println("Enter the size of the Array");
//		int Size=sc.nextInt();
//		int arr[]=new int[Size];
//		
//		for (int i=0; i<arr.length; i++)
//		{
//			arr[i]=sc.nextInt();
//		}
//		for (int i=0; i<arr.length; i++)
//		{
//			System.out.println("Array Element index:- "+i+" is: "+arr[i]);
//		}
//		int max=Integer.MIN_VALUE;
//		int min=Integer.MAX_VALUE;
//		for (int i=0; i<arr.length; i++)
//		{
//			if(arr[i]>=max)
//			{
//				max=arr[i];
//			}
//			else if(arr[i]<=min)
//			{
//				min=arr[i];
//			}
//		}
//		System.out.println("MAX Value:- "+max);
//		System.out.println("MIN Value:- "+min);
		
//		System.out.println("Enter a String");
//		String S=sc.nextLine();
//		String[] All_Split=S.split(" ");
//		for (int i=0; i<All_Split.length; i++)
//		{
//			System.out.println("Length of the this word is:- "+All_Split[i]+" "+ All_Split[i].length());
//		}
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter the length of the Array");
		int Size=sc.nextInt();
		String arr[]=new String[Size];
		sc.nextLine();
		for(int i=0; i<arr.length; i++)
		{
			arr[i]=sc.nextLine();
		}
		String got=arr[0];
		String notgot=arr[0];
		System.out.println("Enter the search elements to find");
		String Search_Element=sc.nextLine();
		
		for(int i=0; i<arr.length; i++)
		{
			if(Search_Element.equals(arr[i]))
			{
				got=arr[i];
				break;
			}
			else
			{
				notgot=arr[i];
			}
		}
		if(Search_Element.equals(got))
		{
			System.out.println("The word :- "+Search_Element+" is present");
		}
		else
		{
			System.out.println("The word :- "+Search_Element+" is not present");
		}
	}
}