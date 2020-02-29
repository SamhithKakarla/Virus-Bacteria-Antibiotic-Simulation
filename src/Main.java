import processing.core.*;

public class Main extends PApplet {
	Simulator simulator;
	String saveFilePath = "c:\\data\\foxesAndRabbitsSaved.txt";
	boolean paused = true;
	public static int hoursPassed = 0;

	@Override
	public void setup() {
		size(640, 550);


		this.simulator = new Simulator(80, 60);
		this.simulator.setGUI(this);
	}

	@Override
	public void draw() {
		background(200);
		if (!paused) {
			simulator.simulateOneStep();
			hoursPassed++;
			System.out.println(hoursPassed);

		}
		simulator.drawField();
		simulator.drawGraph();
	}

	// handle key presses
	public void keyReleased() {
		if (key == 's') {				// 's' saves the current state to a file
			simulator.writeToFile(saveFilePath);
		}

		if (key == 'l') { 				// 'l' loads a saved state
			simulator.readFile(saveFilePath);
		}

		if (key == 'p') { 				// 'p' toggles paused and unpaused
			paused = !paused;
		}

		if (key == 'c') { 				// 'c' resets the simulator
			simulator.reset();
		}
	}

	// if mouse clicked, let the simulator handle the mouse click
	public void mouseClicked() {
		simulator.handleMouseClick(mouseX, mouseY);
	}

	// if mouse is dragged, let the simulator handle the mouse drag
	public void mouseDragged() {
		simulator.handleMouseDrag(mouseX, mouseY);
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "Main" });
	}
}