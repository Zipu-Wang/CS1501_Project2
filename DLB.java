package cs1501_p2;

import java.util.ArrayList;

public class DLB implements Dict {

    private DLBNode root;
    private DLBNode charNode;
    private String input;
    private int size;

    public DLB() {
        root = new DLBNode('/');
        charNode = root;
        input = "";
        size = 0;
    }

    /**
     * Add a new word to the dictionary
     *
     * @param key New word to be added to the dictionary
     */
    public void add(String key) {

        if(key == null || key.isEmpty()) {
            return;
        }

        DLBNode currentNode = root;

        for(int i = 0; i < key.length(); i++) {
            char ch = key.charAt(i);
            if(currentNode.getDown() == null) {
                currentNode.setDown(new DLBNode(ch));
            }
            currentNode = currentNode.getDown();

            while(currentNode.getRight() != null && currentNode.getLet() != ch) {
                currentNode = currentNode.getRight();
            }
            if(currentNode.getLet() != ch) {
                currentNode.setRight(new DLBNode(ch));
                currentNode = currentNode.getRight();
            }
        }

        if(currentNode.getDown() == null) {
            currentNode.setDown(new DLBNode('^'));
        } else {
            DLBNode RightNode = currentNode.getDown();
            currentNode.setDown(new DLBNode('^'));
            currentNode.getDown().setRight(RightNode);
            // currentNode = currentNode.getDown();
            // while(currentNode.getRight() != null) {
            //     currentNode = currentNode.getRight();
            // }
            // currentNode.setRight('^');
        }
        size++;
    }

    /**
     * Check if the dictionary contains a word
     *
     * @param key Word to search the dictionary for
     *
     * @return true if key is in the dictionary, false otherwise
     */
    public boolean contains(String key) {

        return searchHelper(key) == 1 || searchHelper(key) == 2;
    }

    /**
     * Check if a String is a valid prefix to a word in the dictionary
     *
     * @param pre Prefix to search the dictionary for
     *
     * @return true if prefix is valid, false otherwise
     */
    public boolean containsPrefix(String pre) {

        return searchHelper(pre) == 2 || searchHelper(pre) == 0;
    }

    private int searchHelper(String s) {
        //use charNode to utilize searchByChar
        DLBNode currentNode = charNode;
        int result = -1;
        charNode = root;
        for(int i = 0; i < s.length(); i++) {
            result = searchByChar(s.charAt(i));
        }
        //reset charNode to original
        charNode = currentNode;
        return result;
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
        charNode = charNode.getDown();
        while(charNode != null) {
            if(charNode.getLet() == next) {
                if(charNode.getDown() != null) {
                    if(charNode.getDown().getLet() == '^') {
                        return (charNode.getDown().getRight() != null) ? 2 : 1;
                    }
                    return 0;
                }
            }
            charNode = charNode.getRight();
        }
        return -1;

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
        ArrayList<String> suggestions = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        suggest_cur(charNode, input, "", suggestions);
        insertionSort(suggestions);
        for(int i = 0; i < 5 && i < suggestions.size(); i++) {
            result.add(suggestions.get(i));
        }
        return result;
    }

    private void insertionSort(ArrayList<String> list) {
        for(int i = 1; i < list.size(); i++) {
            String key = list.get(i);
            int j = i - 1;
    
            while(j >= 0 && list.get(j).compareTo(key) > 0) {
                list.set(j + 1, list.get(j));
                j = j - 1;
            }
            list.set(j + 1, key);
        }
    }
    
    private void suggest_cur(DLBNode node, String entered, String traversed, ArrayList<String> suggestions) {
        if(node == null) {
            return;
        }
        
        node = node.getDown();
        while(node != null) {
            if(node.getLet() == '^') {
                suggestions.add(entered + traversed); //get rid of ^
            } else {
                suggest_cur(node, entered, traversed + node.getLet(), suggestions);
            }
            node = node.getRight();
        }
    }


    /**
     * List all of the words currently stored in the dictionary
     * 
     * @return ArrayList<String> List of all valid words in the dictionary
     */
    public ArrayList<String> traverse() {
        ArrayList<String> allWords = new ArrayList<>();
        suggest_cur(root, "", "", allWords);
        return allWords;
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