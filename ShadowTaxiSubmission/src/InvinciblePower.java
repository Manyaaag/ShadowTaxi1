
import bagel.Image;
import java.util.Properties;

/**
 * Represents an invincibility power-up in the game, which grants invincibility
 * to the player when collected.
 */
public class InvinciblePower {

    private final Properties PROPS;
    private final Image IMAGE;
    private final float RADIUS;
    private final int MAX_FRAMES;
    private int x, y;  // InvinciblePower's current coordinates
    private int activeFrames;  // Tracks how long the invincibility effect lasts
    private boolean isActive;
    public boolean isCollided;
    public boolean isCollected = false;

    /**
     * Constructs an InvinciblePower instance at specified coordinates with properties.
     *
     * @param x     The x-coordinate for the InvinciblePower.
     * @param y     The y-coordinate for the InvinciblePower.
     * @param props Properties object containing configuration for invincible power attributes.
     */
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
     *
     * @param upArrowPressed Whether the up arrow is currently pressed.
     */
    public void update(boolean upArrowPressed) {
        if (upArrowPressed) {
            y += 5;  // Moves down by 5 pixels per frame when up arrow is pressed
        }
    }

    /**
     * Renders the Invincible Power on the screen if it has not been collected.
     */
    public void draw() {
        if (!isCollected) { // 3. Only draw if not collected
            IMAGE.draw(x, y);
        }
    }

    /**
     * Checks for collision with an entity and activates invincibility if a collision occurs.
     *
     * @param entity The entity (Taxi or Driver) to check for collision.
     */
    public void collide(Collidable entity) {
        if (!isActive && isCollidingWith(entity)) {
            activate();
            entity.setInvincible(MAX_FRAMES);  // Make the entity invincible for MAX_FRAMES
        }
    }

    /**
     * Check if the invincibility power collides with the given entity.
     *
     * @param entity The entity to check collision with (Taxi, Driver, etc.).
     * @return true if a collision occurs, false otherwise.
     */
    private boolean isCollidingWith(Collidable entity) {
        float distance = (float) Math.sqrt(Math.pow(this.x - entity.getX(), 2) + Math.pow(this.y - entity.getY(), 2));
        return distance <= this.RADIUS + entity.getRadius();
    }

    /**
     * Activate the invincibility power.
     */
    private void activate() {
        this.isActive = true;
        this.activeFrames = MAX_FRAMES;
    }

    /**
     * Grants invincibility to the taxi for the duration of MAX_FRAMES.
     *
     * @param taxi The taxi object to make invincible.
     */
    private void activatePower(Taxi taxi) {
        taxi.setInvincible(MAX_FRAMES);  // Set taxi to be invincible for MAX_FRAMES
        isActive = false;  // Deactivate the power-up after activation
    }

    /**
     * Getter for the invincibility status.
     *
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

    /**
     * Getter for the x-coordinate.
     *
     * @return The x-coordinate of the invincibility power.
     */
    public int getX() {
        return x;
    }

    /**
     * Getter for the y-coordinate.
     *
     * @return The y-coordinate of the invincibility power.
     */
    public int getY() {
        return y;
    }

    /**
     * Getter for the radius.
     *
     * @return The radius of the invincibility power.
     */
    public float getRadius() {
        return RADIUS;
    }

    /**
     * Checks for collision with the Taxi and grants invincibility if collected.
     *
     * @param taxi The Taxi object to check for collision and apply invincibility.
     */
    public void collide(Taxi taxi) {
        if (!isCollected && hasCollidedWith(taxi)) { // Ensure it hasn't been collected
            taxi.setInvincible(MAX_FRAMES);  // Set taxi to be invincible for MAX_FRAMES
            isCollected = true; // 2. Mark as collected
        }
    }

    /**
     * Sets the invincibility power as collided.
     */
    public void setIsCollided() {
        this.isCollided = true;
    }

    /**
     * Checks if the invincibility power has collided with the taxi based on their radii.
     *
     * @param taxi The taxi object to check collision with.
     * @return True if the objects are colliding, false otherwise.
     */
    public boolean hasCollidedWith(Taxi taxi) {
        // if the distance between the two objects is less than the sum of their radius, they are collided
        float collisionDistance = RADIUS + taxi.getRadius();
        float currDistance = (float) Math.sqrt(Math.pow(x - taxi.getX(), 2) + Math.pow(y - taxi.getY(), 2));
        return currDistance <= collisionDistance;
    }
}

