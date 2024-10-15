import bagel.Image;
import java.util.Properties;

/**
 * Represents a Fireball in the game, which can move, detect collisions, and apply damage
 * to other entities upon collision. The Fireball has properties such as radius, speed, and damage.
 */
public class Fireball {

    private final Image FIREBALL_IMAGE;
    private final float FIREBALL_RADIUS;
    private final float FIREBALL_DAMAGE;
    private final int FIREBALL_SPEED;

    protected int x, y;  // Fireball's current coordinates
    private boolean isDestroyed;

    /**
     * Constructs a Fireball instance with specified initial position and properties.
     *
     * @param x     The initial x-coordinate of the fireball.
     * @param y     The initial y-coordinate of the fireball.
     * @param props Properties object to configure fireball attributes.
     */
    public Fireball(int x, int y, Properties props) {
        // Load the fireball properties from the properties file
        this.FIREBALL_IMAGE = new Image(props.getProperty("gameObjects.fireball.image"));
        this.FIREBALL_RADIUS = Float.parseFloat(props.getProperty("gameObjects.fireball.radius"));
        this.FIREBALL_DAMAGE = Float.parseFloat(props.getProperty("gameObjects.fireball.damage"));
        this.FIREBALL_SPEED = Integer.parseInt(props.getProperty("gameObjects.fireball.shootSpeedY"));

        this.x = x;
        this.y = y;
        this.isDestroyed = false;
    }

    /**
     * Updates the fireball's position, moving it upwards, and checks if it reaches
     * the top of the screen, which destroys it.
     */
    public void update() {
        if (!isDestroyed) {
            y -= FIREBALL_SPEED;  // Move the fireball upwards at the defined speed

            // Check if fireball goes off-screen and set isDestroyed = true if needed
            if (y < 0) {
                isDestroyed = true;
            }
        }
    }

    /**
     * Renders the fireball on the screen at its current coordinates.
     */
    public void draw() {
        FIREBALL_IMAGE.draw(x, y);
    }

    /**
     * Handles collision with game entities and applies damage if the collision occurs.
     * The fireball is destroyed upon collision.
     *
     * @param entity The entity the fireball collides with.
     */
    public void collide(Object entity) {
        if (!isDestroyed && entity != null && checkCollision(entity)) {
            if (entity instanceof Taxi) {
                ((Taxi) entity).takeDamage(FIREBALL_DAMAGE);
            } else if (entity instanceof Car) {
                ((Car) entity).takeDamage(FIREBALL_DAMAGE);
            } else if (entity instanceof EnemyCar) {
                ((EnemyCar) entity).takeDamage(FIREBALL_DAMAGE);
            }
            isDestroyed = true;  // Fireball is destroyed after the collision
        }
    }

    /**
     * Checks whether the fireball collides with a given entity by calculating the distance
     * between the fireball and the entity.
     *
     * @param entity The entity to check collision with.
     * @return true if collision occurred, false otherwise.
     */
    private boolean checkCollision(Object entity) {
        if (entity instanceof Taxi || entity instanceof Car || entity instanceof EnemyCar) {
            float entityX = ((Taxi) entity).getX();
            float entityY = ((Taxi) entity).getY();
            float entityRadius = ((Taxi) entity).getRadius();
            float distance = (float) Math.sqrt(Math.pow(this.x - entityX, 2) + Math.pow(this.y - entityY, 2));
            return distance <= this.FIREBALL_RADIUS + entityRadius;
        }
        return false;
    }

    /**
     * Returns whether the fireball is destroyed.
     *
     * @return true if the fireball is destroyed; false otherwise.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * Gets the damage the fireball inflicts upon collision.
     *
     * @return The damage value of the fireball.
     */
    public float getDamage() {
        return FIREBALL_DAMAGE;
    }

    /**
     * Gets the radius of the fireball for collision detection purposes.
     *
     * @return The radius of the fireball.
     */
    public float getRadius() {
        return FIREBALL_RADIUS;
    }

    /**
     * Gets the x-coordinate of the fireball.
     *
     * @return The x-coordinate of the fireball.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the fireball.
     *
     * @return The y-coordinate of the fireball.
     */
    public int getY() {
        return y;
    }

    /**
     * Determines if the fireball has collided with a given collidable entity.
     *
     * @param entity The entity to check collision with.
     * @return true if the fireball collided with the entity; false otherwise.
     */
    public boolean hasCollided(Collidable entity) {
        if (entity == null) return false;

        float distance = (float) Math.sqrt(Math.pow(this.getX() - entity.getX(), 2) + Math.pow(this.getY() - entity.getY(), 2));
        return distance <= (this.getRadius() + entity.getRadius());
    }
}
