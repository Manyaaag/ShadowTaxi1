
import java.util.Properties;
import bagel.Image;
import bagel.Input;
import bagel.Keys;


/**
 * The Driver class represents a driver character in the game who can move,
 * enter taxis, and interact with taxis and the game environment. The driver
 * has properties for position, health, movement speed, and collision effects.
 */

public class Driver {

    private final Properties PROPS;
    private final Image IMAGE;

    private final float RADIUS;
    private final int WALK_SPEED_X;
    private final int WALK_SPEED_Y;
    private int x, y;  // Driver's current coordinates
    private int health;
    private int moveY;
    private int walkDirectionX;
    private int walkDirectionY;
    private final int SPEED_Y;

    public boolean inTaxi = true;
    public boolean inNewTaxi = false;
    private final int DRIVER_INTAXI_RADIUS;

    private final int COLLISION_TIMEOUT = 200;
    private int collisionTimeout;
    private boolean isDead;
    private boolean isInvincible;
    protected final Image bloodImage;

    /**
     * Constructs a Driver instance with specified initial position and properties.
     *
     * @param x     Initial x-coordinate of the driver.
     * @param y     Initial y-coordinate of the driver.
     * @param props Properties to configure driver behavior and image.
     */

    public Driver(int x, int y, Properties props) {
        this.PROPS = props;
        this.IMAGE = new Image(props.getProperty("gameObjects.driver.image"));
        // Parse properties with fallbacks
        this.WALK_SPEED_X = parseIntProperty(props, "gameObjects.driver.walkSpeedX", 2);
        this.WALK_SPEED_Y = parseIntProperty(props, "gameObjects.driver.walkSpeedY", 2);
        this.RADIUS = parseFloatProperty(props, "gameObjects.driver.radius", 10.0f);
        this.DRIVER_INTAXI_RADIUS = parseIntProperty(props, "gameObjects.driver.taxiGetInRadius", 10);
        this.SPEED_Y = parseIntProperty(props, "gameObjects.taxi.speedY", 5);
        this.health = parseIntProperty(props, "gameObjects.driver.health", 100);

        this.x = x;
        this.y = y;
        this.moveY = 0;
        this.bloodImage = new Image(String.format(PROPS.getProperty("gameObjects.blood.image")));
        this.collisionTimeout = 0;
        this.isDead = false;
    }

    private int parseIntProperty(Properties props, String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private float parseFloatProperty(Properties props, String key, float defaultValue) {
        try {
            return Float.parseFloat(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves the x-coordinate of the driver.
     *
     * @return The current x-coordinate.
     */

    public int getX() {
        return x;
    }

    /**
     * Retrieves the y-coordinate of the driver.
     *
     * @return The current y-coordinate.
     */

    public int getY() {
        return y;
    }

    /**
     * Sets the x-coordinate of the driver.
     *
     * @param x The x-coordinate to set.
     */

    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the driver.
     *
     * @param y The y-coordinate to set.
     */

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Retrieves the radius within which the driver can enter a taxi.
     *
     * @return Radius for entering a taxi.
     */

    public int getTaxiInRadius() {
        return DRIVER_INTAXI_RADIUS;
    }


    /**
     * Retrieves the health of the driver.
     *
     * @return The current health level.
     */

    // Getter for health
    public float getHealth() {
        return health;
    }

    /**
     * Sets the driver's in-taxi status.
     *
     * @param inTaxi true if the driver is inside a taxi, false otherwise.
     */

    public void setInTaxi(boolean inTaxi) {
        this.inTaxi = inTaxi;
    }

    /**
     * Sets the driver's in-new-taxi status.
     *
     * @param inNewTaxi true if the driver is inside a new taxi, false otherwise.
     */
    public void setInNewTaxi(boolean inNewTaxi) {
        this.inNewTaxi = inNewTaxi;

    }

    /**
     * Sets the driver's health and handles any necessary effects when health reaches zero.
     *
     * @param health New health value.
     */
    // Setter for health
    public void setHealth(int health) {
        this.health = health;
        if (this.health <= 0) {
            // Implement logic to render blood effect here if needed
        }
    }

    /**
     * Updates the driver's position and state when interacting with a taxi.
     *
     * @param input   The Input object representing user controls.
     * @param newTaxi The taxi instance the driver can interact with.
     */

    public void updateWithTaxi(Input input, Taxi newTaxi) {

        if (!inTaxi) { // Control driver movement only when outside the taxi
            if (input != null) {
                adjustToInputMovement(input);
            }
            walk();

            // Check if close enough to enter the new taxi
            if (!newTaxi.isOriginalTaxi && calculateDistance(newTaxi) <= DRIVER_INTAXI_RADIUS) {
                System.out.println("Within radius of new taxi, attempting to enter...");

                enterTaxi(newTaxi);
                //inTaxi = true;
                //moveWithTaxi(newTaxi);
            } else {
                draw();  // Draw the driver if outside the taxi
            }

            // Game loss condition: Driver goes out of bounds at top of screen
            if (y < 0) {
                System.out.println("Game Over: Driver moved out of bounds.");

            }
        } else {
            //moveWithTaxi(taxi);
            moveWithTaxi(newTaxi);  // Keep driver moving with the taxi when inside
        }

//        if (health <= 0) {
//            health = 0;
//            while (collisionTimeout < COLLISION_TIMEOUT) {
//                bloodImage.draw(this.x, this.y);
//                collisionTimeout++;
//            }
//            isDead = true;
//
//        }
    }

    /**
     * Adjusts the driver's movement directions based on the user input.
     *
     * @param input Input object containing key press information.
     */

    private void adjustToInputMovement(Input input) {
        if (input.wasPressed(Keys.UP)) {
            moveY = 1;
        } else if (input.wasReleased(Keys.UP)) {
            moveY = 0;
        }

        if (input.isDown(Keys.LEFT)) {
            walkDirectionX = -1;
        } else if (input.isDown(Keys.RIGHT)) {
            walkDirectionX = 1;
        } else {
            walkDirectionX = 0;
        }

        if (input.isDown(Keys.UP)) {
            walkDirectionY = -1;
        } else if (input.isDown(Keys.DOWN)) {
            walkDirectionY = 1;
        } else {
            walkDirectionY = 0;
        }
        //System.out.println("Current Walk Directions -> X: " + walkDirectionX + " Y: " + walkDirectionY);

    }

    /**
     * Moves the driver based on the set speed and movement direction.
     */

    private void move() {
        this.y += SPEED_Y * moveY;
    }

    /**
     * Draws the driver's image at the current position.
     */

    public void draw() {
        IMAGE.draw(x, y);
    }

    /**
     * Updates the driver's position based on walking speed and direction.
     */

    private void walk() {
        x += WALK_SPEED_X * walkDirectionX;
        y += WALK_SPEED_Y * walkDirectionY;
        //System.out.println("Driver Position -> X: " + x + " Y: " + y);  // Debug for position
    }

    /**
     * Attempts to enter the specified taxi if within the defined entry radius.
     *
     * @param newTaxi The taxi to enter.
     */

    public void enterTaxi(Taxi newTaxi) {
        //System.out.println("attempting to enter taxi");
        if (!inTaxi && calculateDistance(newTaxi) <= DRIVER_INTAXI_RADIUS) {
            //System.out.println("Driver is now entering the taxi.");  // Debug log

            inTaxi = true;
            inNewTaxi = true;
            newTaxi.activate();
            moveWithTaxi(newTaxi);
        }
    }

    /**
     * Updates the driver's position to match the specified taxi's coordinates.
     *
     * @param taxi The taxi the driver is riding in.
     */

    public void moveWithTaxi(Taxi taxi) {
        x = taxi.getX();
        y = taxi.getY();
    }

    /**
     * Calculates the distance between the driver and a specified taxi.
     *
     * @param newTaxi The taxi to calculate distance from.
     * @return The distance between the driver and the taxi.
     */

    public double calculateDistance(Taxi newTaxi) {

        float currDistance = (float) Math.sqrt(Math.pow(newTaxi.getX() - x, 2) + Math.pow(newTaxi.getY() - y, 2));
        return currDistance;

    }

}
