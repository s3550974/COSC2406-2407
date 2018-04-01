import java.io.*;
import java.util.*;

public class dbquery{
	static String query;
	static String fileName;
	static int pageSize;

	public static void main (String[] args){
		//read terminal arguments
		if(args.length == 2){
			query = args[0];
			fileName = args[1];
			try{
				String[] split = fileName.split("\\.");
				pageSize = Integer.parseInt(split[1]);
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

		//convert query to bytes
		byte[] byteQuery = query.getBytes();
		byte[] paddedQuery = Arrays.copyOf(byteQuery, nameSize);
		//used to check if array is empty
		byte[] emptyByte = new byte[200];
		
		//counter for current record in page
		int currRec = 0;

		//Random Access File to move to and fro of a binary file
		RandomAccessFile in = null;
		try{
			in = new RandomAccessFile(fileName, "r");
			while(true){
				//declare current offset
				int currOffset = currRec * recordSize + pageOffset * pageSize;
				//move to offset
				in.seek(currOffset);
				//get the name
				byte[] readByte = new byte[nameSize];
				in.read(readByte);
				//if name matches query
				if(Arrays.equals(readByte, paddedQuery)){
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
					bn_name = query;

					//give status a value
					in.seek(currOffset + nameSize);
					boolean bolStatus = in.readBoolean();
					if(bolStatus)
						bn_status = "Registered";
					else
						bn_status = "Deregistered";

					//give reg date a value
					in.seek(currOffset + nameSize + statusSize);
					byte[] readRegDate = new byte[regDateSize];
					in.read(readRegDate);
					bn_reg_dt = new String(readRegDate);
					
					//give cancel date a value
					in.seek(currOffset + nameSize + statusSize + regDateSize);
					byte[] readCancelDate = new byte[cancelDateSize];
					in.read(readCancelDate);
					bn_cancel_dt = new String(readCancelDate);

					//give renew date a value
					in.seek(
						currOffset + nameSize + statusSize + 
						regDateSize + cancelDateSize);
					byte[] readRenewDate = new byte[renewDateSize];
					in.read(readRenewDate);
					bn_reg_dt = new String(readRegDate);

					//print!
					System.out.println(
						"Query found at page " + pageOffset + ", record " + currRec +
						"\nBusiness name: " + bn_name +
						"\nRegister status: " + bn_status +
						"\nRegister date: " + bn_reg_dt +
						"\nCancel date: " + bn_cancel_dt +
						"\nRenew date: " + bn_reg_dt +
						"\n");
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
			if (in != null){
				try{
					in.close();
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
}//class
