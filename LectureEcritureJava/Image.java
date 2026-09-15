import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;

public class Image {
    private int width;
    private int height;
    // pixels[y][x][0=R,1=G,2=B]
    private int[][][] pixels; // pixels[y][x][0=R,1=G,2=B]

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /**
     * Constructeur : initialise une image vide.
     */
    public Image(int width, int hauteur) {
        this.width = width;
        this.height = hauteur;
        pixels = new int[hauteur][width][3]; //3 = RGB
    }

    /**
     * Définit la couleur d'un pixel à la position (x, y)
     */
    public void setPixel(int x, int y, int r, int g, int b) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y][x][0] = r;
            pixels[y][x][1] = g;
            pixels[y][x][2] = b;
        }
    }

    /**
     * Sauvegarde l'image au format texte PPM (P3)
     */
    public void save_txt(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write("P3\n");
        writer.write(width + " " + height + "\n");
        writer.write("255\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.write(pixels[y][x][0] + " " + pixels[y][x][1] + " " + pixels[y][x][2] + " ");
            }
            writer.write("\n");
        }
        writer.close();
    }
	
	/**
     * Sauvegarde l'image au format binaire PPM (P6)
     */
    public void write_bin(String filename) throws IOException {
        FileOutputStream out = new FileOutputStream(filename);

        // En-tête : en texte (ASCII), donc on l'écrit avec getBytes()
        String header = "P6\n" + width + " " + height + "\n255\n";
        out.write(header.getBytes());

        // Corps : chaque valeur R, G, B est un octet brut (0-255)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                out.write(pixels[y][x][0]); // R
                out.write(pixels[y][x][1]); // G
                out.write(pixels[y][x][2]); // B
            }
        }

        out.close();
    }
}