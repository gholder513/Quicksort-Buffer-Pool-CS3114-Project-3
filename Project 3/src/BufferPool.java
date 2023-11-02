import java.io.*;
import java.nio.ByteBuffer;

/**
 * Mediator for quicksort and random access file
 * 
 * @author {Gabriel Holder}
 * @version {October 2023}
 */
public class BufferPool {
    /**
     * Compare keys and swap two records
     */
    private RandomAccessFile file;
    private Buffer[] bufferPool;
    private int fileLength;
    private int numRecords;
    private Buffer lru;
    private int maxBuffers;
    private int numBuffers;
    private static final int RECORD_SIZE = 4;
    private LinkedList<Buffer> linkedList;

    /**
     * Constructor for a buffer pool
     * 
     * @param numbBuffers
     *            - number of buffers from command line
     * @param randFile
     *            - file with input
     * @throws IOException
     *             - Throws exception on bad files
     */
    public BufferPool(int numbBuffers, RandomAccessFile randFile)
        throws IOException {
        file = randFile;
        maxBuffers = numbBuffers;
        bufferPool = new Buffer[numbBuffers];
//        for (int i = 0; i < numbBuffers; i++) {
//            bufferPool[i] = new Buffer();
//        }
        fileLength = (int)randFile.length();
        linkedList = new LinkedList<Buffer>(); 
        numRecords = fileLength / 4;
        numBuffers = 0;

    }


    
//        
    


    /**
     * Gets the bytes from this buffer pool based on the position of the byte it
     * is looking to access
     * 
     * @param pos
     *            - integer representing the position of the byte we wish to
     *            access in the input file
     * @param size
     *            - size of the record in bytes
     * @return returns a byte array of size 4, 2-key 2-value
     * @throws IOException
     */
    public byte[] getBytes(int pos, int size) throws IOException {
        // should only pass key value positions
        int blockID = (pos * RECORD_SIZE) / 4096;
        int keySpot = (pos * RECORD_SIZE) % 4096;
        Buffer buffer = null;
        for(int i = 0; i < linkedList.getCount(); i++) {
            //if present
            if(linkedList.searchByIndex(i).item.getBlockID() == blockID) {
                buffer = linkedList.searchByIndex(i).item;
                linkedList.removeSpecific(i);
                linkedList.insertHead(buffer);
                return buffer.bufferGetBytes(keySpot, size);
            }
        }
        //if full & not in linked list
        if(maxBuffers == linkedList.getCount()) {
            if(linkedList.searchByIndex(maxBuffers -1).item.isDirty()) {
                write();
                buffer = read(blockID);
            } else {
                buffer = read(blockID);
            }
            linkedList.removeSpecific(maxBuffers - 1);
            linkedList.insertHead(buffer);
            return buffer.bufferGetBytes(keySpot,size);
        }
        //if not full and not in linked list
        buffer = read(blockID);
        linkedList.insertHead(buffer);
        
        
        
        return buffer.bufferGetBytes(keySpot, size);
    }


    /**
     * Adds a buffer to the buffer pool from the input file
     * 
     * @param blockID
     *            - id of the block
     * @return returns the buffer it added
     * 
     * @throws IOException
     *             - throws a input output exception
     */
    public Buffer read(int blockID) throws IOException {

        Buffer buffer = new Buffer();
        buffer.setID(blockID);
        int bytePosition = blockID * 4096;
        file.seek(bytePosition);
        file.read(buffer.getBlockBytes());
        return buffer;
    }


    /**
     * Removes a buffer and writes back to the random access file
     * 
     * @throws IOException
     *             throws an exception on a bad file
     */
    public void write() throws IOException {
        Buffer leastUsed = getLeastRecentlyUsed();
        byte[] bytes = leastUsed.getBlockBytes();
        int id = leastUsed.getBlockID();
        leastUsed.setDirtyBuffer(false);
        int bytePosition = id * 4096;
        file.seek(bytePosition);
        file.write(bytes);
    }


    /**
     * Flushes the buffer pool by writing all dirty buffers back to the file
     * 
     * @throws IOException
     */
    public void flush() throws IOException {
        
        for(int i =0; i< linkedList.getCount()-1; i++) {
            if(linkedList.searchByIndex(i).item.isDirty()) {                
                Buffer currentHead = linkedList.getHead().item;
                byte[] bytes = currentHead.getBlockBytes();
                int id = currentHead.getBlockID();
                currentHead.setDirtyBuffer(false);
                int bytePosition = id * 4096;
                file.seek(bytePosition);
                file.write(bytes);
                linkedList.removeHead();
            }
        }
    }


    /**
     * Alerts you to the status of the buffer pool
     * 
     * @param bp
     *            buffer pool array to be checked
     * @return a boolean value of true if full, false otherwise
     */
    public boolean isFull(Buffer[] bp) {
        return numBuffers == bp.length;
    }


    /**
     * Gets the file length of input
     * 
     * @return file length of input in bytes
     */
    public int getFileLength() {
        return fileLength;
    }


    /**
     * Gets the number of records
     * 
     * @return number of records in the whole file
     */
    public int getNumRecords() {
        return numRecords;
    }


//    /**
//     * Shifts all the values in this array up one towards the highest index,
//     * leaves position [0] to be filled with a new value.
//     * 
//     * @param bp
//     *            array of buffers being changeds
//     */
//    public void shiftUp(Buffer[] bp) {
//        // for the length of the buffer pool overwrite the contents
//        for (int i = bp.length - 1; i > 0; i--) {
//            bp[i] = bp[i - 1];
//        }
//    }

    /**
     * Swap method switches two records
     * @param array1 - record 1
     * @param position1 - position of record 1
     * @param array2 - record 2
     * @param position2 - position of record 2
     * @param pivotSwap - lets us know if this is a pivot we're swapping
     * @throws IOException if file not found
     */
    public void swap(
        byte[] array1, int position1, byte[] array2, int position2,
boolean pivotSwap) throws IOException {
        
        int block1id = (position1 * RECORD_SIZE) / 4096;
        int positionInBlock = (position1 * RECORD_SIZE) % 4096;

        int block2id = (position2 * RECORD_SIZE) / 4096;
        int positionInBlock2 = (position2 * RECORD_SIZE) % 4096;

       boolean found = false;
        for(int i = 0; i < linkedList.getCount(); i++) {
            //if present
            if(linkedList.searchByIndex(i).item.getBlockID() == block1id) {
                
                System.arraycopy(array2, 0, linkedList.searchByIndex(i).item.getBlockBytes(), positionInBlock, RECORD_SIZE);
                linkedList.searchByIndex(i).item.setDirtyBuffer(true);
                found = true;
                break;
            }
        }
        if(!found) {
            loadBlock(block1id);
            System.arraycopy(array2, 0, linkedList.searchByIndex(0).item.getBlockBytes(), positionInBlock, RECORD_SIZE);
            linkedList.searchByIndex(0).item.setDirtyBuffer(true);
        }
        found = false;
        for(int i = 0; i < linkedList.getCount(); i++) {
            //if present
            if(linkedList.searchByIndex(i).item.getBlockID() == block2id) {
                System.arraycopy(array1, 0, linkedList.searchByIndex(i).item.getBlockBytes(), positionInBlock2, RECORD_SIZE);
                linkedList.searchByIndex(i).item.setDirtyBuffer(true);
                found = true;
                break;
            }
        }
        if(!found) {
            loadBlock(block2id);
            System.arraycopy(array1, 0, linkedList.searchByIndex(0).item.getBlockBytes(), positionInBlock2, RECORD_SIZE);
            linkedList.searchByIndex(0).item.setDirtyBuffer(true);
        }
        
        
     
    }
    
    private void loadBlock(int targetID) throws IOException {
        Buffer buffer = new Buffer();
        if(maxBuffers == linkedList.getCount()) {
            if(linkedList.searchByIndex(maxBuffers -1).item.isDirty()) {
                write();
                buffer = read(targetID);
            } else {
                buffer = read(targetID);
            }
            linkedList.removeSpecific(maxBuffers - 1);
            linkedList.insertHead(buffer);
        }
        //if not full and not in linked list
        buffer = read(targetID);
        linkedList.insertHead(buffer);
    }


    /**
     * Private method that gives back the least recently used block in the
     * buffer pool array
     * 
     * @return least recently used buffer in array
     */
    private Buffer getLeastRecentlyUsed() {
        lru = linkedList.searchByIndex(maxBuffers-1).item;
        return lru;
    }

    /**
     * This class represents a Buffer. A buffer can either be dirty or clean and
     * each buffer has an id.
     */
    private class Buffer {

        private byte[] buffer;
        private int id;
        private boolean dirty;

        /**
         * Creates a new buffer of size 4096 bytes
         */
        public Buffer() {
            buffer = new byte[4096];
            id = -1;
            dirty = false;
        }


        /**
         * Gets 4 specific bytes from this buffer
         * 
         * @param spot
         *            - start position of the 4 targeted bytes within the 4096
         *            byte array
         * @return 4 bytes starting at index spot
         */
        public byte[] bufferGetBytes(int spot, int size) {
            byte[] temp = new byte[size];
            System.arraycopy(buffer, spot, temp, 0, size);
            return temp;

        }


        /**
         * Sets the bytes
         * 
         * @param newBytes
         *            - new bytes
         * @param position
         *            - position of bytes in the block
         * @param size
         *            - amount of bytes to copy
         */
        public void setBytes(byte[] newBytes, int position, int size) {

            System.arraycopy(newBytes, 0, buffer, position, size);
        }


        /**
         * Sets the dirty boolean value
         * 
         * @param isDirty
         *            - value to change the boolean to
         */
        public void setDirtyBuffer(boolean isDirty) {
            dirty = isDirty;
        }


        /**
         * Accessor for the current dirty boolean value
         * 
         * @return - dirty value
         */
        public boolean isDirty() {
            return dirty;
        }


        /**
         * Gets the id for this block
         * 
         * @return buffer ID
         */
        public int getBlockID() {
            return id;
        }


        /**
         * Gives back this buffer in byte array form
         * 
         * @return this buffer block in its byte array form
         */
        public byte[] getBlockBytes() {
            return buffer;
        }


        /**
         * This method sets the id of this buffer
         * 
         * @param newID
         */
        public void setID(int newID) {
            id = newID;
        }

    }

}
