import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.*;
public class TrieDisplay extends JPanel implements KeyListener, MouseListener
{
    private JFrame frame;
    private int size = 30, width = 1000, height = 600;
    private Trie trie;
    private String word;
    private Color color; // Word you are trying to spell printed in large font
    private char likelyChar; // Used for single most likely character
    private boolean wordsLoaded; // Use this to make sure words are alll loaded before you start typing
    private ArrayList<String> words;
    private ArrayList<Color> colors;
    private boolean showMenu = true;
    private String selectedFile = null;
    private String[] options = {"Billie.txt", "Taylor.txt", "Ariana.txt"};
    private  boolean deletedWord = false;
    private boolean offScreen = false;
    private int index;
    private FontMetrics metrics;
    private ArrayList<Word> mostCommonWords;
    private Rectangle[] optionBoxes;
    private Rectangle backButton;
    private String selectedArtist = null;
    private Image i;
    ArrayList<Word> likelyWords;

    public TrieDisplay()
    {

        frame=new JFrame("Trie Next");
        frame.setSize(width,height);
        frame.add(this);
        frame.addKeyListener(this);
        frame.addMouseListener(this);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Default Settings
        word = "";
        likelyChar = ' ';  // Used for single most likely character
        wordsLoaded = false;

        trie = new Trie();

        wordsLoaded = true; // Set flag to true indicating program is ready

        //sets locations for the menu "buttons"
        optionBoxes = new Rectangle[options.length];
        for (int i = 0; i < options.length; i++)
        {
            optionBoxes[i] = new Rectangle(400, 200 + i * 60, 180, 50);
        }


        backButton = new Rectangle(frame.getWidth()/2- 80,10, 160,50);
        //i = new ImageIcon("C:\\Users\\mashi\\Downloads\\drive-download-20250227T223144Z-001\\arrow.png").getImage();


        words = new ArrayList<>();
        colors = new ArrayList<>();

        metrics = frame.getFontMetrics(frame.getFont());
    }

    public void paintMenu(Graphics g)
    {
        //main menu where user can select which artist they want.

        g.setColor(Color.decode("#F1E3E4"));
        g.fillRect(0,0,width, height);

        g.setColor(Color.decode("#A288A6"));
        g.setFont(new Font("Montserrat", Font.BOLD, 30));
        g.drawString("Select an Artist", 370,150);

        g.setFont(new Font("Segoe Script", Font.PLAIN, 20));

        // draws the all ready set locations for each box
        // ps. I did boxes because the buttons look ugly
        //uses the loaded text files with removed .txt as "button" labels
        for(int i=0; i<options.length; i++)
        {
            g.setColor(Color.decode("#BB9BB0"));
            g.fillRect(optionBoxes[i].x, optionBoxes[i].y, optionBoxes[i].width, optionBoxes[i].height);
            g.setColor(Color.WHITE);
            g.drawString(options[i].substring(0,options[i].length()-4), optionBoxes[i].x + 50, optionBoxes[i].y + 30);
        }
    }


    public void paintMain(Graphics g)
    {
        //identifies which artist is being used, and what corresponding color to use with it
        Color color1 = null;
        Color color2 = null;
        if(selectedArtist.equals("Billie")) {
            color1 = Color.decode("#9EB7E5");
            color2 = Color.decode("#517EE1");
        }
        else if(selectedArtist.equals("Ariana"))
        {
            color1 = Color.decode("#F7E3AF");
            color2 = Color.decode("#F7AF9D");
        }
        else if(selectedArtist.equals("Taylor"))
        {
            color1 = Color.decode("#AF8267");
            color2 = Color.decode("#ad0f1e");
        }
        Graphics2D g2=(Graphics2D)g;
        g2.setColor(color1);
        g2.fillRect(0,0,frame.getWidth(),frame.getHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Montserrat",Font.PLAIN,24));
        g2.drawString("Selected: " + options[index], 50, 50);

        FontMetrics metrics = g2.getFontMetrics();

        //gets size of the letters vertical wize
        int lineHeight = metrics.getHeight();

        int extra = 0;
        int boxextra = 0;
        int x = 50;
        int sentenceWidth = 0;

        Rectangle textBox = new Rectangle(50, 90, frame.getWidth()-100, 80);

        // this allows for textWarp so it doesn't go off the screen but instead to the next line
        //this for loop checks how many lines should be used and basically calculates how much textbox
        //should expand

        for (String w : words) {
            int wordWidth = metrics.stringWidth(w);

            if (sentenceWidth + wordWidth > textBox.width - 20) {
                x = 50;
                extra += lineHeight;
                boxextra += lineHeight;
                sentenceWidth = 0;
            }

            x += wordWidth;
            sentenceWidth += wordWidth;
        }

        g.setColor(color2);
        g.fillRoundRect(textBox.x, textBox.y, textBox.width, textBox.height + boxextra+30, 40, 40);
        g.setColor(Color.WHITE);

        if(words.isEmpty())
        {
            g2.setFont(new Font("SansSerif",Font.BOLD,30));       // Header
            g2.setColor(Color.WHITE);
            if (wordsLoaded)
                g2.drawString("Start Typing:",70,130);
            else
                g2.drawString("Loading... please wait",70,130);
            return;
        }
        x = 50;
        extra =0;
        sentenceWidth = 0;

        g2.setFont(new Font("Montserrat", Font.PLAIN, 22));

        //this will print out the words with the textwarp implementd
        for(int j=0; j<words.size(); j++)
        {
            String w = words.get(j) + " ";
            int wordWidth = metrics.stringWidth(w);

            if (sentenceWidth + wordWidth > textBox.width - 20)
            {
                x = 50;
                extra += lineHeight;
                sentenceWidth = 0;

            }

            g2.setColor(colors.get(j));
            g2.drawString(w, x+25, 130 + extra);

            x += wordWidth;
            sentenceWidth += wordWidth;
        }

        //this is basically using all the logic from trie to store it all into ArrayList to later be printed
        ArrayList<Letter> likelyChars = trie.likelyNextCharsFrequency(word);
        Collections.sort(likelyChars);
        ArrayList<Word> notsorted = trie.getWords(word);
        Collections.sort(notsorted);
        likelyWords = new ArrayList<Word>();
        Collections.sort(notsorted);
        for (int i = notsorted.size()-1; i >= 0; i--) {
            likelyWords.add(notsorted.get(i));
        }

        // +extra makes sure that the expanding main textbox does not cover
        // the other textboxes
        Rectangle boxes = new Rectangle((frame.getWidth()-(280*2+100))/2, 230+extra, 280, 900);

        //likely char
        g.setColor(color2);
        g.fillRoundRect(boxes.x, boxes.y, 200, 60, 30,30 );
        g2.setColor(Color.WHITE);
        g2.drawString("Likely Char: " + (likelyChar == '_' ? "None": likelyChar), boxes.x+15, boxes.y+40);

        //char frequency
        int nextchar = 0;
        //prints chars making sure that the chars do not go offscreen
        g.setColor(color2);
        g.fillRoundRect(boxes.x + 240, boxes.y, 110, height-270,30,30 );
        g2.setColor(Color.WHITE);
        g2.drawString("Char:", boxes.x+255, boxes.y+25);

        int total = boxes.y + 80;
        for(Letter l:likelyChars) {
            if (total < height) {
                g2.drawString(l.toString(), boxes.x + 255, boxes.y+nextchar+55);
                total += 30;
                nextchar += 30;
            }
        }

        //likely word

        g.setColor(color2);
        g.fillRoundRect(boxes.x + 400, boxes.y, boxes.width, height-270, 30,30);
        g2.setColor(Color.WHITE);
        g2.drawString("Likely Word:", boxes.x+450, boxes.y+25);


        //prints words making sure that they do not go offscreen
        int nextWord = 0;
        total = boxes.y + 80;
        for(Word w:likelyWords)
        {
            if (total < height) {
                g2.drawString(w.toString(), boxes.x + 420, boxes.y + nextWord + 55);
                total += 30;
                nextWord += 30;
            }
        }

        //back to menu button
        g.setColor(color2);
        g.fillRoundRect(backButton.x, backButton.y, backButton.width, backButton.height, 50,50);
        g.setColor(Color.WHITE);
        g.drawString("MENU", backButton.x + 45, backButton.y + 30);
    }



    // All Graphics handled in this method.  Don't do calculations here
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);     // Setup and Background
        if(showMenu)
        {
            paintMenu(g);
        }
        else
            paintMain(g);
    }

    public String printLikelyChars () {
        ArrayList<Letter>possible = trie.likelyNextCharsFrequency(word);
        Collections.sort(possible);
        StringBuilder sb = new StringBuilder();
        for (Letter l: possible) {
            sb.append(l.toString() + " ");
        }
        return sb.toString();
    }

    public String printLikelyWords() {
        double total = 0;
        if (word.length() > 0) {
            StringBuilder sb = new StringBuilder();
            ArrayList<Word>possible = trie.getWords(word);
            System.out.println(possible);
            ArrayList<Word>sorted = new ArrayList<Word>();
            Collections.sort(possible);
            for (int i = possible.size()-1; i >= 0; i--) {
                sorted.add(possible.get(i));
            }
            for (Word w : sorted) {
                sb.append(w.toString() + " ");
            }
            return sb.toString();
        }
        return "";
    }

    private void updateColor() {
        if (trie.endOfWord(word)) {
            color = Color.decode("#23ff00");
        } else if (trie.contains(word)) {
            color = Color.WHITE;
        } else {
            if (likelyChar == '_') {
                color = Color.decode("#fc0000");
            } else {
                color = Color.decode("#fc0000");
            }
        }
    }

    public void keyPressed(KeyEvent e)
    {              // This handles key press
        int keyCode = e.getKeyCode();
        if (keyCode == 8) {
            if (!word.isEmpty()) {
                word = word.substring(0, word.length() - 1);
                updateColor();
                words.set(words.size() - 1, word);
                colors.set(words.size() - 1, color);
            } else if (!words.isEmpty()) {
                words.remove(words.size() - 1);
                colors.remove(words.size() - 1);
                if (!words.isEmpty()) {
                    word = words.get(words.size() - 1);
                }
                else {
                    word = "";
                }
                updateColor();
            }
            likelyChar = trie.mostLikelyNextChar(word);
            repaint();
        }
        else if (keyCode == 32 ) {
            word = "";
            words.add(word);
            color = Color.BLACK;
            colors.add(color);
            likelyChar = trie.mostLikelyNextChar(word);
            repaint();
        }
        else if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {  // alphabetic key
            word += KeyEvent.getKeyText(keyCode).toLowerCase();
            updateColor();
            if (words.isEmpty()) {
                words.add(word);
                colors.add(color);
            }
            else {
                words.set(words.size() - 1, word);
                colors.set(words.size() - 1, color);
            }

            likelyChar = trie.mostLikelyNextChar(word);
            repaint();

        }
    }

    public void mouseClicked(MouseEvent e)
    {
        if (showMenu) {
            for (int i = 0; i < options.length; i++)
            {
                //baiscally identifies where the user clicked and whether its on
                //one of the boxes that was all ready defined
                if (optionBoxes[i].contains(e.getPoint()))
                {
                    selectedFile = options[i];

                    showMenu = false;
                    wordsLoaded = false;
                    index = i;

                    selectedArtist = selectedFile.substring(0, selectedFile.length()-4);
                    new Thread(() -> {
                        readFileToTrie(selectedFile);
                        wordsLoaded = true;

                        repaint();
                    }).start();

                    repaint();
                }
            }
        }
        //this basically checkes if the menu button is clicked by doing same logic as above
        if (!showMenu && backButton.contains(e.getPoint()))
        {
            showMenu = true;
            word = "";
            words = new ArrayList<>();
            selectedArtist = null;
            repaint();
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void readFileToTrie(String fileName) {

        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                String [] split = line.split(" ");
                for(int i=0; i<split.length; i++)
                {
                    if (!split[i].equals(" ")) {
                        trie.insert(split[i].toLowerCase());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

    }


    /*** empty methods needed for interfaces **/
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
    public void actionPerformed(ActionEvent e) {}

    public static void main(String[] args){
        TrieDisplay app=new TrieDisplay();
    }
}