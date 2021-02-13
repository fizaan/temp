import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Chip8UnitTest {
    public static void main(String args[]) throws Exception {
        String rom = "C:/Users/Alifa/Desktop/lc3-obj/chip8roms/test1.BC_test.bin";
        InputStream input = new FileInputStream(rom);  
        DataInputStream inst = new DataInputStream(input);
        int index = 0;
        ArrayList<String> data = data=new ArrayList<String>();
        while((inst.available())>0) { 
            int instruction = inst.readChar();
            int opcode = instruction >> 12;
            int r1 = (instruction >> 8) & 0xf;
            int r2 = (instruction >> 4) & 0xf;
            int lowbyte = instruction & 0xff;
            int last4bits = instruction & 0xf;
            if(args[0].equals("hex"))
                data.add(String.format("%02X %02X %02X %02X %02X %02X",instruction,opcode,r1,r2,lowbyte,last4bits));
            else
                data.add(String.format("%d %d %d %d %d %d",instruction,opcode,r1,r2,lowbyte,last4bits));
        }

        /*
            dos>cd C:\Users\Alifa\Desktop\lc3-backup\eclipse\LC3Compiler\ant
            dos>2.compile.bat src\chip8\chip8emu.lct
            dos>3.run.bat out\chip8emu.lct_asm.txt out\chip8emu.lct_asm.obj hex > C:\Users\Alifa\Desktop\lc3-obj\chip8out\out.txt
            dos>cd C:\Users\Alifa\Desktop\mystuff\faiz.dev.root\gitdir\github-fizaan\temp\vsc-java\java
            dos>java Chip8UnitTest.java hex
        */

        String lc3chip8out = "C:/Users/Alifa/Desktop/lc3-obj/chip8out/out.txt";
        BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(lc3chip8out)));
        String line;
        while((line=reader.readLine())!=null)
            if(index>=data.size())
                break;
            else if(!line.equals(data.get(index++)))
                System.out.println("No match at line: " + index);
    }
}