import java.io.*;
import java.util.*;

public class dbload{
	static int pagesize;
	static String datafile;

	public static void main (String[] args){
		BufferedReader br = null;
		String csvDelimiter = "\t";
		String dataFileDir = "C:/Users/Coffee/Documents/COSC2406-2407/";
		String line;

		String bn_name;
		String bn_status;
		String bn_reg_dt;
		String bn_cancel_dt;
		String bn_renew_dt;

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
		System.out.println("data: " + datafile);
		System.out.println("page: " + pagesize);

		try{
			br = new BufferedReader(new FileReader(dataFileDir + datafile));
			while((line = br.readLine()) != null){
				String[] test = line.split(csvDelimiter);
				System.out.println("test: " + test[1]);

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
	}
}
