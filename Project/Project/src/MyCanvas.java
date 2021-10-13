import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/*
 * An extension of the Java Swing class Canvas. We extend it because we want
 * it to behave in a very special way: it should (1) draw our art and (2)
 * tell the application when the user clicks on it.
 * 
 * This canvas draws an image based on three "colour expressions".
 */
public class MyCanvas extends Canvas {

	/*
	 * We have to add this here to make Java happy.
	 */
	private static final long serialVersionUID = 7570507763686587998L;

	/*
	 * The size of our canvas.
	 */
	private static final int SIZE = 200;

	/*
	 * Each canvas needs to know its number, so that when it is clicked, it can
	 * tell our application who it is.
	 */
	protected final int id;

	/*
	 * The framework (in other words, our application) to which this canvas
	 * belongs. We use this to inform the application that the user has clicked
	 * this canvas.
	 */
	protected final Framework framework;
	
	/*
	 * The three expressions for red, blue and green that each instance of MyCanvas
	 * will have (uniquely). These will be used to create an image in realtime.
	 */
	private Expression redExpression;
	
	private Expression blueExpression;
	
	private Expression greenExpression;

	/*
	 * To be able to react to user clicks, this canvas needs a "listener". We
	 * define it here, once.
	 */
	protected MouseListener clickListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			framework.clicked(id);
		}
	};

	/*
	 * Construct an instance of MyCanvas. Setting the id, framework and size, and also
	 * getting the colour expressions for this canvas, based on a random method.
	 */
	public MyCanvas(int id, Framework framework, Random rng) {
		this.id = id;
		this.framework = framework;
		reset(rng);
		Dimension size = new Dimension(SIZE, SIZE);
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
		setSize(size);
		addMouseListener(clickListener);
	}

	/*
	 * Construct an instance of MyCanvas. It is almost the same as the previous
	 * constructor, except that it sets its colour by making a slight variation
	 * of the colour expressions of the "original" parameter.
	 */
	public MyCanvas(int id, Framework framework, MyCanvas original, Random rng) {
		this.id = id;
		this.framework = framework;
		reset(original, rng);
		Dimension size = new Dimension(SIZE, SIZE);
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
		setSize(size);
		addMouseListener(clickListener);
	}
	
	/*
	 * The getter methods for each colour expression, used when an instance
	 * of MyCanvas needs to create a variation of the "original" canvas' 
	 * expressions.
	 */
	public Expression getRedExp() {
		return redExpression;
	}
	
	public Expression getBlueExp() {
		return blueExpression;
	}
	
	public Expression getGreenExp() {
		return greenExpression;
	}
	
	public void setRedExp(Expression red) {
		redExpression = red;
	}
	
	public void setGreenExp(Expression green) {
		greenExpression = green;
	}
	
	public void setBlueExp(Expression blue) {
		blueExpression = blue;
	}

	/*
	 * Reset the colour expressions to random values.
	 */
	public void reset(Random rng) {
		redExpression = Expression.random(framework.getMaxDepth(), rng);
		greenExpression = Expression.random(framework.getMaxDepth(), rng);
		blueExpression = Expression.random(framework.getMaxDepth(), rng);
	}

	/*
	 * Reset the colour expressions of this canvas by making variations based 
	 * on the "original" canvas' expressions.
	 */
	public void reset(MyCanvas original, Random rng) {
		this.redExpression = original.getRedExp().variation(framework.getMaxDepth(), rng);
		this.blueExpression = original.getBlueExp().variation(framework.getMaxDepth(), rng);
		this.greenExpression = original.getGreenExp().variation(framework.getMaxDepth(), rng);
	}

	/*
	 * Make a copy of the colour expressions of another artwork.
	 */
	public void copy(MyCanvas original) {
		redExpression = original.getRedExp();
		greenExpression = original.getGreenExp();
		blueExpression = original.getBlueExp();
	}

	/*
	 * Paint the artwork on the canvas, based on the colour expressions of each canvas.
	 */
	@Override
	public void paint(Graphics g) {
		double r;
		double gr;
		double b;
		Color pix = null;
		
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				double x = i * 1.0/SIZE;
				double y = j * 1.0/SIZE;
				r = Math.abs(redExpression.evaluate(x, y));
				gr = Math.abs(greenExpression.evaluate(x, y));
				b = Math.abs(blueExpression.evaluate(x, y));
				
				if(framework.isHSV) {
					pix = Color.getHSBColor((float)r, (float)gr, (float)b);
				} else {
					pix = new Color((float)r, (float)gr, (float)b);
				}
				g.setColor(pix);
				g.drawLine(i, j, i, j);
				
			}
		}
	}

	/*
	 * Save this artwork to a JPEG file.
	 */
	public void saveTo(String filename) {
		BufferedImage bi = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
		paint(g2);
		g2.dispose();
		try {
			ImageIO.write(bi, "jpg", new File(filename));
		} catch (IOException x) {
			JOptionPane.showMessageDialog(framework,
				"Problem saving file: " + x.getMessage(),
				"Save error",
				JOptionPane.ERROR_MESSAGE);
		}
	}

}