
/**
 * {This project takes in a file with a bunch of bytes and organizes them using
 * quicksort and a buffer pool}
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The class containing the main method. Shouldn't have access to the file.
 *
 * @author {Gabriel Holder}
 * @version {October 2023}
 */

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

public class Quicksort {

    private static final int RECORD_SIZE = 4;

    /**
     * This method is used to generate a file of a certain size, containing a
     * specified number of records.
     *
     * @param filename
     *            the name of the file to create/write to
     * @param blockSize
     *            the size of the file to generate
     * @param format
     *            the format of file to create
     * @throws IOException
     *             throw if the file is not open and proper
     */
    public static void generateFile(
        String filename,
        String blockSize,
        char format)
        throws IOException {
        FileGenerator generator = new FileGenerator();
        String[] inputs = new String[3];
        inputs[0] = "-" + format;
        inputs[1] = filename;
        inputs[2] = blockSize;
        generator.generateFile(inputs);
    }


    /**
     * Main method
     * 
     * @param args
     *            Command line parameters.
     * @throws IOException
     *             throws an exception on a bad file
     */
    public static void main(String[] args) throws IOException {
        // This is the main file for the program.

        /**
         * Makes sure file exists, number of buffers are between 1 and 20
         */
        String filename = args[0];
        RandomAccessFile file = new RandomAccessFile(filename, "rw");
        String numbBuffersString = args[1];
        int numbBuffers = Integer.parseInt(numbBuffersString);
        if (numbBuffers > 0 && numbBuffers < 21) {

            BufferPool bp = new BufferPool(numbBuffers, file);
            QuicksortSpecialized sorter = new QuicksortSpecialized();
            sorter.quicksort(bp, 0, (bp.getFileLength() / 4) - 1);
            bp.flush();
        }

        file.close();
    }

}
