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
        String rom = "C:/Users/Alifa/Desktop/lc3-backup/eclipse/LC3Chip8Emulator/lc3-obj/chip8roms/test2.bin";
        InputStream input = new FileInputStream(rom);  
        DataInputStream inst = new DataInputStream(input);
        int index = 0;
        int counter = 0;
        ArrayList<String> data = data=new ArrayList<String>();
        while((inst.available())>0) { 
            int instruction = inst.readChar();
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

                case 8:
                    toAppend += String.format(" | opcode = %02X",opcode);
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

        /*
            dos>cd C:\Users\Alifa\Desktop\lc3-backup\eclipse\LC3Chip8Emulator\ant
            dos>2.compile.bat src\chip8\chip8emu.lct
            dos>3.run.bat out\chip8emu.lct_asm.txt out\chip8emu.lct_asm.obj hex > ..\lc3-obj\chip8out\out.txt
            dos>cd C:\Users\Alifa\Desktop\mystuff\faiz.dev.root\gitdir\github-fizaan\temp\vsc-java\java
            dos>java Chip8UnitTest.java hex
        */

        String lc3chip8out = "C:/Users/Alifa/Desktop/lc3-backup/eclipse/LC3Chip8Emulator/lc3-obj/chip8out/out.txt";
        BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(lc3chip8out)));
        String line;
        while((line=reader.readLine())!=null)
            if(index>=data.size())
                break;
            else if(!line.equals(data.get(index++)))
                System.out.println("No match at line: " + index);
    }
}