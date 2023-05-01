package renderer;

import java.awt.geom.Point2D;

public class Point3D {

	final static double fnear = 0.01;
	final static double ffar = 1000;
	final static double fov = Math.toRadians(45);
	final static double ffovrad = 1/Math.tan((fov/2));
	
	double x;
	double y;
	double z;
	
	double height;
	double width;
	
	MatrixMultiplication mm = new MatrixMultiplication();

	static final Point3D origin = new Point3D(0,0,0);
	
	public Point3D(double xx, double yy, double zz, double h, double w) {
		x = xx;
		y = yy;
		z = zz;
		
		height = h;
		width = w;
		
	}

	public Point3D(double xx, double yy, double zz){
		x = xx;
		y = yy;
		z = zz;
	}
	
	public void rotateX(double a) {
		double angle = Math.toRadians(a);
		double[][] input = {
				{x},
				{y},
				{z}
		};
		double[][] rotationX = {
				{1,0,0},
				{0,Math.cos(angle), -Math.sin(angle)},
				{0,Math.sin(angle), Math.cos(angle)},
		};
		double[][] ret1 = mm.doublematmul(rotationX, input);
		x = ret1[0][0];
		y = ret1[1][0];
		z = ret1[2][0];
	}
	
	public void rotateY(double a) {
		double angle = Math.toRadians(a);
		double[][] input = {
				{x},
				{y},
				{z}
		};
		double[][] rotationY = {
				{Math.cos(angle),0, -Math.sin(angle)},
				{0,1,0},
				{Math.sin(angle),0, Math.cos(angle)},
		};
		double[][] ret1 = mm.doublematmul(rotationY, input);
		x = ret1[0][0];
		y = ret1[1][0];
		z = ret1[2][0];
	}
	
	public void rotateZ(double a) {
		double angle = Math.toRadians(a);
		double[][] input = {
				{x},
				{y},
				{z}
		};
		double[][] rotationZ = {
				{Math.cos(angle), -Math.sin(angle),0},
				{Math.sin(angle), Math.cos(angle),0},
				{0,0,1}
		};
		double[][] ret1 = mm.doublematmul(rotationZ, input);
		x = ret1[0][0];
		y = ret1[1][0];
		z = ret1[2][0];
	}
	
	public Point2D getXY() {
		Point2D ret = new Point2D.Double(0,0);
		double aspectRatio = (height+10)/(width+10);
		
		double[][] projection = {
				{aspectRatio*ffovrad, 		0, 						    0, 0},
				{0					, ffovrad, 		 			   	    0, 0},
				{0					, 		0, 			ffar/(ffar-fnear), 1},
				{0					,	    0, (-ffar*fnear)/(ffar-fnear), 0}
		};
		double[][] input = {
				{x},
				{y},
				{z},
				{1}
		};
		double[][] projected = mm.doublematmul(projection, input);
		if(projected[3][0]!=0) {
			projected[0][0] /= projected[3][0];
			projected[1][0] /= projected[3][0];
		}
		
		double retx = projected[0][0];
		double rety = projected[1][0];
		
		//scaling
		retx*=1;
		rety*=1;
		
		ret.setLocation(retx, rety);
		return ret;
	}
	
	public static double dotProduct(Point3D p1, Point3D p2) {
		return (p1.x*p2.x) + (p1.y*p2.y) + (p1.z*p2.z);
	}
	
	public static double dist(double x, double y, double z, double x1, double y1, double z1) {
		double dx = x1-x;
		double dy = y1-y;
		double dz = z1-z;
		return Math.pow(dx*dx + dy*dy + dz*dz, 0.5);
	}

	public static double dist(Point3D p1, Point3D p2) {
		double dx = p2.x - p1.x;
		double dy = p2.y - p2.y;
		double dz = p2.z - p2.z;
		return Math.pow(dx*dx + dy*dy + dz*dz, 0.5);
	}

	public Point3D subtract(Point3D p) {
		return new Point3D(x - p.x, y - p.y, z - p.z, height, width);
	}

	public double getMag(){
		return Math.pow(x*x + y*y + z*z, 0.5);
		// return z;
	}

	public Point3D normal(){
		double mag = getMag();
		return new Point3D(x/mag, y/mag, z/mag, height, width);
	}

	public Point3D multiply(double d){
		return new Point3D(x*d, y*d, z*d, height, width);
	}

	public Point3D multiply(Point3D p){
		return new Point3D(x*p.x, y*p.y, z*p.z, height, width);
	}

	public String toString(){
		return x+", "+y+", "+z;
	}

	public Point3D clone(){
		return new Point3D(x,y,z,height,width);
	}

	public Point3D cross(Point3D p2){
		Point3D ret = new Point3D(0,0,0,height, width);
		ret.x = (y*p2.z - p2.y*z);
		ret.y = -(x*p2.z - p2.x*z);
		ret.z = (x*p2.y - p2.x*y);
		return ret;
	}
}
