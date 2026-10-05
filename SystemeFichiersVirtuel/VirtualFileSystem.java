import java.nio.charset.StandardCharsets;

public class VirtualFileSystem {

    private MemoryManager memoryManager;

    public VirtualFileSystem() {
        this.memoryManager = new MemoryManager();
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    // Cherche le premier inode libre (type == TYPE_FREE).
    // Retourne -1 s'il n'y en a plus.
    private int findFreeInode() {
        for (int i = 0; i < MemoryManager.MAX_INODES; i++) {
            Inode inode = new Inode(memoryManager, i);
            if (inode.getFileType() == Inode.TYPE_FREE) {
                return i;
            }
        }
        return -1;
    }

    private boolean isValidFile(int inodeNumber) {
        if (inodeNumber < 0 || inodeNumber >= MemoryManager.MAX_INODES) {
            return false;
        }
        Inode inode = new Inode(memoryManager, inodeNumber);
        return inode.getFileType() == Inode.TYPE_FILE;
    }

    // Libère tous les blocs de données d'un inode (bitmap) et remet les pointeurs à 0.
    private void releaseBlocks(Inode inode) {
        int[] pointers = inode.getDirectPointers();
        for (int i = 0; i < Inode.DIRECT_POINTERS; i++) {
            if (pointers[i] != 0) {
                memoryManager.setBlockUsed(pointers[i], false);
            }
        }
        inode.setDirectPointers(new int[Inode.DIRECT_POINTERS]);
        inode.setFileSize(0);
    }

    // Crée un fichier vide. Le chemin n'est pas exploité ici :
    // le VFS ne possède qu'un seul répertoire racine "/".
    public boolean createFile(String path, String name) {
        if (path == null || name == null || name.isEmpty()) {
            return false;
        }
        if (name.getBytes(StandardCharsets.UTF_8).length > Inode.NAME_MAX) {
            return false;
        }

        int number = findFreeInode();
        if (number == -1) {
            return false;
        }

        Inode inode = new Inode(memoryManager, number);
        inode.clear();
        inode.setFileType(Inode.TYPE_FILE);
        inode.setFileSize(0);
        inode.setName(name);
        return true;
    }

    // Écrit (ou réécrit) le contenu d'un fichier.
    public boolean writeFile(int inodeNumber, byte[] data) {
        if (data == null || !isValidFile(inodeNumber)) {
            return false;
        }

        int blocksNeeded =
                (data.length + MemoryManager.BLOCK_SIZE - 1) / MemoryManager.BLOCK_SIZE;

        // Avec 10 pointeurs directs, un fichier ne peut pas dépasser 10 x 512 octets.
        if (blocksNeeded > Inode.DIRECT_POINTERS) {
            return false;
        }

        Inode inode = new Inode(memoryManager, inodeNumber);

        // Réécriture : on libère d'abord les anciens blocs.
        releaseBlocks(inode);

        // Allocation de tous les blocs nécessaires.
        int[] pointers = new int[Inode.DIRECT_POINTERS];
        for (int i = 0; i < blocksNeeded; i++) {
            int block = memoryManager.allocateBlock();
            if (block == -1) {
                // Plus de place : on rend les blocs déjà pris.
                for (int j = 0; j < i; j++) {
                    memoryManager.setBlockUsed(pointers[j], false);
                }
                return false;
            }
            pointers[i] = block;
        }

        // Copie des données bloc par bloc.
        byte[] memory = memoryManager.getFilesystemMemory();
        for (int i = 0; i < blocksNeeded; i++) {
            int start = i * MemoryManager.BLOCK_SIZE;
            int length = Math.min(MemoryManager.BLOCK_SIZE, data.length - start);
            System.arraycopy(data, start, memory, pointers[i] * MemoryManager.BLOCK_SIZE, length);
        }

        inode.setDirectPointers(pointers);
        inode.setFileSize(data.length);
        return true;
    }

    // Lit tout le contenu d'un fichier. Retourne null si l'inode n'est pas un fichier.
    public byte[] readFile(int inodeNumber) {
        if (!isValidFile(inodeNumber)) {
            return null;
        }

        Inode inode = new Inode(memoryManager, inodeNumber);
        int size = inode.getFileSize();
        int[] pointers = inode.getDirectPointers();
        byte[] memory = memoryManager.getFilesystemMemory();

        byte[] result = new byte[size];
        int copied = 0;
        int i = 0;
        while (copied < size) {
            int length = Math.min(MemoryManager.BLOCK_SIZE, size - copied);
            System.arraycopy(memory, pointers[i] * MemoryManager.BLOCK_SIZE, result, copied, length);
            copied += length;
            i++;
        }
        return result;
    }

    // Supprime un fichier : libère ses blocs puis son inode.
    public boolean deleteFile(int inodeNumber) {
        if (!isValidFile(inodeNumber)) {
            return false;
        }
        Inode inode = new Inode(memoryManager, inodeNumber);
        releaseBlocks(inode);
        inode.clear();
        return true;
    }
}
