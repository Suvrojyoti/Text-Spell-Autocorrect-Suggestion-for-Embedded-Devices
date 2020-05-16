import java.util.ArrayList;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.*;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Comparator;
import java.util.TreeSet;
import javax.lang.model.util.ElementScanner6;



import java.nio.ByteBuffer;
import java.lang.*;

class Node {
       // stores the word of the current Node 
    // string word;
    public char word[] = new char[30];
    // links to other Node in the tree 
    public int next[] = new int[2 * 30];
// constructors 
    Node(char[] x) {
        int i;
        for (i = 0; i < x.length; i++) {
            word[i] = x[i];
        }
        // initializing next[i] = 0 
        for (i = 0; i < 2 * 30; i++) {
            next[i] = 0;
        }
    }

    Node(char[] x, int[] next1) {
        int i;
        for (i = 0; i < x.length; i++) {
            word[i] = x[i];
        }

        for (i = 0; i < 2 * 30; i++) {
            next[i] = next1[i];
        }
    }

    Node() {
        int i;
        for (i = 0; i < 30; i++)
            word[i] = ' ';
            // initializing next[i] = 0 
        for (i = 0; i < 2 * 30; i++)
            next[i] = 0;
    }
}

public class bktreequery {

    public static int MAXN = 90000;
    public static int TOL = 1;
    public static int LEN = 30;
    public static boolean flag = false;
    public static int rtfd;
    public static int treefd;
    public static int ptr = 0;
    public static MappedByteBuffer tree, RT;

    int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

// Edit Distance 
// Dynamic-Approach O(m*n) 
    int editDistance(String a, String b) {
        int m = a.length();
        int n = b.length();
        int dp[][] = new int[m + 1][n + 1];
        int i, j;
// filling base cases 
        for (i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        // populating matrix using dp-approach 
        for (i = 1; i <= m; i++) {
            for (j = 1; j <= n; j++) {
                if (a.charAt(i - 1) != b.charAt(j - 1)) {
                    dp[i][j] = min(1 + dp[i - 1][j], 1 + dp[i][j - 1], 1 + dp[i - 1][j - 1]);
                } else {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    Node getNode(int index) {
        char a[] = new char[30];
        int b[] = new int[60];
        for (int i = 0; i < 30; i++)
            a[i] = tree.getChar(300 * index + i * 2);
        for (int i = 0; i < 60; i++)
            b[i] = tree.getInt(300 * index + 60 + i * 4);
        return new Node(a, b);

    }

    void putNode(int index, Node node) {
        for (int i = 0; i < 30; i++)
            tree.putChar(300 * index + 2 * i, node.word[i]);
        for (int i = 0; i < 60; i++)
            tree.putInt(300 * index + 60 + 4 * i, node.next[i]);
    }

    Node getNodeRT(int index) {
        char a[] = new char[30];
        int b[] = new int[60];
        for (int i = 0; i < 30; i++)
            a[i] = RT.getChar(300 * index + i * 2);
        for (int i = 0; i < 60; i++)
            b[i] = RT.getInt(300 * index + 60 + i * 4);
        return new Node(a, b);

    }

    void putNodeRT(int index, Node node) {
        for (int i = 0; i < 30; i++)
            RT.putChar(300 * index + 2 * i, node.word[i]);
        for (int i = 0; i < 60; i++) {
            RT.putInt(300 * index + 60 + 4 * i, node.next[i]);
        }
    }

    void add(int rootindex, Node curr) throws IOException {
 // if it is the first Node 
        // then make it the root Node 
        Node root = getNode(rootindex);

    
        String s1 = new String();
        for (int i = 0; curr.word[i] != ' '; i++)
            s1 += curr.word[i];
        String s2 = new String();
        for (int i = 0; root.word[i] != ' '; i++)
            s2 += root.word[i];
  
// get its editDist from the Root Node 
        int dist = editDistance(s1, s2);
        // System.out.println(s1 + " " + s2 + " " + dist);
        if (getNode(root.next[dist]).word[0] == ' ') {
             /* if no Node exists at this dist from root 
        * make it child of root Node*/

        // incrementing the polong inter for curr Node 
            ptr += 1;
             // adding curr Node to the tree 
            putNode(ptr, curr);
                   // curr as child of root Node 
            tree.putInt(300 * rootindex + 60 + 4 * dist, ptr);

        } else {
            // recursively find the parent for curr Node 
            add(root.next[dist], curr);
        }
    }
// adds curr Node to the tree 
    void add(Node curr) throws IOException {
        if (!flag) {
             // if it is the first Node 
        // then make it the root Node 
            flag = true;
            Node cur1 = new Node(curr.word);
            putNodeRT(0, cur1);
            return;
        }

        String s1 = new String();
        for(int i=0;curr.word[i]!=' ';i++)
            s1+=curr.word[i];
        String s2 = new String();
        for (int i = 0; getNodeRT(0).word[i] != ' '; i++)
            s2 += getNodeRT(0).word[i];



        int dist = editDistance(s1, s2);
        if (getNode(getNodeRT(0).next[dist]).word[0] == ' ') {
            ptr += 1;

            putNode(ptr, curr);

            RT.putInt(60 + 4 * dist, ptr);
        } else {
            add(RT.getInt(60 + 4 * dist), curr);
        }

    }

    public ArrayList<String> getsimilarWords(int a, String s) {
        Node rot;
        if (a == -1) {
            rot = getNodeRT(0);
        } else {
            rot = getNode(a);
        }
        String nop = new String(rot.word);

        ArrayList<String> ret = new ArrayList<String>();
        if (nop.charAt(0) == ' ') {
            return ret;
        }
    // calculating editdistance of s from root 
        String s1 = new String();
        for (int i = 0; rot.word[i] != ' '; i++)
            s1 += rot.word[i];
    // if dist is less than tolerance value 
    // add it to similar words 
        int dist = editDistance(s1, s);

        if (dist <= TOL) {
            ret.add(s1);
        }
  // iterate over the string havinng tolerane 
    // in range (dist-TOL , dist+TOL) 
        int start = dist - TOL;
        if (start < 0)
            start = 1;
        while (start < dist + TOL) {
            ArrayList<String> tmp = getsimilarWords(rot.next[start], s);
            if (tmp.size() != 0) {
                for (String i : tmp) {
                    ret.add(i);
                }
            }
            start += 1;
        }
        return ret;
    }

    public static void main(String args[]) throws FileNotFoundException, IOException, InterruptedException {
        int i;
        /* opening mmap file for the tree nodes */
        File f = new File("BKfile");
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        long bufferSize = 90000 * ((Integer.SIZE / 8) * 60 + 60);
        tree = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);
        /* opening mmap file for the root node */
        File f1 = new File("RTfile");
        FileChannel fc1 = new RandomAccessFile(f1, "rw").getChannel();
        long bufferSize1 = ((Integer.SIZE / 8) * 60 + 60);
        RT = fc1.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize1);

        bktreequery bkt = new bktreequery();
        char choice;


        while (1 < 2) {

            System.out.println("enter 1 for word or 0 to exit");
            Scanner sc = new Scanner(System.in);
            choice = sc.next().charAt(0);
            if (choice == '0')
                break;
            else {
                System.out.println("enter the word");
                String in = sc.next();
                ArrayList<String> match = bkt.getsimilarWords(-1, in);
                for (String s : match)
                    System.out.println(s);
            }
        }

    }

}