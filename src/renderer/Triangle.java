package renderer;

import java.awt.Color;

import java.util.ArrayList;

public class Triangle {
	
	int idx = 0;

	int i;
	int i2;
	int i3;
	Point3D[] points = new Point3D[3];
	Color color = Color.white;//*/new Color(0.247059f, 0.282353f, 0.800000f);
	double avgz;
	
	public Triangle(int i1, int i2, int i3, Point3D[] p) {
		i = i1;
		this.i2 = i2;
		this.i3 = i3;
		points[0] = p[i1];
		points[1] = p[i2];
		points[2] = p[i3];
		// avgOut();
	}
	
	public Triangle(int i1,int i2,int i3, ArrayList<Point3D> p) {
		i = i1;
		this.i2 = i2;
		this.i3 = i3;
		points[0] = p.get(i1);
		points[1] = p.get(i2);
		points[2] = p.get(i3);
		// avgOut();
	}
	
	public Triangle(Point3D p1,Point3D p2,Point3D p3) {
		points[0] = p1;
		points[1] = p2;
		points[2] = p3;
		// avgOut();
	}

	public void avgOut(){
		avgz = (points[0].z+points[1].z+points[2].z)/3;
	}

	
	public Point3D normalize() {
		Point3D ret = new Point3D(0,0,0,points[0].height, points[0].width);
		
		double x1 = points[0].x-points[1].x;
		double y1 = points[0].y-points[1].y;
		double z1 = points[0].z-points[1].z;
		
		double x2 = points[2].x-points[1].x;
		double y2 = points[2].y-points[1].y;
		double z2 = points[2].z-points[1].z;
		
		ret.x = (y1*z2 - y2*z1);
		ret.y = -(x1*z2 - x2*z1);
		ret.z = (x1*y2 - x2*y1);

		return ret;
	}

	public double getMeasure(){
		return getCentroid().getMag();
	}
	
	public void clip(Plane3D plane, Point3D ref, ArrayList<Triangle> ret){
		// ret.add(this);
		ArrayList<Point3D> inside = new ArrayList<>();
		ArrayList<Point3D> outside = new ArrayList<>();

		double refference = plane.getPower(ref);
		for(Point3D p : points){
			double dot = plane.getPower(p);
			if(refference*dot >= 0) inside.add(p);
			else outside.add(p);
		}
		
		if(inside.size() == 3) {
			ret.add(this);
		}
		if(inside.size() == 1) {	
			Point3D extra_p1 = plane.linePlaneIntersec(inside.get(0), outside.get(0));
			Point3D extra_p2 = plane.linePlaneIntersec(inside.get(0), outside.get(1));
			Triangle toadd = new Triangle(inside.get(0), extra_p1, extra_p2);
			if(Point3D.dotProduct(toadd.getCentroid(), toadd.normalize()) < 0){
				toadd = new Triangle(inside.get(0), extra_p2, extra_p1);
			}
			ret.add(toadd); //Note : the i values are to be determined later.
		}

		if(inside.size() == 2) {
			Point3D extra_p1 = plane.linePlaneIntersec(inside.get(0), outside.get(0));
			Point3D extra_p2 = plane.linePlaneIntersec(inside.get(1), outside.get(0));

			Triangle t1 = new Triangle(extra_p1,inside.get(0),  extra_p2);
			if(Point3D.dotProduct(t1.getCentroid(), t1.normalize()) < 0){
				t1 = new Triangle(extra_p2,inside.get(0),  extra_p1);
			}

			Triangle t2 = new Triangle(inside.get(1),extra_p2, inside.get(0));
			if(Point3D.dotProduct(t2.getCentroid(), t2.normalize()) < 0){
				t2 = new Triangle(inside.get(0),extra_p2, inside.get(1));
			}

			ret.add(t1);
			ret.add(t2);
		}
	}

	public Point3D getCentroid(){
		double x = (points[0].x + points[1].x + points[2].x)/3;
		double y = (points[0].y + points[1].y + points[2].y)/3;
		double z = (points[0].z + points[1].z + points[2].z)/3;
		return new Point3D(x, y, z,points[0].height, points[0].width);
	}

	public boolean intersects(Point3D p1, Point3D p2, Point3D center){
		Point3D inplane = new Plane3D(normalize().normal(), points[0]).linePlaneIntersec(p1, p2);
		for(Point3D p : points){
			if(p.x == inplane.x && p.y == inplane.y && p.z == inplane.z) return false;
		}
		if(inplane.subtract(p1).getMag() > p1.subtract(p2).getMag() || inplane.subtract(p2).getMag() > p1.subtract(p2).getMag())
			return false;
		return contains(inplane);
	}

	public boolean contains(Point3D p){
		double a1 = area(points[0], p, points[1]);
		double a2 = area(points[1], p, points[2]);
		double a3 = area(points[2], p, points[0]);
		double a = area(points[0], points[1], points[2]);
		return (int)a == (int)(a1+a2+a3);
	}

	public static double area(Point3D p1, Point3D p2, Point3D p3) {
		return 0.5 * (new Triangle(p1,p2,p3).normalize().getMag());
	}

	public Point3D[] getMPs(){
		Point3D p1 = new Point3D((points[0].x+points[1].x)/2, (points[0].y+points[1].y)/2, (points[0].z+points[1].z)/2);
		Point3D p2 = new Point3D((points[1].x+points[2].x)/2, (points[1].y+points[2].y)/2, (points[1].z+points[2].z)/2);
		Point3D p3 = new Point3D((points[2].x+points[0].x)/2, (points[2].y+points[0].y)/2, (points[2].z+points[0].z)/2);
		return new Point3D[]{p1,p2,p3};
	}

	public Triangle clone(){
		Triangle ret = new Triangle(points[0].clone(), points[1].clone(),points[2].clone());
		ret.i = i;
		ret.i2 = i2;
		ret.i3 = i3;
		return ret;
	}

}
