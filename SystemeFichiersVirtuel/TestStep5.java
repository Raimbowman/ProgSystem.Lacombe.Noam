public static void main(String[] args) {
    System.out.println("=== TEST ÉTAPE 5 : Bitmap et Allocation ===");

    MemoryManager mm = new MemoryManager();

    assert mm.setBlockUsed(130, true) :
            "setBlockUsed doit réussir";

    assert mm.isBlockUsed(130) == 1 :
            "Le bloc 130 doit être occupé";

    assert mm.setBlockUsed(130, false) :
            "La libération doit réussir";

    assert mm.isBlockUsed(130) == 0 :
            "Le bloc 130 doit être libre";

    mm.setBlockUsed(129, true);

    int bitmapOffset =
            MemoryManager.BITMAP_OFFSET + (129 / 8);

    assert (mm.getFilesystemMemory()[bitmapOffset]
            & 0xFF) == 0x02 :
            "Le bit du bloc 129 est incorrect";

    mm.setBlockUsed(130, true);

    assert (mm.getFilesystemMemory()[bitmapOffset]
            & 0xFF) == 0x06 :
            "Les bits 129 et 130 sont incorrects";

    mm.setBlockUsed(130, false);

    assert (mm.getFilesystemMemory()[bitmapOffset]
            & 0xFF) == 0x02 :
            "La libération du bloc 130 est incorrecte";

    MemoryManager mm2 = new MemoryManager();

    int first = mm2.allocateBlock();
    int second = mm2.allocateBlock();

    assert first == 129 :
            "Le premier bloc de données doit être 129";

    assert second == 130 :
            "Le second bloc de données doit être 130";

    assert mm2.isBlockUsed(129) == 1;
    assert mm2.isBlockUsed(130) == 1;

    assert mm2.isBlockUsed(-1) == -1 :
            "Un bloc négatif doit être refusé";

    assert mm2.isBlockUsed(
            MemoryManager.NUM_BLOCKS) == -1 :
            "Un bloc hors limites doit être refusé";

    System.out.println("[OK] Étape 5 validée !");
}