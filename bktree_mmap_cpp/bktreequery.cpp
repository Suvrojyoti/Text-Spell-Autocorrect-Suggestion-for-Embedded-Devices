#include<bits/stdc++.h>
using namespace std; 
#include <chrono> 
using namespace std::chrono; 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <math.h>
// maximum number of words in dict[] 
#define MAXN 90000 

// defines the tolerence value 
#define TOL 1

// defines maximum length of a word 
#define LEN 30
bool flag=0;

struct Node 
{ 
    // stores the word of the current Node 
    // string word;
	char word[30]; 

    // links to other Node in the tree 
    int next[2*LEN]; 

    // constructors 
    Node(char *x)
    { 
        int i;
		for( i=0;x[i];i++)
			word[i] = x[i];
        word[i]='\0';
        // initializing next[i] = 0 
        for(long int i=0; i<2*LEN; i++) 
            next[i] = 0; 
    } 
    Node() {
             
        word[0]='\0';
        // initializing next[i] = 0 
        for( int i=0; i<2*LEN; i++) 
            next[i] = 0;            
    } 
}; 

// stores the root Node 
// Node RT; 
int rtfd;
Node *RT=NULL;

// stores every Node of the tree 
// Node tree[MAXN]; 

int treefd;
Node *tree;
// index for current Node of tree 
long int ptr; 

long int min(long int a, long int b, long int c) 
{ 
    return min(a, min(b, c)); 
} 

// Edit Distance 
// Dynamic-Approach O(m*n) 
long int editDistance(string& a,string& b) 
{ 
    long int m = a.length(), n = b.length(); 
    long int dp[m+1][n+1]; 

    // filling base cases 
    for (long int i=0; i<=m; i++) 
        dp[i][0] = i; 
    for (long int j=0; j<=n; j++) 
        dp[0][j] = j; 

    // populating matrix using dp-approach 
    for (long int i=1; i<=m; i++) 
    { 
        for (long int j=1; j<=n; j++) 
        { 
            if (a[i-1] != b[j-1]) 
            { 
                dp[i][j] = min( 1 + dp[i-1][j], // deletion 
                                1 + dp[i][j-1], // insertion 
                                1 + dp[i-1][j-1] // replacement 
                            ); 
            } 
            else
                dp[i][j] = dp[i-1][j-1]; 
        } 
    } 
    return dp[m][n]; 
} 

void add(int rootindex,Node& curr) 
{ 
	Node root = tree[rootindex];
    if (root.word[0] == '\0' ) 
    { 
        // if it is the first Node 
        // then make it the root Node 
        root = curr; 
        return; 
    } 

    // get its editDist from the Root Node 
	string s1(curr.word);
	string s2(root.word);
    long int dist = editDistance(s1,s2); 

    if (tree[root.next[dist]].word[0] == '\0') 
    { 
        /* if no Node exists at this dist from root 
        * make it child of root Node*/

        // incrementing the polong inter for curr Node 
        ptr++; 

        // adding curr Node to the tree 
        tree[ptr] = curr; 

        // curr as child of root Node 
        tree[rootindex].next[dist] = ptr; 
    } 
    else
    { 
        // recursively find the parent for curr Node 
        // add(&tree[root->next[dist]],curr); 
        add(root.next[dist],curr);  
    } 
}

// adds curr Node to the tree 
void add(Node& curr) 
{ 
    if (!flag) 
    { 
        // if it is the first Node 
        // then make it the root Node 
		flag=1;
        strcpy(RT[0].word,curr.word); 
        return; 
    } 
    
    string s1(curr.word);
	string s2(RT[0].word);
    int dist = editDistance(s1,s2); 
    cout<<tree[RT->next[dist]].word<<endl;
    if (tree[RT->next[dist]].word[0] == '\0') 
    { 
        ptr++; 
        tree[ptr] = curr; 
        RT->next[dist] = ptr; 
    } 
     else
    { 
        add(RT->next[dist],curr); 
     } 
} 

vector <string> getSimilarWords(int a,string& s) 
{ 
    Node rot;
    if(a==-1)
        rot=RT[0];
    else
    {
        rot=tree[a];
    }
    string nop(rot.word);
    
    vector < string > ret; 
    if (nop=="") 
    {
        return ret; 
    }

    // calculating editdistance of s from root 
	string s1(rot.word);
    
    int dist = editDistance(s1,s); 
    // if dist is less than tolerance value 
    // add it to similar words 
    if (dist <= TOL)
    {
        string s4(rot.word);
        ret.push_back(s4); 
    } 

    // iterate over the string havinng tolerane 
    // in range (dist-TOL , dist+TOL) 
    int start = dist - TOL; 
    if (start < 0) 
    start = 1; 

    while (start < dist + TOL) 
    { 
        vector <string> tmp = 
            getSimilarWords(rot.next[start],s); 
        for (auto i : tmp) 
            ret.push_back(i); 
        start++; 
    } 
    return ret; 
} 

// driver program to run above functions 
int main() 
{ 

    rtfd = open("RTfile", O_RDWR);
    if (rtfd == -1)
    {
        perror("Error opening file for writing");
        exit(EXIT_FAILURE);
    }
    RT = (Node*)mmap(0, 1 * sizeof(Node), PROT_READ | PROT_WRITE, MAP_SHARED, rtfd, 0);
    if (RT == MAP_FAILED)
    {
        close(rtfd);
        perror("Error mmapping the file");
        exit(EXIT_FAILURE);
    }
    treefd = open("BKfile", O_RDWR);
    if (treefd == -1)
    {
        perror("Error opening file for writing");
        exit(EXIT_FAILURE);
    }
    tree = (Node*)mmap(0, MAXN * sizeof(Node), PROT_READ | PROT_WRITE, MAP_SHARED, treefd, 0);
    if (tree == MAP_FAILED)
    {
        close(treefd);
        perror("Error mmapping the file");
        exit(EXIT_FAILURE);
    }
    fstream file; 
    string word, t, q, filename; 
    // filename of the file 
    filename = "words.txt"; 
     long int i=0;
    // opening file 
    file.open(filename.c_str()); 
	int sz=0;
	bool b=(RT==NULL);
    string choice;
    ptr = 0;
    double t1;
    while(1)
    {
        cout<<"enter word or 0 to exit\n ";
        cin>>choice;
        if(choice=="0")
        break;
        // function call o get similar words
        vector < string > match = getSimilarWords(-1,choice); 
        cout << "similar words in dictionary for : " << choice << ":\n"; 
        for (auto x : match) 
            cout << x << endl; 
    }

    return 0; 
} 
