package renderer;

import java.util.ArrayList;

import WindowManager.mainClass;

import java.io.*;

public class object {
	
	public ArrayList<Point3D> points;
	public ArrayList<Triangle> triangles;
	public static double fac = 1;

	Point3D center;
	
	public object(ArrayList<Point3D> v, ArrayList<Triangle> triangles) {
		points = v;
		this.triangles = triangles;
		center = getCenter();
	}

	public object(){
		points = new ArrayList<>();
		triangles = new ArrayList<>();
	}
	
	public static object cube(double x,double y, double z, double height,double width,double length, double h, double w) {
		ArrayList<Point3D> p = new ArrayList<Point3D>();
		ArrayList<Triangle> t = new ArrayList<Triangle>();
		
		p.add(new Point3D(x+(length/2),y+(height/2),z-(width/2),h, w));
		p.add(new Point3D(x-(length/2),y+(height/2),z-(width/2),h, w));
		p.add(new Point3D(x-(length/2),y-(height/2),z-(width/2),h, w));
		p.add(new Point3D(x+(length/2),y-(height/2),z-(width/2),h, w));
		p.add(new Point3D(x+(length/2),y+(height/2),z+(width/2),h, w));
		p.add(new Point3D(x-(length/2),y+(height/2),z+(width/2),h, w));
		p.add(new Point3D(x-(length/2),y-(height/2),z+(width/2),h, w));
		p.add(new Point3D(x+(length/2),y-(height/2),z+(width/2),h, w));
		
		t.add(new Triangle(1-1, 6-1, 5-1, p));
		t.add(new Triangle(1-1, 3-1, 2-1, p));
		t.add(new Triangle(1-1, 4-1, 3-1, p));
		t.add(new Triangle(1-1, 2-1, 6-1, p));
		t.add(new Triangle(2-1, 7-1, 6-1, p));
		t.add(new Triangle(2-1, 3-1, 7-1, p));
		t.add(new Triangle(3-1, 8-1, 7-1, p));
		t.add(new Triangle(3-1, 4-1, 8-1, p));
		t.add(new Triangle(4-1, 5-1, 8-1, p));
		t.add(new Triangle(4-1, 1-1, 5-1, p));
		t.add(new Triangle(8-1, 6-1, 7-1, p));
		t.add(new Triangle(8-1, 5-1, 6-1, p));
		
		object ret = new object(p,t);
		ret.identify();		

		return ret;
	}

	public void identify(){
		for(int i = 0; i<triangles.size(); i++){
			triangles.get(i).idx = i;
		}
	}

	public static object plane(double x, double y, double z, double len, double width, int res, double height, double w) {
		object ret = new object();
		int rows = (int)width/res;
		int cols = (int)len/res;
		for(int i = 0; i<cols; i++){
			for(int j = 0; j<rows; j++){
				Point3D[] points = {
					new Point3D(x + i*res, y + j*res, z, height, w),
					new Point3D(x + (i+1)*res, y + j*res, z, height, w),
					new Point3D(x + i*res, y + (j+1)*res, z, height, w),
					new Point3D(x + (i+1)*res, y + (j+1)*res, z, height, w)
				};
				for(Point3D p : points) ret.points.add(p);
				
				int s = ret.points.size();
				Triangle t1 = new Triangle(s-3, s-4, s-2,ret.points);
				t1.idx = ret.triangles.size();

				Triangle t2 = new Triangle(s-3, s-2, s-1,ret.points);
				t2.idx = ret.triangles.size()+1;

				ret.triangles.add(t1);
				ret.triangles.add(t2);
			}
		}
		ret.center = ret.getCenter();
		return ret;
	}
	
	public object(Point3D[] v, Triangle[] t) {
		points = new ArrayList<Point3D>();
		triangles = new ArrayList<Triangle>();
		
		for(Point3D i : v) {
			points.add(i);
		}
		for(Triangle i : t) {
			triangles.add(i);
		}
		center = getCenter();
	}
	
	public static object loadFromFile(String fileName, double height, double width) {
		ArrayList<Point3D> ver = new ArrayList<Point3D>();
		ArrayList<Triangle> t = new ArrayList<Triangle>();
		FileInputStream fl;
		try {
			fl = new FileInputStream(fileName);
			byte[] b = fl.readAllBytes();
			String contents = new String(b).trim();
			String[] spl = contents.split(" ");
			double x = 0;
			double y = 0;
			double z = 0;
			int idx1 = 0;
			int idx2 = 0;
			int idx3 = 0;

			for(int i = 0; i<spl.length-3; i++) {
				//if(spl[i].contains("v") && spl[i].toCharArray().length>0 && spl[i].toCharArray().length<5)System.out.println(spl[i].toCharArray().length);
				if(spl[i].contains("v") && spl[i].indexOf("v")==spl[i].toCharArray().length-1) {
					x = fac*Double.parseDouble(spl[i+1]);
					y = fac*Double.parseDouble(spl[i+2]);
					z = fac*Double.parseDouble(getInputString(spl[i+3]));
					ver.add(new Point3D(x,y,z,height,width));
				}
				if(spl[i].contains("f") && spl[i].indexOf("f")==spl[i].toCharArray().length-1){
					if(spl[i+1].contains("/")){
						idx1 = Integer.parseInt(spl[i+1].split("/")[0])-1;
						idx2 = Integer.parseInt(spl[i+2].split("/")[0])-1;
						idx3 = Integer.parseInt(getInputString(spl[i+3].split("/")[0]+" "))-1;
					}else{
						idx1 = Integer.parseInt(spl[i+1])-1;
						idx2 = Integer.parseInt(spl[i+2])-1;
						idx3 = Integer.parseInt(getInputString(spl[i+3]))-1;
					}
					Point3D[] vers = getArray(ver);
					Triangle toAdd = new Triangle(idx1, idx2, idx3, vers);
					toAdd.idx = t.size();
					t.add(toAdd);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new object(ver, t);
	}
	
	public static Point3D[] getArray(ArrayList<Point3D> vers) {
		Point3D[] ret = new Point3D[vers.size()];
		for(int i = 0; i<vers.size(); i++) {
			ret[i] = vers.get(i);
		}
		return ret;
	}
	
	public static String getInputString(String a) {
		String input = a;
		String ret = "";
		char[] bca = input.toCharArray();
		for(int i = 0; i<bca.length-1; i++) {
			String toBeTested = Character.toString(bca[i+1]);
			boolean isInt = false;
			try {
				isInt = true;
				Integer.parseInt(toBeTested);
			}catch(NumberFormatException e) {
				isInt = false;
				if(toBeTested.equals(".")) {
					isInt = true;
				}
			}
			if(isInt) {
				ret+=Character.toString(bca[i]);
			}else {
				ret+=Character.toString(bca[i]);
				break;
			}
		}
		return ret;
	}
	
	public object rotateX(double angle) {
		object o = clone();
		for(Point3D p : o.points) {
			p.rotateX(angle);
		}
		return o;
	}

	public object rotateY(double angle) {
		object o = clone();
		for(Point3D p : o.points) {
			p.rotateY(angle);
		}
		return o;
	}

	public void arrangeTriangle(){
		ArrayList<Triangle> b = new ArrayList<Triangle>();
		for(Triangle t : triangles){
			b.add(t);
			int index = b.size()-1;
			for(int i = b.size()-2; i>=0; i--){
				if(t.getCentroid().getMag() > b.get(i).getCentroid().getMag()){
					Triangle toput = b.get(i);
					b.set(index, toput);
					b.set(i, t);
					index = i;
				}
			}
		}
		triangles = b;
	}

	public object rotateZ(double angle) {
		object o = clone();
		for(Point3D p : o.points) {
			p.rotateZ(angle);
		}
		return o;
	}
	
	public object translate(Point3D point){
		object o = clone();
		for(Point3D p : o.points){
			p.x -= point.x;
			p.y -= point.y;
			p.z -= point.z;
		}

		return o;
	}

	public object clone(){
		object ret = new object();
		for(Point3D p : points){
			ret.points.add(new Point3D(p.x,p.y,p.z,p.height,p.width));
		}
		for(Triangle t : triangles){
			Triangle ta = new Triangle(t.i, t.i2, t.i3, ret.points);
			ta.idx = t.idx;
			ret.triangles.add(ta);
		}

		return ret;
	}

	public object clip(Plane3D plane, Point3D ref){
		object ret = new object();
		for(int i = triangles.size()-1; i>=0; i--){
			ArrayList<Triangle> t = new ArrayList<Triangle>();
			triangles.get(i).clip(plane, ref, t);
			for(Triangle triangle : t){
				for(Point3D p : triangle.points) ret.points.add(p);
				triangle.i = ret.points.size()-3;
				triangle.i2 = ret.points.size()-2;
				triangle.i3 = ret.points.size()-1;
				triangle.idx = triangles.get(i).idx;
				ret.triangles.add(triangle);
				int index = ret.triangles.size()-1;
				for(int j = ret.triangles.size()-2; j>=0; j--){
					if(triangle.getCentroid().getMag() > ret.triangles.get(j).getCentroid().getMag()){
						Triangle toput = ret.triangles.get(j);
						ret.triangles.set(index, toput);
						ret.triangles.set(j, triangle);
						index = j;
					}
				}
			}
		}
		return ret;
	}

	public Point3D getCenter(){
		Point3D ret = new Point3D(0,0,0);
		for(Point3D p : points){
			ret.subtract(p.multiply(-1));
		}
		return ret.multiply((double)1/points.size());
	}

	public void add(object o){
		for(Triangle t : o.triangles){
			t.i += points.size();
			t.i2 += points.size();
			t.i3 += points.size();
			triangles.add(t);
		}
		for(Point3D p : o.points) points.add(p);
	}

	public object allInOne(Point3D camera, Point3D camera_orientation, Plane3D[] planes, Point3D ref){
		object ret = new object();
		for(Point3D p : points) {
			Point3D toadd = new Point3D(p.x - camera.x, p.y - camera.y, p.z - camera.z, p.height, p.width);
			toadd.rotateX(camera_orientation.x);
			toadd.rotateY(camera_orientation.y);
			toadd.rotateZ(camera_orientation.z);
			ret.points.add(toadd);
		}
		for(Triangle t : triangles){
			Triangle triangle = new Triangle(t.i, t.i2, t.i3, ret.points);
			ArrayList<Triangle> ts = new ArrayList<>();
			triangle.clip(planes[0], ref, ts);
			for(int i = 1; i<planes.length; i++){
				for(int j = ts.size()-1; j>=0; j--){
					ts.get(j).clip(planes[i], ref, ts);
				}
			}
			for(Triangle tri : ts){
				Point3D normal = tri.normalize().normal();
				if(Point3D.dotProduct(tri.points[0], normal) >= 0 && 
				Point3D.dotProduct(tri.points[1], normal) >= 0 &&
				Point3D.dotProduct(tri.points[2], normal) >= 0){
					for(Point3D p : tri.points) ret.points.add(p);
					tri.i = ret.points.size()-3;
					tri.i2 = ret.points.size()-2;
					tri.i3 = ret.points.size()-1;
					tri.idx = t.idx;
					ret.triangles.add(tri);
					int index = ret.triangles.size()-1;
					for(int j = ret.triangles.size()-2; j>=0; j--){
						if(tri.getMeasure() > ret.triangles.get(j).getMeasure()){
							Triangle toput = ret.triangles.get(j);
							ret.triangles.set(index, toput);
							ret.triangles.set(j, tri);
							index = j;
						}
					}
				}
			}
		}
		return ret;
	}

	public void reconfigure(){
		for(Triangle t : triangles){
			t.idx = triangles.indexOf(t);
			t.i = points.indexOf(t.points[0]);
			t.i2 = points.indexOf(t.points[1]);
			t.i3 = points.indexOf(t.points[2]);
		}
	}
}
