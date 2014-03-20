package ibur.ticktimecounter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class Util {
	public static int[][] dirDelta = {{0,1},{0,-1},{1,0},{-1,0}};
	
	public static void writeDataToCSV(List<Dot> data, File f) throws IOException{
		PrintStream p = new PrintStream(f);
		p.println("Distance from previous, Total displacement");
		for(Dot d : data) {
			p.println(d.distBack + ", " + d.pos);
		}
	}
}
