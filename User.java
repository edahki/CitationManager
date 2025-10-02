import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This is the User class. It has two String fields for the username and password
 * of a User respectively.
 */

public class User {
	
	//INSTANCE FIELDS
	
	private String username; //string for username
	private String password; //string for password
	
	//STATIC FIELDS
	
	private static int count; //static field for number of total papers instantiated
		
	//CONSTRUCTORS
	
	/**
	 * Default constructor; sets String fields to empty strings
	 * and increments count by 1.
	 */
	
	public User() {
		this.username = ""; //sets username to empty string
		this.password = ""; //sets password to empty string
		count++; //increments count
	}
	
	/**
	 * Constructor that takes two String parameters u and p and sets them
	 * to the username and password field of the instance respectively.
	 * 
	 * @param u
	 * @param p
	 */
	
	public User(String u, String p) { //overloaded constructor
		this.username = u; //sets u to username
		this.password = p; //sets p to password
		count++; //increments count
	}
		
	//GET METHODS
	
	/**
	 * Get method that returns the username field of the instance.
	 * @return
	 */
	
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Get method that returns the password field of the instance.
	 * @return
	 */
		
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * Get method that returns the count field.
	 * @return
	 */
		
	public static int getCount() {
		return count;
	}
	
	//SET METHODS
	
	/**
	 * Instance method that takes a String parameter u and sets
	 * it to the username field of the instance.
	 * @param u
	 */
	
	public void setUsername(String u) {
		this.username = u;
	}
	
	/**
	 * Instance method that takes a String parameter p and sets
	 * it to the password field of the instance.
	 * @param p
	 */
		
	public void setPassword(String p) {
		this.password = p;
	}
	
	//GENERAL METHODS

	/**
	 * Instance method that takes no parameters and returns a string representation
	 * of the implied User object.
	 * @return
	 */
	
	public String toString() {
		return this.username + " " + this.password;
		//example output: User1 Thisisapassword
	}
	
	/**
	 * Instance method that returns User data in the correct string format for writing to files.
	 * This includes the encryption of all fields (using the encryptString method from the Main class)
	 * and adding new lines to sequentially write data to.
	 * 
	 * Pre-condition: There are no invalid characters within the username and password field that are not
	 * accounted for in the HashMap within the encryptString method of the Main class.
	 * 
	 * @return
	 */
	
	public String writeToTxt() {
		
		String un = this.username; //username of instance
		String pw = this.password; //password of instance
		
		String encoded_un = Main.encryptString(un); //using encryptString method from Main class to encode username
		String encoded_pw = Main.encryptString(pw); //using encryptString method to encode password
		
		return encoded_un + "\n" + encoded_pw; //returns encoded username and password on different lines
		
	}
		
}