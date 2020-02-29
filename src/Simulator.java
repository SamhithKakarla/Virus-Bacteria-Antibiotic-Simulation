import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import processing.core.PApplet;

/**
 * A simple predator-prey simulator, based on a field containing rabbits and
 * foxes.
 *
 * @author David J. Barnes and Michael Kolling. Modified by David Dobervich
 * 2007-2013.
 * @version 2006.03.30
 */
public class Simulator {
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 80;

    // The default height of the grid.
    private static final int DEFAULT_HEIGHT = 80;

    private static final double VIRUS_CREATION_PROBABILITY = 0.01;
    private static final double BACTERIA_CREATION_PROBABILITY = 0.5;




    // Lists of animals in the field. Separate lists are kept for ease of
    // iteration.
    List<Organism> organisms;

    // The current state of the field.
    private Field field;

    // A second field, used to build the next stage of the simulation.
    private Field updatedField;

    // The current step of the simulation.
    private int step;

    // A graphical view of the simulation.
    private FieldDisplay view;

    // A graph of animal populations over time
    private Graph graph;

    // Processing Applet (the graphics window we draw to)
    private PApplet graphicsWindow;

    // Object to keep track of statistics of animal populations
    private FieldStats stats;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() {
        this(DEFAULT_HEIGHT, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param height Depth of the field. Must be greater than zero.
     * @param width  Width of the field. Must be greater than zero.
     **/
    public Simulator(int width, int height) {
        if (width <= 0 || height <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            height = DEFAULT_HEIGHT;
            width = DEFAULT_WIDTH;
        }

        organisms = new ArrayList<Organism>();
        field = new Field(width, height);
        updatedField = new Field(width, height);
        stats = new FieldStats();

        // Setup a valid starting point.
        reset();
    }

    public void setGUI(PApplet p, int x, int y, int display_width, int display_height) {
        this.graphicsWindow = p;

        // Create a view of the state of each location in the field.
        view = new FieldDisplay(p, this.field, x, y, display_width, display_height);
        view.setColor(Bacteria.class, p.color(0, 255, 0));
        view.setColor(Virus.class, p.color(155, 150, 255));


        graph = new Graph(p, 100, p.height - 30, p.width - 50, p.height - 110, 0, 0, 500,
                field.getHeight() * field.getWidth());
        graph.title = "Bacteria Populations";
        graph.xlabel = "Time";
        graph.ylabel = "Pop.\t\t";
        graph.setColor(Bacteria.class, p.color(0, 255, 0));
        graph.setColor(Virus.class, p.color(155, 150, 255));


    }

    public void setGUI(PApplet p) {
        setGUI(p, 10, 10, p.width - 10, 400);
    }

    /**
     * Run the simulation from its current state for a reasonably long period, e.g.
     * 500 steps.
     */
    public void runLongSimulation() {
        simulate(500);
    }

    /**
     * Run the simulation from its current state for the given number of steps. Stop
     * before the given number of steps if it ceases to be viable.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps) {
        for (int step = 1; step <= numSteps && isViable(); step++) {
            simulateOneStep();
        }
    }

    /**
     * Run the simulation from its current state for a single step. Iterate over the
     * whole field updating the state of each fox and rabbit.
     */
    public void simulateOneStep() {
        step++;

        calculatePercentageBacteria();

        // New List to hold newborn rabbits.
        List<Organism> newAnimals = new ArrayList<Organism>();

        // Loop through all Rabbits. Let each run around.
        for (int i = 0; i < organisms.size(); i++) {
            Organism animal = organisms.get(i);
            animal.act(field, updatedField, newAnimals);
            if (!animal.isAlive()) {
                organisms.remove(i);
                i--;
            }
        }

        organisms.addAll(newAnimals);

        Field temp = field;
        field = updatedField;
        updatedField = temp;



        updatedField.clear();

        stats.generateCounts(field);
        updateGraph();
    }

    private void calculatePercentageBacteria() {

        int numOfBacteria = 0;
        int numOfEBacteria = 0;

        for (int i = 0; i < organisms.size(); i++) {
            if (organisms.get(i).getClass().equals(Bacteria.class)) {
                numOfBacteria++;
                Bacteria bacteria = (Bacteria)(organisms.get(i));
                if (bacteria.isResistant == true) numOfEBacteria++;
            }

        }

        if(numOfBacteria>0) {
            System.out.println("% of screen taken up by bacteria - " + 100*((double)numOfBacteria / 6400));
            System.out.println("% of bacteria that is evolved - " + 100*((double)numOfEBacteria / (numOfBacteria)));
        }
        else{
            System.out.println("% of screen taken up by bacteria - 0");
            System.out.println("% of bacteria that is evolved - 0");

        }
    }

    public void updateGraph() {
        Counter count;
        for (Counter c : stats.getCounts()) {
            graph.plotPoint(step, c.getCount(), c.getClassName());
        }
    }

    public void reset() {
        step = 0;
        organisms.clear();
        field.clear();
        updatedField.clear();
        initializeBoard(field);

        if (graph != null)
            graph.clear();

        // Show the starting state in the view.
        // view.showStatus(step, field);
    }

    /**
     * Populate a field with foxes and rabbits.
     *
     * @param field The field to be populated.
     */
    private void initializeBoard(Field field) {
        Random rand = new Random();
        field.clear();
        for (int row = 0; row < field.getHeight(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                if (rand.nextDouble() <= VIRUS_CREATION_PROBABILITY) {
                    Virus virus = new Virus(true);
                    virus.setLocation(col, row);
                    organisms.add(virus);
                    field.put(virus, col, row);
                } else if (rand.nextDouble() <= BACTERIA_CREATION_PROBABILITY) {
                    Bacteria bacteria = new Bacteria(true, false);
                    bacteria.setLocation(col, row);
                    organisms.add(bacteria);
                    field.put(bacteria, col, row);
                }

            }
        }
        Collections.shuffle(organisms);


    }

    private boolean isViable() {
        return stats.isViable(field);
    }

    public Field getField() {
        return this.field;
    }

    // Draw field if we have a gui defined
    public void drawField() {
        if ((graphicsWindow != null) && (view != null)) {
            view.drawField(this.field);
        }
    }

    public void drawGraph() {
        graph.draw();
    }

    public void writeToFile(String writefile) {
        try {
            Record r = new Record(organisms, this.field, this.step);
            FileOutputStream outStream = new FileOutputStream(writefile);
            ObjectOutputStream objectOutputFile = new ObjectOutputStream(outStream);
            objectOutputFile.writeObject(r);
            objectOutputFile.close();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    public void readFile(String readfile) {
        try {
            FileInputStream inputStream = new FileInputStream(readfile);
            ObjectInputStream objectInputFile = new ObjectInputStream(inputStream);
            Record r = (Record) objectInputFile.readObject();
            setOrganisms(r.getAnimals());
            setField(r.getField());
            setStep(r.getSteps());
            objectInputFile.close();
            // clear field
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    private void setStep(int steps) {
        step = steps;
    }

    private void setField(Field field2) {
        field = field2;
    }

    private void setOrganisms(List<Organism> animals2) {
        organisms = animals2;
    }

    // Perform an action when the mouse was clicked.
    // parameters are the x, y screen coordinates the user clicked on.
    // Note: you probably want to modify handleMouseClick(Location) which
    // gives you the location they clicked on in the grid.
    public void handleMouseClick(float mouseX, float mouseY) {
        Location loc = view.gridLocationAt(mouseX, mouseY); // get grid at
        // click.

        for (int x = loc.getCol() - 8; x < loc.getCol() + 8; x++) {
            for (int y = loc.getRow() - 8; y < loc.getRow() + 8; y++) {
                Location locToCheck = new Location(x, y);
                if (field.isInGrid(locToCheck)) {
                    Object animal = field.getObjectAt(locToCheck);
                    if (animal instanceof Bacteria)
                        organisms.remove((Bacteria) animal);
                    if (animal instanceof Virus)
                        organisms.remove((Virus) animal);

                    field.put(null, locToCheck);
                    updatedField.put(null, locToCheck);
                }
            }
        }
    }

    private void handleMouseClick(Location l) {
        System.out.println("Change handleMouseClick in Simulator.java to do something!");
    }

    public void handleMouseDrag(int mouseX, int mouseY) {
        Location loc = this.view.gridLocationAt(mouseX, mouseY); // get grid at
        // click.
        if (loc == null)
            return; // if off the screen, exit
        handleMouseDrag(loc);
    }

    private void handleMouseDrag(Location l) {
        System.out.println("Change handleMouseDrag in Simulator.java to do something!");
    }
}