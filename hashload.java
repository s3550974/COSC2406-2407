import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class hashload{
	private static final int HASH = 3698507;
	static String query;
	static int pageSize;

	public static void main (String[] args){
		//read terminal arguments
		if(args.length == 2){
			query = args[0];
			String strPage = args[1];
			try{
				pageSize = Integer.parseInt(strPage);
			} catch (IndexOutOfBoundsException e){
				System.out.println("Page size must be an integer.");
				System.exit(0);
			}
		}else{
			System.out.println("2 arguments required, query and file name.");
			System.exit(0);
		}

		//declare size variables 
		int nameSize = 200;
		int statusSize = 1;
		int regDateSize = 10;
		int cancelDateSize = 10;
		int renewDateSize = 10;
		int stateNumSize = 10;
		int stateRegSize = 2;
		int abnSize = 20;
		int recordSize =
			nameSize + 
			statusSize +
			regDateSize +
			cancelDateSize +
			renewDateSize +
			stateNumSize +
			stateRegSize +
			abnSize;
		int pageOffset = 0;
		int recordOffset = 0;
		int recordPerPage = pageSize/recordSize;
		int remainderPage = pageSize%recordSize;

		//used to check if array is empty
		byte[] emptyByte = new byte[200];
		
		//declare time to see how long queries take
		long startTime = 0;
		long endTime = 0;
		long totalTime = 0;

		//test
		byte[] testQuery = getByteArr(query);
		byte[] testPad1 = getByteArr("mellow");
		byte[] testPad2 = getByteArr("mellow");
		int hashValQ = getHash(testQuery);
		int hashVal1 = getHash(testPad1);
		int hashVal2 = getHash(testPad2);
		if(Arrays.equals(testPad1, testPad2))
			System.out.println("Test eq");
		System.out.println("Query name: " + query);
		System.out.println("Query hash: " + hashValQ);
		System.out.println("Test hash 1: " + hashVal1);
		System.out.println("Test hash 2: " + hashVal2);
	}
	
	//genereates a query that is 200 bytes long of the name
	public static byte[] getByteArr(String name){
		byte[] paddedQuery = Arrays.copyOf(name.getBytes(), 200);
		return paddedQuery;
	}
	//generates a hash value of mode 3698507,
	public static int getHash(byte[] byteArray){
		int hashVal = Math.abs((Arrays.hashCode(byteArray)) % HASH);
		return hashVal;
	}
}//class
