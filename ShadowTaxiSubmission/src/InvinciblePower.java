import bagel.Image;
import java.util.Properties;

public class InvinciblePower {

    private final Properties PROPS;
    private final Image IMAGE;
    private final float RADIUS;
    private final int MAX_FRAMES;
    private int x, y;  // InvinciblePower's current coordinates
    private int activeFrames;  // Tracks how long the invincibility effect lasts
    private boolean isActive;

    public InvinciblePower(int x, int y, Properties props) {
        this.PROPS = props;

        // Load the image and properties for invincible power
        this.IMAGE = new Image(PROPS.getProperty("gameObjects.invinciblePower.image"));
        this.RADIUS = Float.parseFloat(PROPS.getProperty("gameObjects.invinciblePower.radius"));
        this.MAX_FRAMES = Integer.parseInt(PROPS.getProperty("gameObjects.invinciblePower.maxFrames"));

        // Set initial position
        this.x = x;
        this.y = y;

        // Initialize as not active
        this.activeFrames = 0;
        this.isActive = false;
    }

    /**
     * Updates the position of the Invincible Power.
     * Moves vertically down by 5 pixels per frame when the up arrow key is pressed.
     */
    public void update(boolean upArrowPressed) {
        if (upArrowPressed) {
            y += 5;  // Moves down by 5 pixels per frame when up arrow is pressed
        }
    }

    /**
     * Renders the Invincible Power on the screen.
     */
    public void draw() {
        IMAGE.draw(x, y);
    }

    /**
     * Checks for collision with Taxi or Driver, and activates invincibility if a collision occurs.
     * @param entity The entity (Taxi or Driver) to check for collision.

    public void collide(Entity entity) {
        if (isCollidingWith(entity)) {
            activate();
            entity.setInvincible(MAX_FRAMES);  // Make the entity invincible for MAX_FRAMES
        }
    }
     */

    /**
     * Activate the invincibility power.
     */
    private void activate() {
        this.isActive = true;
        this.activeFrames = MAX_FRAMES;
    }

    /**
     * Check if the invincibility power collides with the given entity.
     * @param entity The entity to check collision with (Taxi, Driver, etc.).
     * @return true if a collision occurs, false otherwise.

    private boolean isCollidingWith(Entity entity) {
        float distance = (float) Math.sqrt(Math.pow(this.x - entity.getX(), 2) + Math.pow(this.y - entity.getY(), 2));
        return distance <= this.RADIUS + entity.getRadius();
    }
     */

    /**
     * Getter for the invincibility status.
     * @return true if invincibility is active, false otherwise.
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * Updates the active frames of the invincibility.
     * Decrements the active frames and disables the power when time runs out.
     */
    public void updateActiveFrames() {
        if (isActive) {
            activeFrames--;
            if (activeFrames <= 0) {
                isActive = false;  // Disable the invincibility power
            }
        }
    }

    // Getter for x-coordinate
    public int getX() {
        return x;
    }

    // Getter for y-coordinate
    public int getY() {
        return y;
    }

    // Getter for radius
    public float getRadius() {
        return RADIUS;
    }
}
