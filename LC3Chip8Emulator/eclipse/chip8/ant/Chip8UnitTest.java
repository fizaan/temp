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
        String home = "C:/Users/Alifa/Desktop/LC3Chip8Emulator/eclipse/chip8";
        String rom = home + "/lc3-obj/chip8roms/" + 
            args[1];
        File romfile = new File(rom);
        InputStream input = new FileInputStream(romfile);  
        DataInputStream inst = new DataInputStream(input);
        int index = 0;
        int counter = 0;
        int[] mem = new int[0xfff];
        int START_ADDRESS = 0x200;
        int FONTSET_START_ADDRESS = 0x50;
        int i = 0;
        ArrayList<String> data = data=new ArrayList<String>();
        
        while((inst.available())>0) 
            mem[START_ADDRESS + i++] = inst.readByte();

        int[] fontset = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F 
        };

        for(i=0; i<fontset.length; i++)
            mem[i+FONTSET_START_ADDRESS] = fontset[i];

        StringBuilder fonts = new StringBuilder();    
        for(i = 0; i < fontset.length; i++)
            fonts.append(String.format("%02X ",mem[i+FONTSET_START_ADDRESS]));
        fonts.append("\n");

        data.add(fonts.toString());
            
        
        for(int k = 0; k < romfile.length(); k += 2) { 
            int instruction = (mem[k+START_ADDRESS] & 0xff) << 8 | (mem[k+1+START_ADDRESS] & 0xff);
            int opcode = instruction >> 12;
            int r1 = (instruction >> 8) & 0xf;
            int r2 = (instruction >> 4) & 0xf;
            int lowbyte = instruction & 0xff;
            int last4bits = instruction & 0xf;
            String toAppend = "";
            if(args[0].equals("hex"))
                toAppend = String.format("%02X - %02X: %02X %02X %02X %02X %02X",counter,instruction,opcode,r1,r2,lowbyte,last4bits);
            else
                toAppend = String.format("%d - %d: %d %d %d %d %d",counter,instruction,opcode,r1,r2,lowbyte,last4bits);
            
            counter++;

            switch(opcode) {
                case 0: {
                    switch(lowbyte) {
                        case 0xe0:
                            toAppend += String.format(" | opcode = %02X %02X CLS",opcode,lowbyte);
                        break;
                        case 0xee:
                            toAppend += String.format(" | opcode = %02X %02X RET",opcode,lowbyte);
                        break;
                        default:
                            toAppend += String.format(" | opcode = %02X %02X IGNORE",opcode,lowbyte);
                        break;
                    }
                }
                break;

                case 1:
                    toAppend += String.format(" | opcode = %02X COMPUTE3",opcode);
                break;

                case 2:
                    toAppend += String.format(" | opcode = %02X COMPUTE4",opcode);
                break;

                case 3:
                    toAppend += String.format(" | opcode = %02X COMPUTE5",opcode);
                break;

                case 4:
                    toAppend += String.format(" | opcode = %02X COMPUTE6",opcode);
                break;

                case 5:
                    toAppend += String.format(" | opcode = %02X COMPUTE7",opcode); 
                break;

                case 6:
                    toAppend += String.format(" | opcode = %02X COMPUTE8",opcode);
                break;

                case 7:
                    toAppend += String.format(" | opcode = %02X COMPUTE9",opcode);
                break;

                case 9:
                    toAppend += String.format(" | opcode = %02X COMPUTE10",opcode);
                break;

                case 0xa:
                    toAppend += String.format(" | opcode = %02X COMPUTE11",opcode);
                break;

                case 0xb:
                    toAppend += String.format(" | opcode = %02X COMPUTE12",opcode);
                break;

                case 0xc:
                    toAppend += String.format(" | opcode = %02X COMPUTE13",opcode);
                break;

                case 0xd:
                    toAppend += String.format(" | opcode = %02X COMPUTE14",opcode);
                break;

                case 8: {
                    switch(last4bits) {
                        case 0:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE-8A",opcode,last4bits);
                        break;

                        case 1:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE-8B",opcode,last4bits);
                        break;

                        case 2:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE-8C",opcode,last4bits);
                        break;

                        case 3:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE-8D",opcode,last4bits);
                        break;

                        case 4:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE-8E",opcode,last4bits);
                        break;

                        case 5:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE-8F",opcode,last4bits);
                        break;

                        case 6:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE-8G",opcode,last4bits);
                        break;

                        case 7:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE-8H",opcode,last4bits);
                        break;

                        case 0xe:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE-8I",opcode,last4bits);
                        break;

                        default:
                            toAppend += String.format(" | opcode = %02X %02X Err NO OPCPDE",opcode,last4bits);
                        break;
                    }
                }
                break;

                case 0xe: {
                    switch (lowbyte) {
                        case 0x9e:
                            toAppend += String.format(" | opcode = %02X %02X UNIMPLEMENTED CODE",opcode,lowbyte);
                        break;
                        case 0xa1:
                            toAppend += String.format(" | opcode = %02X %02X UNIMPLEMENTED CODE",opcode,lowbyte);
                        break;
                        default:
                            toAppend += String.format(" | opcode = %02X %02X Err NO OPCPDE",opcode,lowbyte);
                        break;
                    }
                }
                break;

                case 0xf: {
                    switch (lowbyte) {
                        case 7:
                            toAppend += String.format(" | opcode = %02X %02X LD[ %02X ],",opcode,lowbyte,r1);
                        break;
                        
                        case 0xa:
                            toAppend += String.format(" | opcode = %02X %02X UNIMPLEMENTED CODE",opcode,lowbyte);
                        break;
                        
                        case 0x15:
                            toAppend += String.format(" | opcode = %02X %02X LD delaytimer reg[ %02X ]",opcode,lowbyte,r1); 
                        break;
                        
                        case 0x08:
                            toAppend += String.format(" | opcode = %02X %02X LD soundtimer reg[ %02X ]",opcode,lowbyte,r1); 
                        break;
                        
                        case 0x1e:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE15",opcode,lowbyte);
                        break;
                        
                        case 0x29:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE16",opcode,lowbyte);
                        break;

                        case 0x33:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE17",opcode,lowbyte);
                        break;

                        case 0x55:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE18",opcode,lowbyte);
                        break;

                        case 0x65:
                            toAppend += String.format(" | opcode = %02X %02X COMPUTE19",opcode,lowbyte);
                        break;
                    
                        default:
                            toAppend += String.format(" | opcode = %02X %02X Err NO OPCPDE",opcode,lowbyte);
                        break;
                    }
                }
                break;

                default:
                    toAppend += "Error: No opcode found"; 
                break;
            }

            data.add(toAppend);
        }

        /** Testing:
            for(String out: data) {
                System.out.println(out);
                Thread.currentThread();
                try {
                    Thread.sleep(500);
                } catch(Exception e) {

                }
            }
        */

        /*
            dos>cd C:/Users/Alifa/Desktop/lc3-backup/eclipse/LC3Chip8Emulator/ant
            dos>2.compile.bat src/chip8/chip8emu.lct
            dos>3.run.bat out/chip8emu.lct_asm.txt out/chip8emu.lct_asm.obj hex > ../lc3-obj/chip8out/out.txt
            dos>cd C:/Users/Alifa/Desktop/mystuff/faiz.dev.root/gitdir/github-fizaan/temp/vsc-java/java
            dos>java Chip8UnitTest.java hex test1.bin
        */

        String lc3chip8out = home + "/lc3-obj/chip8out/out.txt";
        BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(lc3chip8out)));
        String line;
        while((line=reader.readLine())!=null) {
            if(index>=data.size())
                break;
            
            String element = data.get(index++);
            line=line.trim();
            element=element.trim();
            if(!line.equals(element)) {
                System.out.println("No match at line: " + index);
                System.out.println(line);
                System.out.println(element);
                System.out.println();
            }
        }

        String membinfile = home + "/lc3-obj/chip8roms/memory.bin";
        inst = new DataInputStream(new FileInputStream(membinfile));
        i = 0;
        while((inst.available())>0) {
            int x = mem[i++] & 0xff;
            int y = inst.readByte() & 0xff;
            if (x != y)
                System.out.printf("No match: %02X %02X\n",x,y); 
        }

        if(i != mem.length)
            System.out.printf("Error: file size is not 0xfff: %02X",i);
            
        
    }
}