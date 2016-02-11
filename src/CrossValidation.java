import java.io.BufferedReader;
import java.io.FileReader;
import java.util.PriorityQueue;

import javax.lang.model.element.Element;

public class CrossValidation {

	int kFold;
	int numberOfExamples;
	int numberOfPermutations;
	String[] permutations;
	TrainingDataStructure[] examples;
	int rows, columns;
	public void generateInput(String filename)
	{
		BufferedReader br = null;
		try
		{
			String theCurrentLine = null;
			String msgRead[];
			br = new BufferedReader(new FileReader(filename));
			if((theCurrentLine = br.readLine())!= null)
			{
				msgRead = theCurrentLine.split(" ");
				rows = Integer.parseInt(msgRead[0]);
				columns = Integer.parseInt(msgRead[1]);
			}
			int k =0;
			for(int i =0;(theCurrentLine = br.readLine())!= null;i++)
			{
				String msg[] = theCurrentLine.split(" ");
				for(int j=0;j<msg.length;j++)
				{
					if(!msg[j].equals("."))
					{
						if(msg[j].equals("+"))
							examples[k] = new TrainingDataStructure(j, i,true);
						else if(msg[j].equals("-"))
							examples[k] = new TrainingDataStructure(j, i, false);
						k++;
					}

				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void readFirstFile(String filename)
	{
		BufferedReader br = null;
		try
		{
			String theCurrentLine = null;
			String msgRead[];
			br = new BufferedReader(new FileReader(filename));
			if((theCurrentLine = br.readLine())!= null)
			{
				msgRead = theCurrentLine.split(" ");
				kFold = Integer.parseInt(msgRead[0]);
				numberOfExamples = Integer.parseInt(msgRead[1]);
				numberOfPermutations = Integer.parseInt(msgRead[2]);
				permutations = new String[numberOfPermutations];
				examples = new TrainingDataStructure[numberOfExamples];
			}
			for(int i =0;i<numberOfPermutations &&(theCurrentLine = br.readLine())!= null;i++)
				permutations[i] = theCurrentLine.replaceAll(" ","");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public double calculateDistance(int x1, int x2)
	{
		return Math.sqrt((examples[x1].x - examples[x2].x)*(examples[x1].x - examples[x2].x)
				+ (examples[x1].y - examples[x2].y)*(examples[x1].y - examples[x2].y));
	}

	public boolean getClassifier(PriorityQueue<Dist> heap, int noOfNeighbours)
	{
		Dist prev ;
		int pos =0, neg =0;
		prev = heap.peek();

		while((heap!=null && heap.size() > 0) && (noOfNeighbours >0 || (heap.peek() != null &&  prev.dist == heap.peek().dist)))
		{
			Dist current = heap.poll();
			if(examples[current.index].output)
				pos++;
			else 
				neg++;
			prev = current;
			noOfNeighbours--;
		}
		if( pos > neg)
			return true;
		else
			return false;
	}

	public void printMatrix(int noOfNeighbour)
	{
		char[][] array2d = new char[rows][columns];
		
		for(int i=0;i<rows;i++)
			for(int j=0;j<columns;j++)
				array2d[i][j] = '.';
		for(int i =0; i < examples.length; i++)
			if(examples[i].output)
				array2d[examples[i].y][examples[i].x] = '+';
			else
				array2d[examples[i].y][examples[i].x] = '-';
		
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
			{
				if(array2d[i][j] == '.')
				{
					PriorityQueue<Dist> heap = new PriorityQueue<Dist>();
					for(int k=0;k < examples.length;k++)
					{	
						double dist = Math.sqrt((i-examples[k].y)*(i-examples[k].y)+(j-examples[k].x)*(j-examples[k].x));
//						System.out.println(dist +" ("+j+","+i+")"+" examples : x: "+examples[k].y+" y:"+examples[k].x);
						Dist d = new Dist(dist, k);
						heap.add(d);
					}
					if(getClassifier(heap, noOfNeighbour))
						array2d[i][j] = '+';
					else
						array2d[i][j] = '-';
				}
			}
		}
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
				System.out.print(array2d[i][j]+" ");
			System.out.println();
		}
	}
	public int findNoOfErrors(String train , String test, int noOfNeighbours)
	{
		int noOfErrors = 0;
		char[] testArr = test.toCharArray();
		char[] trainArr = train.toCharArray();
		PriorityQueue<Dist> heap = new PriorityQueue<Dist>();
		for(int i=0; i <  testArr.length;i++)
		{
			for(int j=0;j < trainArr.length;j++)
			{	
				double dist = calculateDistance(Integer.parseInt(testArr[i]+""), Integer.parseInt(trainArr[j]+""));
				Dist d = new Dist(dist, Integer.parseInt(trainArr[j]+""));
				heap.add(d);
			}
			if(getClassifier(heap,noOfNeighbours) != examples[Integer.parseInt(testArr[i]+"")].output)
				noOfErrors++;
		}
		return noOfErrors;
	}

	private String removeGivenSubString(String input, String toBeRemoved, int n) {
		return input.substring(0, input.indexOf(toBeRemoved)) + input.substring(input.indexOf(toBeRemoved) + toBeRemoved.length(), input.length());
	}
	public void nearestNeighbours()
	{
		int n = numberOfExamples/kFold;
		for(int i=1;i<6;i++)
		{
			double e =0, variance =0;
			double standardDeviation;
			double E[] = new double[numberOfPermutations];
			for(int p =0;p<numberOfPermutations;p++)
			{
				double noOfErrors = 0;
				for(int f=0;f<kFold;f++)
				{
					String train , test;

					if(numberOfExamples %2 != 0 && f==kFold-1)
						test = permutations[p].substring(f*n, permutations[p].length());
					else
						test = permutations[p].substring(f*n,f*n+n);
					train = removeGivenSubString(permutations[p],test, n);
					double x = findNoOfErrors(train, test, i);
//					System.out.println(x+ " errors , permutations : "+permutations[p]);
					noOfErrors += x/*findNoOfErrors(train, test, i)*/;
				}
				E[p] = noOfErrors/(double)numberOfExamples;
//				System.out.println(" E : "+E[p]+" permutation : "+permutations[p]);
			}
			for(int c=0;c<numberOfPermutations;c++)
				e += E[c];
			e = e/numberOfPermutations;

			for(int c=0;c<numberOfPermutations;c++)
				variance +=(E[c]-e)*(E[c]-e);
			standardDeviation = Math.sqrt(variance/(numberOfPermutations -1));
			System.out.println("e : "+ e+ " NN : "+i);
			System.out.println("standard deviation :"+standardDeviation+ " NN : "+i);
			printMatrix(i);
		}
	}
}