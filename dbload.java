import java.io.*;
import java.util.*;

public class dbload{
	static int pagesize;
	static String datafile;

	public static void main (String[] args){
		//read terminal arguments
		for(int i = 0; i < 2; i++){
			if(args[i].equals("-p")){
				pagesize = Integer.parseInt(args[i + 1]);
				if(i == 0){
					datafile = args[2];
				}else{
					datafile = args[0];
				}
			}
		}

		//declare csv reading variables
		BufferedReader br = null;
		String csvDelimiter = "\t";
		String line;
		//declare database variables
		String bn_name;
		String bn_status;
		String bn_reg_dt;
		String bn_cancel_dt;
		String bn_renew_dt;
		String bn_state_num;
		String bn_state_of_reg;
		String bn_abn;
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

		//error checking: pagesize must be larger than record size
		if(pagesize<recordSize){
			System.out.println("Page size must be larger or equal to " + recordSize + "\nDbload will not run.");
			System.exit(0);
		}
		int recordPerPage = pagesize/recordSize;
		int remainderPage = pagesize%recordSize;

		//read csv file line by line
		try{
			//declare readers
			br = new BufferedReader(new FileReader(datafile));
			DataOutputStream os = new DataOutputStream(new FileOutputStream("heapfile." + pagesize));
			//skip first line
			br.readLine();
			//for each line
			int currRec = 0;
			while((line = br.readLine()) != null){
				//separate the line
				String[] split = line.split(csvDelimiter);
				bn_name = split[1];
				bn_status = split[2];
				bn_reg_dt = split[3];
				bn_cancel_dt = split[4];
				bn_renew_dt = split[5];
				try{
					bn_state_num = split[6];
				} catch (IndexOutOfBoundsException e){
					bn_state_num = "";
				}
				try{
					bn_state_of_reg = split[7];
				} catch (IndexOutOfBoundsException e){
					bn_state_of_reg = "";
				}
				try{
					bn_abn = split[8];
				} catch (IndexOutOfBoundsException e){
					bn_abn = "";
				}

				//name
				byte[] byteName = bn_name.getBytes();
				byte[] paddedName = Arrays.copyOf(byteName, nameSize);
				//status
				boolean bolStatus = bn_status.contentEquals("Registered");
				//reg date
				byte[] byteRegDate = bn_reg_dt.getBytes();
				byte[] paddedRegDate = Arrays.copyOf(byteRegDate, regDateSize);
				//cancel date
				byte[] byteCancelDate = bn_cancel_dt.getBytes();
				byte[] paddedCancelDate = Arrays.copyOf(byteCancelDate, cancelDateSize);
				//renew date
				byte[] byteRenewDate = bn_renew_dt.getBytes();
				byte[] paddedRenewDate = Arrays.copyOf(byteRenewDate, renewDateSize);
				//renew date
				byte[] byteStateNum = bn_state_num.getBytes();
				byte[] paddedStateNum = Arrays.copyOf(byteStateNum, stateNumSize);
				//state of reg
				short shtState = mapState.get(bn_state_of_reg);
				//abn
				byte[] byteABN = bn_abn.getBytes();
				byte[] paddedABN = Arrays.copyOf(byteABN, abnSize);

				//write to file
				os.write(paddedName);
				os.writeBoolean(bolStatus);
				os.write(paddedRegDate);
				os.write(paddedCancelDate);
				os.write(paddedRenewDate);
				os.write(paddedStateNum);
				os.writeShort(shtState);
				os.write(paddedABN);
				//check page padding
				currRec++;
				if(currRec == recordPerPage){
					currRec = 0;
					for(int i=0; i<remainderPage; i++){
						os.write(0);
					}
				}
			}
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (br != null){
				try{
					//os.close(); //not sure why os.close doesn't work :c
					br.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
		System.out.println("headfile." + pagesize + " has been created.");
	}//main

	//convert state to short
	private static final Map<String, Short> mapState = new HashMap<String, Short>();
	static{
		mapState.put("", (short)0);
		mapState.put("NSW", (short)1);
		mapState.put("ACT", (short)2);
		mapState.put("VIC", (short)3);
		mapState.put("QLD", (short)4);
		mapState.put("SA", (short)5);
		mapState.put("WA", (short)6);
		mapState.put("TAS", (short)7);
		mapState.put("NT", (short)8);
	}//Map
}//class
