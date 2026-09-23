import java.nio.charset.StandardCharsets;

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
        return valeur;
    }

    public static int writeLong(byte[] memory, int offset, long value) {
        memory[offset+7] = (byte)(value & 0xFF);
        memory[offset+6] = (byte)(value >> 8 & 0xFF);
        memory[offset+5] = (byte)(value >> 16 & 0xFF);
        memory[offset+4] = (byte)(value >> 24 & 0xFF);
        memory[offset+3] = (byte)(value >> 32 & 0xFF);
        memory[offset+2] = (byte)(value >> 40 & 0xFF);
        memory[offset+1] = (byte)(value >> 48 & 0xFF);
        memory[offset+0] = (byte)(value >> 56 & 0xFF);
        return 8;
    }

    public static long readLong(byte[] memory, int offset) {
        long valeur =   ((memory[offset]     & 0xFFL) << 56)
                      | ((memory[offset + 1] & 0xFFL) << 48)
                      | ((memory[offset + 2] & 0xFFL) << 40)
                      | ((memory[offset + 3] & 0xFFL) << 32)
                      | ((memory[offset + 4] & 0xFFL) << 24)
                      | ((memory[offset + 5] & 0xFFL) << 16)
                      | ((memory[offset + 6] & 0xFFL) << 8)
                      |  (memory[offset + 7] & 0xFFL);
        return valeur;
    }

    public static int writeString(
            byte[] memory,
            int offset,
            String str,
            int maxLength) {

        // 1. Convertir la chaîne en octets.
        byte[] octets = str.getBytes(StandardCharsets.UTF_8);

        // 2. Copier les octets sans dépasser maxLength.
        int longueur = Math.min(octets.length, maxLength);
        for (int i = 0; i < longueur; i++) {
            memory[offset + i] = octets[i];
        }

        // 3. Nettoyer le reste de la zone avec des zéros.
        for (int i = longueur; i < maxLength; i++) {
            memory[offset + i] = 0;
        }

        return maxLength;
    }

    public static String readString(
            byte[] memory,
            int offset,
            int maxLength) {

        // Lire jusqu'au premier octet nul
        // ou jusqu'à maxLength.
        int longueur = 0;
        while (longueur < maxLength && memory[offset + longueur] != 0) {
            longueur++;
        }

        return new String(memory, offset, longueur, StandardCharsets.UTF_8);
    }
}