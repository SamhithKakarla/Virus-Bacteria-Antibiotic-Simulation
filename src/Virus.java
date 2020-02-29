import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class Virus extends Organism implements Serializable {

    public Virus(boolean randomAge) {
        super();
        BREEDING_AGE = 0;
        MAX_AGE = 25;
        BREEDING_PROBABILITY = 0.1;
        LITTER_SIZE = 1;

        rand = new Random();

        age = 0;
        alive = true;
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        } else {
            // leave age at 0
        }
    }

    public void act(Field currentField, Field updatedField, List<Organism> newViruses) {
        incrementAge();
        if (alive) {
            // New foxes are born into adjacent locations.
            int births = breed();
            for (int b = 0; b < births; b++) {
                Location newLocation = findFood(currentField, location);
                if(newLocation != null){
                    Virus newVirus = new Virus(false);
                    newViruses.add(newVirus);
                    Location loc = updatedField.randomAdjacentLocation(location);
                    newVirus.setLocation(loc);
                    updatedField.put(newVirus, loc);
                }
            }
            // Move towards the source of food if found.
            Location newLocation = findFood(currentField, location);
            if (newLocation == null) { // no food found - move randomly
                newLocation = updatedField.freeAdjacentLocation(location);
            }
            if (newLocation != null) {
                setLocation(newLocation);
                updatedField.put(this, newLocation);
            } else {
                // can neither move nor stay - overcrowding - all locations
                // taken
                alive = false;
            }
        }
    }

    private Location findFood(Field field, Location location) {
        List<Location> adjacentLocations = field.adjacentLocations(location);

        for (Location where : adjacentLocations) {
            Object animal = field.getObjectAt(where);
            if (animal instanceof Bacteria) {
                Bacteria bacteria = (Bacteria) animal;
                if (bacteria.isAlive()) {
                    bacteria.setEaten();
                    return where;
                }
            }
        }

        return null;
    }


}
