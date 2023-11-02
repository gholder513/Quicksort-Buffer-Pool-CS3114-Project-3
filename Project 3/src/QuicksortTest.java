import java.io.IOException;
import student.TestCase;

/**
 * @author {Your Name Here}
 * @version {Put Something Here}
 */
public class QuicksortTest extends TestCase {
    private CheckFile fileChecker;

    /**
     * Sets up the tests that follow. In general, used for initialization.
     */
    public void setUp() {
        fileChecker = new CheckFile();
    }


    /**
     * This method is a demonstration of the file generator and file checker
     * functionality. It calles generateFile to create a small "ascii" file.
     * It then calls the file checker to see if it is sorted (presumably not
     * since we don't call a sort method in this test, so we assertFalse).
     *
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testFileGenerator() throws Exception {
        String[] args = new String[3];
        args[0] = "test.txt";
        args[1] = "4";
        args[2] = "statFile.txt";
        //Quicksort.generateFile("test2.txt", "11", 'a');
        // In a real test we would call the sort
        Quicksort.main(args);
        // In a real test, the following would be assertTrue()
        assertFalse(fileChecker.checkFile("input.txt"));
    }


    /**
     * Get code coverage of the class declaration.
     * 
     * @throws IOException
     */
    public void testQInit() throws IOException {
        Quicksort tree = new Quicksort();
        assertNotNull(tree);
    }
}
