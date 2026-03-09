import java.io.*;

class UseRFR{
    public static String predict(String country, String[][] data){
        // find the row for the country
        int row = -1;
        for (int i = 1; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(country)) {
                row = i;
                break;
            }
        }
        if (row == -1) {
            return "Country not found.";
        }
        // check if firepower is N/A
        if (!data[row][7].equals("N/A")) {
            return data[row][0] + " already has a Firepower Index: " + data[row][7];
        }

        // Parameters: GDPPerCapita, Population, MalesPer100Females, perc18AndOver, IncarceratedPer100k, CorporationTax
        String gdp = data[row][1];
        String pop = data[row][2];
        String males = data[row][3];
        String over18 = data[row][5];
        String incarcerated = data[row][6];
        String corpTax = data[row][8];

        try {
            ProcessBuilder pb = new ProcessBuilder("python", "predict_firepower.py", gdp, pop, males, over18, incarcerated, corpTax);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine();

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String error = errorReader.readLine();

            process.waitFor();

            if (result != null) {
                return "Predicted Firepower Index for " + data[row][0] + ": " + result;
            } else {
                return "Error running prediction: " + error;
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // returns list of countries with N/A firepower
    public static String[] getNACountries(String[][] data) {
        int count = 0;
        for (int i = 1; i < data.length; i++) {
            if (data[i][7].equals("N/A")) {
                count++;
            }
        }
        String[] countries = new String[count];
        int index = 0;
        for (int i = 1; i < data.length; i++) {
            if (data[i][7].equals("N/A")) {
                countries[index] = data[i][0];
                index++;
            }
        }
        return countries;
    }
}
