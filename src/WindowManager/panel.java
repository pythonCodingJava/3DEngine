package WindowManager;

import javax.swing.*;
import renderer.Graphics3D;
import renderer.Plane3D;
import renderer.Point3D;
import renderer.Triangle;
import renderer.object;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
	int vel;
	Point3D camera;
	
	int[] orientoff;
	double mag = 1;
	Point3D orientation;

	Point3D light;
	Point3D lightdir;

	private double fps = 0;
	private final int cap = 120;
	private long lastTime = 0;

	final ExecutorService exec;

	Timer t = new Timer(10, new ActionListener(){
		public void actionPerformed(ActionEvent e){
			// Capping at 60 fps.
			while(System.currentTimeMillis() - lastTime <= 1000/cap){}

			putLight();
			// Calculating fps.
			long t = System.currentTimeMillis();
			if(lastTime - t != 0)
				fps = 1000/(t - lastTime);
			mainClass.f.setTitle("FPS : "+fps);

			fheight = mainClass.f.getHeight();
			fwidth = mainClass.f.getWidth();

			double velocity = vel*(double)(cap*(t-lastTime))/1000;
			Point3D off = new Point3D(offset[0]*velocity, offset[1]*velocity, offset[2]*velocity);
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

			if(!dealt){
				Triangle triangle = o.triangles.get(selectedIdx);
				if(del){
					o.triangles.remove(selectedIdx);
					boolean[] exists = new boolean[3];
					for(int i = 0; i<o.triangles.size(); i++){
						for(int j = 0; j<3; j++){
							for(int k = 0; k<3; k++){
								if(o.triangles.get(i).points[j].equals(triangle.points[k])) {
									exists[k] = true;
								}
							}
						}	
					}
					for(int i = 0; i<3; i++){
						if(!exists[i]) {
							o.points.remove(triangle.points[i]);
						}
					}
					o.reconfigure();
					// System.out.println(o.points.size());
					// System.exit(0);
					dealt = true;
				}else{
					double len = -o.triangles.get(selectedIdx).getAvgLength();
					Point3D point = triangle.normalize().normal().multiply(len).subtract(triangle.getCentroid().multiply(-1));
					o.points.add(point);
					int[] id = triangle.getIds();
					int i = o.points.size()-1;
					int size = o.triangles.size();
					o.triangles.set(selectedIdx, new Triangle(id[0],id[1],i,o.points,selectedIdx));
					o.triangles.add(new Triangle(id[2],id[0],i,o.points,size));
					o.triangles.add(new Triangle(id[1],id[2],i,o.points,size+1));
					dealt = true;
				}
			}

			repaint();
			lastTime = t;
		}
	});

	double z = 00;
	int nthreads = 3;

	private Graphics2D g2d;

	int res = 50;
	int n = 30;

	private int selectedIdx = 0;
	private boolean pressed = false;
	private boolean dealt = true;
	boolean del = false;

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
		o = object.plane(p.x-res*n/2,p.y-res*n/2,p.z,res*n,res*n,res,h,w);
		double x = 0;
		double y = 0;
		for(Point3D point : o.points){
			x = (point.x+350)/50;
			y = (point.y+350)/50;
			point.z += Perlin.PerlinNoise(x, y)*70 + Perlin.PerlinNoise((point.x+289)/120, (point.y+123)/120)*90;
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
		vel = 1;

		orientoff = new int[3];
		
		light = new Point3D(350,350,-180);
		lightdir = new Point3D(1,-1,0).normal();
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				pressed = true;
			}
		});

		t.start();
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(5));
		g2d.translate(mainClass.f.getWidth()/2, mainClass.f.getHeight()/2);
		g2d.setColor(Color.WHITE);
		g3d.g = g2d;
		
		//o = o.rotateX(0.5);		//Drawing a plane
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

		Plane3D[] plane = {new Plane3D(new Point3D(0,0,0.01,fheight,fwidth), new Point3D(0,0,1,fheight,fwidth))};
		object toDraw = o.allInOne(camera, orientation,	plane, new Point3D(0, 0, 50, fheight, fwidth));
		
		if(pressed && getMousePosition() != null){
			Point mouse = getMousePosition();
			// Point3D p = new Point3D((-mouse.x+fwidth/2) * (double)(2*Point3D.fnear/(fwidth*Math.pow(0.5,3))), (-mouse.y+fheight/2) * (double)(2*Point3D.fnear/(fheight*Math.pow(0.5,3))), 0.01);
			Point3D p = new Point3D(fwidth/2 - mouse.x, fheight/2 - mouse.y, fwidth*Math.tan(Point3D.fov/2)/2);
			double z = -99999;
			for(Triangle t : toDraw.triangles){
				Point3D interP = t.getPlane().linePlaneIntersec(Point3D.origin, p);
				if(t.contains(interP) && interP.z > z){
					selectedIdx = t.idx;
				}
			}
			pressed = false;
			dealt = false;
		}

		for(int i = 0; i<toDraw.triangles.size(); i++){
			Triangle t = toDraw.triangles.get(i);
			Point3D normal = o.triangles.get(t.idx).normalize().normal();
			Point3D centroid = o.triangles.get(t.idx).getCentroid();
			double dot = Point3D.dotProduct(centroid.subtract(light).normal(), normal);
			// double dot = Point3D.dotProduct(lightdir, normal);
			double dist = Point3D.dist(centroid, light);
			double rad = 5000;
			if(dist > rad) dist = rad;
			double sub = map(dist, 0, rad, 0, 255);
			int color = (int)(map(dot, -1, 1, 0, 255) - sub);
			if(color < 0) color = 0;
			if(t.idx != selectedIdx) g3d.setColor(new Color(0xceeb));
			else g3d.setColor(Color.PINK);
			g3d.fillTriangle(t, 255-color, 1);
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
