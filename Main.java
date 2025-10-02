import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.print.attribute.standard.PagesPerMinuteColor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.*;

/**
 * This is the main controller class, in which the program can be opened by creating an instance of the class.
 * The main purpose of this software is to organize and sort data for scientific citations and generate formatted
 * citations from user given data.
 */
public class Main {
	
	//FIELDS
	
	private ArrayList<User> users = new ArrayList<User>(); //temporary ArrayList of users registered in the program (populated when program is run)
	private ArrayList<Paper> papers = new ArrayList<Paper>(); //temporary ArrayList of papers for a given user account (populated when user logs in)
	
	//JFrame for software screen
	private static JFrame frame;
	
	//Login page GUI elements
	private static JPanel loginPage; //panel for login page
	private static JLabel productTitle; //label: "Scientific Citation Manager"
	private static JLabel welcomeHeading1; //label: "Welcome"
	private static JLabel welcomeHeading2; //label: "Please Login or Sign Up"
	private static JLabel userLabel; //label for username text field
	private static JTextField userText; //text field for username
	private static JLabel passwordLabel; //label for password text field
	private static JPasswordField passwordText; //text field for password
	private static JButton login;  //login button
	private static JButton signup; //sign up button
	
	//Login button ActionListener
	private ActionListener loginButton = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) { //performs action when login button is pressed
			
			String username = userText.getText(); //takes text from username text field
			String password = passwordText.getText(); //takes text from password text field
        	boolean found = false; //boolean to verify credentials
        	
        	for (int i = 0; i < users.size(); i++) { //loops through users list
        		if (users.get(i).getUsername().equals(username) && (users.get(i).getPassword().equals(password))) { //if username and password coincide with one of the registered users
    				found = true; //set found to true
        		}
        	}
        	
        	if (found == true) { //if found is true; login is successful
        		readPaperData(username); //calls readpaperData() with username to populate the papers field
        		updateComboBox(); //updates combo box of overview page with updated paper data 
    			loginPage.setVisible(false); //sets login page panel invisible
				frame.remove(loginPage); //removes login page
				overviewPage.setVisible(true); //sets overview page panel visible
				frame.add(overviewPage); //adds overview page
        	} else {
        		JOptionPane.showMessageDialog(frame, "Incorrect username or password.", "Error", JOptionPane.ERROR_MESSAGE); //shows error message if user not found
        	}
        }
	};
	
	//Sign up button ActionListener
	private ActionListener signupButton = new ActionListener() { 
		
		public void actionPerformed(ActionEvent e) { //performs action when sign up button is pressed
			
            int ans = JOptionPane.showConfirmDialog(frame, "Confirm signup?", "", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE); //confirm sign up dialogue box
            if (ans == JOptionPane.YES_OPTION) { //if user chooses "yes"
            	
            	if (userText.getText().trim().equals("") || passwordText.getText().trim().equals("")) { //checks if text fields are filled
            		
            		JOptionPane.showMessageDialog(frame, "The text fields have not been filled.", "Error", JOptionPane.ERROR_MESSAGE); //shows error message if they are not filled
            		
            	} else { //if both text fields are filled
            		
            		User t_user = new User(userText.getText(),passwordText.getText()); //creates new user from text field entries
            		boolean duplicate = false; //instantiates boolean variable to check for duplicate usernames
                	
                	for (int i = 0; i < users.size(); i++) { //loops through list of users
                		if (users.get(i).getUsername().equals(t_user.getUsername())) { //if t_user's username is equal to another user's username
                			duplicate = true; //set duplicate to true
                		}
                	}
                	
                	if (duplicate == false) { //if duplicate is false; sign up sucessful
                		
                		users.add(t_user); //adds t_user to users list
                		writeUsers(); //calls function writeUsers() to write user data to the userdata.txt file
                		
                		String filename = t_user.getUsername() + ".txt"; //creates string "username.txt"
                		File t_file = new File(filename); //instantiates new File with filename and proper filepath - 
                																											        //this file is where user-specific data is written
                		
                		try {
                			t_file.createNewFile(); //creates new file at specified location
                		} catch(IOException i) {
                			System.out.println("An error occured."); //prints error message in console if file cannot be created
                		}
                		
                    	JOptionPane.showMessageDialog(frame,"Signed Up! You can now login."); //confirm dialogue message
                    	
                	} else {
            			JOptionPane.showMessageDialog(frame, "That username is already taken.", "Error", JOptionPane.ERROR_MESSAGE); //error message if username already taken
                	}
            	}
            }
		}
	};
	
	//Overview page GUI elements
	private static JPanel overviewPage; //panel for overview page
	private static JButton signOut; //sign out button
	private static JComboBox choosePaper; //combo box to choose paper
	private static JButton addPaper; //button to add paper to database
	private static JButton removePaper; //button to remove paper from database
	private static JLabel selectLabel; //label for combo box select ("Select Paper to View:")
	private static JTable citationTable; //table displaying paper data
	private static DefaultTableModel model; //TableModel for JTable
	private static JScrollPane scrollPane; //scroll pane for JTable
	private static JButton addCitation; //button to add citation to current paper
	private static JButton removeCitation; //button to remove selected citation from current paper
	private static JButton editCitation; //button to edit selected citation from current paper
	private static JButton generateCitation; //button to generate formatted citation from selected citation
	
	//Sign out button ActionListener
	private ActionListener signoutButton = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) { //performs action when sign out button pressed
			
			writePaperData(userText.getText()); //calls writePaperData using the string entry from the username text field to write paper data to the correct .txt file
			papers.clear(); //clears the ArrayList papers field (avoids duplication of data when logging back in)
			model.setRowCount(0); //sets table to blank
			overviewPage.setVisible(false); //sets overview page panel invisible
			frame.remove(overviewPage); //removes overview page panel from frame
			frame.add(loginPage); //adds login page panel to frame
			loginPage.setVisible(true); //sets login page panel visible
			userText.setText(""); //sets text fields to blank (otherwise they still hold previous login information)
			passwordText.setText("");
			
		}
		
	};
	
	//Add paper button ActionListener
	private ActionListener addPaperButton = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) { //performs action when add paper button pressed
			
			String name = JOptionPane.showInputDialog(frame, "Enter the name of your paper:", ""); //prompt for paper name/title
			ArrayList<Citation> t_citations = new ArrayList<Citation>(); //instantiates temporary ArrayList of citations
			
			try { //try-catch structure for invalid inputs
				
				int num = Integer.parseInt(JOptionPane.showInputDialog(frame, "How many citations do you wish to add to this paper?"));  //prompt for number of citations within paper
				
				for (int i = 0; i < num; i++) { //loops through # of citations user specified in previous input
					
					String n = JOptionPane.showInputDialog(frame, "Enter the name of the cited paper " + (i+1) + ":", ""); //prompt for citation name/title
					ArrayList<String> t_authors = new ArrayList<String>(); //instantiates temporary ArrayList of authors
					int num2 = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the number of authors:","")); //prompt for number of authors for citation
					
					for (int j = 0; j < num2; j++) { //loops through # of authors user specified in previous input
						String a = JOptionPane.showInputDialog(frame, "Enter author name " + (j+1) + ":", ""); //prompt for author name
						t_authors.add(a); //adds a to the t_authors ArrayList
					}
					
					int d = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the year of publication: ")); //prompt for publication year of citation
					String j = JOptionPane.showInputDialog(frame, "Enter the journal of the paper: "); //prompt for journal citation belongs to
					String l = JOptionPane.showInputDialog(frame, "Enter the link to the citation: "); //prompt for link to citation
					
					Citation t_citation = new Citation(n,t_authors,j,d,l); //instantiates new citation using constructor
					t_citations.add(t_citation); //adds citation to the t_citations ArrayList
					
				}
				
				Paper t_paper = new Paper(name, t_citations); //instantiates new paper using constructor
				papers.add(t_paper); //adds paper to papers field
				updateComboBox(); //calls updateComboBox() to update combo box choosePaper with new data
				writePaperData(userText.getText()); //calls writePaperData using the string entry from the username text field to write paper data to the correct .txt file 
				
				JOptionPane.showMessageDialog(frame, "Success! The database has been updated.", "", JOptionPane.INFORMATION_MESSAGE); //confirmation message
				
			} catch(RuntimeException r) {
				JOptionPane.showMessageDialog(frame, "Please enter a valid input.", "Error", JOptionPane.ERROR_MESSAGE); //error message for invalid inputs
			}
		}
	};
	
	//Remove paper button ActionListener
	private ActionListener removePaperButton = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) { //performs action when remove paper button pressed
			
			Object[] t_names = new String[papers.size()]; //instantiates new array t_names for JOptionPane combo box
			for (int i = 0; i < papers.size(); i++) { //loops through papers field
				t_names[i] = papers.get(i).getName(); //populates t_names with paper names
			}
			
			String ans = (String)JOptionPane.showInputDialog(frame, "Which paper would you like to remove?", "", JOptionPane.PLAIN_MESSAGE, null, t_names, t_names[0]); //prompt to remove paper by name
			
			for (int i = 0; i < papers.size(); i++) { //loops through papers field
				if (ans.equals(papers.get(i).getName())) { //if user input matches name of paper within papers field
					papers.remove(i); //remove that paper from the ArrayList
					writePaperData(userText.getText()); ////updates paper data to the applicable .txt file
					break; //break out of loop
				}
			}
			
			updateComboBox(); //updates data for choosePaper combo box
			model.setRowCount(0); //sets citation table blank
			
			JOptionPane.showMessageDialog(frame, "Success! The paper has been removed.", "Success", JOptionPane.INFORMATION_MESSAGE); //confirmation message
			
		}
		
	};
	
	//Combobox "choosePaper" ActionListener
	private ActionListener choosePaperSelect = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) { //performs action when combobox interacted with
			
			String t_pname = (String)choosePaper.getSelectedItem(); //casts selected combobox item to string variable
			
			try { //try-catch for runtime exceptions
				
				for (int i = 0; i < papers.size(); i++) { //loops through papers field
					
					if (t_pname.equals("")) { //if combobox item chosen is empty
						model.setRowCount(0); //set table blank
						break; //break out of loop
					}
					
					if (t_pname.equals(papers.get(i).getName())) { //if combobox item chosen coincides with a paper name in the papers field
						Paper t_paper = papers.get(i); //instantiate new Paper object
						updateTable(t_paper); //update table display with t_paper
						break; //break out of loop
					}
				}
			
			} catch (RuntimeException r) {
				model.setRowCount(0); //set table blank if error occurs
			}
		}
	};
	
	//Add citation button ActionListener
	private ActionListener addCitationButton = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) { //performs action when add citation button pressed
			
			try { //try-catch for invalid inputs
				
				String n = JOptionPane.showInputDialog(frame, "Enter the name of the cited paper:", ""); //prompt for paper name/title
				
				ArrayList<String> t_authors = new ArrayList<String>(); //instantiates new ArrayList of strings for citation authors
				int num = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the number of authors:","")); //prompt for number of authors for citation
				
				for (int j = 0; j < num; j++) { //loops through # of authors specified in previous input
					String a = JOptionPane.showInputDialog(frame, "Enter author name " + (j+1) + ":", ""); //prompt for each author name
					t_authors.add(a); //adds input to t_authors
				}
				
				int d = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the year of publication: ")); //prompt for citation publication year
				String j = JOptionPane.showInputDialog(frame, "Enter the journal of the paper: "); //prompt for journal citation belongs to
				String l = JOptionPane.showInputDialog(frame, "Enter the link to the citation: "); //prompt for link to citation
				
				Citation t_citation = new Citation(n,t_authors,j,d,l); //instantiates new citation using collected input data
				
				String t_pname = (String)choosePaper.getSelectedItem(); //casts selected combobox item to string variable
				
				if (papers.size() == 0) { //if there are no Paper objects in the papers field
					JOptionPane.showMessageDialog(frame, "Please have a paper selected in the option box.", "Error", JOptionPane.ERROR_MESSAGE); //error message
				}
				
				for (int i = 0; i < papers.size(); i++) { //loops through papers field
					if (t_pname.equals("") || t_pname.equals(null)) { //if selected combobox option resolves to empty string or null
						JOptionPane.showMessageDialog(frame, "Please have a paper selected in the option box.", "Error", JOptionPane.ERROR_MESSAGE); //error message
						break; //breaks out of loop
					}
					if (t_pname.equals(papers.get(i).getName())) { //if selected combobox item coincides with a paper name in the papers field
						papers.get(i).addCitation(t_citation); //adds instantiated citation to paper
						updateTable(papers.get(i)); //updates table with new paper data
						writePaperData(userText.getText()); //writes updated data to applicable .txt file
						JOptionPane.showMessageDialog(frame, "Success! The citation has been added to the selected paper.", "Success", JOptionPane.INFORMATION_MESSAGE); //confirmation message
						break; //breaks out of loop
					}
				}
				
			} catch (RuntimeException r) {
				JOptionPane.showMessageDialog(frame, "Please enter a valid input.", "Error", JOptionPane.ERROR_MESSAGE); //error message
			}
		}
	};
	
	//Remove citation button ActionListener
	private ActionListener removeCitationButton = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) { //performs action when remove citation button pressed
			
			try { //try-catch for citation (table row) not being selected by user
				
				String t_pname = (String)choosePaper.getSelectedItem(); //casts selected combobox item to string variable
				
				for (int i = 0; i < papers.size(); i++) { //loops through papers field
					if (t_pname.equals(papers.get(i).getName())) { //if combobox item chosen coincides with a paper name in the papers field
						int num = citationTable.convertRowIndexToModel(citationTable.getSelectedRow()); //get index of row selected
						papers.get(i).removeCitation(num); //remove citation of paper at specified index
						updateTable(papers.get(i)); //updates table display with new paper data
						writePaperData(userText.getText()); //writes updated data to applicable .txt file
					}
				}
			
			} catch (RuntimeException r) {
				JOptionPane.showMessageDialog(frame, "Please have a citation selected from the table.", "Error", JOptionPane.ERROR_MESSAGE); //error message if citation is not selected from table
			}
		}
	};
	
	//Edit citation button ActionListener
	private ActionListener editCitationButton = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) { //performs action when sign out button pressed
			
			try { //try-catch for citation (table row) not being selected by user
				
				String t_pname = (String)choosePaper.getSelectedItem(); //casts selected combobox item to string variable
				
				for (int i = 0; i < papers.size(); i++) { //loops through papers field
					if (t_pname.equals(papers.get(i).getName())) { //if combobox item chosen coincides with a paper name in the papers field
						
						Paper t_paper = papers.get(i); //instantiates new Paper object and sets equal to papers.get(i)
						
						int num = citationTable.convertRowIndexToModel(citationTable.getSelectedRow()); //gets index of row selected
						
						try { //try-catch for invalid inputs
							
							Citation t_citation = t_paper.getCitations().get(num); //instantiates Citation object and sets equal to t_paper at index num
							
							String n = JOptionPane.showInputDialog(frame, "Edit the name of the cited paper:", t_citation.getName()); //prompt for editing citation name/title (t_citation.getName() used as placeholder text)
							
							ArrayList<String> t_authors = new ArrayList<String>(); //instantiates new ArrayList for citation author names
							
							for (int j = 0; j < t_citation.getAuthors().size(); j++) { //loops through citation author list length
								String a = JOptionPane.showInputDialog(frame, "Edit author name " + (j+1) + ":", t_citation.getAuthors().get(j)); //prompt for each author name
								t_authors.add(a); //adds input to t_authors
							}
							
							int d = Integer.parseInt(JOptionPane.showInputDialog(frame, "Edit the year of publication: ", t_citation.getDate())); //prompt for citation publication year
							String j = JOptionPane.showInputDialog(frame, "Edit the journal of the paper: ", t_citation.getJournal()); //prompt for journal citation belongs to
							String l = JOptionPane.showInputDialog(frame, "Edit the link to the citation: ", t_citation.getLink()); //prompt for link to citaiton
							
							Citation edited_citation = new Citation(n,t_authors,j,d,l); //instantiates new citation using collected input data
							papers.get(i).editCitation(num, edited_citation); //calls editCitation method from Paper class to enter edited_citation at index num of the Paper's citations field
							
							updateTable(papers.get(i)); //updates table display with new paper data
							writePaperData(userText.getText()); //writes updated data to applicable .txt file
							
						} catch (RuntimeException r) {
							JOptionPane.showMessageDialog(frame, "Please enter a valid input.", "Error", JOptionPane.ERROR_MESSAGE); //error message for invalid input
						}
					}
				}
			} catch (RuntimeException r) {
				JOptionPane.showMessageDialog(frame, "Please have a citation selected from the table.", "Error", JOptionPane.ERROR_MESSAGE); //error message if citation is not selected from table
			}
		}
	};
	
	//Generate citation button ActionListener
	private ActionListener generateCitationButton = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) { //performs action when sign out button pressed
			
			String t_pname = (String)choosePaper.getSelectedItem(); //casts selected combobox item to string variable
			
			for (int i = 0; i < papers.size(); i++) { //loops through papers field
				if (t_pname.equals(papers.get(i).getName())) { //if combobox item chosen coincides with a paper name in the papers field
					
					Paper t_paper = papers.get(i); //instantiates Citation object and sets equal to papers.get(i)
					
					try { //try-catch for citation (table row) not being selected by user
						
						int num = citationTable.convertRowIndexToModel(citationTable.getSelectedRow()); //gets index of row selected
						
						Citation t_citation = t_paper.getCitations().get(num); //instantiates citation that is equal to citation of t_paper at index num
						String full_citation = t_citation.returnFormattedCitation(); //calls returnFormattedCitation method from citation class to get full formatted citation
						
						JOptionPane.showMessageDialog(frame, "Your generated citation is:" + "\n" + full_citation); //message that displays generated citation
						
					} catch (RuntimeException r) {
						JOptionPane.showMessageDialog(frame, "Please have a citation selected from the table.", "Error", JOptionPane.ERROR_MESSAGE); //error message if citation is not selected from table
					}
			
				}
			}
		}
	};
	
	/**
	 * This is the constructor for the Main class. It loads in user login data
	 * and instantiates all the necessary GUI elements for the program.
	 * When an instance is created in the main method, the program runs and the software opens.
	 */
	
	public Main() {
		
		loadUsers(); //reads userdata.txt and populates users field for login functionality
	
		frame = new JFrame(); //frame for main software window
		loginPage = new JPanel(); //panel representing login page
		
		frame.setSize(700,500); //sets size of software window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exits application on close
		frame.setTitle("Scientific Citation Manager"); //title of window
		frame.setResizable(false); //makes the window not able to be resized
		frame.add(loginPage); //adds login page to frame
		
		loginPage.setLayout(null); //allows full control for positioning of GUI elements
		
		/*
		 * Login page GUI Setup
		 */
		
		//productTitle - Title of program
		productTitle = new JLabel("Scientific Citation Manager");
		productTitle.setFont(new Font("Sans Serif",Font.BOLD,20)); //sets font and size of productTitle
		productTitle.setBounds(210,70,300,30); //positions productTitle (x,y,width,height)
		loginPage.add(productTitle); //adds to JPanel loginPage
		
		//welcomeHeading1 - "Welcome"
		welcomeHeading1 = new JLabel("Welcome");
		welcomeHeading1.setFont(new Font("Sans Serif",Font.PLAIN,20)); //plain text
		welcomeHeading1.setBounds(310,110,250,30);
		loginPage.add(welcomeHeading1);
		
		//WelcomeHeading2 - "Please Login or Sign Up"
		welcomeHeading2 = new JLabel("Please Login or Sign Up");
		welcomeHeading2.setFont(new Font("Sans Serif", Font.PLAIN,14)); //smaller font
		welcomeHeading2.setBounds(275,145,200,30);
		loginPage.add(welcomeHeading2);
		
		//userLabel - Label for username text field
		userLabel = new JLabel("Username:");
		userLabel.setBounds(230,185,80,25);
		loginPage.add(userLabel);
		
		//userText - Username text field
		userText = new JTextField(20); //20 character limit
		userText.setBounds(300,185,165,25); //70 pixels right of userLabel
		loginPage.add(userText);
		
		//passwordLabel - Label for password text field
		passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(230,215,80,25); //30 pixels below userLabel
		loginPage.add(passwordLabel);
		
		//passwordText - Password text field
		passwordText = new JPasswordField(20); //20 character limit
		passwordText.setBounds(300,215,165,25); //70 pixels right of passwordLabel
		loginPage.add(passwordText);
		
		//login - Button to login user
		login = new JButton("Login");
		login.setBounds(300,255,100,50);
		login.addActionListener(loginButton); //adds ActionListener loginButton to login
		loginPage.add(login);
		
		//signup - Button to sign up user
		signup = new JButton("Sign Up");
		signup.setBounds(300,315,100,50); //60 pixels below login
		signup.addActionListener(signupButton); //adds ActionListener signupButton to signup
		loginPage.add(signup);
		
		frame.setVisible(true); //makes the frame visible
		
		/*
		 * Overview Page GUI Setup
		 */
		
		overviewPage = new JPanel(); //JPanel for overview page
		overviewPage.setLayout(null); //layout set to null for full control over GUI layout
		
		//removePaper - Button that removes paper from database
		removePaper = new JButton("Remove Paper");
		removePaper.setBounds(150,15,120,40);
		removePaper.addActionListener(removePaperButton); //adds ActionListener removePaperButton to removePaper button
		overviewPage.add(removePaper);
		
		//addPaper - Button that adds paper to database
		addPaper = new JButton("Add Paper");
		addPaper.setBounds(20,15,120,40); //130 pixels left of removePaper
		addPaper.addActionListener(addPaperButton); //adds ActionListener addPaperButton to addPaper button
		overviewPage.add(addPaper);
		
		//signOut - Button that signs user out
		signOut = new JButton("Sign Out");
		signOut.setBounds(580,15,100,40);
		signOut.addActionListener(signoutButton); //adds ActionListener signoutButton to signout button
		overviewPage.add(signOut);
		
		//selectLabel - Label that prompts user to select a paper from dropdown
		selectLabel = new JLabel("Select Paper To View:");
		selectLabel.setFont(new Font("Sans Serif",Font.BOLD,16)); //16 pt size, bold font
		selectLabel.setBounds(255,70,300,30);
		overviewPage.add(selectLabel);
		
		//choosePaper - Combo box where user can select all papers specific to their account
		choosePaper = new JComboBox<Object>(); //each object is the name of a paper
		updateComboBox(); //calls updateComboBox() to get all data registered in the combo box
		choosePaper.setBounds(280,115,120,20);
		choosePaper.addActionListener(choosePaperSelect); //adds ActionListener choosePaperSelect to choosePaper
		overviewPage.add(choosePaper);
		
		//JTable implementation
		model = new DefaultTableModel(); //model for JTable
		model.addColumn("Citation Name"); //adding columns for each citation field
		model.addColumn("Authors");
		model.addColumn("Journal");
		model.addColumn("Year Published");
		model.addColumn("Link");
		
		citationTable = new JTable(model); //JTable intialization (taking model as passed parameter)
		citationTable.setBounds(20,145,520,300);
		citationTable.setRowSorter(new TableRowSorter(model)); //allows sorting functionality
		citationTable.setDefaultEditor(Object.class, null); //sets cells to be unable to be edited
		citationTable.setRowHeight(citationTable.getRowHeight() + 20); //increases default row height by 20 pixels

		scrollPane = new JScrollPane(citationTable); //scroll pane for table
		scrollPane.setBounds(20,145,520,300); //bounds are same as citationTable
		
		overviewPage.add(scrollPane); //adding scrollPane to overviewPage JPanel
		
		//addCitation - Button that allows users to add citations to the JTable
		addCitation = new JButton("Add Citation");
		addCitation.setBounds(555,200,130,40);
		addCitation.addActionListener(addCitationButton); //adds ActionListener signoutButton to signout
		overviewPage.add(addCitation);
		
		//removeCitation - JButton that allows users to remove citations from the JTable
		removeCitation = new JButton("Remove Citation");
		removeCitation.setBounds(555,250,130,40); //50 pixels below addCitation
		removeCitation.addActionListener(removeCitationButton); //adds ActionListener removeCitationButton to removeCitation button
		overviewPage.add(removeCitation);
		
		//editCitation - JButton that allows users to edit JTable values
		editCitation = new JButton("Edit Citation");
		editCitation.setBounds(555,300,130,40); //50 pixels below removeCitation
		editCitation.addActionListener(editCitationButton); //adds ActionListener editCitationButton to editCitation button
		overviewPage.add(editCitation);
		
		//generateCitation - JButton that allows users to generate a formatted string citation from a JTable selected row
		generateCitation = new JButton("Generate Citation");
		generateCitation.setBounds(555,350,130,40); //50 pixels below editCitation
		generateCitation.addActionListener(generateCitationButton); //adds ActionListener generateCitationButton to generateCitation button
		overviewPage.add(generateCitation);

	}
	
	/**
	 * This instance method reads the data from userdata.txt and loads it to the "users" ArrayList field.
	 * Run when the program starts (called in Main class constructor).
	 * 
	 * Pre-conditions: User data is written to userdata.txt in the correct format.
	 */
	
	public void loadUsers() {
		
		File usertxt = new File("userdata.txt"); //userdata.txt holds user data
		
		try { //try-catch for if file not found
			
			Scanner fileScanner = new Scanner(usertxt); //instantiates new scanner for usertxt
			
			while (fileScanner.hasNext()) { //while there are lines in the file not yet read by fileScanner
				String u = fileScanner.nextLine(); //"u" representing username
				String p = fileScanner.nextLine(); //"p" representing password
				User t_user = new User(decryptString(u),decryptString(p)); //instantiates new user based on u,p (decrypting both strings with decryptString method)
				users.add(t_user); //adds t_user to users ArrayList field
			}
			
			fileScanner.close(); //closes scanner
			
			System.out.println(users.toString()); //testing with system output
			
		} catch(FileNotFoundException e) {
			e.printStackTrace(); //outputs stack trace of the instance
		}
	}
	
	/**
	 * This instance method reads the data from the users field and writes it back to userdata.txt.
	 * Run whenever a user uses the sign up button to create a new user.
	 * 
	 * Pre-conditions: The username and password's characters are such that the encryptString method works.
	 */
	
	public void writeUsers() {
		
		File usertxt = new File("userdata.txt"); //userdata.txt holds user data
		FileWriter user_writer; //FileWriter user_writer aids in writing to files
		
		try { //try-catch for IOException
			
			user_writer = new FileWriter(usertxt); //instantiates new FileWriter
			
			for (int i = 0; i < users.size(); i++) { //loops through users ArrayList
				User t_user = users.get(i); //gets user at index i
				user_writer.write(t_user.writeToTxt()); //runs writeToTxt() function (from User class) for each user
				user_writer.write("\n"); //new line
			}
			
			user_writer.close(); //closes FileWriter
			
			
		} catch(IOException e) {
			e.printStackTrace(); //outputs stack trace of the instance
		}
	}
	
	/**
	 * This static method takes in a string parameter s and encrypts it such that it can be 
	 * securely written to a text file.
	 * 
	 * Pre-conditions: The passed parameter's characters are accounted for in the array "chars"
	 * within the method.
	 * 
	 * @param s
	 * @return
	 */
	
	public static String encryptString(String s) {
		
		HashMap<Character,Integer> code = new HashMap<Character, Integer>(); //instantiates HashMap - initial code for characters
		
		Character[] chars = {'A','a','B','b','C','c','D','d','E','e','F','f','G','g','H','h',
							 'I','i','J','j','K','k','L','l','M','m','N','n','O','o','P','p',
							 'Q','q','R','r','S','s','T','t','U','u','V','v','W','w','X','x',
							 'Y','y','Z','z','0','1','2','3','4','5','6','7','8','9','!','@',
							 '#','$','%','^','&','*','(',')',',','.','/','?',' '}; //possible characters for string s
		
		for (int i = 0; i < chars.length; i++) { //loops through chars array
			code.put(chars[i], i+1); //assigns values 1-77 for characters in HashMap
		}
		
		ArrayList<Integer> decode_nums = new ArrayList<Integer>(); //ArrayList of ints that represent passed string (converting using HashMap)
		
		for (int i = 0; i < s.length(); i++) { //loops through string s
			int temp_num = code.get(s.charAt(i)); //gets corresponding int value for each char using code
			decode_nums.add(temp_num); //adds to decode_nums ArrayList
		}
		
		if (decode_nums.size() % 2 == 1) { //if uneven ArrayList length
			decode_nums.add(77); //add int representing empty string to make length even
		}
		
		//2D array with a fixed size of 2 rows - represents matrix to be multiplied with the encoding matrix
		int[][] decode_nums_2D = new int[2][decode_nums.size()/2];
		
		for (int i = 0; i < decode_nums.size()/2; i++) { //loops through half the ArrayList size
			decode_nums_2D[0][i] = decode_nums.get(i); //assigns decode_nums_2D first row values
			decode_nums_2D[1][i] = decode_nums.get(decode_nums.size()/2 + i); //assigns second row values
		}
		
		//2D array representing encoded string matrix
		int[][] encode_nums_2D = new int[2][decode_nums.size()/2];
		
		int[][] e = {{3,5},
				 	 {1,2}}; //encoding matrix
		int erows = e.length; //# of rows in e
	
		int drows = decode_nums_2D.length; //# of rows in decode_nums_2D
		int dcolumns = decode_nums_2D[0].length; //# of columns in decode_nums_2D
		
		//triple nested loop for matrix multiplication: e * decode_nums_2D = encode_nums_2D
		for (int i = 0; i < erows; i++) {
			for (int j = 0; j < dcolumns; j++) {
				for (int k = 0; k < drows; k++) {
					encode_nums_2D[i][j] += e[i][k] * decode_nums_2D[k][j];
				}
			}
		}
		
		String encode_nums = ""; //represents encoded string
		
		//2D array traversal
		for (int i = 0; i < encode_nums_2D.length; i++) {
			for (int j = 0; j < encode_nums_2D[i].length; j++) {
				encode_nums += encode_nums_2D[i][j] + " "; //adds each int with whitespace in between
			}
		}
		
		return encode_nums.trim(); //returns trimmed encoded string
		
	}
	
	/**
	 * This static method takes in a string parameter s and decrypts it such that 
	 * it can be loaded as data into the program when the software is run.
	 * This method is very similar to the encryption method; 
	 * the decoding matrix is simply the inverse of the encoding matrix.
	 * 
	 * Pre-conditions: The same pre-conditions of the encryptString method applies.
	 * 
	 * @param s
	 * @return
	 */
	
	public static String decryptString(String s) {
		
		HashMap<Integer,Character> code = new HashMap<Integer, Character>(); //initial code for characters
		
		Character[] chars = {'A','a','B','b','C','c','D','d','E','e','F','f','G','g','H','h',
				 			 'I','i','J','j','K','k','L','l','M','m','N','n','O','o','P','p',
				 			 'Q','q','R','r','S','s','T','t','U','u','V','v','W','w','X','x',
				 			 'Y','y','Z','z','0','1','2','3','4','5','6','7','8','9','!','@',
				 			 '#','$','%','^','&','*','(',')',',','.','/','?',' '}; //possible characters for string s
		
		for (int i = 0; i < chars.length; i++) { //loops through chars array
			code.put(i+1, chars[i]);  //assigns ints 1-77 the character values from chars
		}
		
		String[] encode_str_arr = s.split(" "); //splits the string s by whitespace
		int[] encode_int_arr = new int[encode_str_arr.length]; //array of encoded ints
		
		for (int i = 0; i < encode_str_arr.length; i++) { //loops through split array
			encode_int_arr[i] = Integer.parseInt(encode_str_arr[i]); //adds ints to encode_int_arr
		}
		
		//2D array representing encoded string matrix
		int[][] encode_2D = new int[2][encode_int_arr.length/2];
		
		for (int i = 0; i < encode_int_arr.length/2; i++) { //loops through half the array length
			encode_2D[0][i] = encode_int_arr[i]; //assigns first row values
			encode_2D[1][i] = encode_int_arr[encode_int_arr.length/2 + i]; //assigns second row values
		}
		
		int[][] e_inverse = {{2,-5},
	 			 			 {-1,3}}; //decoding matrix
		
		//2D array representing decoded string matrix
		int[][] decode_2D = new int[2][encode_int_arr.length/2];
		
		int erows = e_inverse.length; //# of rows in e_inverse
		int ecolumns = e_inverse[0].length; //# of columns in e_inverse
		
		int rows = encode_2D.length; //# of rows in encode_2D
		int columns = encode_2D[0].length; //# of columns in encode_2D
		
		//triple nested loop for matrix multiplication: e_inverse * encode_2D = decode_2D
		for (int i = 0; i < erows; i++) {
			for (int j = 0; j < columns; j++) {
				for (int k = 0; k < rows; k++) {
					decode_2D[i][j] += e_inverse[i][k] * encode_2D[k][j];
				}
			}
		}
		
		String decode_str = ""; //represents decoded string
		
		//2D array traversal
		for (int i = 0; i < decode_2D.length; i++) {
			for (int j = 0; j < decode_2D[i].length; j++) {
				char t_char = code.get(decode_2D[i][j]); //gets char from int value
				decode_str += t_char; //adds each char to decode_str
			}
		}
		
		return decode_str.trim(); //returns trimmed decoded string
		
	}
	
	/**
	 * This instance method  updates the combo box choosePaper by looping through the ArrayList papers
	 * and re-adding each name to choosePaper. it is called when the combo box needs to be re-populated.
	 */
	
	public void updateComboBox() { //doesn't return anything
		
		choosePaper.removeAllItems(); //removes all items from the combo box
		
		choosePaper.addItem(""); //adds empty string as item to choosePaper; this is what the default option is set to

		for (int i = 0; i < papers.size(); i++) { //loops through the ArrayList papers field
			String temp = papers.get(i).getName(); //gets name of each paper
			choosePaper.addItem(temp); //adds as option to combo box
		}
	}
	
	/**
	 * This static method updates the table display on the overview page based on data from
	 * the passed Paper parameter p. It is called whenever a change is made to currently selected
	 * paper data or the JComboBox choosePaper is interacted with.
	 * 
	 * @param p
	 */
	
	public static void updateTable(Paper p) {
		
		model.setRowCount(0); //clears table of all data
		
		String[] columns = {"Paper Name", "Authors", "Journal", "Year Published", "Link"}; //array for column labels
		
		ArrayList<Citation> t_citations = p.getCitations(); //ArrayList of citations from passed parameter
		
		for (int i = 0; i < t_citations.size(); i++) { //loops through ArrayList
			String[] t_row = t_citations.get(i).toArray(); //calls toArray() method from Citation class for each citation
			model.addRow(t_row); //adds array as a row to the table model
		}
	}
	
	/**
	 * This instance method takes in a string parameter u (for the username of a user)
	 * and reads the data from the .txt file corresponding to that user's specific paper data
	 * to populate the papers ArrayList field.
	 * 
	 * Pre-conditions: Data in the text file is formatted correctly. 
	 * 
	 * @param u
	 */
	
	public void readPaperData(String u) {
		
		File datatxt = new File(u + ".txt"); //username.txt

		try { //try-catch for file not found exception
			
			Scanner fileScanner = new Scanner(datatxt); //instantiates new scanner for datatxt
			
			while (fileScanner.hasNext()) { //while there are lines in the file not read yet by fileScanner
				
				//Note: decryptString method necessary as file data is encrypted.
				
				String n = decryptString(fileScanner.nextLine()); //name of paper
				int num_c = Integer.parseInt(decryptString(fileScanner.nextLine())); //number of citations in the paper
				
				ArrayList<Citation> t_citations = new ArrayList<Citation>(); //ArrayList of paper's citations
				
				for (int i = 0; i < num_c; i++) { //loops for num_c times
					
					String c_name = decryptString(fileScanner.nextLine()); //citation name
					String a = decryptString(fileScanner.nextLine()); //author names in singular string format
					String[] authors = a.split(", "); //splits a by ", " to get author names in array
					ArrayList<String> list_authors = new ArrayList<String>(); //instantiates new ArrayList for list of citation authors
					
					for (int j = 0; j < authors.length; j++) { //loops through authors array
						list_authors.add(authors[j]); //adds each author to list_authors
					}
					
					String journal = decryptString(fileScanner.nextLine()); //journal citation belongs to
					int date = Integer.parseInt(decryptString(fileScanner.nextLine())); //publication year of citation
					String link = decryptString(fileScanner.nextLine()); //link to citation
					
					Citation t_citation = new Citation(c_name,list_authors,journal,date,link); //instantiates new Citation with data read from scanner
					t_citations.add(t_citation); //adds to t_citations ArrayList
					
				}
				
				Paper t_paper = new Paper(n,t_citations); //uses constructor to instantiate new Paper object
				papers.add(t_paper); //adds to papers field

			}
			
			fileScanner.close(); //closes scanner
			
		} catch(FileNotFoundException e) {
			e.printStackTrace(); //outputs stack trace of the instance
		}
	}
	
	/**
	 * This instance method takes in a string parameter u (for the username of a user)
	 * and writes the paper data for that user to the corresponding .txt file where said 
	 * data is held.
	 * 
	 * Pre-conditions: All fields are properly filled out for citations and papers.
	 * 
	 * @param u
	 */
	
	public void writePaperData(String u) {
		
		File datatxt = new File(u + ".txt"); //filepath to username.txt
		
		FileWriter data_writer; //FileWriter data_writer aids in writing to files
		
		try { //try-catch for IOException
			

			
			data_writer = new FileWriter(datatxt); //instantiates new FileWriter
			
			for (int i = 0; i < papers.size(); i++) { //loops through papers field
				Paper t_paper = papers.get(i); //gets i
				data_writer.write(t_paper.writeToTxt()); //runs writeToTxt() function (from Paper class) for each paper
				data_writer.write("\n"); //new line
			}
			
			data_writer.close(); //closes FileWriter
			
			
		} catch(IOException e) {
			e.printStackTrace(); //outputs stack trace of the instance
		}
		
	}
	
	//Main method
	public static void main(String[] args) {
		Main m = new Main(); //instantiating Main object in main method runs the program
	}
}