import java.io.*;
import java.util.*;

public class NaiveBayes {
    // <word, # of emails containing word>
    private HashMap<String, Integer> allSpam = new HashMap<String, Integer>();
    private HashMap<String, Integer> allHam = new HashMap<String, Integer>();

    // <word, P(w | S)>
    private HashMap<String, Double> pSpamWord = new HashMap<String, Double>();
    // <word, P(w | H)>
    private HashMap<String, Double> pHamWord = new HashMap<String, Double>();

    private double pSpamEmails; // P(S)
    private double pHamEmails; // P(H)
    private double totalSpamEmail; // # of spam emails
    private double totalHamEmail; // # of ham emails

    public void train(File[] hams, File[] spams) throws IOException {
        totalSpamEmail = spams.length * 1.0; // total spam emails number
        totalHamEmail = hams.length * 1.0; // total spam emails number
        pSpamEmails = Math.log(spams.length / (totalSpamEmail + totalHamEmail)); // log(P(S))
        pHamEmails = Math.log(hams.length / (totalSpamEmail + totalHamEmail)); // log(P(H))

        // getting each word from each file and put in ham
        for (int i = 0; i < hams.length; i++) {
            // looping through each email(given as string set) in a file
            for (String word : tokenSet(hams[i])) {
                // for each word
                // <word, # of email containing word>
                if (allHam.containsKey(word)) {
                    // word is already in the map
                    allHam.put(word, allHam.get(word) + 1);
                } else {
                    allHam.put(word, 1);
                }
            }
        }

        // getting each word from each file and put in spam
        for (int i = 0; i < spams.length; i++) {
            // looping through each email(given as string set) in a file
            for (String word : tokenSet(spams[i])) {
                // for each word
                // <word, # of email containing word>
                if (allSpam.containsKey(word)) {
                    // word is already in the map
                    // increment # of emails containing word
                    allSpam.put(word, allSpam.get(word) + 1);
                } else {
                    allSpam.put(word, 1);
                }
            }
        }

        // Calculating words given Ham and put them in pHamWord
        for (String word : allHam.keySet()) {
            // <word, P(w | H)>
            pHamWord.put(word, (allHam.get(word) + 1.0) / totalHamEmail);
        }

        // Calculating words given Spam and put them in pSpamWord
        for (String word : allSpam.keySet()) {
            // <word, P(w | S)>
            pSpamWord.put(word, (allSpam.get(word) + 1.0) / totalSpamEmail);
        }

    }

    public void classify(File[] emails, Set<File> spams, Set<File> hams) throws IOException {

        for (int i = 0; i < emails.length; i++) {
            // loop through each email
            double pSumSpam = 0.0; // log((P(S)) + log(P(xi | S))
            double pSumHam = 0.0; // log((P(H)) + log(P(xi | H))
                for (String word : tokenSet(emails[i])) {
                    // loop through each word
                    if (pSpamWord.containsKey(word)) {
                        // word is in the spam map
                        //  + log(P(wordi | S))
                        pSumSpam += Math.log(pSpamWord.get(word));
                    } else {
                        // word is not in the spam map
                        // smoothing
                        pSumSpam += Math.log(1.0 / (2.0 + totalSpamEmail));
                    }
                    if (pHamWord.containsKey(word)) {
                        // word is in the spam map
                        //  + log(P(wordi | H))
                        pSumHam += Math.log(pHamWord.get(word));
                    } else {
                        // word is not in the spam map
                        // smoothing
                        pSumHam += Math.log(1.0 / (2.0 + totalHamEmail));
                    }
                }
                if ((pSumSpam + pSpamEmails) > (pSumHam + pHamEmails)) {
                    spams.add(emails[i]);
                } else {
                    hams.add(emails[i]);
                }
        }
    }

    public static HashSet<String> tokenSet(File filename) throws IOException {
        HashSet<String> tokens = new HashSet<String>();
        Scanner filescan = new Scanner(filename);
        filescan.next(); // Ignoring "Subject"
        while (filescan.hasNextLine() && filescan.hasNext()) {
            tokens.add(filescan.next());
        }
        filescan.close();
        return tokens;
    }
}
