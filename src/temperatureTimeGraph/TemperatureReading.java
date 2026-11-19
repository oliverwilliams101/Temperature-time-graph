package temperatureTimeGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TemperatureReading {

    // variables for the day and hour, as well as the temperature for the 2 locations
    private final int day;
    private final int hour;
    private final ArrayList<Float> temps;

    // constructor which takes in the day, hour, and 2 temperatures
    public TemperatureReading(int day, int hour, ArrayList<Float> temps) {
        this.day = day;
        this.hour = hour;
        this.temps = temps;
    }

    // gets the day
    public int getDay() {
        return this.day;
    }

    // gets the hour
    public int getHour() {
        return this.hour;
    }

    public ArrayList<Float> getTemps() {
        return this.temps;
    }

    // gets the temperature, t is passed in as an integer which determines if it's the first or second temperature value
    public float getTemp(int t) {
        return this.temps.get(t);
    }

    // gets the min temperature in the file, this is used to find the lowest point on the line graph
    public static float getMinTemperature(ArrayList<TemperatureReading> tempList) {
        float current;
        float currentMin = Float.MAX_VALUE;
        for (TemperatureReading t : tempList) {
            current = Math.min(t.getTemp(0), t.getTemp(1));
            if (current < currentMin) {
                currentMin = current;
            }
        }
        return currentMin;
    }

    // gets the max temperature in the file, this is used to find the highest point on the line graph
    public static float getMaxTemperature(ArrayList<TemperatureReading> tempList) {
        float current;
        float currentMax = Float.MIN_VALUE;
        for (TemperatureReading t : tempList) {
            current = Math.max(t.getTemp(0), t.getTemp(1));
            if (current > currentMax) {
                currentMax = current;
            }
        }
        return currentMax;
    }

    // reads the data from a file and adds Temperature Reading objects to an array list
    public static ArrayList<TemperatureReading> readFromFile(String fileName) throws FileNotFoundException {
        ArrayList<TemperatureReading> tempList = new ArrayList<>();
        Scanner scanner = new Scanner(new File(fileName));

        int currentHour = 0;
        int currentDay = 1;

        while (scanner.hasNextLine()) {
            String currentLine = scanner.nextLine();

            String[] currentData = currentLine.split(",");
            ArrayList<Float> temps = new ArrayList<>();
            float temp;

            for (String f : currentData) {
                temp = convertToCelsius(Float.parseFloat(f));
                temps.add(temp);
            }

            tempList.add(new TemperatureReading(currentDay, currentHour, temps));
            currentHour++;
            if (currentHour == 24) {
                currentDay++;
                currentHour = 0;
            }
        }
        scanner.close();
        return tempList;
    }

    // methods to convert from kelvin to celsius
    public static float convertToCelsius(float temp) {
        return temp - 273.15f;
    }

    public static float round(float temp, int dp) {
        float rounded = Math.round(temp * (Math.pow(10, dp)));
        rounded = (float) (rounded / (Math.pow(10, dp)));
        return rounded;
    }


    // this is what's called when the mouse clicks on a point on the graph
    public void showTemperatureDate() {
        // the suffix is the end of the date, e.g., 1st, 2nd, 3rd, 4th
        String suffix = (this.getDay() % 10 == 1) ? "st" :
                        (this.getDay() % 10 == 2) ? "nd" :
                        (this.getDay() % 10 == 3) ? "rd" :
                        "th";
        int hourLength = String.valueOf(this.getHour()).length();
        String hourString = (hourLength == 1) ? "0" + this.getHour() : String.valueOf(this.getHour());
        System.out.println("On the " + this.getDay() + suffix + " of July 2017 at " +
                hourString + ":00, the temperature for Downing street in central London is " + round(this.getTemp(0), 1) +
                "c and for the University of Essex East 15 acting school is " + round(this.getTemp(1), 1) + "c.");
    }
}
