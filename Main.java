import java.io.* ;
import java.util.ArrayList;
import java.util.Scanner;

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
    public static String[][] addCountry(String[][] arr) {
        String name, GDP, population, genderRatio, u18, o18, incarcerated, firepower, corpTax;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the country: ");
        name = sc.nextLine();
        System.out.println("Enter the GDP of the country: ");
        GDP = sc.nextLine();
        System.out.println("Enter the population of the country: ");
        population = sc.nextLine();
        System.out.println("Enter the gender ratio of the country: ");      
        genderRatio = sc.nextLine();
        System.out.println("Enter the percentage of the population under 18: ");
        u18 = sc.nextLine();
        System.out.println("Enter the percentage of the population over 18: ");
        o18 = sc.nextLine();
        System.out.println("Enter the percentage of the population that is incarcerated: ");
        incarcerated = sc.nextLine();
        System.out.println("Enter the firepower of the country: ");
        firepower = sc.nextLine();
        System.out.println("Enter the corporate tax rate of the country: ");
        corpTax = sc.nextLine();
        String[][] newArr = new String[arr.length + 1][arr[0].length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                newArr[i][j] = arr[i][j];
            }
        } 
        newArr[arr.length][0] = name;
        newArr[arr.length][1] = GDP;
        newArr[arr.length][2] = population;
        newArr[arr.length][3] = genderRatio;
        newArr[arr.length][4] = u18;
        newArr[arr.length][5] = o18;
        newArr[arr.length][6] = incarcerated;
        newArr[arr.length][7] = firepower;
        newArr[arr.length][8] = corpTax;
        sc.close();
        return newArr;      
    }
    public String[][] removeCountry(String[][] arr) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the country to remove: ");
        String name = sc.nextLine();
        String[][] newArr = new String[arr.length - 1][arr[0].length];
        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i][0].equals(name)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            System.out.println("Country not found.");
            return arr;
        }
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                newArr[i][j] = arr[i][j];
            }
        }
        for (int i = index + 1; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                newArr[i - 1][j] = arr[i][j];
            }
        }
        sc.close();
        return newArr;
    }
    public static void main(String[] args) {
        String[][] data = pullCSV();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Country Catalogue ---");
            System.out.println("1. View all countries");
            System.out.println("2. Add a country");
            System.out.println("3. Remove a country");
            System.out.println("4. Predict Firepower Index");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    for (int i = 0; i < data.length; i++) {
                        for (int j = 0; j < data[i].length; j++) {
                            System.out.print(data[i][j] + " ");
                        }
                        System.out.println();
                    }
                    break;
                case "2":
                    data = addCountry(data);
                    break;
                case "3":
                    Main m = new Main();
                    data = m.removeCountry(data);
                    break;
                case "4":
                    // show countries with N/A firepower
                    String[] naCountries = UseRFR.getNACountries(data);
                    if (naCountries.length == 0) {
                        System.out.println("No countries with missing Firepower Index.");
                        break;
                    }
                    System.out.println("\nCountries with N/A Firepower Index:");
                    for (int i = 0; i < naCountries.length; i++) {
                        System.out.println((i + 1) + ". " + naCountries[i]);
                    }
                    System.out.print("Select a country (number): ");
                    int selection = Integer.parseInt(sc.nextLine());
                    if (selection < 1 || selection > naCountries.length) {
                        System.out.println("Invalid selection.");
                        break;
                    }
                    String result = UseRFR.predict(naCountries[selection - 1], data);
                    System.out.println(result);
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
        sc.close();
    }
}
