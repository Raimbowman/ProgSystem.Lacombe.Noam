import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;

public class Image {

    private int width;
    private int height;
    private int[][][] pixels; // pixels[x][y][0=R,1=G,2=B]

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /**
     * Constructeur : initialise une image vide.
     */
    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width][height][3];
    }

    /**
     * Définit la couleur d'un pixel à la position (x, y)
     */
    public void setPixel(int x, int y, int r, int g, int b) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[x][y][0] = r;
            pixels[x][y][1] = g;
            pixels[x][y][2] = b;
        }
    }

    /**
     * Sauvegarde l'image au format texte PPM (P3)
     */
    public void save_txt(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("P3\n");
            writer.write("200 100\n");
            writer.write("255\n");
            for (int y = 0; y < 100; y++) {
                for (int x = 0; x < 200; x++) {
                    writer.write(pixels[x][y][0] + " " + 
                                 pixels[x][y][1] + " " + 
                                 pixels[x][y][2] + " ");
                }
                writer.write("\n");
            }
        writer.close();
        }
    }

    /**
     * Sauvegarde l'image au format texte / binaire
     */
    public void save_bin(String filename) throws IOException {
        FileOutputStream writer = new FileOutputStream(filename);

        // écriture de l'entête du fichier binarie PPM
        String entete = "P6\n" + width + " " + height + "\n255\n";
        writer.write(entete.getBytes());

        byte[] pixel = new byte[3];

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 200; x++) {
                pixel[0] = (byte) pixels[x][y][0];
                pixel[1] = (byte) pixels[x][y][1];
                pixel[2] = (byte) pixels[x][y][2];
                writer.write(pixel);
            }
        }
        writer.close();
    }

}