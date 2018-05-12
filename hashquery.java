import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class hashquery{
	private static final boolean DEBUG = false;
	private static final int HASH = 3698507;
	private static final int NAMESIZE = 200;
	private static final int RECSIZE = 263;
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

		//convert query to byte array
		byte[] byteQuery = query.getBytes();
		byte[] paddedQuery = Arrays.copyOf(byteQuery, nameSize);
		//used to check if array is empty
		byte[] emptyByte = new byte[200];
		
		//counter for current record in page
		int currRec = 0;
		
		//declare time to see how long queries take
		long startTime = 0;
		long endTime = 0;
		long totalTime = 0;

		//Random Access File to move to and fro of a binary file
		RandomAccessFile heapIn = null;
		RandomAccessFile hashIn = null;
		try{
			startTime = System.nanoTime();
			hashIn = new RandomAccessFile("hash."+pageSize, "r");
			while(true){
				//declare current offset
				int currOffset = currRec * recordSize + pageOffset * pageSize;
				//move to offset
				hashIn.seek(currOffset);
				//get the name
				byte[] readByte = new byte[nameSize];
				hashIn.read(readByte);
				//if name matches query
				if(Arrays.equals(readByte, paddedQuery)){
				}
				//increment record and page offset
				currRec++;
				if(currRec == recordPerPage){
					currRec = 0;
					pageOffset++;
				}
				//check if name is empty, empty name implies end of file
				if(Arrays.equals(readByte, emptyByte)){
					System.out.println("End of file reached.");
					break;
				}
			}
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (hashIn != null){
				try{
					hashIn.close();
					endTime = System.nanoTime();
					totalTime = endTime - startTime;
					long msTime = TimeUnit.NANOSECONDS.toMillis(totalTime);
					System.out.println("Queries found in " + msTime + " ms.");
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}//main

	//convert short int to state 
	private static final Map<Short, String> mapState = new HashMap<Short, String>();
	static{
		mapState.put((short)0, "");
		mapState.put((short)1, "NSW");
		mapState.put((short)2, "ACT");
		mapState.put((short)3, "VIC");
		mapState.put((short)4, "QLD");
		mapState.put((short)5, "SA");
		mapState.put((short)6, "WA");
		mapState.put((short)7, "TAS");
		mapState.put((short)8, "NT");
	}//Map
	
	//genereates an array that is 200 bytes long of the name
	public static byte[] getByteArr(String name){
		return Arrays.copyOf(name.getBytes(), NAMESIZE);
	}
	//generates a hash value of mode 3698507,
	public static int getHash(byte[] byteArray){
		return Math.abs((Arrays.hashCode(byteArray)) % HASH);
	}
}//class
