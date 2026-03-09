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
    public static String[][] editCountry(String[][] arr) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the name of the country to edit: ");
    String countryName = sc.nextLine();

    int rowIndex = -1;
    for (int i = 0; i < arr.length; i++) {
        if (arr[i][0].equalsIgnoreCase(countryName)) {
            rowIndex = i;
            break;
        }
    }

    if (rowIndex == -1) {
        System.out.println("Country not found.");
        return arr;
    }

    System.out.println("What feature do you want to edit: ");
    System.out.println("Enter option: " +
            "\n0. Name" +
            "\n1. GDP " +
            "\n2. Population " +
            "\n3. Gender ratio" +
            "\n4. Under 18" +
            "\n5. Over 18" +
            "\n6. Incarcerated" +
            "\n7. Firepower" +
            "\n8. Corporation Tax");

    int feature = sc.nextInt();
    sc.nextLine(); // Clear the buffer

    switch (feature) {
        case 0:
            System.out.println("Enter new country name: ");
            arr[rowIndex][0] = sc.nextLine();
            break;
        case 1:
            System.out.println("Enter new GDP of the country: ");
            arr[rowIndex][1] = sc.nextLine();
            break;
        case 2:
            System.out.println("Enter new population of the country: ");
            arr[rowIndex][2] = sc.nextLine();
            break;
        case 3:
            System.out.println("Enter new gender ratio: ");
            arr[rowIndex][3] = sc.nextLine();
            break;
        case 4:
            System.out.println("Enter new Under 18 %: ");
            arr[rowIndex][4] = sc.nextLine();
            break;
        case 5:
            System.out.println("Enter new Over 18 %: ");
            arr[rowIndex][5] = sc.nextLine();
            break;
        case 6:
            System.out.println("Enter new Incarcerated %: ");
            arr[rowIndex][6] = sc.nextLine();
            break;
        case 7:
            System.out.println("Enter new Firepower: ");
            arr[rowIndex][7] = sc.nextLine();
            break;
        case 8:
            System.out.println("Enter new Corp Tax: ");
            arr[rowIndex][8] = sc.nextLine();
            break;
        default:
            System.out.println("Invalid option.");
    }
    System.out.println("Update complete.");
    return arr;
}

    public void displayCountryStats(String[][] arr) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the country to display: ");
        String name = sc.nextLine();
        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i][0].equals(name)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            System.out.println("Country not found.");
            return;
        }
        System.out.println("Name: " + arr[index][0]);
        System.out.println("GDP: " + arr[index][1]);
        System.out.println("Population: " + arr[index][2]);
        System.out.println("Gender Ratio: " + arr[index][3]);
        System.out.println("Percentage of population under 18: " + arr[index][4]);
        System.out.println("Percentage of population over 18: " + arr[index][5]);
        System.out.println("Percentage of population that is incarcerated: " + arr[index][6]);
        System.out.println("Firepower: " + arr[index][7]);
        System.out.println("Corporate Tax Rate: " + arr[index][8]);
        sc.close();
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
