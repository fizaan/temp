public class Test {
    public static void main(String args[]) {
        int x = 0x838;
        int y = 0xfc;
        int z = x+y;
        System.out.printf("%04X\n",z);
        /*
            Solution:
            Technically this is
            same as:

            y |= 0xffffff << 2;
            y |= 0xffffff00;
            
            This is a 32-bit 
            sign-extension.
            It tells java to 
            treat y as a signed 
            negative number. 
            An int is 4-bytes
            on both 32/64 bit 
            systems. Google
            "how many bytes is 
            an int in java" if
            you're not sure.
        */

        y |= (0xffffff << 2);
        z = x+y;
        System.out.printf("%04X\n",z);
        z &= 0xffff;    //Don't forget
                        //to mask last
                        //2-bytes (16 bits)
        System.out.printf("%04X\n",z);

    }
}