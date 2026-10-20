public class TestSemaphore {

    // Lance un scénario : nbProd producteurs (chacun produit elementsParProd éléments)
    // et nbCons consommateurs (chacun consomme elementsParCons éléments)
    private static void lancerScenario(String titre, int nbProd, int elementsParProd,
                                       int nbCons, int elementsParCons) throws InterruptedException {
        System.out.println("\n===== " + titre + " =====");
        System.out.println(nbProd + " producteurs x " + elementsParProd + " = " + (nbProd * elementsParProd)
                + " produits | " + nbCons + " consommateurs x " + elementsParCons + " = "
                + (nbCons * elementsParCons) + " consommations");

        ProducteurConsommateur pc = new ProducteurConsommateur();
        Thread[] producteurs = new Thread[nbProd];
        Thread[] consommateurs = new Thread[nbCons];

        for (int i = 0; i < nbProd; i++) {
            producteurs[i] = new Thread(new ProducteurConsommateur.Producteur(pc, "P" + i, elementsParProd));
        }
        for (int i = 0; i < nbCons; i++) {
            consommateurs[i] = new Thread(new ProducteurConsommateur.Consommateur(pc, "C" + i, elementsParCons));
        }

        for (Thread t : producteurs) t.start();
        for (Thread t : consommateurs) t.start();

        for (Thread t : producteurs) t.join();
        for (Thread t : consommateurs) t.join();

        System.out.println("--- Bilan ---");
        System.out.println("Total produit  : " + pc.getTotalProduit());
        System.out.println("Total consommé : " + pc.getTotalConsomme());
        System.out.println(pc.getTotalProduit() == pc.getTotalConsomme() ? "Cohérent" : "INCOHERENT !");
    }

    public static void main(String[] args) throws InterruptedException {
        // Scénario équilibré : 2 x 15 = 30 produits, 3 x 10 = 30 consommations
        lancerScenario("Scénario équilibré", 2, 15, 3, 10);

        // Scénario déséquilibré : 5 x 10 = 50 produits, 2 x 25 = 50 consommations
        // Les producteurs sont plus nombreux : le buffer se remplit et ils attendent
        lancerScenario("Scénario déséquilibré", 5, 10, 2, 25);
    }
}
