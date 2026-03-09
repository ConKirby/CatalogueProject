import java.io. * ;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main{
    private static String[][] pullCSV() {
        try {
            // Initialise arraylist
            ArrayList<String[]> rows = new ArrayList<>();

            // scan in the countries csv
            Scanner sc = new Scanner(new File("countries.csv"));
            // delimit by ,
            sc.useDelimiter(",");

            // iterate through each line
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                // split the list by , for the arraylist
                String[] values = line.split(",");
                // add the row to the rows array
                rows.add(values);
            }

            sc.close();

            // convert ArrayList to 2D array
            String[][] data = new String[rows.size()][];

            // put row into 2d array
            for (int i = 0; i < rows.size(); i++) {
                data[i] = rows.get(i);
            }

            return data;

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return null;
        }
    }

    public static void main(String[] args) {
        String[][] data = pullCSV();

        // print the 2D array
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                System.out.print(data[i][j] + " ");
            }
            System.out.println();
        }
    }
}
