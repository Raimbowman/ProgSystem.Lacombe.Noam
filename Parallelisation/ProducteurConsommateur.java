import java.util.concurrent.Semaphore;

public class ProducteurConsommateur {

    private static final int TAILLE_BUFFER = 5;

    // Le buffer circulaire
    private final Integer[] buffer = new Integer[TAILLE_BUFFER];
    private int indexEcriture = 0;   // où le producteur écrit
    private int indexLecture = 0;    // où le consommateur lit
    private int nbDansBuffer = 0;    // pour l'affichage

    // Compteurs pour vérifier la cohérence (modifiés uniquement sous mutexBuffer)
    private int totalProduit = 0;
    private int totalConsomme = 0;

    // 3 sémaphores
    private final Semaphore placesLibres = new Semaphore(TAILLE_BUFFER); // buffer vide au départ
    private final Semaphore elementsDisponibles = new Semaphore(0);      // rien à consommer au départ
    private final Semaphore mutexBuffer = new Semaphore(1);              // un seul thread dans le buffer

    // ---------- Méthodes appelées par les threads ----------

    public void ajouter(int produit, String nom) throws InterruptedException {
        placesLibres.acquire();          // 1. attendre une place libre
        mutexBuffer.acquire();           // 2. accès exclusif au buffer

        buffer[indexEcriture] = produit; // 3. ajout réel
        indexEcriture = (indexEcriture + 1) % TAILLE_BUFFER;
        nbDansBuffer++;
        totalProduit++;
        System.out.println(nom + " produit " + produit + "  | buffer : " + nbDansBuffer + "/" + TAILLE_BUFFER);

        mutexBuffer.release();           // 4. libérer le buffer
        elementsDisponibles.release();   // 5. signaler un élément disponible
    }

    public int retirer(String nom) throws InterruptedException {
        elementsDisponibles.acquire();   // 1. attendre un élément
        mutexBuffer.acquire();           // 2. accès exclusif au buffer

        int produit = buffer[indexLecture]; // 3. retrait réel
        buffer[indexLecture] = null;
        indexLecture = (indexLecture + 1) % TAILLE_BUFFER;
        nbDansBuffer--;
        totalConsomme++;
        System.out.println(nom + " consomme " + produit + " | buffer : " + nbDansBuffer + "/" + TAILLE_BUFFER);

        mutexBuffer.release();           // 4. libérer le buffer
        placesLibres.release();          // 5. signaler une place libre
        return produit;
    }

    public int getTotalProduit() {
        return totalProduit;
    }

    public int getTotalConsomme() {
        return totalConsomme;
    }

    // ---------- Classes Producteur / Consommateur ----------

    static class Producteur implements Runnable {
        private final ProducteurConsommateur pc;
        private final String nom;
        private final int nombreElements;

        public Producteur(ProducteurConsommateur pc, String nom, int nombreElements) {
            this.pc = pc;
            this.nom = nom;
            this.nombreElements = nombreElements;
        }

        private int genererProduit() {
            return (int) (Math.random() * 100);
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < nombreElements; i++) {
                    int produit = genererProduit();
                    pc.ajouter(produit, nom);
                    Thread.sleep(50); // simule un temps de production
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Consommateur implements Runnable {
        private final ProducteurConsommateur pc;
        private final String nom;
        private final int nombreElements;

        public Consommateur(ProducteurConsommateur pc, String nom, int nombreElements) {
            this.pc = pc;
            this.nom = nom;
            this.nombreElements = nombreElements;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < nombreElements; i++) {
                    pc.retirer(nom);
                    Thread.sleep(80); // simule un temps de consommation
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
