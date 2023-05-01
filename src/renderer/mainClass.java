package renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class mainClass{

	static JFrame f = new JFrame("Preview");
	static panel panel;

	static String filename = "";
	static double factor = 1;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length != 0) {
			filename = args[0];
			factor = Double.parseDouble(args[1]);
		}

		Dimension size = /*new Dimension(1000,500);//*/Toolkit.getDefaultToolkit().getScreenSize();
		f.setSize(size.height, size.height);
		f.setResizable(false);
		f.setLocation(0,0);
		//it is supposed to be cuz undercoated is cooooooooooooooooooooooool
		//f.setUndecorated(true);
		f.setFocusable(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if(filename.equals("")) panel = new panel(f.getHeight(), f.getWidth());
		else panel = new panel(f.getHeight(), f.getWidth(), filename);
		f.setContentPane(panel);
		f.getContentPane().setBackground(new Color(18,18,18));//new Color(0x87ceeb));

		f.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e){

				// For translation of camera.
				if(e.getKeyCode() == KeyEvent.VK_W) panel.offset[2] = 1;
				if(e.getKeyCode() == KeyEvent.VK_S) panel.offset[2] = -1;
				if(e.getKeyCode() == KeyEvent.VK_A) panel.offset[0] = 1;
				if(e.getKeyCode() == KeyEvent.VK_D) panel.offset[0] = -1;
				if(e.getKeyCode() == KeyEvent.VK_Q) panel.offset[1] = 1;
				if(e.getKeyCode() == KeyEvent.VK_E) panel.offset[1] = -1;

				// Setting the light source.
				if(e.getKeyCode() == KeyEvent.VK_L) panel.putLight();

				//For orienting the camera
				if(e.getKeyCode() == KeyEvent.VK_UP) panel.orientoff[0] = 1;
				if(e.getKeyCode() == KeyEvent.VK_DOWN) panel.orientoff[0] = -1;
				if(e.getKeyCode() == KeyEvent.VK_RIGHT) panel.orientoff[1] = 1;
				if(e.getKeyCode() == KeyEvent.VK_LEFT) panel.orientoff[1] = -1;
			}

			public void keyReleased(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_W) panel.offset[2] = 0;
				if(e.getKeyCode() == KeyEvent.VK_S) panel.offset[2] = 0;
				if(e.getKeyCode() == KeyEvent.VK_A) panel.offset[0] = 0;
				if(e.getKeyCode() == KeyEvent.VK_D) panel.offset[0] = 0;
				if(e.getKeyCode() == KeyEvent.VK_Q) panel.offset[1] = 0;
				if(e.getKeyCode() == KeyEvent.VK_E) panel.offset[1] = 0;

				if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) panel.orientoff[0] = 0;
				if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) panel.orientoff[1] = 0;
			}
		});

		f.setVisible(true);
	}
}
