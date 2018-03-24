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

		if(args[0].equals("-p")){
			pagesize = Integer.parseInt(args[1]);
			datafile = args[2];
		}

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
