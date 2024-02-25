package cs1501_p2;

import java.util.ArrayList;
import java.util.HashMap;

public class UserHistory implements Dict {
    private UserHistoryNode root = new UserHistoryNode();
    private UserHistoryNode charNode = root;
    private String input = "";
    private int size = 0;
    public ArrayList<String> history = new ArrayList<String>();

    public UserHistory() {
    }

    public UserHistory(ArrayList<String> history) {
        this.history = history;
        for(String h: history) {
            put(root, h, 0); 
        }
    }

    private UserHistoryNode put(UserHistoryNode x, String key, int d) {
        if (x == null) {
            x = new UserHistoryNode();
        }
        if (d == key.length()) {
            x.value++; 
            if (x.value == 1) {
                size++;
            }
            return x;
        }
        char c = key.charAt(d);
        x.next[c] = put(x.next[c], key, d+1);
        return x;
    }


    public int get(String key) {
        UserHistoryNode x = get(root, key, 0);
        if (x == null) {
            return 0;
        }
        return x.value;
    }
    private UserHistoryNode get(UserHistoryNode x, String key, int d) {
        if (x == null) {
            return null;
        }
        if (d == key.length()) {
            return x;
        }
        char c = key.charAt(d);
        return get(x.next[c], key, d+1);
    }

    
    /**
     * Add a new word to the dictionary
     *
     * @param key New word to be added to the dictionary
     */
    public void add(String key) {
        put(root, key, 0); 
        history.add(key);
    }

    /**
     * Check if the dictionary contains a word
     *
     * @param key Word to search the dictionary for
     *
     * @return true if key is in the dictionary, false otherwise
     */
    public boolean contains(String key) {
        UserHistoryNode x = get(root, key, 0);
        if (x == null) {
            return false;
        }
        return x.value > 0;
    }

    /**
     * Check if a String is a valid prefix to a word in the dictionary
     *
     * @param pre Prefix to search the dictionary for
     *
     * @return true if prefix is valid, false otherwise
     */
    public boolean containsPrefix(String pre) {
        UserHistoryNode x = get(root, pre, 0);
        if (x == null) {
            return false;
        }
        return x.value >= 0;
    }

    /**
     * Search for a word one character at a time
     *
     * @param next Next character to search for
     *
     * @return int value indicating result for current by-character search:
     *         -1: not a valid word or prefix
     *         0: valid prefix, but not a valid word
     *         1: valid word, but not a valid prefix to any other words
     *         2: both valid word and a valid prefix to other words
     */
    public int searchByChar(char next) {
        input += next;
        if (charNode == null) {
            return -1;
        }
        if(charNode.next[next] == null) {
            return -1;
        } else {
            charNode = charNode.next[next];
            if(charNode.value > 0) {
                for(int i = 0; i < 255; i++) {
                    if(charNode.next[i] != null) {
                        return 2;
                    }
                }
                return 1;
            }
            return 0;
        }
    }

    /**
     * Reset the state of the current by-character search
     */
    public void resetByChar() {
        charNode = root;
        input = "";
    }

    /**
     * Suggest up to 5 words from the dictionary based on the current
     * by-character search. Ordering should depend on the implementation.
     * 
     * @return ArrayList<String> List of up to 5 words that are prefixed by
     *         the current by-character search
     */
    public ArrayList<String> suggest() {
        HashMap<String, Integer> suggestionMap = new HashMap<String, Integer>();
        ArrayList<String> suggestions = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        suggest_cur(charNode, input, "", suggestionMap);
        suggestions = insertionSort(suggestionMap);
        for(int i = 0; i < 5 && i < suggestions.size(); i++) {
            result.add(suggestions.get(i));
        }
        return result;
    }

    private ArrayList<String> insertionSort(HashMap<String, Integer> map) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Integer> counts = new ArrayList<>();
        for (String key : map.keySet()) {
            result.add(key);
            counts.add(map.get(key));
        }

        for(int i = 1; i < result.size(); i++) {
            String key = result.get(i);
            int count = counts.get(i);
            int j = i - 1;
    
            while(j >= 0 && counts.get(j).compareTo(count) < 0) {
                result.set(j + 1, result.get(j));
                counts.set(j + 1, counts.get(j));
                j = j - 1;
            }
            result.set(j + 1, key);
            counts.set(j + 1, count);
        }
        return result;
    }

    private void suggest_cur(UserHistoryNode node, String entered, String traversed, HashMap<String, Integer> suggestions) {
        if(node == null) {
            return;
        }

        if (node.value > 0) {
            suggestions.put(entered + traversed, node.value);
        }
        for (int i = 0; i < 255; i++) {
            suggest_cur(node.next[i], entered, traversed + ((char)i), suggestions);
        }
    }

    /**
     * List all of the words currently stored in the dictionary
     * 
     * @return ArrayList<String> List of all valid words in the dictionary
     */
    public ArrayList<String> traverse() {
        HashMap<String, Integer> suggestions = new HashMap<String, Integer>();
        ArrayList<String> result = new ArrayList<String>();
        suggest_cur(root, "", "", suggestions);
        for (String key : suggestions.keySet()) {
            result.add(key);
        }
        return result;
    }

    /**
     * Count the number of words in the dictionary
     *
     * @return int, the number of (distinct) words in the dictionary
     */
    public int count() {
        return size;
    }
}

class UserHistoryNode
{
    int value = 0;
    UserHistoryNode[] next = new UserHistoryNode[256];
}