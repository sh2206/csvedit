import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class CSVPanel extends JPanel
{
	JButton edit;
	JTextArea log;
	JFileChooser fc;
	JLabel label;
	JLabel hlabel;
	JLabel clabel;
	JLabel poslabel;
	JTextField coltitle;
	JTextField colcontents;
	JTextField insertpos;
	
	public CSVPanel()
	{
		//Construct empty top JPanel.
		super(new BorderLayout());
		
		//Creating the log scroll pane.
		log = new JTextArea(5,20);
		log.setMargin(new Insets(5,5,5,5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);
		
		//File chooser object.
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		//Create button and attach the event listener.
		edit = new JButton("Select File to Edit");
		ButtonListener listener = new ButtonListener();
		edit.addActionListener(listener);
		
		//Labels and textfields.
		label = new JLabel("Fill in new header and column contents, then choose CSV file to edit. \n");
		hlabel = new JLabel("Header:");
		clabel = new JLabel("Column:");
		poslabel = new JLabel("Column Number (blank to create as new first column):");
		coltitle = new JTextField(5);
		coltitle.addFocusListener(new java.awt.event.FocusAdapter() {
    	    public void focusGained(java.awt.event.FocusEvent evt) {
    	    	SwingUtilities.invokeLater( new Runnable() {
    				public void run() {
    					coltitle.selectAll();		
    				}
    			});
    	    }
    	});
		colcontents = new JTextField(5);
		colcontents.addFocusListener(new java.awt.event.FocusAdapter() {
    	    public void focusGained(java.awt.event.FocusEvent evt) {
    	    	SwingUtilities.invokeLater( new Runnable() {
    				public void run() {
    					colcontents.selectAll();		
    				}
    			});
    	    }
    	});
		insertpos = new JTextField(5);
		insertpos.addFocusListener(new java.awt.event.FocusAdapter() {
    	    public void focusGained(java.awt.event.FocusEvent evt) {
    	    	SwingUtilities.invokeLater( new Runnable() {
    				public void run() {
    					insertpos.selectAll();		
    				}
    			});
    	    }
    	});
		
		
		//Add the buttons and the log to a panel.	
		JPanel buttonPanel = new JPanel(); //use FlowLayout
		buttonPanel.setPreferredSize(new Dimension(450, 120));
		buttonPanel.setBackground(Color.white);
		buttonPanel.add(hlabel);
		buttonPanel.add(coltitle);
		buttonPanel.add(clabel);
		buttonPanel.add(colcontents);
		buttonPanel.add(poslabel);
		buttonPanel.add(insertpos);
		buttonPanel.add(label);
		buttonPanel.add(edit);
		
		//Format frame and add panel to it.
		setPreferredSize(new Dimension(450, 200));
		add(buttonPanel, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.CENTER);
	}
	
	//Class to define the 'Edit button' event listener.
	public class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == edit) {
				String title = coltitle.getText();
				String contents = colcontents.getText();
				String strposition = insertpos.getText();
				
				//Pop up an error if the text fields are empty.
				if(title.equals("") || contents.equals("")) {
					CSVPanel.errorBox("Fill in the header and column fields!!", "Fields not entered.");
				} else {
					int returnVal = fc.showOpenDialog(CSVPanel.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						try {
							//Open file and create temporary work file.
							File file = fc.getSelectedFile();
							log.append("Opening: " + file.getName() + "." + "\n");
							File tmpfile = File.createTempFile("tmpfile", ".tmp");
							tmpfile.deleteOnExit();
													
							//Create writer and scanner to scan each line.
							BufferedWriter bw = new BufferedWriter(new FileWriter(tmpfile));
							Scanner fileScan = new Scanner(file);
							String eachline=" ";
							
							//Add the new header entry
							eachline = fileScan.nextLine();
							String[] temp = eachline.split(",");
							LinkedList<String> list = new LinkedList();
							for(int i = 0; i < temp.length; i++) {
								list.add(temp[i]);
							}
							
							if(!strposition.equals("")) {
								int position = Integer.parseInt(strposition);
								list.add((position-1), title);
							} else {
								list.addFirst(title);
							}
							
							int count = list.size();
							Iterator<String> iter = list.iterator();
							while(iter.hasNext()) {
								bw.write(iter.next());
								count--;
								if(count >= 1) {
									bw.write(",");
								}
							}
							bw.newLine();

							//Add the new column to the remainder of the file
							while(fileScan.hasNextLine()) {
								eachline = fileScan.nextLine();
								String[] temp2 = eachline.split(",");
								LinkedList<String> list2 = new LinkedList();
								for(int j = 0; j < temp2.length; j++) {
									list2.add(temp2[j]);
								}
								
								if(!strposition.equals("")) {
									int position = Integer.parseInt(strposition);
									list2.add((position-1), contents);
								} else {
									list2.addFirst(contents);
								}
								
								int count2 = list2.size();
								Iterator<String> iter2 = list2.iterator();
								while(iter2.hasNext()) {
									bw.write(iter2.next());
									count2--;
									if(count2 >= 1) {
										bw.write(",");
									}
								}
								bw.newLine();
							}
							
							fileScan.close();
							bw.close();

							File oldFile = new File(file.getAbsolutePath());
							if (oldFile.delete()) {
								log.append("Overwriting original...");
								if(tmpfile.renameTo(oldFile)) {
									log.append("\nOverwrite successful.\n");
								}
							}
						}
						catch (IOException e) {
							log.append("IOException: " + e.getMessage());
						}
					} else {
						log.append("Open command cancelled." + "\n");
					}
				}
			}
		}	
	}
	
	public static void errorBox(String errorMessage, String location)
	{
        JOptionPane.showMessageDialog(null, errorMessage, "Error: " + location, JOptionPane.INFORMATION_MESSAGE);
	}
}