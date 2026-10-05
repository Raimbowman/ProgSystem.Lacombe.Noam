import java.io.FileReader;
import java.io.IOException;

public class TestRunner {

    public static void main(String[] args) {

        testStep2();
        testStep3();
        testStep4();
        testStep5();
        testStep6();
        testStep7();
        testStep8();
        testStep9();
        testStep10();

        if (args.length > 0) {
            testExternalFile(args[0]);
        } else {
            System.out.println("[INFO] Aucun fichier externe fourni.");
        }

        System.out.println("=== TOUS LES TESTS SONT TERMINÉS ===");
    }

    // ------------------------------------------------------------
    // ÉTAPE 2 : Utils entiers
    // ------------------------------------------------------------
    public static void testStep2() {
        System.out.println("=== TEST ÉTAPE 2 : Utils Entiers ===");

        byte[] buffer = new byte[32];

        int value = 0xF0A1B2E3;
        int written = Utils.writeInt(buffer, 3, value);

        assert written == 4 : "writeInt doit retourner 4";

        assert (buffer[3] & 0xFF) == 0xF0 : "Octet 0 incorrect";
        assert (buffer[4] & 0xFF) == 0xA1 : "Octet 1 incorrect";
        assert (buffer[5] & 0xFF) == 0xB2 : "Octet 2 incorrect";
        assert (buffer[6] & 0xFF) == 0xE3 : "Octet 3 incorrect";

        assert Utils.readInt(buffer, 3) == value : "Erreur writeInt / readInt";

        short shortValue = (short) 0xF0A1;
        int shortWritten = Utils.writeShort(buffer, 20, shortValue);

        assert shortWritten == 2 : "writeShort doit retourner 2";
        assert (buffer[20] & 0xFF) == 0xF0 : "Premier octet du short incorrect";
        assert (buffer[21] & 0xFF) == 0xA1 : "Deuxième octet du short incorrect";
        assert Utils.readShort(buffer, 20) == shortValue : "Erreur writeShort / readShort";

        System.out.println("[OK] Étape 2 validée !");
    }

    // ------------------------------------------------------------
    // ÉTAPE 3 : Utils long et String
    // ------------------------------------------------------------
    public static void testStep3() {
        System.out.println("=== TEST ÉTAPE 3 : Utils Long & String ===");

        byte[] buffer = new byte[64];

        long value = 0x1122334455667788L;
        int written = Utils.writeLong(buffer, 0, value);

        assert written == 8 : "writeLong doit retourner 8";

        assert (buffer[0] & 0xFF) == 0x11;
        assert (buffer[1] & 0xFF) == 0x22;
        assert (buffer[2] & 0xFF) == 0x33;
        assert (buffer[3] & 0xFF) == 0x44;
        assert (buffer[4] & 0xFF) == 0x55;
        assert (buffer[5] & 0xFF) == 0x66;
        assert (buffer[6] & 0xFF) == 0x77;
        assert (buffer[7] & 0xFF) == 0x88;

        assert Utils.readLong(buffer, 0) == value : "Erreur writeLong / readLong";

        for (int i = 16; i < 32; i++) {
            buffer[i] = (byte) 0x7F;
        }

        int stringWritten = Utils.writeString(buffer, 16, "MYFS", 16);

        assert stringWritten == 16 : "writeString doit retourner maxLength";

        assert (buffer[16] & 0xFF) == 'M';
        assert (buffer[17] & 0xFF) == 'Y';
        assert (buffer[18] & 0xFF) == 'F';
        assert (buffer[19] & 0xFF) == 'S';

        for (int i = 20; i < 32; i++) {
            assert buffer[i] == 0 : "La zone inutilisée doit être nettoyée";
        }

        assert Utils.readString(buffer, 16, 16).equals("MYFS") : "Erreur writeString / readString";

        System.out.println("[OK] Étape 3 validée !");
    }

    // ------------------------------------------------------------
    // ÉTAPE 4 : Initialisation mémoire
    // ------------------------------------------------------------
    public static void testStep4() {
        System.out.println("=== TEST ÉTAPE 4 : Initialisation Mémoire ===");

        MemoryManager mm = new MemoryManager();
        byte[] mem = mm.getFilesystemMemory();

        assert mem != null : "La mémoire ne doit pas être nulle";
        assert mem.length == MemoryManager.TOTAL_MEMORY : "Taille mémoire incorrecte";

        assert Utils.readString(mem, MemoryManager.SUPERBLOCK_OFFSET, 16).equals("MYFS1.0")
                : "Signature du superbloc incorrecte";

        assert Utils.readInt(mem, MemoryManager.SUPERBLOCK_OFFSET + 16) == MemoryManager.BLOCK_SIZE
                : "Taille de bloc incorrecte";

        assert Utils.readInt(mem, MemoryManager.SUPERBLOCK_OFFSET + 20) == MemoryManager.TOTAL_MEMORY
                : "Taille mémoire incorrecte";

        assert Utils.readInt(mem, MemoryManager.SUPERBLOCK_OFFSET + 24) == MemoryManager.NUM_BLOCKS
                : "Nombre de blocs incorrect";

        assert Utils.readInt(mem, MemoryManager.SUPERBLOCK_OFFSET + 28) == MemoryManager.MAX_INODES
                : "Nombre maximal d'inodes incorrect";

        System.out.println("[OK] Étape 4 validée !");
    }

    // ------------------------------------------------------------
    // ÉTAPE 5 : Bitmap et allocation
    // ------------------------------------------------------------
    public static void testStep5() {
        System.out.println("=== TEST ÉTAPE 5 : Bitmap et Allocation ===");

        MemoryManager mm = new MemoryManager();

        assert mm.setBlockUsed(130, true) : "setBlockUsed doit réussir";
        assert mm.isBlockUsed(130) == 1 : "Le bloc 130 doit être occupé";
        assert mm.setBlockUsed(130, false) : "La libération doit réussir";
        assert mm.isBlockUsed(130) == 0 : "Le bloc 130 doit être libre";

        mm.setBlockUsed(129, true);

        int bitmapOffset = MemoryManager.BITMAP_OFFSET + (129 / 8);

        assert (mm.getFilesystemMemory()[bitmapOffset] & 0xFF) == 0x02
                : "Le bit du bloc 129 est incorrect";

        mm.setBlockUsed(130, true);

        assert (mm.getFilesystemMemory()[bitmapOffset] & 0xFF) == 0x06
                : "Les bits 129 et 130 sont incorrects";

        mm.setBlockUsed(130, false);

        assert (mm.getFilesystemMemory()[bitmapOffset] & 0xFF) == 0x02
                : "La libération du bloc 130 est incorrecte";

        MemoryManager mm2 = new MemoryManager();

        int first = mm2.allocateBlock();
        int second = mm2.allocateBlock();

        assert first == 129 : "Le premier bloc de données doit être 129";
        assert second == 130 : "Le second bloc de données doit être 130";

        assert mm2.isBlockUsed(129) == 1;
        assert mm2.isBlockUsed(130) == 1;

        assert mm2.isBlockUsed(-1) == -1 : "Un bloc négatif doit être refusé";
        assert mm2.isBlockUsed(MemoryManager.NUM_BLOCKS) == -1 : "Un bloc hors limites doit être refusé";

        System.out.println("[OK] Étape 5 validée !");
    }

    // ------------------------------------------------------------
    // ÉTAPE 6 : Adressage des inodes
    // ------------------------------------------------------------
    public static void testStep6() {
        System.out.println("=== TEST ÉTAPE 6 : Adressage Inode ===");

        MemoryManager mm = new MemoryManager();
        Inode inode = new Inode(mm, 4);

        int expectedOffset = MemoryManager.INODE_TABLE_OFFSET + (4 * Inode.INODE_SIZE);

        assert inode.getInodeOffset() == expectedOffset : "Offset d'inode incorrect";

        System.out.println("[OK] Étape 6 validée !");
    }

    // ------------------------------------------------------------
    // ÉTAPE 7 : Lecture / écriture des champs d'un inode
    // ------------------------------------------------------------
    public static void testStep7() {
        System.out.println("=== TEST ÉTAPE 7 : Champs d'un Inode ===");

        MemoryManager mm = new MemoryManager();
        Inode inode = new Inode(mm, 3);

        assert inode.getFileType() == Inode.TYPE_FREE : "Un inode neuf doit être libre";

        inode.setFileType(Inode.TYPE_FILE);
        inode.setFileSize(1234);
        inode.setName("test.txt");

        int[] pointers = new int[Inode.DIRECT_POINTERS];
        pointers[0] = 129;
        pointers[1] = 130;
        inode.setDirectPointers(pointers);

        assert inode.getFileType() == Inode.TYPE_FILE : "Type incorrect";
        assert inode.getFileSize() == 1234 : "Taille incorrecte";
        assert inode.getName().equals("test.txt") : "Nom incorrect";

        int[] read = inode.getDirectPointers();
        assert read[0] == 129 && read[1] == 130 : "Pointeurs incorrects";
        assert read[2] == 0 : "Les pointeurs inutilisés doivent valoir 0";

        // Vérification directe des octets : le type est stocké à l'offset +4
        byte[] mem = mm.getFilesystemMemory();
        assert Utils.readInt(mem, inode.getInodeOffset() + 4) == Inode.TYPE_FILE
                : "Le type n'est pas à l'offset +4";

        // L'inode voisin ne doit pas avoir été modifié
        assert new Inode(mm, 2).getFileType() == Inode.TYPE_FREE : "Débordement sur l'inode 2";
        assert new Inode(mm, 4).getFileType() == Inode.TYPE_FREE : "Débordement sur l'inode 4";

        System.out.println("[OK] Étape 7 validée !");
    }

    // ------------------------------------------------------------
    // ÉTAPE 8 : Création de fichiers
    // ------------------------------------------------------------
    public static void testStep8() {
        System.out.println("=== TEST ÉTAPE 8 : createFile ===");

        VirtualFileSystem vfs = new VirtualFileSystem();

        assert vfs.createFile("/", "a.txt") : "Création de a.txt impossible";
        assert vfs.createFile("/", "b.txt") : "Création de b.txt impossible";

        MemoryManager mm = vfs.getMemoryManager();
        Inode first = new Inode(mm, 0);
        Inode second = new Inode(mm, 1);

        assert first.getFileType() == Inode.TYPE_FILE : "L'inode 0 doit être un fichier";
        assert first.getName().equals("a.txt") : "Nom du premier fichier incorrect";
        assert first.getFileSize() == 0 : "Un fichier neuf doit être vide";
        assert second.getName().equals("b.txt") : "Le second fichier doit utiliser l'inode 1";

        assert !vfs.createFile("/", "") : "Un nom vide doit être refusé";
        assert !vfs.createFile("/", "x".repeat(Inode.NAME_MAX + 1)) : "Un nom trop long doit être refusé";

        System.out.println("[OK] Étape 8 validée !");
    }

    // ------------------------------------------------------------
    // ÉTAPE 9 : Écriture et lecture
    // ------------------------------------------------------------
    public static void testStep9() {
        System.out.println("=== TEST ÉTAPE 9 : writeFile / readFile ===");

        VirtualFileSystem vfs = new VirtualFileSystem();
        vfs.createFile("/", "hello.txt");

        byte[] data = "Bonjour le VFS !".getBytes();

        assert vfs.writeFile(0, data) : "Écriture impossible";

        byte[] back = vfs.readFile(0);
        assert back != null && back.length == data.length : "Taille lue incorrecte";
        for (int i = 0; i < data.length; i++) {
            assert back[i] == data[i] : "Différence à l'octet " + i;
        }

        // Le premier bloc de données doit être le bloc 129, marqué occupé
        Inode inode = new Inode(vfs.getMemoryManager(), 0);
        assert inode.getFileSize() == data.length : "Taille de l'inode incorrecte";
        assert inode.getDirectPointers()[0] == 129 : "Le premier pointeur doit être 129";
        assert vfs.getMemoryManager().isBlockUsed(129) == 1 : "Le bloc 129 doit être occupé";

        // Cas limites
        assert vfs.readFile(5) == null : "Lire un inode libre doit retourner null";
        assert !vfs.writeFile(5, data) : "Écrire dans un inode libre doit échouer";

        System.out.println("[OK] Étape 9 validée !");
    }

    // ------------------------------------------------------------
    // ÉTAPE 10 : Fichiers sur plusieurs blocs, réécriture, suppression
    // ------------------------------------------------------------
    public static void testStep10() {
        System.out.println("=== TEST ÉTAPE 10 : Multi-blocs et libération ===");

        VirtualFileSystem vfs = new VirtualFileSystem();
        MemoryManager mm = vfs.getMemoryManager();
        vfs.createFile("/", "gros.bin");

        // 1300 octets -> 3 blocs (512 + 512 + 276)
        byte[] data = new byte[1300];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i * 7);
        }

        assert vfs.writeFile(0, data) : "Écriture multi-blocs impossible";

        int[] p = new Inode(mm, 0).getDirectPointers();
        assert p[0] == 129 && p[1] == 130 && p[2] == 131 : "Les 3 blocs doivent être 129, 130, 131";
        assert p[3] == 0 : "Le 4e pointeur doit être vide";

        byte[] back = vfs.readFile(0);
        assert back.length == data.length : "Taille lue incorrecte";
        for (int i = 0; i < data.length; i++) {
            assert back[i] == data[i] : "Différence à l'octet " + i;
        }

        // Réécriture avec un contenu plus court : les blocs en trop sont libérés
        assert vfs.writeFile(0, new byte[100]) : "Réécriture impossible";
        assert mm.isBlockUsed(129) == 1 : "Le bloc 129 doit être réutilisé";
        assert mm.isBlockUsed(130) == 0 : "Le bloc 130 doit être libéré";
        assert mm.isBlockUsed(131) == 0 : "Le bloc 131 doit être libéré";

        // Fichier trop gros pour 10 pointeurs directs (10 x 512 = 5120 octets)
        assert !vfs.writeFile(0, new byte[5121]) : "Un fichier > 5120 octets doit être refusé";

        // Suppression
        assert vfs.deleteFile(0) : "Suppression impossible";
        assert mm.isBlockUsed(129) == 0 : "Le bloc 129 doit être libéré";
        assert new Inode(mm, 0).getFileType() == Inode.TYPE_FREE : "L'inode doit être libre";
        assert vfs.readFile(0) == null : "Lire un fichier supprimé doit retourner null";

        System.out.println("[OK] Étape 10 validée !");
    }

    // ------------------------------------------------------------
    // ÉTAPE 11 : Test avec un fichier externe
    // ------------------------------------------------------------
    public static void testExternalFile(String filename) {

        System.out.println("=== TEST FICHIER EXTERNE ===");

        StringBuilder builder = new StringBuilder();

        try (FileReader reader = new FileReader(filename)) {

            char[] buffer = new char[1024];
            int count;

            while ((count = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, count);
            }

        } catch (IOException e) {
            throw new AssertionError("Impossible de lire le fichier externe", e);
        }

        String content = builder.toString();
        byte[] original = content.getBytes();

        VirtualFileSystem vfs = new VirtualFileSystem();

        assert vfs.createFile("/", "external.txt") : "Impossible de créer le fichier VFS";
        assert vfs.writeFile(0, original) : "Impossible d'écrire le fichier externe";

        byte[] recovered = vfs.readFile(0);

        assert recovered != null : "Les données récupérées sont nulles";
        assert recovered.length == original.length : "Taille du fichier différente";

        for (int i = 0; i < original.length; i++) {
            assert recovered[i] == original[i] : "Différence à l'octet " + i;
        }

        System.out.println("[OK] Fichier externe correctement transféré !");
    }
}
