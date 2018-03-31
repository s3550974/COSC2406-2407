import java.io.*;
import java.util.*;

public class dbquery{
	static String query;
	static String fileName;
	static int pageSize;
	static boolean debug = true;

	public static void main (String[] args){
		//read terminal arguments
		if(debug)System.out.println("No of args: " + args.length);
		if(args.length == 2){
			query = args[0];
			fileName = args[1];
			try{
				String[] split = fileName.split("\\.");
				pageSize = Integer.parseInt(split[1]);
			} catch (IndexOutOfBoundsException e){
				System.out.println("Oh no, page size must be an integer!");
			}
		}else{
			System.out.println("Oh no, you need 2 arguments!");
		}
		if(debug)System.out.println("Query: " + query);
		if(debug)System.out.println("File: " + fileName);
		if(debug)System.out.println("Page size: " + pageSize);
		
		//declare variables
		int pageOffset = 0;
		int recordOffset = 0;
		int recordSize = 263;
		int recordPerPage = pageSize/recordSize;
		int remainderPage = pageSize%recordSize;
		int nameSize = 200;

		RandomAccessFile in = null;
		try{
			in = new RandomAccessFile(fileName, "r");
			in.seek(0);
			byte[] test = new byte[nameSize];
			in.read(test);
			String nameTest = new String(test);
			System.out.println(nameTest);
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
