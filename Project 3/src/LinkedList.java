public class LinkedList<E> {

    /* Head is needed to keep track of first node */
    private Node<E> head;

    private int count = 0;

    /**
     * Inserts into the linked list
     * 
     * @param value
     */
    public void insertHead(E value) {
        Node<E> newNode = new Node<E>(value);
        newNode.next = head;
        head = newNode;
        count++;
    }


    /**
     * Inserts at the back of the list
     * 
     * @param value
     *            - Buffer to be added
     */
    public void insertBack(E value) {
        Node<E> newNode = new Node<E>(value);
        newNode.next = null;

        // nothing in the list
        if (null == head) {

            head = newNode;
        }
        else {
            Node<E> tempNode = head;
            while (null != tempNode.next) {
                tempNode = tempNode.next;
            }
            tempNode.next = newNode;
        }
        count++;
    }


    /**
     * Inserts a specific value at a certain spot
     * 
     * @param value
     *            - value we wish to add
     * @param position
     *            - position we wish to add value
     */
    public void insertAtPosition(E value, int position) {
        if (position < 0 || position > count) {
            throw new IllegalArgumentException("Illegal Index");
        }

        Node<E> newNode = new Node<E>(value);
        if (position == 0) {
            newNode.next = head;
        }
        else {
            Node<E> tempNode = head;
            for (int i = 0; i < position - 1; i++) {
                tempNode = tempNode.next;
            }
            Node<E> nodeNextToNewNode = tempNode.next;
            tempNode.next = newNode;
            newNode.next = nodeNextToNewNode;
        }
        count++;
    }


    /**
     * Gets the amount of elements in the linked list
     * 
     * @return count of elements in the list
     */
    public int getCount() {
        return count;
    }


    /**
     * Returns true is list is empty
     * 
     * @return returns true if nothing is in the list
     */
    public boolean isEmpty() {
        return count == 0;
    }


    /**
     * Returns the Node containing data item after searching
     * for a given index. If invalid index is passed, proper
     * exception is thrown.
     * 
     * @param index
     *            - index position we are searching for
     * @return gives back the node we want
     */
    public Node<E> searchByIndex(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Illegal index");
        }
        /* Validation passed, let's search for value using the index */
        Node<E> temp = head;
        for (int i = 0; i < index; i++) {
            /*
             * Start from 0 and go till one less then index
             * because we are jumping to next node inside the loop
             */
            temp = temp.next;
        }
        return temp;
    }


    /**
     * Gives back the first node containing what's in parameter value
     * 
     * @param value
     *            - element we wish to get
     * @return null if not found, returns element if found
     */
    public Node<E> getValue(E value) {
        /* Traverse through each node until this value is found */
        Node<E> temp = head;
        while (null != temp.next && temp.item != value) {
            temp = temp.next;
        }
        if (temp.item == value) {
            return temp;
        }
        return null;
    }


    /**
     * Delete's the element present at head node
     */
    public void removeHead() {
        /* If list is empty, return */
        if (null == head) {
            return;
        }
        /* Update head and reduce size */
        head = head.next;
        count--;
    }


    /**
     * Delete for the last node
     */
    public void deleteFromTail() {
        // if empty then no additional action needed
        if (null == head) {
            return;
        }

        Node<E> currentNode = head;
        Node<E> nextNode = currentNode.next;
        while (currentNode.next != null && nextNode.next != null) {
            currentNode = currentNode.next;
            nextNode = nextNode.next;
        }
        currentNode.next = null;
        count--;
    }


    /**
     * Delete's the element present at index position
     * 
     * @param position
     *            position of the item wanted to remove from the list
     */
    public void removeSpecific(int position) {
        if (position < 0 || position >= count) {
            throw new IllegalArgumentException("Position is Invalid");
        }
        /* Conditions check passed, let's delete the node */
        Node<E> nodeToBeDeleted = head;
        for (int i = 0; i < position; i++) {
            nodeToBeDeleted = nodeToBeDeleted.next;
        }
        if (nodeToBeDeleted.next == null) {
            /* If this is a last node */
            deleteFromTail();
        }
        else {
            nodeToBeDeleted.item = nodeToBeDeleted.next.item;
            nodeToBeDeleted.next = nodeToBeDeleted.next.next;
        }
    }


    /**
     * Gets the node at the start of the linkedList
     * 
     * @return head buffer(most recently used)
     */
    public Node<E> getHead() {
        return head;
    }

    public class Node<T> {

        /**
         * Data item in the node
         */
        private T item;

        /**
         * Pointer to the next node
         */
        Node<T> next;

        /**
         * Node constructor
         * 
         * @param item
         *            item to be added
         */
        public Node(T item) {
            this.item = item;
        }
        
        /**
         * Gets this buffer
         * @return - item buffer
         */
        public T getItem() {
            return item;
        }

    }

}
