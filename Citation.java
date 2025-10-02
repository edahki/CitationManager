import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * This is the citation class. It has a String field for the name/title of the cited paper, an ArrayList field of strings
 * for the authors of the citation, a String field for the journal the citation belongs to, an int field for the year published,
 * and a String field for a link to the citation.
 */

public class Citation {
	
	//INSTANCE FIELDS
	
	private String name; //name of cited paper
	private ArrayList<String> authors = new ArrayList<String>(); //authors of cited paper
	private String journal; //journal that paper belongs to
	private int publishDate; //year of publication
	private String link; //link to citation source
	
	//STATIC FIELDS
	
	private static int count; //number of instantiated citations
	
	//CONSTRUCTORS
	
	/**
	 * Default constructor; sets string fields to empty string, 
	 * publishDate to 0 and authors to null. Increments count by 1.
	 */
	
	public Citation() {
		name = ""; //sets name to empty string
		authors = null; //sets authors to null
		journal = ""; //sets journal to empty string
		publishDate = 0; //sets publishDate to 0
		link = ""; //sets link to empty string
		count++; //increments count
	}
	
	/**
	 * Constructor that takes parameters for each field of the Paper class
	 * and sets them all equal respectively.
	 * 
	 * @param n
	 * @param a
	 * @param j
	 * @param d
	 * @param l
	 */
	
	public Citation (String n, ArrayList<String> a, String j, int d, String l) { //overloaded constructor
		this.name = n; //sets name to n
		this.authors = a; //sets authors to a
		this.journal = j; //sets journal to j
		this.publishDate = d; //sets publishDate to d
		this.link = l; //sets link to l
		count++; //increments count
	}
	
	//GET METHODS
	
	/**
	 * Get method that that returns the name field of the instance.
	 * @return
	 */
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get method that returns the authors field of the instance.
	 * @return
	 */
	
	public ArrayList<String> getAuthors() {
		return this.authors;
	}
	
	/**
	 * Get method that returns the journal field of the instance.
	 * @return
	 */
	
	public String getJournal() {
		return this.journal;
	}
	
	/**
	 * Get method that returns the date field of the instance.
	 * @return
	 */
	
	public int getDate() {
		return this.publishDate;
	}
	
	/**
	 * Get method that returns the link field of the instance.
	 * @return
	 */
	
	public String getLink() {
		return this.link;
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
	 * Instance method that takes a String parameter n and
	 * sets it to the name field of the instance.
	 * 
	 * @param n
	 */
	
	public void setName(String n) {
		this.name = n;
	}

	/**
	 * Instance method that takes an ArrayList<String> parameter a
	 * and sets it to the authors field of the instance.
	 * 
	 * @param a
	 */
	
	public void setAuthors(ArrayList<String> a) {
		this.authors = a;
	}
	
	/**
	 * Instance method that takes a String parameter j and
	 * sets it to the journal field of the instance.
	 * 
	 * @param j
	 */
	
	public void setJournal(String j) {
		this.journal = j;
	}
	
	/**
	 * Instance field that takes an int parameter d and 
	 * sets it to the publishDate field of the instance.
	 * 
	 * @param d
	 */
	
	public void setDate(int d) {
		this.publishDate = d;
	}
	
	/**
	 * Instance method that takes a String parameter l and
	 * sets it to the link field of the instance.
	 * 
	 * @param l
	 */
	
	public void setLink(String l) {
		this.link = l;
	}
	
	//GENERAL METHODS
	
	/**
	 * Instance method that instantiates and returns an array of 
	 * strings representing each field of the implied Citation object.
	 * 
	 * @return
	 */
	
	public String[] toArray() {

		String a = authors.toString(); //utilizing the toString() method of the ArrayList class
		a = a.replace("[",""); //replacing brackets with empty strings
		a = a.replace("]","");
		
		String[] arr = {this.name, a, this.journal, Integer.toString(this.publishDate), this.link}; //instantiates array with each field (note publishDate needs to be cast to a string)
		return arr; //returns instantiated array
	}
	
	/**
	 * Instance method that returns Citation data in the correct string format for writing to files.
	 * This includes the encryption of all fields (using the encryptString method from the Main class)
	 * and adding new lines to sequentially write data to.
	 * 
	 * Pre-conditions: There are no invalid characters within the implied object's fields that are not
	 * accounted for in the HashMap within the encryptString method of the Main class.
	 * 
	 * @return
	 */
	
	public String writeToTxt() {
		
		String c_n = Main.encryptString(this.name); //encrypted citation name
		String t_a = this.authors.toString(); //utilizing .toString() method of ArrayList class
		t_a = t_a.replace("[",""); //replacing brackets with empty strings
		t_a = t_a.replace("]","");
		String a = Main.encryptString(t_a); //encrypted list of authors (in string format)
		String j = Main.encryptString(this.journal); //encrypted journal
		String p = Main.encryptString(Integer.toString(this.publishDate)); //encrypted publishDate (need to cast to string first)
		String l = Main.encryptString(this.link); //encrypted link
		
		return c_n + "\n" + a + "\n" + j + "\n" + p + "\n" + l; //returns each encrypted field, separated by new lines
		
		
	}
	
	/**
	 * Instance method that takes no parameters and returns a string
	 * representing the implied citation object formatted in APA 7th edition.
	 * Used for the generate citation button in the program.
	 * 
	 * Pre-condition: Authors for a citation are formatted correctly
	 * such that the first, middle, and last names are separated by a space.
	 * 
	 * @return
	 */
	
	public String returnFormattedCitation() {
		
		int size = this.getAuthors().size(); //size of the authors ArrayList field of the instance
		
		ArrayList<String> t_authors = new ArrayList<String>(); //instantiates new ArrayList for each author of the citation
		
		for (int j = 0; j < size; j++) { //looping through the authors field of the instance
			
			String t_author = ""; //instantiates empty string for each formatted author
			
			String[] temp_arr = this.getAuthors().get(j).split(" "); //uses .split() to instantiate new array containing each word 
			
			t_author += temp_arr[temp_arr.length-1] + ", "; //adds last element of temp_arr (expected last name) + ", " to t_author
			
			for (int k = 0; k < temp_arr.length-1; k++) { //loops through the rest of temp_arr (excluding last element)
				t_author += temp_arr[k].charAt(0) + ". "; //adds first character of each element + ". " to t_author
			}
			
			t_authors.add(t_author.trim()); //adds trimmed t_author string to t_authors ArrayList
		}
		
		String full_citation = ""; //instantiates empty string for full citation
		
		for (int l = 0; l < t_authors.size(); l++) { //loops through t_authors ArrayList
			if (l == t_authors.size()-1) { //for the last element of t_authors
				full_citation += " " + t_authors.get(l); //add " " + the last element to full_citation
			} else {
				full_citation += t_authors.get(l) + " &"; //add the element at index l + " &" to full_citation
			}
		}
		
		full_citation += " (" + this.getDate() + ")" + "."; //add publish year in brackets + "." to full_citation
		full_citation += " " + this.getName() + "."; //add " " + citation name + "." to full_citation
		full_citation += " " + this.getJournal() + "."; //add " " + journal + "." to full_citation
		full_citation += " " + this.getLink() + "."; //add " " + link + "." to full_citation
		
		return full_citation; //return full formatted citation
		
	}
}