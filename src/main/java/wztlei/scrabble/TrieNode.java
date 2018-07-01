/*
 * TrieNode.java
 *
 * This code is the property of its creator William Lei.
 *
 * Contact Email: leiw9425@gmail.com
 */

package wztlei.scrabble;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author  William Lei
 */
public class TrieNode {

    public char letter;                  // Special value: "*" if root node
    public boolean isTerminalNode;       // Stores if node completes a word
    public ArrayList<TrieNode> children; // Stores the possible next letters

    // Stores the index of each letter of each children node
    // Ex. If a child has a letter 'C' at children [1],
    //     then letterIndexes['C'-'A'] == letterIndexes[2] == 1
    public int[] letterIndexes;

    // Constructor function
    public TrieNode() {
        children = new ArrayList<TrieNode>();
        letterIndexes = new int[26];
        Arrays.fill(letterIndexes, -1);
    }
}
