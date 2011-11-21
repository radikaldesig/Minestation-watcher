

package minestation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

class DirFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return new File(dir.getPath() + File.separator + name).isDirectory();
	}
	
}

public class Minestation implements ActionListener {
	
	
	
	public static int time;
	public static int raintime;
	public static int thundertime;
	
	private Tag main;
	private JFrame frame;
	private JPanel panel;
	private JTextField Timetext;
	private JTextField Stormtext;
	private JTextField Raintext;
	
	private String savePath;
	private String[] filePaths;
	private String lastFolder;
	private int validNames;
	private JLabel label1;
	private JLabel TimeLabel;
	private JLabel RainLabel;
	private JLabel StormLabel;	

	private JComboBox combo;
	private JComboBox comboPort;
	private JLabel portLabel;
//	private JCheckBox cbCreative;
//	private boolean creativeEnabled;
//	private boolean hardcoreEnabled;
//	private boolean abilitiesExist;     //To control tags added in Beta 1.9 PR 5.
										//If these tags are not changed properly, Creative mode
										//abilities can be enabled in Survival mode.
	
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnRead;
	private JButton btnWrite;
	private String selectedFilePath;
	
	private final String version = "0.1";
	private Serial serialPort = new Serial();  //Serial port class
//	private JCheckBox cbHardcore;
	public static boolean start=false;
	public timer timerPort = new timer();
	
	
	public Minestation()
	{
		int widthframe=250;
		int  heightframe=100;
		//Set the system look and feel
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		
		//Windows
		if(System.getProperty("os.name").toLowerCase().contains("windows"))
		{
			savePath = System.getenv("APPDATA") + "\\.minecraft\\saves\\";
		}
		
		//Mac
		if(System.getProperty("os.name").toLowerCase().contains("mac os x"))
		{
			savePath = System.getProperty("user.home") + "/Library/Application Support/minecraft/saves/";
		}
		
		//Linux(hopefully)
		if(System.getProperty("os.name").toLowerCase().contains("linux"))
		{
			savePath = System.getProperty("user.home") + "/.minecraft/saves/";
		}
		
		//Aplication Frame construction
		frame = new JFrame("Minestation watcher " + version);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(widthframe,heightframe);
		frame.setIconImage (new ImageIcon("src/minestation/icon.jpg").getImage());
		
		panel = new JPanel();
		
		combo = new JComboBox();
		combo.addActionListener(this);
		
		comboPort = new JComboBox();
		comboPort.addActionListener(this);
		
		Timetext = new JTextField();
		Timetext.setColumns(10);
		Timetext.setEditable(true);
		
		Raintext = new JTextField();
		Raintext.setColumns(10);
		Raintext.setEditable(true);
		
		Stormtext = new JTextField();
		Stormtext.setColumns(10);
		Stormtext.setEditable(true);
		
		
		label1 = new JLabel("Select World");
		portLabel = new JLabel("Select Port:");
		TimeLabel = new JLabel("Time:        ");
		StormLabel = new JLabel("Storm Time:");
		RainLabel = new JLabel ("Rain Time:  ");
		
		btnStart = new JButton("Start");
		btnStart.setEnabled(true);
		btnStart.setActionCommand("start");
		btnStart.addActionListener(this);
		
		btnStop = new JButton("Stop");
		btnStop.setEnabled(true);
		btnStop.setActionCommand("stop");
		btnStop.addActionListener(this);
		
		btnRead = new JButton("Read");
		btnRead.setEnabled(true);
		btnRead.setActionCommand("Read");
		btnRead.addActionListener(this);
		
		btnWrite = new JButton("Write");
		btnWrite.setEnabled(true);
		btnWrite.setActionCommand("Write");
		btnWrite.addActionListener(this);
		
		
		JMenuBar menuBar = new JMenuBar();
		JMenu helpMenu = new JMenu("Help");
		
		JMenuItem howToUse = new JMenuItem("How to use");
		howToUse.setActionCommand("use");
		howToUse.addActionListener(this);
		helpMenu.add(howToUse);
		
		JMenuItem newFolder = new JMenuItem("Select MC Save Folder");
		newFolder.setActionCommand("folder");
		newFolder.addActionListener(this);
		helpMenu.add(newFolder);
		
		helpMenu.addSeparator();
		
		JMenuItem about = new JMenuItem("About");
		about.setActionCommand("about");
		about.addActionListener(this);
		helpMenu.add(about);
		
		menuBar.add(helpMenu);
		frame.setJMenuBar(menuBar);
		
		panel.setLayout(new MigLayout("", "[30px]10[50px]10[40px]10[50px]10", "[20px][][]"));
		panel.add(label1);
		panel.add(combo, "alignx left,aligny center");
		panel.add(portLabel);
		panel.add(comboPort,"alignx left,aligny center,span 2,wrap");
		panel.add(btnStart);
		panel.add(btnStop,"wrap");
		panel.add(TimeLabel);
		panel.add(Timetext,"wrap");
		panel.add(RainLabel);
		panel.add(Raintext,"wrap");
		panel.add(StormLabel);
		panel.add(Stormtext,"wrap");
		panel.add(btnRead);
		panel.add(btnWrite);
		
		frame.getContentPane().add(panel);
		frame.pack();
		
		//Center on screen
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		//Try to load the save data
		setupData();
		//Load Com ports
		loadPorts();
	}
	
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new Minestation();
			
		} catch(Exception e) {  //If anything unexpected goes wrong in the main program,
			     				//write it to an error log
			try {
				FileOutputStream fos = new FileOutputStream("Minestation.error.log");
				e.printStackTrace(new PrintStream(fos));
				fos.close();
				
				JOptionPane.showMessageDialog(null, "An error occured." +
						"\nCheck the Minestation.error.log file for more information.", 
						"ERROR", JOptionPane.ERROR_MESSAGE);
				
			} catch (FileNotFoundException e1) {  //If the error log messes up,
												  //fall back to console
				e.printStackTrace();
			} catch (IOException e2) {
				e.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	//Seek and load Com Ports in ComboPort
	public void loadPorts(){
		String portNames[] = new String[30];
		comboPort.removeAllItems();	
		portNames=serialPort.list();
		for (String s:portNames){
			comboPort.addItem(s);
		}
	}
	
	//Looking for save files
	public void setupData()
	{
		boolean loop = false;
		
		//Check to reload data, used when checking folders with no valid save files
		do {
			loop = false;
			
			File file = new File(savePath);
			
			//If the save folder selected actually exists
			if(file.exists())
			{
				//Grab a list of sub-directories in the saves directory
				String[] tempPaths = file.list(new DirFilter());
				filePaths = new String[tempPaths.length];
		
				validNames = 0;
				FileInputStream fis;
		
				combo.removeAllItems();
				for(String s : tempPaths)
				{
					//Check each sub-directory for a 'level.dat' file
					//If one is found, then assume it's a valid MC save folder
					file = new File(savePath + File.separator + s + File.separator + "level.dat");
					if(file.exists()) {
						filePaths[validNames] = file.getPath();
						
						lastFolder = file.getParent();
						
						try {
							fis = new FileInputStream(file);
							main = Tag.readFrom(fis);
							fis.close();
							
							//If the level.dat doesn't have a 'LevelName' entry,
							//go by the folder's name
							Tag name = main.findTagByName("LevelName");
							if(name == null) {
								combo.addItem(file.getParentFile().getName());
							} else {
								combo.addItem((String)name.getValue());								
							}
							
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e)
						{
							//Unknown problem with loading save file.  Corrupted?
							JOptionPane.showMessageDialog(frame, "There was a problem reading the save file in " + lastFolder
									+ ".\n It will not be loaded in this program.", "Error", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						
						validNames++;
					}
				}
				
				if(validNames > 0) 
				{
					combo.setSelectedIndex(0);
				} else { //No valid save files were found
					JOptionPane.showMessageDialog(panel, "No valid save files were detected in current folder." +
							"\n\nPlease choose a new one.", "Error", JOptionPane.ERROR_MESSAGE);
					
					if(chooseSaveFolder()) loop = true;
					//This won't loop if the user cancels out of the folder selection dialog.
				}
			} else {  //Save folder doesn't exist
				JOptionPane.showMessageDialog(panel, "Save folder doesn't exist.\n\nPlease choose a new one.",
						"Error", JOptionPane.ERROR_MESSAGE);
				
				if(chooseSaveFolder()) loop = true;
				//Again, won't loop if the user cancels out of the folder selection dialog.
			}
		} while (loop);
	}
	
	//Choose a folder for savefiles
	public boolean chooseSaveFolder()
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int ret = fc.showDialog(panel, "Open Minecraft Saves Folder");
		if(ret == JFileChooser.APPROVE_OPTION) {
			savePath = fc.getSelectedFile().getPath();
			
			//Three tests to see if the folder chosen was a world folder, not the saves folder
			//Test one: check for existence of 'level.dat'
			if((new File(savePath + File.separator + "level.dat")).exists())
			{
				//'level.dat' exists, now check for 'session.lock'
				if((new File(savePath + File.separator + "session.lock")).exists())
				{
					//'session.lock' exists, now the final check: region subfolder.
					if((new File(savePath + File.separator + "region")).exists())
					{
						//At this point, we've almost indisputably gotten a world folder instead
						//of the saves folder.  Let's set 'savePath' to the parent directory.
						savePath = new File(savePath).getParent();
					}
				}
			}
			return true;
		}
		
		return false;
	}
	public void putData(){
		sendData();
	}
	
	public  void sendData(){
		Timetext.setText(main.findTagByName("Time").getValue().toString());
		Raintext.setText(main.findTagByName("rainTime").getValue().toString());
		Stormtext.setText(main.findTagByName("thunderTime").getValue().toString());	
	}
	
	
	//Actions control
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//If a combobox item was selected
		if(arg0.getSource() == combo)
		{
			int val = combo.getSelectedIndex();
			
			//Make sure something was actually chosen
			if(val > -1)
			{
				
				try {
					selectedFilePath = filePaths[val];
					FileInputStream fis = new FileInputStream(new File(selectedFilePath));
					main = Tag.readFrom(fis);
					fis.close();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else { //One of the menu items was chosen, or the checkbox was checked,
			     //or "save" was pressed
			String cmd = arg0.getActionCommand();
			
			//"How to use" menu item
			if(cmd.equals("use"))
			{
				String message = "Choose the world's name from the dropdown list." +
								 "\nThe world's seed will show up in the textbox." + 
								 "\n\nIf the list is empty, click on Help > Select MC Save Folder," +
								 "\nand find and open the saves folder in the file chooser.\n" +
								 "\nthen hitting Start.";
				
				JOptionPane.showMessageDialog(panel, message, "How to Use", JOptionPane.INFORMATION_MESSAGE);
			}
			
			//"About" menu item
			if(cmd.equals("about"))
			{
				String message = "Based on Chris Iverson's Minecraftseed. \n\n" +
								 "A watcher files for Minestation \n" +
								 "   minestation.me\n" +
								 "\nProblems? \nReport them either to: minestation@gmail.com\n" +
								 "or locate us at:\n @radikaldesig\n @xbelanch\n" +
								 "\nEnjoy!";
				
				JOptionPane.showMessageDialog(panel, message, "About Minestaion watcher v" + version,
						JOptionPane.INFORMATION_MESSAGE);
			}
			
			//"Select MC Save Folder" menu item
			if(cmd.equals("folder"))
			{
				//Only load the data if the user selected a folder
				if(chooseSaveFolder()) setupData();
			}
			
		
			//Start button pressed
			if(cmd.equals("start"))
			{
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				start=true;
				timerPort.start();//(500);
				
			}
			
			if(cmd.equals("write"))
			{
				/*int chosen = JOptionPane.showConfirmDialog(frame, "Are you sure you want to overwrite your save file?", 
						"Overwrite", JOptionPane.YES_NO_OPTION);
				
				if(chosen == JOptionPane.YES_OPTION);
				{
					try {
						FileOutputStream fos = new FileOutputStream(new File(selectedFilePath));
						main.writeTo(fos);
						fos.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Problem saving file: File Not Found", "File Not Found", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Problem saving file: IO Error", "IO Error", JOptionPane.ERROR_MESSAGE);
					}
					
					JOptionPane.showMessageDialog(frame, "File saved!");
				}*/
			}
			
			//Stop button pressed
			if(cmd.equals("stop"))
			{
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
				start=false;
				timerPort.stop();
			}
			
			//Save button pressed
			if(cmd.equals("Read"))
			{
				try {
					int val = combo.getSelectedIndex();
					if ((val<0) && (combo.getItemCount()>-1)) combo.getItemAt(0);
					
					selectedFilePath = filePaths[val];
					FileInputStream fis = new FileInputStream(new File(selectedFilePath));
					main = Tag.readFrom(fis);
					fis.close();
					
					Timetext.setText(main.findTagByName("Time").getValue().toString());
					Raintext.setText(main.findTagByName("rainTime").getValue().toString());
					Stormtext.setText(main.findTagByName("thunderTime").getValue().toString());
					

					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} // else
		
	} //actionPerformed
	

}