import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class hashload{
	private static final int HASH = 3698507;
	//private static final int HASH = 8;
	private static final int NAMESIZE = 200;
	private static final int RECSIZE = 263;
	private static final int INTBYTES = 4;
	static int pageSize;

	public static void main (String[] args){
		//read terminal arguments
		if(args.length == 1){
			String strPage = args[0];
			try{
				pageSize = Integer.parseInt(strPage);
			} catch (IndexOutOfBoundsException e){
				System.err.println("Page size must be an integer.");
				System.exit(0);
			}
		}else{
			System.err.println("1 argument required, page size.");
			System.exit(0);
		}

		//declare offset variables
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
		
		//Read heap
		RandomAccessFile in = null;
		RandomAccessFile out = null;
		try{
			//start timer
			startTime = System.nanoTime();
			//progress output text
			System.out.println("Initialising hash."+pageSize+"...");
			//declare random access file to read and write to
			in = new RandomAccessFile("heap."+pageSize, "r");
			out = new RandomAccessFile("hash."+pageSize, "rw");

			//initilaise the hash file to all -1s
			for(int i = 0; i < HASH; i++)
				out.writeInt(-1);
			//out.setLength((HASH - 1) * INTBYTES);

			//progress output text
			System.out.println("Hashing into hash."+pageSize+" from heap."+pageSize+"...");
			//counter to keep track of collisions, debugging purposes
			int ctrCol = 0;
			int ctrIns = 0;

			//loop until end of heap file
			while(true){
				//declare current offset
				int currOffset = currRec * RECSIZE + pageOffset * pageSize;
				//move to offset
				in.seek(currOffset);
				//get the bn_name in bytes
				byte[] readByte = new byte[NAMESIZE];
				in.read(readByte);

				//check if read bn_name is empty, empty name implies end of file
				if(Arrays.equals(readByte, emptyByte)){
					System.out.println("End of file reached.");
					System.out.println("No of insertions: " + ctrIns);
					System.out.println("No of collisions: " + ctrCol);
					break;
				}

				//write to hash file
				int hashName = getHash(readByte) * INTBYTES;
				int hashOffset = hashName;
				while(true){
					out.seek(hashOffset);
					int bucket = out.readInt();
					out.seek(hashOffset);

					//if slot is free, insert
					if(bucket == -1){
						ctrIns++;
						out.writeInt(currOffset);
						break;
					}else{
						ctrCol++;
						//increment offset by int byte
						hashOffset = hashOffset + INTBYTES;
						//check if passed the file size
						if(hashOffset > (HASH - 1) * INTBYTES){
							//move to beginning
							hashOffset = 0;
						}
						//check if hash file is full
						if(hashOffset == hashName){
							//terminate program
							System.err.println("ERROR ADDING HASH, HASH FILE IS FULL");
							System.err.println("TERMINATING PROGRAM");
							System.exit(0);
						}
					}
				}

				//increment record and page offset
				currRec++;
				if(currRec == recordPerPage){
					currRec = 0;
					pageOffset++;
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
