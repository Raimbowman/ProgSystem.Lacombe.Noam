public static void main(String[] args) {
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

    assert Utils.readLong(buffer, 0) == value :
            "Erreur writeLong / readLong";

    for (int i = 16; i < 32; i++) {
        buffer[i] = (byte) 0x7F;
    }

    int stringWritten =
            Utils.writeString(buffer, 16, "MYFS", 16);

    assert stringWritten == 16 :
            "writeString doit retourner maxLength";

    assert (buffer[16] & 0xFF) == 'M';
    assert (buffer[17] & 0xFF) == 'Y';
    assert (buffer[18] & 0xFF) == 'F';
    assert (buffer[19] & 0xFF) == 'S';

    for (int i = 20; i < 32; i++) {
        assert buffer[i] == 0 :
                "La zone inutilisée doit être nettoyée";
    }

    assert Utils.readString(buffer, 16, 16).equals("MYFS") :
            "Erreur writeString / readString";

    System.out.println("[OK] Étape 3 validée !");
}