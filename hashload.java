import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class hashload{
	private static final int HASH = 3698507;
	private static final boolean DEBUG = false;
	static int pageSize;

	public static void main (String[] args){
		//read terminal arguments
		if(args.length == 1){
			String strPage = args[0];
			try{
				pageSize = Integer.parseInt(strPage);
			} catch (IndexOutOfBoundsException e){
				System.out.println("Page size must be an integer.");
				System.exit(0);
			}
		}else{
			System.out.println("1 argument required, page size.");
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

		//counter for current record in page
		int currRec = 0;

		//used to check if array is empty
		byte[] emptyByte = new byte[200];
		
		//declare time to see how long queries take
		long startTime = 0;
		long endTime = 0;
		long totalTime = 0;

		//test
		if(DEBUG == true){
			byte[] testPad1 = getByteArr("mellow");
			byte[] testPad2 = getByteArr("mellow");
			int hashVal1 = getHash(testPad1);
			int hashVal2 = getHash(testPad2);
			if(Arrays.equals(testPad1, testPad2))
				System.out.println("Test eq");
			System.out.println("Test hash 1: " + hashVal1);
			System.out.println("Test hash 2: " + hashVal2);
		}
		
		//Read heap
		RandomAccessFile in = null;
		try{
			startTime = System.nanoTime();
			in = new RandomAccessFile("heap."+pageSize, "r");
			while(true){
				//declare current offset
				int currOffset = currRec * recordSize + pageOffset * pageSize;
				//move to offset
				in.seek(currOffset);
				//get the name
				byte[] readByte = new byte[nameSize];
				in.read(readByte);

				//DEBUG
				System.out.println("Hash: " + getHash(readByte));
				System.out.println("Offset: " + currOffset);
				System.out.println();

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
			if (in != null){
				try{
					in.close();
					endTime = System.nanoTime();
					totalTime = endTime - startTime;
					long msTime = TimeUnit.NANOSECONDS.toMillis(totalTime);
					System.out.println("hash file generated in " + msTime + " ms.");
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	//genereates an array that is 200 bytes long of the name
	public static byte[] getByteArr(String name){
		return Arrays.copyOf(name.getBytes(), 200);
	}
	//generates a hash value of mode 3698507,
	public static int getHash(byte[] byteArray){
		return Math.abs((Arrays.hashCode(byteArray)) % HASH);
	}
}//class
