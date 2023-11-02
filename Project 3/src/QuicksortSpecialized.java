import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Contains quicksort method and functionality
 * 
 * @author {Gabriel Holder}
 * @version {October 2023}
 */
public class QuicksortSpecialized {

    private ByteBuffer byteBuffer;
    private static final int RECORDSIZE = 4;

    /**
     * Constructor for quicksort method
     */
    public QuicksortSpecialized() {

    }


    /**
     * Quicksort algorithim that sorts the array via splitting your data into
     * two sets with the help of a partion method and a pivot
     * 
     * @param bp
     *            - buffer pool used to access values we need
     * @param leftIndex
     *            - left index of the left set
     * @param rightIndex
     *            - right index of the right set
     * @throws IOException
     *             - throws an exception on a bad file
     */
    public void quicksort(BufferPool bp, int leftIndex, int rightIndex)
        throws IOException {

        // Pick a pivot
        int pivotIndex = findPivot(leftIndex, rightIndex);

        // get value at that pivot
        byte[] pivotBytes = bp.getBytes(pivotIndex, RECORDSIZE);
        short pivotValue = getKey(pivotBytes);
        byte[] rightBytes = bp.getBytes(rightIndex, RECORDSIZE);

        // Stick pivot at end
        bp.swap(pivotBytes, pivotIndex, rightBytes, rightIndex, true);

        // k will be the first position in the right subarray

        int k = partition(bp, leftIndex, rightIndex - 1, pivotValue);
        byte[] kBytes = bp.getBytes(k, RECORDSIZE);
        rightBytes = bp.getBytes(k, RECORDSIZE);

        bp.swap(kBytes, k, rightBytes, rightIndex, true);

        if ((k - leftIndex) > 1) {
            quicksort(bp, leftIndex, k - 1);
        } // Sort left partition
        if ((rightIndex - k) > 1) {
            quicksort(bp, k + 1, rightIndex);
        } // Sort right partition

    }


    /**
     * Finds the pivot
     * 
     * @param lowest
     *            - low index
     * @param highest
     *            - highest index
     * @return - returns the pivot in the middle
     */
    public int findPivot(int lowest, int highest) {
        return (lowest + highest) / 2;
    }


    /**
     * Gets the key of the 4 bytes
     * 
     * @param array
     *            - 4 bytes
     * @return - short formed from the first two bytes
     */
    public short getKey(byte[] array) {
        byteBuffer = ByteBuffer.wrap(array);
        return byteBuffer.getShort();
    }


    /**
     * 
     * @param bp
     *            - buffer pool
     * @param leftIndex
     *            - current left index to be compared with the pivot
     * @param rightIndex
     *            s * - current right index to be compared with the pivot
     * @param pivot
     *            - value to be compared in order to determine a swap
     * @return Gives back an int that is the first position in the right set
     * @throws IOException
     */
    public int partition(
        BufferPool bp,
        int leftIndex,
        int rightIndex,
        short pivot)
        throws IOException {
        while (leftIndex <= rightIndex) { // Move bounds inward until they meet
            byte[] leftBytes = bp.getBytes(leftIndex, RECORDSIZE);
            short leftValue = getKey(leftBytes);
            while (leftValue < pivot) {
                leftIndex++;
                if (leftIndex <= 1024) {
                    leftBytes = bp.getBytes(leftIndex, RECORDSIZE);
                    leftValue = getKey(leftBytes);
                }
            }
            byte[] rightBytes = bp.getBytes(rightIndex, RECORDSIZE);
            short rightValue = getKey(rightBytes);

            while ((rightIndex >= leftIndex) && (rightValue >= pivot)) {
                rightIndex--;
                if (rightIndex >= 0) {
                    rightBytes = bp.getBytes(rightIndex, RECORDSIZE);
                    rightValue = getKey(rightBytes);
                }
            }
            if (rightIndex > leftIndex) {
                leftBytes = bp.getBytes(leftIndex, RECORDSIZE);
                rightBytes = bp.getBytes(rightIndex, RECORDSIZE);
                bp.swap(leftBytes, leftIndex, rightBytes, rightIndex, true);
            } // Swap out-of-place values
        }
        return leftIndex; // Return first position in right partition
    }

}
