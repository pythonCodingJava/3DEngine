package WindowManager;

import java.util.*;
import java.io.*;

public class test {
	
	public static void main(String[] args) {
		File f = new File("/home/kartik/projects/3DEngine/res/cow.obj");
		try{
			Scanner s = new Scanner(f);
			String o = "";
			while(s.hasNextLine()){
				String line = s.nextLine();
				if(line.contains("f")){
					String[] parts = line.split(" ");
					o += parts[0]+ " ";
					for(int i = 1; i<4; i++){
						o += parts[i].split("/")[0];
						if(i != 3) o += " ";
					}
					o+='\n';
				}else if(!line.contains("vt")){
					o+=line+'\n';
				}
			}
			FileWriter fw = new FileWriter(f);
			fw.write(o);
			fw.close();
			s.close();
		}catch(Exception e){}
	}
	
}
