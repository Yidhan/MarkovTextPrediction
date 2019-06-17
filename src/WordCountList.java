

import java.util.*;
import java.io.*;

/**
 * This class defines how the a WordCountList object is defined,
 * how to add words to the list of the object
 * and how to convert it to String.
 */
public class WordCountList {

	ArrayList<WordCount> list;

	/**
	 * Default no-arg constructor for class WordCountList
	 */
	public WordCountList() {
		this.list = new ArrayList<>();
	}

	/**
	 * Helper method to get the value of instance variable list
	 * @return list
	 */
	public ArrayList<WordCount> getList(){
		return this.list;
	}

	/**
	 * This method takes a String word,
	 * if there exists a WordCount object in list that has word
	 * as its String value, increment its count value.Otherwise,
	 * create a new WordCount object with the word, and append it to the list.
	 * @param word the word to find or to create a WordCount object with
	 */
	public void add(String word) {
		if(word == null) return;
		word = word.toLowerCase();
		// check if there exists a WordCount object in list that has word
		// as its String value
		if(locationInList(list,word) > -1){
			//increment its count value
			list.get(locationInList(list,word)).increment();
		}
		// if not, create a new WordCount object with the word,
		// and append it to the list
		else{
			WordCount newWordCount = new WordCount(word);
			list.add(newWordCount);
		}
	}

	/**
	 * Helper method to locate the WordCount object in the list
	 * @param list the WordCount list
	 * @param word the word to find
	 * @return the index of the WordCount object in the list
	 * that contains the word, return -1 if the word is
	 * not in any of the WordCount objects.
	 */
	private int locationInList(ArrayList<WordCount> list,String word){
		//check if word is in list, return its index
		for(int i = 0; i < list.size(); i++){
			if((list.get(i).getWord().toLowerCase()).equals(word)){
				return i;
			}
		}
		//if word is not in list, return -1
		return -1;
	}

	/**
	 * This method returns a String representing
	 * the specifics of the WordCountList
	 * @return Return a String representation of a WordCountList object.
	 */
	@Override
	public String toString() {
		String a = "";
		String b = "";
		for(int i = 0; i < list.size(); i++){
			if(i == list.size()-1){
				b = list.get(i).getWord() + "(" +list.get(i).getCount() + ")";
				a = a + b;
			}
			//make sure there is no trailing space after the last word(count)
			else {
				b = list.get(i).getWord() + "(" +
						list.get(i).getCount() + ")" + " ";
				a = a + b;
			}
		}
		return a;
	}
}
