
import bagel.Image;
import java.util.Properties;
import java.util.Random;

/**
 * The EnemyCar class represents an enemy car in the game, capable of moving,
 * colliding with other game objects, and taking damage. It has properties such as
 * radius, damage, health, and collision behavior.
 */
public class EnemyCar implements Collidable {

    private final Properties PROPS;
    private final Image IMAGE;
    private final float RADIUS;
    private final float DAMAGE_POINTS;
    protected final int COLLISION_TIMEOUT = 200;
    private final int COLLISION_KNOCKBACK_FRAMES = 10;

    private int x, y;  // Car's current coordinates
    private int speedY; // Random speed between minSpeedY and maxSpeedY
    private float health;
    protected int collisionTimeout;
    private boolean isColliding;
    private boolean isDestroyed;
    public boolean invincible;

    private final int FIRE_RENDER_TIMEOUT_FRAMES = 20;
    private int fireRenderTimeout;

    /**
     * Constructs an EnemyCar instance with specified properties.
     *
     * @param props Properties to configure enemy car behavior and image.
     */
    public EnemyCar(Properties props) {
        this.PROPS = props;
        this.fireRenderTimeout = 0;
        this.IMAGE = new Image(String.format(PROPS.getProperty("gameObjects.enemyCar.image")));

        // Get car properties
        this.RADIUS = Float.parseFloat(PROPS.getProperty("gameObjects.enemyCar.radius"));
        this.DAMAGE_POINTS = Float.parseFloat(PROPS.getProperty("gameObjects.enemyCar.damage"));
        this.health = Float.parseFloat(PROPS.getProperty("gameObjects.enemyCar.health"));

        // Randomly choose the speed of the car between the provided range
        int minSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.enemyCar.minSpeedY"));
        int maxSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.enemyCar.maxSpeedY"));
        this.speedY = new Random().nextInt(maxSpeedY - minSpeedY + 1) + minSpeedY;

        // Randomly choose the x-coordinate from one of the lanes
        int[] lanes = {360, 480, 620};
        this.x = lanes[new Random().nextInt(lanes.length)];

        // Randomly choose the starting y-coordinate (-50 or 768)
        int[] yCoords = {-50, 768};
        this.y = yCoords[new Random().nextInt(yCoords.length)];

        // Initialize other properties
        this.isColliding = false;
        this.isDestroyed = false;
    }

    /**
     * Updates the car's position and checks for collisions.
     */
    public void update() {
        if (isDestroyed) return;

        if (collisionTimeout > 0) {
            collisionTimeout--;
            if (collisionTimeout <= COLLISION_KNOCKBACK_FRAMES) {
                y += speedY * (collisionTimeout > 0 ? -1 : 1);
            }
            if (collisionTimeout == 0) {
                resetSpeed();
            }
        } else {
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
     * Applies knockback effect to the car upon collision.
     *
     * @param entity The entity to apply knockback against.
     */
    private void applyKnockback(Object entity) {
        if (entity instanceof Taxi) {
            Taxi taxi = (Taxi) entity;
            for (int i = 0; i < 10; i++) {
                if (this.y < taxi.getY()) {
                    this.y -= 1;
                    taxi.setY(taxi.getY() + 1);
                } else {
                    this.y += 1;
                    taxi.setY(taxi.getY() - 1);
                }
            }
        }
    }

    /**
     * Handles collision with other game objects.
     */
    public void collide(Object entity) {
        if (isDestroyed || collisionTimeout > 0) {
            return;  // Skip collisions if the car is already destroyed or in timeout
        }

        if (entity instanceof Taxi) {
            Taxi taxi = (Taxi) entity;
            if (!taxi.isDestroyed() && hasCollided((Collidable) taxi)) {
                float entityDamage = taxi.getDamage();
                takeDamage(entityDamage);
                applyKnockback(taxi);
                taxi.takeDamage(DAMAGE_POINTS * 100);
                collisionTimeout = COLLISION_TIMEOUT;
            }
        } else if (entity instanceof Car) {
            Car car = (Car) entity;
            float entityDamage = car.getDamage();
            takeDamage(entityDamage);
            car.takeDamage(DAMAGE_POINTS);
            collisionTimeout = COLLISION_TIMEOUT;

        } else if (entity instanceof EnemyCar) {
            EnemyCar enemyCar = (EnemyCar) entity;
            float entityDamage = enemyCar.getDamage();
            takeDamage(entityDamage);
            enemyCar.takeDamage(DAMAGE_POINTS);
            collisionTimeout = COLLISION_TIMEOUT;

        } else if (entity instanceof Fireball) {
            Fireball fireball = (Fireball) entity;
            float entityDamage = fireball.getDamage();
            takeDamage(entityDamage);
            collisionTimeout = COLLISION_TIMEOUT;
        }
    }

    /**
     * Takes damage and checks if the car is destroyed.
     *
     * @param damage The amount of damage to apply.
     */
    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            isDestroyed = true;
            fireRenderTimeout = 0;
            if (fireRenderTimeout < FIRE_RENDER_TIMEOUT_FRAMES) {
                System.out.println("hi");
                Image fireImage = new Image("res/fire.png");
                fireImage.draw(this.x, this.y);
                fireRenderTimeout++;
            }
        }
    }

    /**
     * Resets the car's speed after a collision.
     */
    private void resetSpeed() {
        int minSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.enemyCar.minSpeedY"));
        int maxSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.enemyCar.maxSpeedY"));
        this.speedY = new Random().nextInt(maxSpeedY - minSpeedY + 1) + minSpeedY;
    }

    /**
     * Retrieves the car's damage points for collision calculations.
     *
     * @return The car's damage points.
     */
    public float getDamage() {
        return DAMAGE_POINTS * 100;
    }

    @Override
    public void setX(int x) {
        // Implementation not required for this class
    }

    /**
     * Retrieves the car's collision radius for collision detection.
     *
     * @return The car's collision radius.
     */
    public float getRadius() {
        return RADIUS;
    }

    @Override
    public void setInvincible(int frames) {
        // Implementation not required for this class
    }

    /**
     * Retrieves the car's x-coordinate.
     *
     * @return The car's x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the car's y-coordinate.
     *
     * @return The car's y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the car's y-coordinate.
     *
     * @param y The y-coordinate to set.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Retrieves the car's collision timeout value.
     *
     * @return The collision timeout duration.
     */
    public int getCollisionTimeout() {
        return COLLISION_TIMEOUT;
    }

    /**
     * Sets the car's collision timeout to a specified value.
     *
     * @param collisionTimeout The timeout value to set.
     */
    public void setCollisionTimeout(int collisionTimeout) {
        this.collisionTimeout = collisionTimeout;
    }

    /**
     * Checks if the car is invincible.
     *
     * @return true if the car is invincible; otherwise, false.
     */
    protected boolean isInvincible() {
        return invincible;
    }

    /**
     * Checks if the car is currently colliding with another object.
     *
     * @return true if the car is colliding; otherwise, false.
     */
    boolean hasCollided() {
        return isColliding;
    }

    /**
     * Determines if the car has collided with another collidable entity.
     *
     * @param entity The entity to check for collision.
     * @return true if a collision occurred; otherwise, false.
     */
    public boolean hasCollided(Collidable entity) {
        if (entity == null) return false;

        float distance = (float) Math.sqrt(Math.pow(this.getX() - entity.getX(), 2) + Math.pow(this.getY() - entity.getY(), 2));
        return distance <= (this.getRadius() + entity.getRadius());
    }
}








