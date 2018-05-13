import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class hashquery{
	private static final boolean DEBUG = false;
	private static final int HASH = 3698507;
	private static final int INTBYTES = 4;
	private static final int NAMESIZE = 200;
	private static final int STATUSSIZE = 1;
	private static final int REGDATESIZE = 10;
	private static final int CANCELDATESIZE = 10;
	private static final int RENEWDATESIZE = 10;
	private static final int STATENUMSIZE = 10;
	private static final int STATEREGSIZE = 2;
	private static final int ABNSIZE = 20;
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
		int pageOffset = 0;
		int recordOffset = 0;
		int recordPerPage = pageSize/RECSIZE;
		int remainderPage = pageSize%RECSIZE;

		//used to check if array is empty
		byte[] emptyByte = new byte[200];
		
		//declare time to see how long queries take
		long startTime = 0;
		long endTime = 0;
		long totalTime = 0;

		//Random Access File to move to and fro of a binary file
		RandomAccessFile heapIn = null;
		RandomAccessFile hashIn = null;
		//convert query to byte array
		byte[] byteQuery = getByteArr(query);
		try{
			//time
			startTime = System.nanoTime();
			//declare random acces files for heap and hash files
			hashIn = new RandomAccessFile("hash."+pageSize, "r");
			heapIn = new RandomAccessFile("heap."+pageSize, "r");
			//declare the initial offset of the hash from the hashcode
			int initOffset = getHash(byteQuery) * INTBYTES;
			int currOffset = initOffset;
			//loop until end of file or found
			while(true){
				//move to hash offset
				hashIn.seek(currOffset);
				//read the pointer to heap
				int hashPointer = hashIn.readInt();
				byte[] heapName = new byte[200];
				if(hashPointer > -1){
					//move to heap offset with the pointer
					heapIn.seek(hashPointer);
					//obtain the name from the hash pointer
					heapIn.read(heapName);
					if(Arrays.equals(heapName, byteQuery)){
						String name = new String(heapName);
						System.out.println("Pointer: " + hashPointer);
						//declare strings to be printed
						String bn_name;
						String bn_status;
						String bn_reg_dt;
						String bn_cancel_dt;
						String bn_renew_dt;
						String bn_state_num;
						String bn_state_of_reg;
						String bn_abn;

						//give name value
						bn_name = name;
						//give status a value
						heapIn.seek(hashPointer + NAMESIZE);
						boolean bolStatus = heapIn.readBoolean();
						if(bolStatus)
							bn_status = "Registered";
						else
							bn_status = "Deregistered";
						//give reg date a value
						heapIn.seek(hashPointer + NAMESIZE + STATUSSIZE);
						byte[] readRegDate = new byte[REGDATESIZE];
						heapIn.read(readRegDate);
						bn_reg_dt = new String(readRegDate);
						
						//give cancel date a value
						heapIn.seek(hashPointer + NAMESIZE + STATUSSIZE + REGDATESIZE);
						byte[] readCancelDate = new byte[CANCELDATESIZE];
						heapIn.read(readCancelDate);
						bn_cancel_dt = new String(readCancelDate);

						//give renew date a value
						heapIn.seek(
							hashPointer + NAMESIZE + STATUSSIZE + 
							REGDATESIZE + CANCELDATESIZE);
						byte[] readRenewDate = new byte[RENEWDATESIZE];
						heapIn.read(readRenewDate);
						bn_renew_dt = new String(readRenewDate);

						//give state number a value
						heapIn.seek(
							hashPointer + NAMESIZE + STATUSSIZE + 
							REGDATESIZE + CANCELDATESIZE + RENEWDATESIZE);
						byte[] readStateNum = new byte[STATENUMSIZE];
						heapIn.read(readStateNum);
						bn_state_num = new String(readStateNum);

						//give state of reg a value
						heapIn.seek(
							hashPointer + NAMESIZE + STATUSSIZE + 
							REGDATESIZE + CANCELDATESIZE + RENEWDATESIZE +
							STATENUMSIZE);
						short shtState = heapIn.readShort();
						bn_state_of_reg = mapState.get(shtState);
						
						//give abn a value
						heapIn.seek(
							hashPointer + NAMESIZE + STATUSSIZE + 
							REGDATESIZE + CANCELDATESIZE + RENEWDATESIZE +
							STATENUMSIZE + STATEREGSIZE);
						byte[] readABN = new byte[ABNSIZE];
						heapIn.read(readABN);
						bn_abn = new String(readABN);

						endTime = System.nanoTime();
						totalTime = endTime - startTime;
						long msTime = TimeUnit.NANOSECONDS.toMillis(totalTime);
						//print!
						System.out.println(
							"Query found in " + msTime + " ms" +
							"\nBusiness name: " + bn_name +
							"\nRegister status: " + bn_status +
							"\nRegister date: " + bn_reg_dt +
							"\nCancel date: " + bn_cancel_dt +
							"\nRenew date: " + bn_renew_dt +
							"\nState number: " + bn_state_num +
							"\nState of registration: " + bn_state_of_reg +
							"\nABN: " + bn_abn +
							"\n");
					}
				}

				currOffset = currOffset + INTBYTES;

				if(currOffset > (HASH - 1) * INTBYTES){
					//move to beginning
					currOffset = 0;
				}

				//check if name is empty, empty name implies end of file
				/*
				if(Arrays.equals(readByte, emptyByte)){
					System.out.println("End of file reached.");
					break;
				}
				*/
				//break;
			}
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (hashIn != null){
				try{
					hashIn.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
			if (heapIn != null){
				try{
					heapIn.close();
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
