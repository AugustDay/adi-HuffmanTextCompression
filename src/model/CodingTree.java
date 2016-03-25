package model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Creates a Huffman Tree for encoding text.
 * @author Austin Ingraham
 * @version February 21 2016
 */
public final class CodingTree {
    
    /** The Huffman Tree used to encode this input. */
    private final Node myHuffmanTree;
    
    /** The characters of the input, and their corresponding huffman codes. */
    public final Map<Character, String> codes;
    
    /** The encoded message as a unbroken string of 1s and 0s. */
    public final String bits;
    
    /**
     * Given a String of input, builds an encoded huffman tree for data compression.
     * @param theInput a String of input
     */
    CodingTree(final String theInput) {
        codes = new HashMap<Character, String>();
        myHuffmanTree = buildHuffTree(buildQueue(countFrequency(theInput)));
        buildEncodedMap();
        bits = encodeMessage(theInput);
    }
    
    /**
     * Iterates through String input, counts the frequency of each char into a map.
     * @param theInput a String of input
     * @return frequency a Map of Characters and their number of appearances
     */
    private Map<Character, Integer> countFrequency(final String theInput) {
        final Map<Character, Integer> frequency = new HashMap<>();
        for (final char c : theInput.toCharArray()) {
            final Integer count = frequency.get(c);
            if (count == null) {
                frequency.put(c, 1);
            } else {
                frequency.put(c, count + 1);
            }
        }
        return frequency;
    }
    
    /**
     * Iterates through Map of characters, builds queue of Nodes with them as input.
     * @param theFrequency Map of <Character, Integer> to read
     * @return PriorityQueue of Nodes
     */
    private PriorityQueue<Node> buildQueue(final Map<Character, Integer> theFrequency) {
        final PriorityQueue<Node> nodeQ = new PriorityQueue<>();
        for (final Character c : theFrequency.keySet()) {
            nodeQ.add(new Node(c, theFrequency.get(c)));
        }
        return nodeQ;
    }
    
    /**
     * Constructs a Huffman Tree from a queue of individual Nodes arranged by frequency.
     * @param theQ PriorityQueue of Nodes
     * @return Node the root of the finished Huffman Tree
     */
    private Node buildHuffTree(final PriorityQueue<Node> theQ) {
        int count = 0;
        while (theQ.size() > 1) {
            final Node left = theQ.poll();
            final Node right = theQ.poll();
            theQ.add(new Node(left.getValue() + right.getValue(), left, right));
            count++;
        }
        return theQ.poll();
    }
    
    /** Creates the map of encoded characters using the Huffman Tree. */
    private void buildEncodedMap() {
        if (myHuffmanTree.isLeaf()) {
            codes.put(myHuffmanTree.getElement(), "1");
        } else {
            myHuffmanTree.buildBits("");
        }
    }
    
    /**
     * Creates the encoded message using the encoded map.
     * @param theInput message to encode.
     * @return encoded String of bits.
     */
    private String encodeMessage(final String theInput) {
        final StringBuffer sb = new StringBuffer();
        for (final Character c : theInput.toCharArray()) {
            sb.append(codes.get(c));
        }
        return sb.toString();
    }
    
    /**
     * Returns the encoded message.
     * @return bits an encoded message
     */
    public String getBits() {
        return bits;
    }
    
    /**
     * Returns the bits for each character in the String.
     * @return codes
     */
    public Map<Character, String> getCodes() {
        return codes;
    }
    
    /**
     * Gets the byte array of this tree's encoded output, for writing to a file.
     * @return theBytes
     */
    public byte[] getBytes() {
        StringBuffer sb = new StringBuffer();
        final byte[] b = new byte[(int) Math.ceil((double) bits.length() / 8)];
        int i = 0;
        for (final char c : bits.toCharArray()) {
            sb.append(c);
            if (sb.length() == 8) {
                b[i] = (byte) Integer.parseInt(sb.toString(), 2);
                i++;
                sb = new StringBuffer(); //clear
            }
        }
        if (sb.length() > 0) {
            while (sb.length() < 8) {
                sb.append('0');
            }
            b[i] = (byte) Integer.parseInt(sb.toString(), 2);
        }
        return b;        
    }
    
    /**
     * Node class to represent a Huffman Tree.
     * @author Austin Ingraham
     * @version February 21 2016
     */
    private final class Node implements Comparable {
        
        /** A character to be held by this Node (if it is a leaf). */
        private final Character myElement;
        
        /** The frequency of this node's data in the text. */
        private final int myValue;
        
        /** Left and Right chilren of this Node (null if this is a leaf). */
        private final Node myLeft, myRight;
        
        /** true if this Node is a leaf. */
        private final boolean myLeaf;
        
        /**
         * Constructor for a Huffman Tree Node (Branch).
         * @param theValue frequency of this data
         * @param theLeft Left child
         * @param theRight Right child
         */
        Node(final int theValue, final Node theLeft, final Node theRight) {
            this(null, theValue, theLeft, theRight, false);
        }
        
        /**
         * Constructor for a Huffman Tree Node (Leaf).
         * @param theElement character to store
         * @param theValue frequency of this character
         */
        Node(final Character theElement, final int theValue) {
            this(theElement, theValue, null, null, true);
        }
        
        /**
         * Complete, private constructor for a Huffman Tree Node.
         * @param theElement character to store
         * @param theValue frequency of this data
         * @param theLeft Left child
         * @param theRight Right child
         * @param theLeaf if this is a leaf or not
         */
        private Node(final Character theElement, final int theValue, 
                     final Node theLeft, final Node theRight, final boolean theLeaf) {
            myElement = theElement;
            myValue = theValue;
            myLeft = theLeft;
            myRight = theRight;
            myLeaf = theLeaf;
        }
        
        /**
         * Gets whether this is a leaf or not.
         * @return true if this Node is a leaf
         */
        public boolean isLeaf() {
            return myLeaf;
        }
        
        /**
         * Gets this Node's value.
         * @return myValue
         */
        public int getValue() {
            return myValue;
        }
        
        /**
         * Gets this Node's character.
         * @return the character
         */
        public Character getElement() {
            return myElement;
        }
        
        /**
         * Iterates through tree recursively, building the encoded bits.
         * @param theBits String bitcode
         */
        public void buildBits(final String theBits) {
            if (myLeaf) {
                codes.put(myElement, theBits);
            } else {
                myLeft.buildBits(new String(theBits + '0'));
                myRight.buildBits(new String(theBits + '1')); 
            }
        }
        
        @Override
        public String toString() {
            String s;
            if (myLeaf) {
                s = '{' + myElement.toString() + ", " + myValue + '}';
            } else {
                s = "<" + myValue;
                if (myLeft.isLeaf()) {
                    s += "{" + myLeft.getElement().toString() + ", " + myLeft.getValue() + '}';
                } else {
                    s += "{" + myLeft.getValue() + '}';
                }
                if (myRight.isLeaf()) {
                    s += "{" + myRight.getElement().toString() + ", " + myRight.getValue() + '}';
                } else {
                    s += "{" + myRight.getValue() + '}';
                }
                s += '>';
            }
            return s;
        }

        @Override
        public int compareTo(final Object theOther) {
            int compare = -1;
            if (theOther.getClass() == this.getClass()) {
                if (((Node) theOther).getValue() < myValue) {
                    compare = 1;
                } else if (((Node) theOther).getValue() == myValue) {
                    compare = 0;
                }               
            }
            return compare;
        }
    }
}
