import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.*;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.lang.model.util.ElementScanner6;

import java.nio.ByteBuffer;
import java.lang.*;

public class initbkgtree {

    public static MappedByteBuffer tree,rt;

    void putNodeRT(int index) {
        for (int i = 0; i < 30; i++)
            rt.putChar(300 * index + 2 * i, ' ');
        for (int i = 0; i < 60; i++)
        {
            // System.out.println(300*index + 60 + 4*i);
            rt.putInt(300 * index + 60 + 4 * i, 0);
        }
    }
    
    void putNode(int index)
    {
        for(int i=0;i<30;i++)
            tree.putChar(300*index + 2*i, ' ');
        for(int i=0;i<60;i++)
            tree.putInt(300*index + 60 + 4*i, 0);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        /* Creating mmap file for the tree nodes */
        File f = new File("BKfile");
        f.delete();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        long bufferSize = 90000*((Integer.SIZE/8)*60+60);
        tree = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);
        initbkgtree bkt = new initbkgtree();
        bkt.putNode(0);
        fc.close();
        /* Creating mmap file for the root node */
        File rtfp = new File("RTfile");
        rtfp.delete();
        FileChannel fc1 = new RandomAccessFile(rtfp, "rw").getChannel();
        long bufferSize1 = ((Integer.SIZE / 8) * 60 + 60);
        rt = fc1.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize1);
        bkt.putNodeRT(0);
        fc1.close();

        

        

    }

}