package temperatureTimeGraph;

import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.*;

public class JulyTemperatures {
    public static void main(String[] args) {
        // creates a JFrame object and adds a temperature plot object to the JFrame, and checks for a FileNotFoundException
        // prompts the user for a filename
        Scanner input = new Scanner(System.in);
        TemperaturePlot tempPlot;
        String option;

        while (true) {
            try {
                System.out.println("Enter filename: ");
                System.out.println("Type exit to leave the program. ");
                option = input.nextLine();
                if (option.equalsIgnoreCase("exit")) {
                    return;
                }
                tempPlot = new TemperaturePlot(TemperatureReading.readFromFile(option));
                break;
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("File in incorrect format");
            } finally {
                input.close();
            }
        }
        JFrame frame = new JFrame("July Temperatures for Downing street in central London and the University of Essex East 15 acting school");
        frame.setSize(1500, 1200);
        frame.add(tempPlot);
        frame.addMouseMotionListener(tempPlot);
        frame.addMouseListener(tempPlot);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
