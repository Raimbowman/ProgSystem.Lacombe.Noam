public class Utils {

    public static int writeInt(byte[] memory, int offset, int value) {
        // Écrire les 4 octets de 'value' dans 'memory'
        // à partir de 'offset', en big-endian.
        memory[offset+3] = (byte)(value & 0xFF);
		memory[offset+2] = (byte)(value >> 8 & 0xFF);
		memory[offset+1] = (byte)(value >> 16 & 0xFF);
		memory[offset+0] = (byte)(value >> 24 & 0xFF);
		return 4;
    }

    public static int readInt(byte[] memory, int offset) {
        int valeur =    ((memory[offset]     & 0xFF) << 24) 
                      + ((memory[offset + 1] & 0xFF) << 16) 
                      + ((memory[offset + 2] & 0xFF) << 8)  
                      + ( memory[offset + 3] & 0xFF);
        return valeur;
    }

    public static int writeShort(byte[] memory, int offset, short value) {
        memory[offset]     = (byte)((value >> 8) & 0xFF);
        memory[offset + 1] = (byte)( value & 0xFF);
        return 2;
    }

    public static short readShort(byte[] memory, int offset) {
        short valeur =  (short) (((memory[offset] & 0xFF) << 8) 
                        |         (memory[offset + 1] & 0xFF));
    }
}
