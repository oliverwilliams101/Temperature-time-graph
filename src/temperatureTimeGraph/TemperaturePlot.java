package temperatureTimeGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

public class TemperaturePlot extends JComponent implements MouseListener, MouseMotionListener {

    private final ArrayList<TemperatureReading> tempList;

    // variables that are used throughout a variety of methods
    private final int OFFSET = 100;
    private int windowWidth;
    private int windowHeight;
    private final int DAYS = 31;

    // variables for drawing a mouse moving line
    private float markerPositionX;
    private boolean drawLine = false;

    // constructor takes in a TemperatureReading array list
    public TemperaturePlot(ArrayList<TemperatureReading> tempList) {
        this.tempList = tempList;
    }

    // gets the width of the x-axis
    public int getxAxisWidth() {
        return this.windowWidth - (2 * this.OFFSET);
    }

    // gets the height of the y-axis
    public int getyAxisHeight() {
        return this.windowHeight - (2 * this.OFFSET);
    }

    // gets the width of each day on the graph
    public float getDayWidth() {
        return (float) this.getxAxisWidth() / this.DAYS;
    }

    // gets the width of each hour on the graph
    public float getHourWidth() {
        return this.getDayWidth() / 24;
    }

    @Override
    public void paintComponent(Graphics g) {
        this.windowHeight = getHeight();
        this.windowWidth = getWidth();

        // map used to store the positions of y-axis
        final HashMap<Integer, Float> tempPos = new HashMap<>();

        System.out.println(tempPos);
        // drawing axis
        this.drawingAxis(g, tempPos);

        System.out.println(tempPos);

        // drawing line graphs
        // first location
        Color c1 = new Color(0, 200, 0);
        this.drawingGraph(g, c1, 0, this.getHourWidth(), tempPos);

        // second location
        Color c2 = new Color(220, 150, 0);
        this.drawingGraph(g, c2, 1, this.getHourWidth(), tempPos);

        // drawing keys
        final int KEY_LENGTH = 25;
        final int STRING_OFFSET = 5;
        final int KEY_X = (int) (this.windowWidth - (1.5 * this.OFFSET));
        int keyY =  2 * this.OFFSET;

        // key 1
        this.drawingKey(g, c1, KEY_LENGTH, KEY_X, keyY, STRING_OFFSET, "Downing Street");

        // key 2
        keyY = (int) (1.5 * this.OFFSET);
        this.drawingKey(g, c2, KEY_LENGTH, KEY_X, keyY, STRING_OFFSET, "East 15 Acting School");

        // drawing following mouse marker/line
        this.drawingMarker(g, tempPos);
    }

    // draws the axis and the marks with values for both the x and y-axis
    public void drawingAxis(Graphics graphics, HashMap<Integer, Float> tempPos) {
        Graphics2D g = (Graphics2D) graphics;
        // drawing axis
        g.drawLine(this.OFFSET, this.OFFSET, this.OFFSET, this.windowHeight - this.OFFSET);
        g.drawLine(this.OFFSET, this.windowHeight - this.OFFSET,
                this.windowWidth - this.OFFSET, this.windowHeight - this.OFFSET);

        // drawing x and y-axis marks
        final int MAX_TEMP = (int) Math.ceil(TemperatureReading.getMaxTemperature(this.tempList));
        final int MIN_TEMP = (int) Math.floor(TemperatureReading.getMinTemperature(this.tempList));
        final int TEMP_RANGE = MAX_TEMP - MIN_TEMP;
        Line2D line;

        // x-axis
        final int TEMP_HEIGHT = this.getyAxisHeight() / TEMP_RANGE;
        final int MARK_X = 85;
        final int TEMP_POSITION = MARK_X - 20;
        float currentPositionTemp = this.windowHeight - this.OFFSET;

        for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
            line = new Line2D.Float(MARK_X, currentPositionTemp, this.OFFSET, currentPositionTemp);
            g.draw(line);
            g.drawString(String.valueOf(i), TEMP_POSITION, currentPositionTemp);
            tempPos.put(i, currentPositionTemp);
            currentPositionTemp-=TEMP_HEIGHT;
        }

        // y_axis
        final int MARK_Y = this.windowHeight - (this.OFFSET - 15);
        final int DAY_POSITION = MARK_Y + 20;
        float currentPositionDay = this.OFFSET;

        for (int i = 1; i <= this.DAYS; i++) {
            line = new Line2D.Float(currentPositionDay, MARK_Y, currentPositionDay, this.windowHeight - this.OFFSET);
            g.draw(line);
            g.drawString(String.valueOf(i), currentPositionDay, DAY_POSITION);
            currentPositionDay += this.getDayWidth();
        }
    }

    // generates the temperature data on the graph
    public void drawingGraph(Graphics graphics, Color c, int tempType, float hourWidth, HashMap<Integer, Float> tempPos) {
        Graphics2D g = (Graphics2D) graphics;

        for (int i = 0; i < this.tempList.size() - 1; i++) {
            g.setColor(c);
            float temp1 = this.tempList.get(i).getTemp(tempType);
            float temp2 = this.tempList.get(i + 1).getTemp(tempType);

            // getting first coordinates for the line
            float firstPointX = this.OFFSET + (i * (hourWidth));
            float firstPointY = this.ratioCalc((int) Math.floor(temp1), (int) Math.ceil(temp1), temp1, tempPos);

            // getting second coordinates for the line
            float secondPointX = this.OFFSET + ((i + 1) * (hourWidth));
            float secondPointY = this.ratioCalc((int) Math.floor(temp2), (int) Math.ceil(temp2), temp2, tempPos);

            Line2D.Float currentLine = new Line2D.Float(firstPointX, firstPointY, secondPointX, secondPointY);
            g.draw(currentLine);
        }
    }

    // draws the keys which show what colour represents what data
    public void drawingKey(Graphics g, Color c, int keyLength, int keyX, int keyY, int stringOffset, String location) {
        int stringX = keyX + stringOffset;
        int stringY = keyY + stringOffset;
        g.setColor(c);
        g.drawLine(keyX, keyY, keyX - keyLength, keyY);
        g.setColor(Color.BLACK);
        g.drawString(location, stringX, stringY);
    }

    // draws a line that's parallel to the y-axis that shows the temperature for the points it crosses
    public void drawingMarker(Graphics graphics, HashMap<Integer, Float> tempPos) {
        Graphics2D g = (Graphics2D) graphics;
        int diameter = 10;

        if (this.drawLine) {
            Color c = Color.black;
            g.setColor(c);
            Line2D.Float line = new Line2D.Float(this.markerPositionX, this.OFFSET, this.markerPositionX,
                    this.windowHeight - this.OFFSET);
            g.draw(line);

            int i = (int) ((this.markerPositionX - this.OFFSET) / this.getHourWidth());
            i = Math.min(this.tempList.size() - 1, i);
            int stringOffsetIncrease = 20;

            try {
                for (float temp : this.tempList.get(i).getTemps()) {
                    float markerPositionY = this.ratioCalc((int) Math.floor(temp), (int) Math.ceil(temp), temp, tempPos);
                    float pointerX = this.markerPositionX - ((float) diameter / 2);
                    float pointerY = markerPositionY - ((float) diameter / 2);
                    Ellipse2D.Float pointer = new Ellipse2D.Float(pointerX, pointerY, diameter, diameter);
                    g.fill(pointer);
                }
                c = new Color(0, 200, 0);
                g.setColor(c);
                g.drawString(TemperatureReading.round(this.tempList.get(i).getTemp(0), 1) + "C", this.markerPositionX, this.OFFSET - stringOffsetIncrease);
                c = new Color(220, 150, 0);
                g.setColor(c);
                g.drawString(TemperatureReading.round(this.tempList.get(i).getTemp(1), 1) + "C", this.markerPositionX, this.OFFSET);
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }

    // calculates the position for the point on the y-axis
    public int ratioCalc(int axisValue1, int axisValue2, float value, HashMap<Integer, Float> axisValues) {
        float position1 = axisValues.get(axisValue1);
        float position2 = axisValues.get(axisValue2);
        return (int) ((position2 * (value - axisValue1)) + (position1 * (axisValue2 - value)));
    }

    // when mouse is clicked it generates a message to the console saying
    // what day, hour and the temperature it was for both sets of data
    @Override
    public void mouseClicked(MouseEvent e) {
        int i = (int) ((e.getX() - this.OFFSET) / this.getHourWidth());
        try {
            TemperatureReading tempReading = this.tempList.get(i);
            tempReading.showTemperatureDate();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    public void handleLine(MouseEvent e) {
        if (e.getX() > this.windowWidth - this.OFFSET) {
            this.markerPositionX = this.windowWidth - this.OFFSET;
        } else if (e.getX() < this.OFFSET) {
            this.markerPositionX = this.OFFSET;
        } else {
            this.markerPositionX = e.getX();
            this.repaint();
        }
        this.drawLine = true;
    }

    // when the mouse is pressed or dragged, it allows a line to be drawn parallel to the y-axis
    @Override
    public void mousePressed(MouseEvent e) {
        this.handleLine(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.handleLine(e);
    }

    // stops showing the line when the mouse is released
    @Override
    public void mouseReleased(MouseEvent e) {
        this.drawLine = false;
        this.repaint();
    }

    // unused
    @Override
    public void mouseMoved(MouseEvent e) {
    }

    // unused
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    // unused
    @Override
    public void mouseExited(MouseEvent e) {
    }
}
