import java.io. * ;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Main{
    private static void pullCSV() {
        try {
            // scan in the countries csv
            Scanner sc = new Scanner(new File("countries.csv"));
            sc.useDelimiter(",");

            while (sc.hasNext()) {
                System.out.print(sc.next());
            }

            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    public static void main(String[] args) {
        pullCSV();
    }
}
