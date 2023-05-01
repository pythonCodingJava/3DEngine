package renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class panel extends JPanel{

	final boolean plane = false;

	int fheight;
	int fwidth;

	object o;
	Graphics3D g3d;

	int[] offset;
	double[] vel;
	Point3D camera;
	
	int[] orientoff;
	double mag = 1;
	Point3D orientation;

	Point3D light;
	Point3D lightdir;

	private double fps = 0;
	private long lastTime = 0;

	final ExecutorService exec;

	Timer t = new Timer(10, new ActionListener(){
		public void actionPerformed(ActionEvent e){
			// Capping at 60 fps.
			while(System.currentTimeMillis() - lastTime <= 1000/60){}

			// Calculating fps.
			long t = System.currentTimeMillis();
			if(lastTime - t != 0)
				fps = 1000/(t - lastTime);
			mainClass.f.setTitle("FPS : "+fps);

			fheight = mainClass.f.getHeight();
			fwidth = mainClass.f.getWidth();

			Point3D off = new Point3D(offset[0]*vel[0], offset[1]*vel[1], offset[2]*vel[2]);
			Point3D pointing = new Point3D(0,0,1);
			Point3D up = new Point3D(0,-1,0);
			pointing.rotateX(-orientation.x);
			pointing.rotateY(-orientation.y);
			pointing.rotateZ(-orientation.z);
			up.rotateX(-orientation.x);
			up.rotateY(-orientation.y);
			up.rotateZ(-orientation.z);
			camera = camera.subtract(pointing.multiply(off.z).multiply(-1)); // along z
			camera = camera.subtract(pointing.cross(up).normal().multiply(off.x).multiply(-1)); // along x
			camera = camera.subtract(up.multiply(off.y).multiply(-1)); // along y

			//Orientation
			orientation= orientation.subtract(new Point3D(-orientoff[0]*mag, -orientoff[1]*mag, -orientoff[2]*mag));
			camera.rotateX(-orientoff[0]*mag);
			camera.rotateY(-orientoff[1]*mag);
			camera.rotateZ(-orientoff[2]*mag);

			repaint();
			lastTime = t;
		}
	});

	double z = 00;
	int nthreads = 3;

	private Graphics2D g2d;

	public panel(int h, int w){
		exec = Executors.newFixedThreadPool(nthreads);
		fheight = h;
		fwidth = w;
		g3d = new Graphics3D(h,w);
		o = new object();
		
		// Loaded object test
		// o = object.loadFromFile("/home/kartik/projects/3DRendering/res/teapot.obj", h, w);

		// Cube
		// o = object.cube(0,0,0,50,50,50,h,w);
	
		//Plane
		Point3D p = new Point3D(0,0,0);
		o = object.plane(p.x-250,p.y-250,p.z,500,500,20,h,w);
		double x = 0;
		double y = 0;
		for(Point3D point : o.points){
			x = (point.x+350)/50;
			y = (point.y+350)/50;
			point.z += Perlin.PerlinNoise(x, y)*50;
		}
		o = o.rotateX(90);

		init();
	}

	public panel(int h, int w, String file){
		exec = Executors.newFixedThreadPool(nthreads);
		fheight = h;
		fwidth = w;
		g3d = new Graphics3D(h,w);
		o = object.loadFromFile(file, h, w);
		init();
	}

	public void init(){
		camera = new Point3D(0,0,0);
		orientation = new Point3D(0,0,0);
		offset = new int[3];
		vel = new double[]{2,2,2};

		orientoff = new int[3];
		
		light = new Point3D(350,350,-180);
		lightdir = new Point3D(1,-1,0).normal();
		t.start();
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(1));
		g2d.translate(mainClass.f.getWidth()/2, mainClass.f.getHeight()/2);
		g2d.setColor(Color.WHITE);
		g3d.g = g2d;
		
		//Drawing a plane
		if(plane){
			double res = 50;
			int n = 20;
			Point3D p = new Point3D(-n*res/2 ,0,-n*res/2);
			p = p.subtract(camera);
			g3d.setColor(new Color(230,230,230));
			for(int i = 0; i<=n; i++){
				double z2 = p.z + n*res;
				double z1 = p.z;
				g3d.draw3DLine(p.x + i*res,p.y,z1,p.x + (i)*res,p.y,z2, orientation, camera);
			}

			for(int j = 0; j<=n; j++){
				g3d.draw3DLine(p.x,p.y,p.z + j*res,p.x + n*res,p.y,p.z + j*res, orientation, camera);
			}
		}

		object toDraw = o.allInOne(camera, orientation, new Point3D(0,0,0.01,fheight,fwidth), new Point3D(0,0,1,fheight,fwidth), new Point3D(0, 0, 50, fheight, fwidth));
		for(int i = 0; i<toDraw.triangles.size(); i++){
			Triangle t = toDraw.triangles.get(i);
			Point3D normal = o.triangles.get(t.idx).normalize().normal();
			Point3D centroid = o.triangles.get(t.idx).getCentroid();
			double dot = Point3D.dotProduct(centroid.subtract(light).normal(), normal);
			// double dot = Point3D.dotProduct(lightdir, normal);
			double dist = Point3D.dist(centroid, light);
			double rad = 1000;
			if(dist > rad) dist = rad;
			double sub = map(dist, 0, rad, 0, 255);
			int color = (int)(map(dot, -1, 1, 0, 255) - sub);
			if(color < 0) color = 0;
			g3d.setColor(new Color(color, color, color));
			g3d.fillTriangle(t, 1);
		}
		


		// object toDraw = o.translate(camera);
		// if(orientation.x != 0) toDraw = toDraw.rotateX(orientation.x);
		// if(orientation.y != 0) toDraw = toDraw.rotateY(orientation.y);
		// if(orientation.z != 0) toDraw = toDraw.rotateZ(orientation.z);
		// toDraw = toDraw.clip(new Point3D(0,0,0.01,fheight,fwidth), new Point3D(0,0,1,fheight,fwidth), new Point3D(0, 0, 50, fheight, fwidth));
		// toDraw.arrangeTriangle();

		// double scale = 1;
		// for(Triangle t : toDraw.triangles){
		// 	Point3D normal = t.normalize().normal();
		// 	if(Point3D.dotProduct(t.points[0], normal) > 0 && 
		// 	Point3D.dotProduct(t.points[1], normal) > 0 &&
		// 	Point3D.dotProduct(t.points[2], normal) > 0){
		// 		double dot = Point3D.dotProduct(lightdir.normal(), normal);
				// Point3D centroid = t.getCentroid();
				// double dist = Point3D.dist(centroid, light);
				// double rad = 5000;
				// if(dist > rad) dist = rad;
		// 		double sub = 0;//map(dist, 0, rad, 0, 255);
		// 		int color = (int)(map(dot, -1, 1, 0, 255) - sub);
		// 		if(color < 0) color = 0;
		// 		g3d.setColor(new Color(color, color, color));
		// 		g3d.fillTriangle(t, scale);
		// 	}
		// }

		z += 1;
		if(z >=360) z = 0;

		g2d.setColor(Color.WHITE);
		g2d.drawString((int)camera.x+", "+(int)camera.y+", "+(int)camera.z, mainClass.f.getWidth()/2 - 100, mainClass.f.getHeight()/2 - g2d.getFont().getSize() - 50);

	}

	public boolean checkIntersection(Triangle t, ArrayList<Triangle> triangles, Point3D center){
		for(Point3D p : t.points){
			for(Triangle triangle : triangles){
				boolean b = triangle.intersects(new Point3D(0,0,0), p, center);
				if(b) return true;
			}
		}
		return false;
	}

	public double map(double x, double in_min, double in_max, double out_min, double out_max)
	{
		  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public void putLight(){
		Point3D pointing = new Point3D(0,0,1);
		pointing.rotateX(-orientation.x);
		pointing.rotateY(-orientation.y);
		pointing.rotateZ(-orientation.z);
		lightdir = new Point3D(pointing.x, pointing.y, pointing.z);

		light = new Point3D(camera.x, camera.y, camera.z);
	}
}
