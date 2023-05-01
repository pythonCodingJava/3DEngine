package renderer;

public class Plane3D {
    
    Point3D plane_n;
    Point3D plane_p;

    public Plane3D(Point3D n, Point3D p){
        plane_n = n.clone();
        plane_p = p.clone();
    }

    public Plane3D(Point3D a, Point3D b, Point3D c){
        plane_n = new Triangle(a,b,c).normalize().normal();
        plane_p = a;
    }

    public double getPower(Point3D p){
        return Point3D.dotProduct(plane_n, p) - Point3D.dotProduct(plane_n, plane_p);
    }

    public Point3D linePlaneIntersec(Point3D p1, Point3D p2){
        Point3D ret = new Point3D(0,0,0,p1.height,p1.width);
		
		double d = Point3D.dotProduct(plane_p, plane_n);

		Point3D p = p2.subtract(p1);
		double lambda = (d - Point3D.dotProduct(p1, plane_n))/Point3D.dotProduct(plane_n, p);

		ret.x = lambda*(p.x) + p1.x;
		ret.y = lambda*(p.y) + p1.y;
		ret.z = lambda*(p.z) + p1.z;

		return ret;
    }
}
