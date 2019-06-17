

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;
import java.util.Random;
import java.util.Scanner;

/**
 * This class defines how the a MarkovModel object is defined,
 * how it is trained and how to convert it to String.
 */
public class MarkovModel {

    protected HashMap<String, WordCountList> predictionMap;
    // key = String of prefix words,
    // value = WordCountList of predicted words and number of occurrences
    protected int degree;         // how many words/chars are in our prefix
    protected Random random;    // random number generator for picking things
    protected boolean isWordModel;// determines whether word or char model
    protected final static char DELIMITER = '\u0000';

    /**
     * Constructor that takes an int and a boolean value as parameters
     * @param degree how many words/chars are in prefix
     * @param isWordModel determines whether word or char model
     */
    public MarkovModel (int degree, boolean isWordModel) {
        this.predictionMap = new HashMap<>();
        this.degree = degree;
        this.isWordModel = isWordModel;
        this.random = new Random();
    }

    /**
     * Helper method to train the content of a file as Word Model.
     * @param content the training content as a single String
     */
    private void trainWordModel(String content) {
        //read in all the words in content into a ArrayList of String
        Scanner words = new Scanner(content);
        ArrayList<String> wordList = new ArrayList<>();
        while (words.hasNext()) {
            String s1 = words.next();
            if (!s1.equals("\n")) {
                wordList.add(s1);
            }
        }
        words.close();
        //Wrap around the training content
        for(int i = 0; i< degree; i++) {
            wordList.add(wordList.get(i));
        }
        //generate key(prefix)
        for(int i = 0; i < wordList.size() - degree; i++) {
            String key = "";
            for(int j = i; j< i+degree;j++) {
                key = key + wordList.get(j) + DELIMITER;
            }
            //check if the key(prefix) is already in the predictionMap
            //add key to predictionMap if it is not in it
            if (!predictionMap.containsKey(key.toLowerCase())) {
                predictionMap.put(key.toLowerCase(), new WordCountList());
                WordCountList w = predictionMap.get(key.toLowerCase());
                w.add(wordList.get(i+degree).toLowerCase());
            }
            // otherwise, just update the WordCountList
            // that is associated with the key
            else{
                WordCountList w = predictionMap.get(key.toLowerCase());
                w.add(wordList.get(i+degree).toLowerCase());
            }

        }
    }

    /**
     * Helper method to train the content of a file as Character Model
     * @param content the training content as a single String
     */
    private void trainCharacterModel(String content){
        //Wrap around the training content
        content = content + content.substring(0,degree);
        //put all the char in content into a ArrayList of String
        ArrayList<String> charList = new ArrayList<>();
        for(int i = 0; i<content.length();i++){
            charList.add("" + content.charAt(i));
        }
        //generate key(prefix)
        for(int i = 0; i < charList.size() - degree; i++) {
            String key = "";
            for(int j = i; j< i+degree;j++) {
                key = key + charList.get(j) + DELIMITER;
            }
            //check if the key(prefix) is already in the predictionMap
            //add key to predictionMap if it is not in it
            if (!predictionMap.containsKey(key.toLowerCase())) {
                predictionMap.put(key.toLowerCase(), new WordCountList());
                WordCountList w = predictionMap.get(key.toLowerCase());
                w.add(charList.get(i+degree).toLowerCase());
            }
            // otherwise, just update the WordCountList
            // that is associated with the key
            else{
                WordCountList w = predictionMap.get(key.toLowerCase());
                w.add(charList.get(i+degree).toLowerCase());
            }
        }
    }

    /**
     * This method takes a text file name, and train the content of the file
     * using Markov Model either as Word Model or Character Model
     * @param filename  the name of the text file used to train the model
     */
    public void trainFromText(String filename) {
        //Read the entire file, save the content as a string
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // `content` contains everything from the file, in one single string
        //train as Word Model
        if(isWordModel) {
            trainWordModel(content);
        }
        //train as Character Model
        else{
            trainCharacterModel(content);
        }
    }

    /**
     * This method takes a prefix and find its prediction WordCountList,
     * and return a list of words where each word is repeated
     * the same amount of times as its frequency
     * @param prefix a String of <degree> word/character strings
     * that comes immediately before the prediction word.
     * @return a list of words predicted by the prefix,
     * where each word is repeated the same amount of times as its frequency
     */
    public ArrayList<String> getFlattenedList(String prefix){
        if(prefix == null) return null;
        ArrayList<String> flattened = new ArrayList<>();
        WordCountList w = predictionMap.get(prefix);
        ArrayList<WordCount> wordlist = w.getList();
        //add the same frequency of every word to the Arraylist
        for(int i = 0; i<wordlist.size();i++){
            int count = wordlist.get(i).getCount();
            String word = wordlist.get(i).getWord();
            for(int j = 0; j< count; j++){
                flattened.add(word);
            }
        }
        return flattened;
    }

    /**
     * This method takes a prefix and find its prediction
     * word by randomly select a word from the prediction word list
     * @param prefix a String of <degree> word/character strings
     * that comes immediately before the prediction word.
     * @return a word that the model predicts based on the prefix
     */
    public String generateNext(String prefix) {
        if(prefix == null) return null;
        ArrayList<String> flattened = getFlattenedList(prefix);
        //generate a valid integer within range
        int next = random.nextInt(flattened.size());

        return flattened.get(next);
    }

    /**
     * This method generates count number of words/characters
     * based on the Markov language model
     * @param count number of words/characters in your returned String
     * @return a String of <count> words / characters generated
     */
    public String generate(int count) {
        if(count <= 0) return null;
        //get all the keys in predictionMap and put them into an Arraylist
        ArrayList<String> keys = new ArrayList<>(predictionMap.keySet());
        //randomly select a prefix from all the available prefixes
        String prefix = keys.get(random.nextInt(keys.size()));
        ArrayList<String> text = new ArrayList<>();
        text.add(prefix);
        for(int i = 0; i < count - degree; i++){
            String next = generateNext(prefix)+DELIMITER;
            //add the prediction word/character to text
            text.add(next);
            //update the prefix
            String temp = prefix + next;
            prefix = temp.substring(prefix.indexOf(DELIMITER)+1);
        }
        String texts ="";
        if(isWordModel) {
            for(String s : text){
                texts += s;
            }
            texts = texts.replaceAll(""+DELIMITER," ");
        }
        else{
            for(String s : text){
                texts += s;
                texts = texts.replaceAll(""+DELIMITER,"");
            }
        }

        return texts;
    }

    /**
     * This method returns a String representing
     * the specifics of the MarkovModel
     * @return Return a String representation of a MarkovModel object.
     */
    @Override 
    public String toString(){
        int cnt = 1;
        int totalCnt = predictionMap.size();
        String result = "";
        for(String key : predictionMap.keySet()){
            result += key + ": " + predictionMap.get(key).toString()+ '\n';
            System.out.println("Training " + cnt + "/" +totalCnt + "\n");
            cnt++;
        }
        //replace all DELIMITER with space
        result = result.replaceAll(""+DELIMITER," ");
        return result;
    }

    public static void main(String[] args) {
        MarkovModel m = new MarkovModel(2, true);
        m.trainFromText("paul.txt");
        String s = "i" + DELIMITER + "like" + DELIMITER;
        ArrayList<String> list = m.getFlattenedList(s);
        System.out.println(list.toString());
        //String output = m.toString();
        //System.out.println(output);
    }
}
