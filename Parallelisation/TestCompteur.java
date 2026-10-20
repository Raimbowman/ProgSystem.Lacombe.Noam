public class TestCompteur {

    // Lance un test : nbThreads threads qui font chacun nbIncrements incréments.
    // Retourne la valeur finale du compteur.
    static int lancerTest(int nbThreads, int nbIncrements) throws InterruptedException {
        CompteurDangereux.resetCompteur();

        Thread[] threads = new Thread[nbThreads];
        for (int i = 0; i < nbThreads; i++) {
            threads[i] = new Thread(new CompteurDangereux.Incrementeur("T" + i, nbIncrements));
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        return CompteurDangereux.getCompteur();
    }

    // Répète le test nbEssais fois et affiche les statistiques.
    static void testerSerie(int nbThreads, int nbIncrements, int nbEssais) throws InterruptedException {
        int attendu = nbThreads * nbIncrements;
        int nbCorrects = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int essai = 0; essai < nbEssais; essai++) {
            int resultat = lancerTest(nbThreads, nbIncrements);
            if (resultat == attendu) {
                nbCorrects++;
            }
            min = Math.min(min, resultat);
            max = Math.max(max, resultat);
        }

        double pourcentage = 100.0 * nbCorrects / nbEssais;
        System.out.println("\n--- " + nbThreads + " threads x " + nbIncrements + " incréments ---");
        System.out.println("Attendu          : " + attendu);
        System.out.println("Corrects         : " + nbCorrects + "/" + nbEssais
                + " (" + pourcentage + " %)");
        System.out.println("Min / Max obtenus: " + min + " / " + max);
        System.out.println("Pertes max       : " + (attendu - min));
    }

    public static void main(String[] args) throws InterruptedException {
        int nbEssais = 50;

        // Test demandé : 9 threads x 1111111 incréments (attendu : 9999999)
        testerSerie(9, 1111111, nbEssais);

        // Impact du nombre de threads
        System.out.println("\n===== Variation du nombre de threads =====");
        for (int nbThreads : new int[]{1, 2, 5, 10}) {
            testerSerie(nbThreads, 1111111, nbEssais);
        }

        // Impact du nombre d'incréments
        System.out.println("\n===== Variation du nombre d'incréments (9 threads) =====");
        for (int nbIncrements : new int[]{100, 10000, 1111111}) {
            testerSerie(9, nbIncrements, nbEssais);
        }
    }
}
