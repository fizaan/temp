public class TwosComplement {
    public static void main(String args[]) {
      int x=-3;
      int y=3;
      y = ~y;
      y++;
      System.out.printf("1. x in Java Two's complement signed #: %02X\n", x);
      System.out.printf("2. y in Java Two's complement signed #: %02X\n", y);
      int imm5 = x & 0x1f;
      System.out.printf("3. imm5: %d, %02X\n", imm5, imm5);
      int sext32 = 0xfffffff0 | imm5;
      int sext16 = sext32 & 0xffff;
      System.out.printf("4. imm5 SEXT: %d, %d, %02X %02X\n",
        sext32, sext16, sext32, sext16);

      /*************************************************
        1. x in Java Two's complement signed #: FFFFFFFD
        2. y in Java Two's complement signed #: FFFFFFFD
        3. imm5: 29, 1D
        4. imm5 SEXT: -3, 65533, FFFFFFFD FFFD
      **************************************************/
    }
}