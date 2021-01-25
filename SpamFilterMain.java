import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.HashSet;

public class SpamFilterMain {

    public static void main(String[] args) throws IOException {
        String cwd = Paths.get(".").toAbsolutePath().normalize().toString();
        System.out.println("# Current working directory (CWD): " + cwd);

        File dataDir = new File("data");
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.out.println("# Could not find the data directory. " +
                               "Make sure it is under the CWD printed above.");
            return;
        }

        File[] trainHams = null;
        File[] trainSpams = null;
        File[] tests = null;
        // Traverse the data directory to get training and testing files.
        File[] subDirs = dataDir.listFiles();
        for (File dir : subDirs) {
            if (dir.getName().equals("train")) {
                File[] trainDirs = dir.listFiles();
                for (File trainDir : trainDirs) {
                    if (trainDir.getName().equals("ham")) {
                        trainHams = trainDir.listFiles();
                    } else if (trainDir.getName().equals("spam")) {  // spams
                        trainSpams = trainDir.listFiles();
                    }
                }
            } else if (dir.getName().equals("test")) {  // test
                tests = dir.listFiles();
            }
        }

        if (sanityCheck(trainHams, trainSpams, tests)) {
            System.out.println("# Testing/training files loaded successfully.");

            NaiveBayes nb = new NaiveBayes();

            System.out.println("# Training...");
            nb.train(trainHams, trainSpams);
            System.out.println("# Done training.");

            System.out.println("# Test results:");
            printClassificationResults(nb, tests);
        }
    }

    public static void printClassificationResults(NaiveBayes nb, File[] data) throws IOException {
        Set<File> spams = new HashSet<>();
        Set<File> hams = new HashSet<>();
        nb.classify(data, spams, hams);

        Set<String> answers = new HashSet<>();
        for (File email : spams) {
            answers.add(email.getName() + " spam");
        }
        for (File email : hams) {
            answers.add(email.getName() + " ham");
        }

        for (String ans : answers) {
            System.out.println(ans);
        }
    }

    public static boolean sanityCheck(File[] trainHams, File[] trainSpams, File[] tests) {
        boolean pass = true;

        if (trainHams == null || trainHams.length == 0) {
            System.out.println("# Error loading training ham files.");
            pass = false;
        }

        if (trainSpams == null || trainSpams.length == 0) {
            System.out.println("# Error loading training spam files.");
            pass = false;
        }

        if (tests == null || tests.length == 0) {
            System.out.println("# Error loading testing files.");
            pass = false;
        }

        return pass;
    }
}
