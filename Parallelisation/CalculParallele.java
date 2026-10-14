public class CalculParallele {
	
	private static final int[] DONNEES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	private static Calcule[] threads = new Calcule[DONNEES.length];
	
	public static void main(String[] args) {
		System.out.println("=== Calcul Parallèle ===");
        long debut = System.nanoTime();
        long sommeTotal = 0;
		
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Calcule(DONNEES[i], 1000000000, i);
			threads[i].start();
		}
		
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
				sommeTotal += threads[i].getResultat();
				System.out.println("Traitement de " + threads[i].getValeur() + " terminé : " + threads[i].getResultat());
			} catch (Exception e) {
			}
		}
		
		long duree = (System.nanoTime() - debut);
        System.out.println("Résultat total : " + sommeTotal);
        System.out.println("Durée : " + duree + " ms");
	}
}