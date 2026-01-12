import java.util.*;

public class Trie {

    private TrieNode root;
    //may consider using an into to count total words or notes.
    // if the work flashback exists in the tree and user wants to add the word flash,
    // it shouldn't add to the size cause that subtree exists, but using regular size may cause the to happen

    //consider a reference to maintain the current node
    //such that it goes to the word stop it only goes s-t-o-p rather than s-st-sto-stop

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode curr = root;
        for (char c: word.toCharArray()) {
            Map<Character, TrieNode> kids = curr.getChildren();
            if (kids.containsKey(c)) {
                curr = kids.get(c);
                curr.incrementCount();
            }
            else {
                kids.put(c, new TrieNode());
                curr = kids.get(c);
            }
        }
        curr.setEndOfWord();
    }

    private TrieNode goToNode(String str) {
        TrieNode curr = root;
        for (char c: str.toCharArray()) {
            Map<Character, TrieNode> kids = curr.getChildren();
            if (!kids.containsKey(c)) {
                return null;
            }
            curr = kids.get(c);
        }
        return curr;
    }

    public char mostLikelyNextChar(String str) {
        TrieNode curr = goToNode(str);
        if (curr == null) {
            return '_';
        }
        Map<Character, TrieNode> kids = curr.getChildren();
        int max = Integer.MIN_VALUE;
        char likely = '_';
        for (Map.Entry<Character, TrieNode> entry: kids.entrySet()) {
            if (entry.getValue().getCount() > max) {
                max = entry.getValue().getCount();
                likely = entry.getKey();
            }
        }
        return likely;
    }

    public ArrayList<Letter> likelyNextCharsFrequency(String str) {
        ArrayList<Letter> letters = new ArrayList<>();
        int total = 0;
        TrieNode curr = goToNode(str);
        Map<Character, TrieNode> kids = new HashMap<>();
        try {
            kids = curr.getChildren();
        } catch (Exception e) {
            return letters;
        }
        if (kids.isEmpty()) {
            return letters;
        }
        for (Map.Entry<Character, TrieNode> entry: kids.entrySet()) {
            letters.add(new Letter(entry.getKey(), entry.getValue().getCount()));
            total += entry.getValue().getCount();
        }
        for (Letter l: letters) {
            double deicmal = (double)l.getFrequency()/total;
            int percentage = (int) Math.round(deicmal * 100);
            l.setFrequency(percentage);
        }
        return letters;
    }

    public boolean contains(String word) {
        TrieNode curr = root;
        for (char c: word.toCharArray()) {
            Map<Character, TrieNode> kids = curr.getChildren();
            //if c is present in kids then go to the next level. if not, return false
            if (kids.containsKey(c)) {
                curr = kids.get(c);
            }
            else {
                return false;
            }
        }
        //return true if you have successfully reached the end
        return true;
    }

    public boolean endOfWord(String word) {
        TrieNode curr = goToNode(word);
        if (curr == null) {
            return false;
        }
        return curr.isEndOfWord();
    }

    public ArrayList<Word>  getWords(String word) {
        TrieNode curr = goToNode(word);
        StringBuilder sb = new StringBuilder(word);
        //Map<String, Integer> possible = new HashMap<>();
        ArrayList<Word>possible = new ArrayList<>();

       // sb.append(word);
        collectWords(possible, sb, curr);

        return possible;
    }

    private void collectWords(ArrayList<Word> possible, StringBuilder sb, TrieNode curr)
    {
        //System.out.println(sb.toString());
        if (curr == null) {
            return;
        }
        if(curr.isEndOfWord())
        {
           // possible.put(sb.toString(), curr.count);
            possible.add(new Word(sb.toString(), curr.wordEndCount));
        }
        for (Map.Entry<Character, TrieNode> entry: curr.getChildren().entrySet()) {
            sb.append(entry.getKey());
            collectWords(possible, sb, entry.getValue());
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    class TrieNode {//internal class
        int count; //count # of times path taken
        int wordEndCount; //# of times full word appears
        Map<Character,TrieNode> children;

        private Map<Character, TrieNode> getChildren() {
            return children;
        }

        public TrieNode() {
            count = 1;
            wordEndCount = 0;
            children = new HashMap<Character, TrieNode>();
        }
        private void incrementCount() {
            count++;
        }
        private int getCount() {
            return count;
        }
        private boolean isEndOfWord() {
            return wordEndCount>0;
        }
        private void setEndOfWord() {
            this.wordEndCount++;
        }

        public String toString() {
            return "(" + count + "," + wordEndCount + ")";
        }
    }

    /*public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple");
        trie.insert("orange");
        trie.insert("banana");
        System.out.println(trie.contains("banana"));
        System.out.println(trie.contains("banan"));
        System.out.println(trie.contains("app"));
        System.out.println(trie.contains("apples"));
        System.out.println(trie.contains("kiwi"));
    }*/

}