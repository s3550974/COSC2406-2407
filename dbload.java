import java.io.*;
import java.util.*;

public class dbload{
	static int pagesize;
	static String datafile;

	public static void main (String[] args){
		if(args[0].equals("-p")){
			pagesize = Integer.parseInt(args[1]);
			datafile = args[2];
		}
	}
}
