public class Calcule extends Thread {
    
	private int valeur;
	private int multiplicateur;
	private int index;
	private long resultat;
	
	public Calcule(int valeur, int multiplicateur, int index) {
		this.valeur = valeur;
		this.multiplicateur = multiplicateur;
		this.index = index;
		this.resultat = 0;
	}
	
	@Override
	public void run() {
		// TODO: Implémenter le calcul intensif
		// Même logique que dans la boucle interne de CalculSequentiel
		for (int j = 0; j < multiplicateur; j++) {
			this.resultat += valeur * valeur + valeur;
		}
	}
	
	public long getResultat() {
		return resultat;
	}
	
	public int getValeur() {
		return valeur;
	}
}