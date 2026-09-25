public class TestStep4 {
    public static void main(String[] args) {
        System.out.println("=== TEST ÉTAPE 4 : Initialisation Mémoire ===");

        MemoryManager mm = new MemoryManager();

        byte[] mem = mm.getFilesystemMemory();

        assert mem != null :
                "La mémoire ne doit pas être nulle";

        assert mem.length == MemoryManager.TOTAL_MEMORY :
                "Taille mémoire incorrecte";

        assert Utils.readString(
                mem,
                MemoryManager.SUPERBLOCK_OFFSET,
                16).equals("MYFS1.0") :
                "Signature du superbloc incorrecte";

        assert Utils.readInt(
                mem,
                MemoryManager.SUPERBLOCK_OFFSET + 16)
                == MemoryManager.BLOCK_SIZE :
                "Taille de bloc incorrecte";

        assert Utils.readInt(
                mem,
                MemoryManager.SUPERBLOCK_OFFSET + 20)
                == MemoryManager.TOTAL_MEMORY :
                "Taille mémoire incorrecte";

        assert Utils.readInt(
                mem,
                MemoryManager.SUPERBLOCK_OFFSET + 24)
                == MemoryManager.NUM_BLOCKS :
                "Nombre de blocs incorrect";

        assert Utils.readInt(
                mem,
                MemoryManager.SUPERBLOCK_OFFSET + 28)
                == MemoryManager.MAX_INODES :
                "Nombre maximal d'inodes incorrect";

        System.out.println("[OK] Étape 4 validée !");
    }
}