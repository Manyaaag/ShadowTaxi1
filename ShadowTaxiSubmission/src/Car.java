import bagel.Image;
import bagel.Input;
import java.util.Properties;
import java.util.Random;

public class Car {

    private final Properties PROPS;
    private final Image IMAGE;
    private final float RADIUS;
    private final float DAMAGE_POINTS;
    private final int COLLISION_TIMEOUT = 200;
    private final int COLLISION_KNOCKBACK_FRAMES = 10;

    private int x, y;  // Car's current coordinates
    private int speedY; // Random speed between minSpeedY and maxSpeedY
    private float health;
    private int collisionTimeout;
    private boolean isColliding;
    private boolean isDestroyed;

    public Car(Properties props) {
        this.PROPS = props;

        // Randomly choose one of the car images
        int carType = new Random().nextInt(Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.types"))) + 1;
        this.IMAGE = new Image(String.format(PROPS.getProperty("gameObjects.otherCar.image"), carType));

        // Get car properties
        this.RADIUS = Float.parseFloat(PROPS.getProperty("gameObjects.otherCar.radius"));
        //this.DAMAGE_POINTS = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.damage"));
        this.DAMAGE_POINTS = Float.parseFloat(PROPS.getProperty("gameObjects.otherCar.damage"));  // Parse as float
        //this.health = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.health"));
        this.health = Float.parseFloat(PROPS.getProperty("gameObjects.otherCar.health"));

        // Randomly choose the speed of the car between the provided range
        int minSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.minSpeedY"));
        int maxSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.maxSpeedY"));
        this.speedY = new Random().nextInt(maxSpeedY - minSpeedY + 1) + minSpeedY;

        // Randomly choose the x-coordinate from one of the lanes
        int[] lanes = {360, 480, 620};
        this.x = lanes[new Random().nextInt(lanes.length)];

        // Randomly choose the starting y-coordinate (-50 or 768)
        int[] yCoords = {-50, 768};
        this.y = yCoords[new Random().nextInt(yCoords.length)];

        // Initialize other properties
        this.collisionTimeout = 0;
        this.isColliding = false;
        this.isDestroyed = false;
    }

    /**
     * Updates the car's position and checks for collisions.
     */
    public void update() {
        if (isDestroyed) {
            return;  // Do not update the car if it is destroyed
        }

        // If the car is colliding, handle collision behavior
        if (collisionTimeout > 0) {
            collisionTimeout--;
            if (collisionTimeout <= COLLISION_KNOCKBACK_FRAMES) {
                // Knockback logic - move the car away from the collided object
                y += speedY * (collisionTimeout > 0 ? -1 : 1);
            }
            if (collisionTimeout == 0) {
                // After collision timeout, reset the speedY
                resetSpeed();
            }
        } else {
            // Normal movement, move the car vertically upwards
            y -= speedY;
        }
    }

    /**
     * Renders the car on the screen if it's not destroyed.
     */
    public void draw() {
        if (!isDestroyed) {
            IMAGE.draw(x, y);
        }
    }

    /**
     * Handles collision with other game objects.
     */
    /*
    public void collide(Entity entity) {
        if (isDestroyed || collisionTimeout > 0) {
            return;  // Skip collisions if the car is already destroyed or in timeout
        }

        if (entity instanceof Taxi || entity instanceof Car || entity instanceof EnemyCar || entity instanceof Fireball) {
            int entityDamage = entity.getDamage();
            takeDamage(entityDamage);
            entity.takeDamage(DAMAGE_POINTS);

            // Start collision timeout
            collisionTimeout = COLLISION_TIMEOUT;
        }
    }

     */

    /**
     * Takes damage and checks if the car is destroyed.
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            isDestroyed = true;
            // Render fire effect or similar visual indicator (not implemented here)
        }
    }

    /**
     * Resets the car's speed after a collision.
     */
    private void resetSpeed() {
        int minSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.minSpeedY"));
        int maxSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.maxSpeedY"));
        this.speedY = new Random().nextInt(maxSpeedY - minSpeedY + 1) + minSpeedY;
    }

    // Getters for damage and radius (for collision detection)
    public float getDamage() {
        return DAMAGE_POINTS;
    }

    public float getRadius() {
        return RADIUS;
    }

    // Getter for car's coordinates (for other entity to detect proximity)
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

