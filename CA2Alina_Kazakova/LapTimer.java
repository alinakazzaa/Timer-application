package timerApplication;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class LapTimer extends JFrame {

	private Font counterFont = new Font("Arial", Font.BOLD, 20);
	private Font totalFont = new Font("Arial", Font.PLAIN, 14);

	private JLabel lapLabel = new JLabel("Seconds running:");
	private JTextField lapField = new JTextField(15);
	private JLabel totalLabel = new JLabel("Total seconds:");
	private JTextField totalField = new JTextField(15);


	private JButton startButton = new JButton("START");
	private JButton lapButton = new JButton("LAP");
	private JButton stopButton = new JButton("STOP");

	// The text area and the scroll pane in which it resides
	private JTextArea display;

	private JScrollPane myPane;

	// These represent the menus
	private JMenuItem saveData = new JMenuItem("Save data", KeyEvent.VK_S);
	private JMenuItem displayData = new JMenuItem("Display data", KeyEvent.VK_D);
	
	private JMenu options = new JMenu("Options");

	private JMenuBar menuBar = new JMenuBar();

	private boolean started;

	private float totalSeconds = (float)0.0;
	private float lapSeconds = (float)0.0;

	private int lapCounter = 1;

	private LapTimerThread lapThread;

	private Session currentSession;
	
	private LapTimer lapTimer = this;
	
	private Lap newLap;
	
	private final JLabel lblLapTimeGoal = new JLabel("Lap time goal:");
	private final JTextField goalTextField = new JTextField();
	private final JPanel SetTimePanel = new JPanel();
	private final JLabel lblSeconds = new JLabel("seconds");
	
	private String[] goal_message = {"GOAL REACHED", "GOAL NOT REACHED"};
	
	File file = new File("textData.txt");


	public LapTimer() {

		setTitle("Lap Timer Application");

		MigLayout layout = new MigLayout("fillx");
		JPanel panel = new JPanel(layout);
		getContentPane().add(panel);

		options.add(saveData);
		options.add(displayData);
		menuBar.add(options);

		this.setJMenuBar(menuBar);

		MigLayout centralLayout = new MigLayout("fillx");

		JPanel centralPanel = new JPanel(centralLayout);

		GridLayout timeLayout = new GridLayout(0,2);

		JPanel timePanel = new JPanel(timeLayout);

		lapField.setEditable(false);
		lapField.setFont(counterFont);
		lapField.setText("00:00:00.0");

		totalField.setEditable(false);
		totalField.setFont(totalFont);
		totalField.setText("00:00:00.0");
		timePanel.add(lblLapTimeGoal);
		
		timePanel.add(SetTimePanel);
		goalTextField.setText("60");
		goalTextField.setColumns(10);
		SetTimePanel.add(goalTextField);
		SetTimePanel.add(lblSeconds);

		// Setting the alignments of the components
		lblLapTimeGoal.setHorizontalAlignment(SwingConstants.RIGHT);
		goalTextField.setHorizontalAlignment(SwingConstants.CENTER);
		totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lapLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lapField.setHorizontalAlignment(JTextField.CENTER);
		totalField.setHorizontalAlignment(JTextField.CENTER);

		timePanel.add(lapLabel);
		timePanel.add(lapField);
		timePanel.add(totalLabel);
		timePanel.add(totalField);

		centralPanel.add(timePanel, "wrap");

		GridLayout buttonLayout = new GridLayout(1, 3);

		JPanel buttonPanel = new JPanel(buttonLayout);

		buttonPanel.add(startButton);
		buttonPanel.add(lapButton);
		buttonPanel.add(stopButton);

		centralPanel.add(buttonPanel, "spanx, growx, wrap");

		panel.add(centralPanel, "wrap");

		display = new JTextArea(100,150);
		display.setMargin(new Insets(5,5,5,5));
		display.setEditable(false);
		myPane = new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(myPane, "alignybottom, h 100:320, wrap");


		// Initial state of system
		started = false;
		currentSession = new Session();

		// Allowing interface to be displayed
		setSize(400, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		/* This method should allow the user to save data to a file called textData.txt */
		saveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					writeDataFile(file);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});

		/* This method should retrieve the contents of a file representing a 
		   previous report using a JFileChooser. The result should be displayed 
		   as the contents of a dialog object. */
		displayData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JTextArea ta = new JTextArea(20,20); // text area to be used in displaying data
				JFileChooser fc = new JFileChooser(file); // create a file chooser in current directory
				
				int returnVal = fc.showOpenDialog(LapTimer.this); // open a dialog to choose file
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					
					file = fc.getSelectedFile();
					
				} // end if
				
				try {
					ta.setText(readDataFile(file));
				} catch (ClassNotFoundException e1) { 
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} // set data of text area from file
				
				JOptionPane.showMessageDialog(null, ta); // display the data in a jdialog
			}
			}); // end displayData

		/* This method should check to see if the application is already running, 
		   and if not, launch a LapTimerThread object, but if there is another 
		   session already under way, it should ask the user whether they want to 
		   restart - if they do then the existing thread and session should be 
		   reset. The lap counter should be set to 1 and a new Session object 
		   should be created. A new LapTimerThread object should be created with 
		   totalSeconds set to 0.0 and the display area should be cleared. When the 
		   new thread is started, make sure the goal textField is disabled */
		
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			display.setText(""); // clear display area
			goalTextField.setEditable(false);
				
				/* Insert code here */
				
				if(!started) { // if there isn't a session running
					
					started = true; // notify that the pogram is now running
					lapThread = new LapTimerThread(lapTimer, (float)0.0); // launch new time thread object
				}
				
				else { // if there is already a session underway
					
					lapThread.stop(); // stop current thread
					
					int input = JOptionPane.showConfirmDialog(null, "A session is already running. Do you want to restart?", "Warning", JOptionPane.YES_NO_OPTION);
					// ask user if they wish to abort current session
					if(input == 0) { // yes - restart thread and session
						
						currentSession = new Session(); // new session object
						lapCounter = 1; // this is now the first lap
						lapThread = new LapTimerThread(lapTimer, (float)0.0); // start a new thread
					} // if they wish to continue with current session
					else if (input == 1) { // continue from where it stopped
						
						lapSeconds = lapThread.getLapSeconds();
						totalSeconds = lapThread.getTotalSeconds();
						lapThread = new LapTimerThread(lapTimer, lapSeconds, totalSeconds); // create an object with an existing lap time
					}
				}
				
			}
		});

		/* This method should only work if a session has been started. Once started,
		   clicking the Lap button should cause the length of the current lap to be
		   retrieved and used to create a new Lap object which is added to the 
		   session collection. The old LapTimerThread object should be stopped and 
		   a new thread should be started with the updated value of total seconds.
		   The lap number and time should be added to the display area. The message 
		   saying if the goal was reached also need to be added */
		lapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(started) {
					
				lapThread.stop(); // stop the thread
				// create lap object
				newLap = new Lap(lapCounter, lapThread.getLapSeconds()); // create a new lap object
				currentSession.addLap(newLap); // add lap to session 
				
				lapThread = new LapTimerThread(lapTimer, (lapThread.getTotalSeconds())); // start new thread
				lapCounter++; // add 1 lap to lap counter
				
				// check if lap time met the goal
				if(newLap.getLapTime() <= Float.parseFloat(getGoalValue())) { // if it did
					// update display
					display.append(newLap.getId() + "                  " + convertToHMSString(newLap.getLapTime()) + "           " + goal_message[0] + "\n");				
				} // end if
				else { // if it didn't
					
					display.append(newLap.getId() + "                  " + convertToHMSString(newLap.getLapTime()) + "           " + goal_message[1] + "\n");				

				} // end else
				} // end if started
			}
		});

		/* This method should have most of the same functionality as the Lap
		   button's action listener, except that a new LapTimerThread object is
		   NOT started. In addition, the total time for all the laps should be 
		   calculated and displayed in the text area, along with the average lap
		   time and the numbers and times of the fastest and slowest laps. */
		
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String average; 
				
				EnableGoalEditing(true); // set goal field to be editable again
				
				if(started) {	// check to make sure there was a session running		
					
					lapThread.stop(); // stop the thread
					started = false; // notify that there isn't a session running
					
					newLap = new Lap(lapCounter, lapThread.getLapSeconds()); // create new lap object
					currentSession.addLap(newLap); // add to current session
					
					average = String.format("%.2f",currentSession.calculateAverageTime()); // get average in correct format
					
					if(newLap.getLapTime() <= Float.parseFloat(getGoalValue())) {
						// update display
						display.append(newLap.getId() + "                  " + convertToHMSString(newLap.getLapTime()) + "           " + goal_message[0] + "\n");			
					} // end if
					else {
						
						display.append(newLap.getId() + "                  " + convertToHMSString(newLap.getLapTime()) + "           " + goal_message[1] + "\n");							

					} // end else
					// update display with all information on the session
					display.append("\nStopped at: " + convertToHMSString(lapThread.getTotalSeconds())); 
					display.append("\nAverage lap time: " + average +"\n");
					display.append("\nFastest lap: " + currentSession.getFastestLap().getId() +	"              " + convertToHMSString(currentSession.getFastestLap().getLapTime()) + "\n");				
					display.append("\nSlowest lap: " + currentSession.getSlowestLap().getId() +   "              " + convertToHMSString(currentSession.getSlowestLap().getLapTime()) + "\n");
					
					lapCounter = 1; // reset lap counter
				}
				else { // if there wasn't a session running
					
					JOptionPane.showMessageDialog(null, "You haven't started a session."); // notify user that there isn't a session to stop
				}	
				
			}
		});

	}
	
	/* These two methods are used by the LapTimerThread to update the values
	   displayed in the two text fields. Each value is formatted as a 
	   hh:mm:ss.S string by calling the convertToHMSString method below/. */

	public void updateLapDisplay(float value) {

		lapField.setText(convertToHMSString(value));

	}

	public void updateTotalDisplay(float value) {

		totalField.setText(convertToHMSString(value));

	}
	
	/* These methods are here to help access the
	 *  goaltextField in the GUI */
	
	public String getGoalValue(){
		return  goalTextField.getText();
	}
	
	public void EnableGoalEditing(boolean makeEditable){
		goalTextField.setEditable(makeEditable);
	}
	
	public void setTextArea(String str){
		display.setText(str);
	}

	private String convertToHMSString(float seconds) {
		long msecs, secs, mins, hrs;
		// String to be displayed
		String returnString = "";

		// Split time into its components

		long secondsAsLong = (long)(seconds * 10);

		msecs = secondsAsLong % 10;
		secs = (secondsAsLong / 10) % 60;
		mins = ((secondsAsLong / 10) / 60) % 60;
		hrs = ((secondsAsLong / 10) / 60) / 60;

		// Insert 0 to ensure each component has two digits
		if (hrs < 10) {
			returnString = returnString + "0" + hrs;
		}
		else returnString = returnString + hrs;
		returnString = returnString + ":";

		if (mins < 10) {
			returnString = returnString + "0" + mins;		
		}
		else returnString = returnString + mins;
		returnString = returnString + ":";

		if (secs < 10) {
			returnString = returnString + "0" + secs;		
		}
		else returnString = returnString + secs;

		returnString = returnString + "." + msecs;

		return returnString;

	}

	/* These methods will be used by the action listeners attached
	   to the two menu items. */

	public synchronized void writeDataFile(File f) throws IOException, FileNotFoundException {

		// create object output stream
		ObjectOutputStream oos = null;

		try { // create output stream
			
			oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			oos.writeObject(display.getText()); // write display contents as one object
			
		} finally {
				oos.close(); // close stream
		}
	}

	public synchronized String readDataFile(File f) throws IOException, ClassNotFoundException {

		String result = new String(); 
		ObjectInputStream ois = null; // create input stream object
			
			try {
				ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file))); // open file input stream
				result = ois.readObject().toString(); // read to string object

			} finally {
					ois.close(); // close stream
					
			} // end try-finally
			
		return result;
	}

	public static void main(String[] args) {

		LapTimer timer = new LapTimer();
		timer.setVisible(true);

	}


}
