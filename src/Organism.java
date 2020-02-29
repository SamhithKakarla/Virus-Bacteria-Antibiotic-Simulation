import java.util.List;
import java.util.Random;

public abstract class Organism {

	protected int age;
	protected boolean alive;
	protected Location location;
	protected int BREEDING_AGE;
	protected int MAX_AGE;
	protected double BREEDING_PROBABILITY;
	protected int LITTER_SIZE;
	protected Random rand = new Random();

	
	public Organism() {
		age = 0;
		alive = true;
	}

	public abstract void act(Field currentField, Field updatedField, List<Organism> babies);
		
	
	public void incrementAge() {
		age++;
		if (age > MAX_AGE) {
			alive = false;
		}
	}

	public int breed() {
		int births = 0;
		if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
			births = LITTER_SIZE;
		}
		return births;
	}

	public boolean canBreed() {
		return age >= BREEDING_AGE;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setEaten() {
		alive = false;
	}

	public void setLocation(int row, int col) {
		this.location = new Location(row, col);
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
