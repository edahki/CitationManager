import java.util.ArrayList; 
import java.util.Arrays;

/**
 * This is the Paper class. It has a String field for the title/name of a paper and an ArrayList field
 * of Citation objects for the citations within said paper. There is also a static field that counts the
 * number of instantiated Papers. The general methods of this class mostly revolve around editing citations
 * in a given Paper and writing data to the .txt file representing the database.
 */

public class Paper {
	
	//INSTANCE FIELDS
	
	private String name; //name/title of the paper
	private ArrayList<Citation> citations = new ArrayList<Citation>(); //list of citations in the paper
	
	//STATIC FIELDS
	
	private static int count; //counts the number of instantiated Papers
	
	//CONSTRUCTORS
	
	/**
	 * Default constructor; sets name to empty string and citations to null.
	 * Count increments by 1.
	 */
	
	public Paper() {
		name = ""; //sets name to empty string
		citations = null; //sets citations to null
		count++; //increments count
	}
	
	/**
	 * Constructor that takes parameters n and c and sets them as
	 * the name and citations fields respectively.
	 * 
	 * @param n
	 * @param c
	 */
	
	public Paper(String n, ArrayList<Citation> c) { //overloaded constructor
		this.name = n; //sets name to passed parameter n
		this.citations = c; //sets citations to passed parameter c
		count++; //increments count
	}
	
	//GET METHODS
	
	/**
	 * Get method that returns the name field of the instance.
	 * @return
	 */
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get method that returns the citations field of the instance.
	 * @return
	 */
	
	public ArrayList<Citation> getCitations() {
		return this.citations;
	}
	
	/**
	 * Get method that returns the count field.
	 * @return
	 */
	
	public int getCount() {
		return count;
	}
	
	//SET METHODS
	
	/**
	 * Instance method that takes a String parameter n and sets
	 * it to the name field of the instance.
	 * @param n
	 */
	
	public void setName(String n) {
		this.name = n;
	}
	
	/**
	 * Instance method that takes an ArrayList parameter c and sets
	 * it to the citations field of the instance.
	 * @param c
	 */
	
	public void setCitations(ArrayList<Citation> c) {
		this.citations = c;
	}
	
	//GENERAL METHODS
	
	/**
	 * Instance method that takes a Citation parameter c and adds
	 * it to the instance's citations ArrayList.
	 * @param c
	 */
	
	public void addCitation(Citation c) {
		this.citations.add(c); //adds c to citations ArrayList of instance
	}
	
	/**
	 * Instance method that takes a Citation parameter c and removes
	 * it from the instance's citations ArrayList.
	 * @param c
	 */
	
	public void removeCitation(Citation c) { //unused
		this.citations.remove(c); //removes c from citations ArrayList of instance
	}
	
	/**
	 * Instance method that removes a citation by index from the instance's citations field. 
	 * The integer parameter i is this index.
	 * @param i
	 */
	
	public void removeCitation(int i) { //overloaded method
		this.citations.remove(i); //removes by index
	}
	
	/**
	 * Instance method that replaces a specific citation from the instance's citations field with a new one.
	 * The integer parameter i is the index in the ArrayList where the citation is replaced and the Citation 
	 * parameter c is the citation replacing the previous list element.
	 * 
	 * @param i
	 * @param c
	 */
	
	public void editCitation(int i, Citation c) {
		this.citations.set(i, c); //using .set method from ArrayList class; sets index i to citation c
	}
	
	/**
	 * Instance method that returns Paper data in the correct string format for writing to files.
	 * This includes the encryption of all fields (using the encryptString method from the Main class)
	 * and adding new lines to sequentially write data to.
	 * 
	 * Pre-conditions: The pre-conditions of the .writeToTxt method of the Citation class apply.
	 * 
	 * @return
	 */
	
	public String writeToTxt() {
		
		String n = Main.encryptString(this.name); //encrypted paper name
		String num_c = Main.encryptString(Integer.toString(this.citations.size())); //encrypted number of citations in the paper 
		String c = ""; //string representing all citation data; instantiated as empty string
		
		for (int i = 0; i < this.citations.size(); i++) { //looping through the citations ArrayList
			if (i == this.citations.size()-1) { //if the last citation has been reached
				c += citations.get(i).writeToTxt(); //call the .writeToTxt() method from the Citation class and add to existing String c
			} else { //for all other cases
				c += citations.get(i).writeToTxt() + "\n"; //call the .writeToTxt() method from the Citation class + a new line and add to existing String c
			}
		}

		return n + "\n" + num_c + "\n" + c; //return encrypted name + new line + encrypted number of citations + new line + encrypted citation data

	}
}