import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class hashload{
	private static final boolean DEBUG = false;
	private static final int HASH = 3698507;
	private static final int NAMESIZE = 200;
	private static final int RECSIZE = 263;
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

		int pageOffset = 0;
		int recordOffset = 0;
		int recordPerPage = pageSize/RECSIZE;
		int remainderPage = pageSize%RECSIZE;

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
		RandomAccessFile out = null;
		try{
			//start timer
			startTime = System.nanoTime();
			//declare random access file to read and write to
			in = new RandomAccessFile("heap."+pageSize, "r");
			out = new RandomAccessFile("hash."+pageSize, "rw");
			//set length of the hash file to 4 bytes * no. of hash
			out.setLength(4 * HASH);

			//loop until end of heap file
			while(true){
				//declare current offset
				int currOffset = currRec * RECSIZE + pageOffset * pageSize;
				//move to offset
				in.seek(currOffset);
				//get the name in bytes
				byte[] readByte = new byte[NAMESIZE];
				in.read(readByte);

				//DEBUG
				if(DEBUG == true){
					String name = new String(readByte);
					System.out.println("Name: " + name);
					System.out.println("Hash: " + getHash(readByte));
					System.out.println("Offset: " + currOffset);
					System.out.println();
				}
				int hashName = getHash(readByte) * 4;
				out.seek(hashName);
				out.writeInt(currOffset);

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
					//close random access file
					in.close();
					//calculate passed time
					endTime = System.nanoTime();
					totalTime = endTime - startTime;
					long msTime = TimeUnit.NANOSECONDS.toMillis(totalTime);
					//output passed time
					System.out.println("hash file generated in " + msTime + " ms.");
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	//genereates an array that is 200 bytes long of the name
	public static byte[] getByteArr(String name){
		return Arrays.copyOf(name.getBytes(), NAMESIZE);
	}
	//generates a hash value of mode 3698507,
	public static int getHash(byte[] byteArray){
		return Math.abs((Arrays.hashCode(byteArray)) % HASH);
	}
}//class
