public class Inode {

    private MemoryManager memoryManager;
    private int inodeNumber;

    public static final int INODE_SIZE = 128;
    public static final int DIRECT_POINTERS = 10;

    // Types de fichier
    public static final int TYPE_FREE = 0;
    public static final int TYPE_FILE = 1;

    // Disposition d'un inode (en octets, relative au début de l'inode)
    // 0..3   : réservé
    // 4..7   : type
    // 8..11  : taille du fichier
    // 12..27 : réservé (dates)
    // 28..67 : 10 pointeurs directs (10 x 4 octets)
    // 72..103: nom du fichier (32 octets max)
    private static final int OFFSET_TYPE = 4;
    private static final int OFFSET_SIZE = 8;
    private static final int OFFSET_POINTERS = 28;
    private static final int OFFSET_NAME = 72;
    public static final int NAME_MAX = 32;

    public Inode(
            MemoryManager memoryManager,
            int inodeNumber) {

        this.memoryManager = memoryManager;
        this.inodeNumber = inodeNumber;
    }

    public int getInodeOffset() {
        return MemoryManager.INODE_TABLE_OFFSET + (this.inodeNumber * INODE_SIZE);
    }

    // ---------- Lecture ----------

    public int getFileType() {
        byte[] memory = memoryManager.getFilesystemMemory();
        return Utils.readInt(memory, this.getInodeOffset() + OFFSET_TYPE);
    }

    public int getFileSize() {
        byte[] memory = memoryManager.getFilesystemMemory();
        return Utils.readInt(memory, this.getInodeOffset() + OFFSET_SIZE);
    }

    public int[] getDirectPointers() {
        byte[] memory = memoryManager.getFilesystemMemory();
        int[] pointers = new int[DIRECT_POINTERS];

        for (int i = 0; i < DIRECT_POINTERS; i++) {
            pointers[i] = Utils.readInt(memory, this.getInodeOffset() + OFFSET_POINTERS + i * 4);
        }

        return pointers;
    }

    public String getName() {
        byte[] memory = memoryManager.getFilesystemMemory();
        return Utils.readString(memory, this.getInodeOffset() + OFFSET_NAME, NAME_MAX);
    }

    // ---------- Écriture ----------

    public void setFileType(int type) {
        byte[] memory = memoryManager.getFilesystemMemory();
        Utils.writeInt(memory, this.getInodeOffset() + OFFSET_TYPE, type);
    }

    public void setFileSize(int size) {
        byte[] memory = memoryManager.getFilesystemMemory();
        Utils.writeInt(memory, this.getInodeOffset() + OFFSET_SIZE, size);
    }

    public void setDirectPointers(int[] pointers) {
        byte[] memory = memoryManager.getFilesystemMemory();
        for (int i = 0; i < DIRECT_POINTERS; i++) {
            int value = (i < pointers.length) ? pointers[i] : 0;
            Utils.writeInt(memory, this.getInodeOffset() + OFFSET_POINTERS + i * 4, value);
        }
    }

    public void setName(String name) {
        byte[] memory = memoryManager.getFilesystemMemory();
        Utils.writeString(memory, this.getInodeOffset() + OFFSET_NAME, name, NAME_MAX);
    }

    // Remet tout l'inode à zéro (inode libre)
    public void clear() {
        byte[] memory = memoryManager.getFilesystemMemory();
        int start = this.getInodeOffset();
        for (int i = 0; i < INODE_SIZE; i++) {
            memory[start + i] = 0;
        }
    }
}
