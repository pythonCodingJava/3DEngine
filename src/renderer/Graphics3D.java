package renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class Graphics3D {

	private double height;
	private double width;
	
	Graphics2D g;
	
	private Color c = Color.black;
	private float stroke = 1f;
	
	private double[][] depthBuffer;

	public Graphics3D(int h, int w) {
		height = h;
		width = w;

		depthBuffer = new double[h][w];
	}
	
	public void setColor(Color color) {
		c = color;
		g.setColor(c);
	}
	
	public void setStroke(float s) {
		stroke = s;
		g.setStroke(new BasicStroke(stroke));
	}
	
	public void setGraphics(Graphics2D g2d) {
		g = g2d;
	}
	
	public void draw3DLine(Point3D p1, Point3D p2) {
		Point2D p1t = p1.getXY();
		Point2D p2t = p2.getXY();
		
		g.draw(new Line2D.Double(p1t.getX(), p1t.getY(), p2t.getX(), p2t.getY()));
	}
	
	public void draw3DLine(double x1, double y1, double z1, double x2, double y2, double z2, Point3D orientation, Point3D camera) {
		Point3D p1 = new Point3D(x1,y1,z1,height,width);
		p1.rotateX(orientation.x);
		p1.rotateY(orientation.y);
		p1.rotateZ(orientation.z);

		Point3D p2 = new Point3D(x2,y2,z2,height,width);
		p2.rotateX(orientation.x);
		p2.rotateY(orientation.y);
		p2.rotateZ(orientation.z);

		if(p1.z < 0.1 && p2.z < 0.1) return;
		
		if(p1.z <= 0.1) {
			double z = 0.1;
			double lambda = (z-p1.z)/(p2.z-p1.z);
			p1.z = z;
			p1.x = lambda*(p2.x-p1.x) + p1.x;
			p1.y = lambda*(p2.y-p1.y) + p1.y;
		}
		if(p2.z <= 0.1) {
			double z = 0.1;
			double lambda = (z-p2.z)/(p1.z-p2.z);
			p2.z = z;
			p2.x = lambda*(p1.x-p2.x) + p2.x;
			p2.y = lambda*(p1.y-p2.y) + p2.y;
		}

		Point2D p1t = p1.getXY();
		Point2D p2t = p2.getXY();
		
		g.draw(new Line2D.Double(p1t.getX(), p1t.getY(), p2t.getX(), p2t.getY()));
	}

	public void draw3Dpoint(Point3D p) {
		Point2D translated = p.getXY();
		g.draw(new Line2D.Double(translated.getX(), translated.getY(), translated.getX(), translated.getY()));
	}
	
	public void draw3Dpoint(double xx, double yy, double zz) {
		Point3D p = new Point3D(xx,yy,zz,height,width);
		Point2D translated = p.getXY();
		g.draw(new Line2D.Double(translated.getX(), translated.getY(), translated.getX(), translated.getY()));
	}
	
	public void drawTriangle(Triangle t, double scale) {
		Point2D p1 = t.points[0].getXY();
		Point2D p2 = t.points[1].getXY();
		Point2D p3 = t.points[2].getXY();

		p1.setLocation(p1.getX()*scale,p1.getY()*scale);
		p2.setLocation(p2.getX()*scale,p2.getY()*scale);
		p3.setLocation(p3.getX()*scale,p3.getY()*scale);
		
		g.draw(new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY()));
		g.draw(new Line2D.Double(p2.getX(), p2.getY(), p3.getX(), p3.getY()));
		g.draw(new Line2D.Double(p1.getX(), p1.getY(), p3.getX(), p3.getY()));
	}
	
	public void fillTriangle(Triangle t, double scale) {
		Point2D p1 = t.points[0].getXY();
		Point2D p2 = t.points[1].getXY();
		Point2D p3 = t.points[2].getXY();
		
		p1.setLocation(p1.getX()*scale,p1.getY()*scale);
		p2.setLocation(p2.getX()*scale,p2.getY()*scale);
		p3.setLocation(p3.getX()*scale,p3.getY()*scale);
		
		Path2D p = new Path2D.Double();
		p.moveTo(p1.getX(), p1.getY());
		p.lineTo(p2.getX(), p2.getY());
		p.lineTo(p3.getX(), p3.getY());
		p.lineTo(p1.getX(), p1.getY());
		
		g.fill(p);
	}
	
}
