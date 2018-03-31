import java.io.*;
import java.util.*;

public class dbload{
	static int pagesize;
	static String datafile;
	static boolean debug = false;

	public static void main (String[] args){
		//declare csv reading variables
		BufferedReader br = null;
		String csvDelimiter = "\t";
		String line;
		//fix this later
		String dataFileDir = "C:/Users/Coffee/Documents/COSC2406-2407/";

		//declare database variables
		String bn_name;
		String bn_status;
		String bn_reg_dt;
		String bn_cancel_dt;
		String bn_renew_dt;
		String bn_state_num;
		String bn_state_of_reg;
		String bn_abn;

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
		//debug output
		if(debug){
			System.out.println("data: " + datafile);
			System.out.println("page: " + pagesize);
		}

		//read csv file line by line
		try{
			br = new BufferedReader(new FileReader(dataFileDir + datafile));
			DataOutputStream os = new DataOutputStream(new FileOutputStream("binout.dat"));
			//skip first line
			br.readLine();
			//for each line
			while((line = br.readLine()) != null){
				//separate 
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
				//sometimes this ends at 8, so try catch to make it null
				try{
					bn_abn = split[8];
				} catch (IndexOutOfBoundsException e){
					bn_abn = "";
				}

				//name
				byte[] byteName = bn_name.getBytes();
				byte[] paddedName = Arrays.copyOf(byteName, 200);

				//status
				boolean bolStatus = bn_status.contentEquals("Registered");
				if(debug){
					System.out.println("status: " + bn_status);
					System.out.println("status bol: " + bolStatus);
				}

				//reg date
				byte[] byteRegDate = bn_reg_dt.getBytes();
				byte[] paddedRegDate = Arrays.copyOf(byteRegDate, 10);

				//cancel date
				byte[] byteCancelDate = bn_cancel_dt.getBytes();
				byte[] paddedCancelDate = Arrays.copyOf(byteCancelDate, 10);

				//renew date
				byte[] byteRenewDate = bn_renew_dt.getBytes();
				byte[] paddedRenewDate = Arrays.copyOf(byteRenewDate, 10);

				//renew date
				byte[] byteStateNum = bn_state_num.getBytes();
				byte[] paddedStateNum = Arrays.copyOf(byteStateNum, 10);

				//state of reg
				short shtState = mapState.get(bn_state_of_reg);
				if(debug){
					System.out.println("state short: " + shtState);
				}
				
				//abn
				byte[] byteABN = bn_abn.getBytes();
				byte[] paddedABN = Arrays.copyOf(byteABN, 20);

				os.write(paddedName);
				os.writeBoolean(bolStatus);
				os.write(paddedRegDate);
				os.write(paddedCancelDate);
				os.write(paddedRenewDate);
				os.write(paddedStateNum);
				os.writeShort(shtState);
				os.write(paddedABN);
			}
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (br != null){
				try{
					br.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
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
