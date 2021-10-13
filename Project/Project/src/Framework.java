import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Framework extends JFrame implements ActionListener {

	/*
	 * We have to add this here to make Java happy.
	 */
	private static final long serialVersionUID = 2180515451827694740L;

	/*
	 * The main panel that holds the entire application.
	 */
	private JPanel mainPanel;

	/*
	 * The subpanel of the main panel for holding the 2x2 canvases.
	 */
	private JPanel gridPanel;

	/*
	 * The subpanel of the main panel (at the bottom of the main panel) for
	 * holding my buttons.
	 */
	private JPanel buttonPanel;

	/*
	 * The button to press to save a canvas.
	 */
	private JButton saveButton;

	/*
	 * The button to press to quit the application.
	 */
	private JButton quitButton;
	
	/*
	 * The button to press to see the previous original canvas, up to
	 * a maximum of 10 previous canvases.
	 */
	private JButton previousButton;
	
	/*
	 * The button to press to change the image mode from RGB to HSV or vice versa
	 */
	private JButton colorTypeButton;

	/*
	 * A file chooser dialog. We create it here -- once -- and reuse it every
	 * time we save the file. The main reason is that file choosers are quit
	 * slow to create.
	 */
	private final JFileChooser fileChooser = new JFileChooser();

	/*
	 * An array that hold the actual canvases. In this case, the top left
	 * (canvas[4]) is the "original".
	 */
	private MyCanvas[] canvas = new MyCanvas[9];
	
	/*
	 * An array of the previous 10 expressions, to be used by the previous method
	 */
	private Expression[][] previousMains = new Expression[10][3];

	/*
	 * The one, single random number generator used in the program.
	 */
	private Random rng = new Random(123);
	
	/*
	 * The maximum depth that colour expressions can take on using recursion.
	 */
	private final int maxDepth;
	
	/*
	 * A boolean to check whether the current image rendering mode is RGB or HSV,
	 * used when repainting the instances of MyCanvas
	 */
	protected boolean isHSV = false;
	
	/*
	 * A getter method for finding the maximum depth, used by the methods used 
	 * to get the colour expressions.
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/*
	 * Construct a Framework instance. This mainly calls the "initializeApp"
	 * method to set up the GUI.
	 */
	public Framework() {
		Scanner scan = new Scanner(System.in); 
		System.out.print("Please specify a maximum depth: ");
		maxDepth = scan.nextInt(); 
		scan.close();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initializeApp();
	}

	/*
	 * Initialize the application by creating the panels, buttons, and -- most
	 * importantly -- the canvases. At the end we invoke some Swing routines to
	 * make everything "active".
	 */
	private void initializeApp() {
		this.setTitle("Art Explorer CS144");
		buttonPanel = new JPanel();

		previousButton = new JButton("Previous");
		previousButton.addActionListener(this);
		previousButton.setActionCommand("previous");
		previousButton.setEnabled(false); //There are no "previous" artworks initially
		buttonPanel.add(previousButton);
		
		saveButton = new JButton("Save image");
		saveButton.addActionListener(this);
		saveButton.setActionCommand("save");
		buttonPanel.add(saveButton);
		
		colorTypeButton = new JButton("Switch to HSV");
		colorTypeButton.addActionListener(this);
		colorTypeButton.setActionCommand("switch");
		buttonPanel.add(colorTypeButton);

		quitButton = new JButton("Quit");
		quitButton.addActionListener(this);
		quitButton.setActionCommand("quit");
		buttonPanel.add(quitButton);
		
		

		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 3; j++) {
				previousMains[i][j] = null;
			}
		}
		canvas[4] = new MyCanvas(4, this, rng);
		for (int i = 0; i < 9; i++) {
			if(i == 4) continue;
			canvas[i] = new MyCanvas(i, this, canvas[4], rng);
		}
		gridPanel = new JPanel(new GridLayout(3, 3, 5, 5));
		for (int i = 0; i < 9; i++) {
			gridPanel.add(canvas[i]);
		}
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		mainPanel.add(gridPanel, BorderLayout.CENTER);
		setContentPane(mainPanel);

		pack();
		setLocationRelativeTo(null);
	}

	/*
	 * The actual main routine that Java executes when we run this class.
	 * Essentially, it creates an instance of this class and then asks Swing to
	 * "invoke it later".
	 */
	public static void main(final String args[]) {
		final Framework fart = new Framework();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fart.setVisible(true);
			}
		});
	}

	/*
	 * React to an "action" (for example, when the user presses a button).
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("save")) {
			saveImage();
		} else if (e.getActionCommand().equals("quit")) {
			quit();
		} else if(e.getActionCommand().equals("previous")) {
			previous();
		} else if(e.getActionCommand().equals("switch")) {
			if(isHSV) {
				isHSV = false;
				colorTypeButton.setText("Switch to HSV");
			} else {
				isHSV = true;
				colorTypeButton.setText("Switch to RGB");
			}
			
			update();
		}
	}

	/*
	 * Save the current "original" canvas (which is canvas[0]).
	 */
	private void saveImage() {
		int r = fileChooser.showSaveDialog(this);
		if (r == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			canvas[4].saveTo(file.getAbsolutePath());
		}
	}

	/*
	 * Quit the application.
	 */
	private void quit() {
		dispose();
	}
	
	private void previous() {
		canvas[4].setRedExp(previousMains[9][0]);
		canvas[4].setGreenExp(previousMains[9][1]);
		canvas[4].setBlueExp(previousMains[9][2]);
		
		for(int i = 9; i > 0; i--) {
			previousMains[i][0] = previousMains[i - 1][0];
			previousMains[i][1] = previousMains[i - 1][1];
			previousMains[i][2] = previousMains[i - 1][2];
		}
		
		previousMains[0][0] = null;
		previousMains[0][1] = null;
		previousMains[0][2] = null;
		
		if(previousMains[9][0] == null) {
			previousButton.setEnabled(false);
		}
		
		for (int i = 1; i < 9; i++) {
			if(i == 4) continue;
			canvas[i].reset(canvas[4], rng);
		}
		update();
	}

	/*
	 * React to the user clicking on some canvas.
	 */
	public void clicked(int id) {
		if(id == 4) return;
		
		if(!previousButton.isEnabled()) {
			previousButton.setEnabled(true);
		}
		
		for(int i = 0; i < 9; i++) {
			previousMains[i][0] = previousMains[i + 1][0];
			previousMains[i][1] = previousMains[i + 1][1];
			previousMains[i][2] = previousMains[i + 1][2];
		}
		
		previousMains[9][0] = canvas[4].getRedExp();
		previousMains[9][1] = canvas[4].getGreenExp();
		previousMains[9][2] = canvas[4].getBlueExp();
		
		canvas[4].copy(canvas[id]);
		for (int i = 1; i < 9; i++) {
			if(i == 4) continue;
			canvas[i].reset(canvas[4], rng);
		}
		update();
	}

	/*
	 * One or more of the canvases has changed: ask Swing to repaint all of the
	 * canvases.
	 */
	public void update() {
		for (int i = 0; i < 9; i++) {
			canvas[i].repaint();
		}
	}

}