package net.mtsoftware;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;


public class Main {
	public static void main(String[] args) {
		
		// create the main frame window
		JFrame frame = new JFrame("Handmade Hero");
		
		// center the main frame window
		frame.setLocationRelativeTo(null);

		// set initial window size to be 300x100
		frame.setSize(300, 100);
		
		// set custom content pane
		frame.setContentPane(new MyComponent());
		
		// show it
		frame.setVisible(true);
	}

	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public Main() {
		super();
	}
	
    static class MyComponent extends JComponent {
    	
    	static Color color = Color.white;
    	
    	public void paint(Graphics g) {
    		
    		// alternate between blackness and whiteness
    		if( color==Color.white ) {
    			color = Color.black;
    		} else {
    			color = Color.white;
    		}
    		
    		// fill the component contents with the chosen color
    		g.setColor(color);
    		g.fillRect(0, 0, getWidth(), getHeight());
	    }
	}
}
