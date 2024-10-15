

import bagel.Image;
import bagel.Input;

/**
 * Abstract base class representing game objects with common properties such as position, image, speed, radius, and health.
 */
public abstract class Objects {
    protected int x;  // X-coordinate of the object
    protected int y;  // Y-coordinate of the object
    protected Image image;  // Image representation of the object
    protected int speedY;  // Vertical speed of the object; can be made final if unchanging
    protected float RADIUS;  // Radius of the object for collision detection; can be made final if unchanging
    protected float health;  // Health level of the object

    /**
     * Constructs an object with specified position, image, speed, and radius.
     *
     * @param x        Initial x-coordinate of the object.
     * @param y        Initial y-coordinate of the object.
     * @param imagePath File path for the image of the object.
     * @param speedY   Vertical speed of the object.
     * @param radius   Radius of the object for collision detection.
     */
    public Objects(int x, int y, String imagePath, int speedY, float radius) {
        this.x = x;
        this.y = y;
        this.image = new Image(imagePath);
        this.speedY = speedY;
        this.RADIUS = radius;
        this.health = 100; // Default health, can be overridden in subclasses
    }

    /**
     * Checks for a collision with another object based on their radii.
     *
     * @param other The other object to check collision with.
     * @return true if a collision occurs, false otherwise.
     */
    public boolean hasCollided(Objects other) {
        float collisionDistance = this.RADIUS + other.getRadius();
        float currDistance = (float) Math.sqrt(Math.pow(this.x - other.getX(), 2) + Math.pow(this.y - other.getY(), 2));
        return currDistance <= collisionDistance;
    }

    /**
     * Abstract method to handle collision with another object, to be implemented by subclasses.
     *
     * @param other The other object involved in the collision.
     */
    public abstract void onCollision(Objects other);

    /**
     * Abstract method to update the object's state, to be implemented by subclasses.
     *
     * @param input The input to control object updates.
     */
    public abstract void update(Input input);

    /**
     * Moves the object vertically based on its speed.
     */
    public void move() {
        this.y += speedY;
    }

    /**
     * Renders the object on the screen at its current coordinates.
     */
    public void draw() {
        image.draw(x, y);
    }

    /**
     * Reduces the object's health by a specified damage amount.
     *
     * @param damage The damage to apply to the object.
     */
    public void takeDamage(float damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    /**
     * Gets the object's radius.
     *
     * @return The radius of the object.
     */
    public float getRadius() {
        return RADIUS;
    }

    /**
     * Gets the object's health.
     *
     * @return The current health of the object.
     */
    public float getHealth() {
        return health;
    }

    /**
     * Gets the x-coordinate of the object.
     *
     * @return The x-coordinate of the object.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the object.
     *
     * @return The y-coordinate of the object.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the x-coordinate of the object.
     *
     * @param x The x-coordinate to set.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the object.
     *
     * @param y The y-coordinate to set.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets the object's health.
     *
     * @param health The health value to set.
     */
    public void setHealth(float health) {
        this.health = health;
    }
}


