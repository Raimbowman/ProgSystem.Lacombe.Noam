public static void main(String[] args) {
    System.out.println("=== TEST ÉTAPE 6 : Adressage Inode ===");

    MemoryManager mm = new MemoryManager();

    Inode inode = new Inode(mm, 4);

    int expectedOffset =
            MemoryManager.INODE_TABLE_OFFSET
            + (4 * Inode.INODE_SIZE);

    assert inode.getInodeOffset() == expectedOffset :
            "Offset d'inode incorrect";

    System.out.println("[OK] Étape 6 validée !");
}