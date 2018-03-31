import java.io.*;
import java.util.*;

public class dbload{
	static int pagesize;
	static String datafile;

	public static void main (String[] args){
		//declare csv reading variables
		BufferedReader br = null;
		String csvDelimiter = "\t";
		//fix this later
		String dataFileDir = "C:/Users/Coffee/Documents/COSC2406-2407/";
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


		//read arguments
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
		System.out.println("data: " + datafile);
		System.out.println("page: " + pagesize);


		//read csv file line by line
		try{
			br = new BufferedReader(new FileReader(dataFileDir + datafile));
			DataOutputStream os = new DataOutputStream(new FileOutputStream("binout.dat"));
			br.readLine();
			while((line = br.readLine()) != null){
				String[] test = line.split(csvDelimiter);
				bn_name = test[1];
				bn_status = test[2];
				bn_reg_dt = test[3];
				bn_cancel_dt = test[4];
				bn_renew_dt = test[5];
				bn_state_num = test[6];
				bn_state_of_reg = test[7];
				//sometimes this ends at 8, so try catch to make it null
				try{
					bn_abn = test[8];
				} catch (IndexOutOfBoundsException e){
					bn_abn = "";
				}
				System.out.println("test: " + bn_abn);

				//char[] chr_name = bn_name.toCharArray();
				byte[] byteArray = bn_name.getBytes();
				byte[] temp = Arrays.copyOf(byteArray, 199);

				System.out.println("length: " + temp.length);

				os.write(temp);
				os.writeInt(9);
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
		}//finally
	}//main
}//class
