public class TestCompteur {

    static final int NB_THREADS = 9;
    static final int NB_INCREMENTS = 1111111;
    static final int NB_ESSAIS = 50;

    public static void main(String[] args) throws InterruptedException {
        int attendu = NB_THREADS * NB_INCREMENTS;
        int nbCorrects = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int essai = 0; essai < NB_ESSAIS; essai++) {
            CompteurDangereux.resetCompteur();

            Thread[] threads = new Thread[NB_THREADS];
            for (int i = 0; i < NB_THREADS; i++) {
                threads[i] = new Thread(new CompteurDangereux.Incrementeur("T" + i, NB_INCREMENTS));
            }

            for (Thread t : threads) {
                t.start();
            }
            for (Thread t : threads) {
                t.join();
            }

            int resultat = CompteurDangereux.getCompteur();
            System.out.println("Essai " + essai + " : " + resultat + " (attendu " + attendu + ")");

            if (resultat == attendu) {
                nbCorrects++;
            }
            min = Math.min(min, resultat);
            max = Math.max(max, resultat);
        }

        System.out.println("\nCorrects : " + nbCorrects + "/" + NB_ESSAIS
                + " (" + (100.0 * nbCorrects / NB_ESSAIS) + " %)");
        System.out.println("Min : " + min + " | Max : " + max);
    }
}
