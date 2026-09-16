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
        // TODO: Reconstituer le int sur 4 octets.
		int valeur = 0x00;
		valeur = valeur & (memory[offset]) << 0;
		valeur = valeur & (memory[offset]) << 8;
		valeur = valeur & (memory[offset]) << 16;
		valeur = valeur & (memory[offset]) << 24;
        return valeur;
    }

    public static int writeShort(byte[] memory, int offset, short value) {
        // TODO: Écrire les 2 octets de 'value'.
        return 2;
    }

    public static short readShort(byte[] memory, int offset) {
        // TODO: Lire le short sur 2 octets.
        return 0;
    }
}
