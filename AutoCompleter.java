package cs1501_p2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.io.PrintStream;
import java.io.IOException;

public class AutoCompleter extends DLB implements AutoComplete_Inter{
    private DLB dictionaryDLB;
    private UserHistory userHistory;

    public AutoCompleter(String dict) {
        this.dictionaryDLB = loadDictionary(dict);
        this.userHistory = new UserHistory();
    }

    public AutoCompleter(String dict, String userHistoryFilename) {
        this.dictionaryDLB = loadDictionary(dict);
        this.userHistory = loadUserHistory(userHistoryFilename);
    }
    
    /**
     * Produce up to 5 suggestions based on the current word the user has
     * entered These suggestions should be pulled first from the user history
     * dictionary then from the initial dictionary. Any words pulled from user
     * history should be ordered by frequency of use. Any words pulled from
     * the initial dictionary should be in ascending order by their character
     * value ("ASCIIbetical" order).
     *
     * @param next char the user just entered
     *
     * @return ArrayList<String> List of up to 5 words prefixed by cur
     */
    public ArrayList<String> nextChar(char next) {
        dictionaryDLB.searchByChar(next);
        userHistory.searchByChar(next);
        
        ArrayList<String> suggestions = new ArrayList<>();
    
        ArrayList<String> historySuggestions = userHistory.suggest();
        if (historySuggestions != null && !historySuggestions.isEmpty()) {
            suggestions.addAll(historySuggestions);
        }    
        
        if(suggestions.size() < 5) {
            ArrayList<String> dictionarySuggestions = dictionaryDLB.suggest();
            if (dictionarySuggestions != null && !dictionarySuggestions.isEmpty()) {
                for(String suggestion : dictionarySuggestions) {
                    if(suggestions.size() == 5) {
                        break;
                    }
                    suggestions.add(suggestion);
                }
            } 
        }
    
        return suggestions;
    }

    /**
     * Process the user having selected the current word
     *
     * @param cur String representing the text the user has entered so far
     */
    public void finishWord(String cur) {
        dictionaryDLB.resetByChar();
        userHistory.resetByChar();
        userHistory.add(cur);
    }

    /**
     * Save the state of the user history to a file
     *
     * @param fname String filename to write history state to
     */
    public void saveUserHistory(String fname) {
        try {
            PrintStream out = new PrintStream(new File(fname));
            for (String h: userHistory.history) {
                out.println(h);
            }
            out.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private DLB loadDictionary(String filename) {
        DLB dlb = new DLB();
        try (Scanner s = new Scanner(new File(filename))) {
            while (s.hasNext()) {
                dlb.add(s.nextLine().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dlb;
    }

    private UserHistory loadUserHistory(String filename) {
        UserHistory uh;
        ArrayList<String> history = new ArrayList<String>();
        try (Scanner s = new Scanner(new File(filename))) {
            while (s.hasNext()) {
                history.add(s.nextLine().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        uh = new UserHistory(history);
        return uh;
    }
}