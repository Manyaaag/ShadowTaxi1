
import bagel.Image;
import java.util.Properties;
import java.util.Random;

/**
 * Represents a Car in the game, which can move, detect collisions, and take damage.
 * The Car has properties such as health, damage points, radius, and speed.
 */
public class Car implements Collidable {

    private final Properties PROPS;
    private final Image IMAGE;
    protected final Image smokeImage;
    private final float RADIUS;
    private final float DAMAGE_POINTS;
    private final int COLLISION_TIMEOUT = 200;
    private final int COLLISION_KNOCKBACK_FRAMES = 10;

    private int x, y;  // Car's current coordinates
    private int speedY;  // Car's movement speed
    private float health;
    private int collisionTimeout;
    private boolean isDestroyed;
    private boolean isInvincible;

    /**
     * Constructs a Car instance with properties loaded from a given properties file.
     *
     * @param props Properties object containing configurations for car attributes.
     */
    public Car(Properties props) {
        this.PROPS = props;
        int carType = new Random().nextInt(Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.types"))) + 1;
        this.IMAGE = new Image(String.format(PROPS.getProperty("gameObjects.otherCar.image"), carType));
        this.smokeImage = new Image(String.format(PROPS.getProperty("gameObjects.smoke.image")));
        this.RADIUS = Float.parseFloat(PROPS.getProperty("gameObjects.otherCar.radius"));
        this.DAMAGE_POINTS = Float.parseFloat(PROPS.getProperty("gameObjects.otherCar.damage"));
        this.health = Float.parseFloat(PROPS.getProperty("gameObjects.otherCar.health"));
        int minSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.minSpeedY"));
        int maxSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.maxSpeedY"));
        this.speedY = new Random().nextInt(maxSpeedY - minSpeedY + 1) + minSpeedY;
        int[] lanes = {360, 480, 620};
        this.x = lanes[new Random().nextInt(lanes.length)];
        int[] yCoords = {-50, 768};
        this.y = yCoords[new Random().nextInt(yCoords.length)];
        this.collisionTimeout = 0;
        this.isDestroyed = false;
    }

    /**
     * Updates the car's position and collision state.
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
     * Renders the car on the screen if it is not destroyed.
     */
    public void draw() {
        if (!isDestroyed) {
            IMAGE.draw(x, y);
        }
//        // Render fire if the health is 0 and fire timeout has not expired
//        if (health <= 0) {
//            if (fireRenderTimeout < FIRE_RENDER_TIMEOUT_FRAMES) {
//                System.out.println("Car Health: " + health + " - Rendering fire.");
//                fireImage.draw(this.x, this.y);
//                fireRenderTimeout++;
//            }
//
    }

    /**
     * Applies knockback to the car based on its collision with another entity.
     *
     * @param entity The entity with which this car has collided.
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
        } else if (entity instanceof EnemyCar) {
            EnemyCar enemyCar = (EnemyCar) entity;
            for (int i = 0; i < 10; i++) {
                if (this.y < enemyCar.getY()) {
                    this.y -= 1;
                    enemyCar.setY(enemyCar.getY() + 1);
                } else {
                    this.y += 1;
                    enemyCar.setY(enemyCar.getY() - 1);
                }
            }
        } else if (entity instanceof Car) {
            Car otherCar = (Car) entity;
            for (int i = 0; i < 10; i++) {
                if (this.y < otherCar.getY()) {
                    this.y -= 1;
                    otherCar.setY(otherCar.getY() + 1);
                } else {
                    this.y += 1;
                    otherCar.setY(otherCar.getY() - 1);
                }
            }
        }
    }

    /**
     * Reduces the car's health by a specified amount of damage.
     *
     * @param damage The damage amount to subtract from the car's health.
     */
    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            isDestroyed = true;

            // Render fire effect if needed
        }
    }

    /**
     * Sets a timeout for the car's collision state.
     *
     * @param timeout Duration for which the car is in collision timeout.
     */
    public void setCollisionTimeout(int timeout) {
        this.collisionTimeout = timeout;
    }

    /**
     * Resets the car's speed after a collision timeout.
     */
    private void resetSpeed() {
        int minSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.minSpeedY"));
        int maxSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.maxSpeedY"));
        this.speedY = new Random().nextInt(maxSpeedY - minSpeedY + 1) + minSpeedY;
    }

    /**
     * Gets the car's damage points.
     *
     * @return The amount of damage the car can inflict.
     */
    public float getDamage() {
        return DAMAGE_POINTS;
    }

    /**
     * Gets the radius of the car.
     *
     * @return The car's radius.
     */
    public float getRadius() {
        return RADIUS;
    }

    @Override
    public void setInvincible(int frames) {
        // Implementation left blank as per game requirements
    }

    /**
     * Gets the x-coordinate of the car.
     *
     * @return The car's x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the car.
     *
     * @return The car's y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the car.
     *
     * @param y The y-coordinate to set.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets the x-coordinate of the car.
     *
     * @param x The x-coordinate to set.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Checks if the car is currently in a collision timeout.
     *
     * @return true if the car is in collision timeout; false otherwise.
     */
    public boolean hasCollided() {
        return collisionTimeout > 0;
    }

    /**
     * Handles collision logic with different entities in the game.
     *
     * @param entity The entity with which this car collides.
     */
    public void collide(Object entity) {
        if (entity instanceof Taxi) {
            Taxi taxi = (Taxi) entity;
            // Handle collision logic directly without calling another method
            if (!taxi.isDestroyed() && hasCollided((Collidable) taxi)) {
                takeDamage(taxi.getDamage()*100); //////MADE CHANGES OT THIS, HAS AN AFFECT IN THE GAME
                //taxi.takeDamage(getDamage());
                taxi.takeDamage(DAMAGE_POINTS*100);
                applyKnockback(taxi);
                smokeImage.draw(this.x, this.y);
            }
        } else if (entity instanceof Car) {
            Car otherCar = (Car) entity;
            // Handle collision directly for another Car
            if (hasCollided((Collidable) otherCar)) {
                takeDamage(otherCar.getDamage());
                otherCar.takeDamage(getDamage());
                applyKnockback(otherCar);
            }
        } else if (entity instanceof EnemyCar) {
            EnemyCar enemyCar = (EnemyCar) entity;
            // Handle collision directly for EnemyCar
            if (hasCollided((Collidable) enemyCar)) {
                takeDamage(enemyCar.getDamage());
                enemyCar.takeDamage(getDamage());
                applyKnockback(enemyCar);
            }
        } else if (entity instanceof Fireball) {
            Fireball fireball = (Fireball) entity;
            // Handle collision directly for Fireball
            if (hasCollided((Collidable) fireball)) {
                takeDamage(fireball.getDamage());
                //fireball.setDestroyed();
            }
        }
    }

    /**
     * Handles collision with another Car.
     *
     * @param otherCar The Car with which this car has collided.
     */
    public void collideWithOtherCar(Car otherCar) {
        if (isDestroyed || collisionTimeout > 0 || otherCar.collisionTimeout > 0) return;

        float distance = (float) Math.sqrt(Math.pow(this.x - otherCar.getX(), 2) + Math.pow(this.y - otherCar.getY(), 2));
        if (distance < this.RADIUS + otherCar.getRadius()) {
            this.takeDamage(otherCar.getDamage());
            otherCar.takeDamage(this.DAMAGE_POINTS*100);
            this.collisionTimeout = COLLISION_TIMEOUT;
            otherCar.collisionTimeout = COLLISION_TIMEOUT;
            applyKnockback(otherCar);
        }
    }

    /**
     * Handles collision with an EnemyCar.
     *
     * @param enemyCar The EnemyCar with which this car has collided.
     */
    public void collideWithEnemyCar(EnemyCar enemyCar) {
        if (isDestroyed || collisionTimeout > 0 || enemyCar.isInvincible() || enemyCar.getCollisionTimeout() > 0) return;

        float distance = (float) Math.sqrt(Math.pow(this.x - enemyCar.getX(), 2) + Math.pow(this.y - enemyCar.getY(), 2));
        if (distance < this.RADIUS + enemyCar.getRadius()) {
            this.takeDamage(enemyCar.getDamage());
            enemyCar.takeDamage(this.DAMAGE_POINTS);
            this.collisionTimeout = COLLISION_TIMEOUT;
            enemyCar.setCollisionTimeout(COLLISION_TIMEOUT);
            applyKnockback(enemyCar);
        }
    }

    /**
     * Handles collision with a Fireball.
     *
     * @param fireball The Fireball with which this car has collided.
     */
    public void collideWithFireball(Fireball fireball) {
        if (isDestroyed || collisionTimeout > 0) return;

        float distance = (float) Math.sqrt(Math.pow(this.x - fireball.getX(), 2) + Math.pow(this.y - fireball.getY(), 2));
        if (distance < this.RADIUS + fireball.getRadius()) {
            this.takeDamage(fireball.getDamage());
            collisionTimeout = COLLISION_TIMEOUT;
        }
    }

    @Override
    public boolean hasCollided(Collidable entity) {
        if (entity == null) return false;

        float distance = (float) Math.sqrt(Math.pow(this.getX() - entity.getX(), 2) + Math.pow(this.getY() - entity.getY(), 2));
        return distance <= (this.getRadius() + entity.getRadius());
    }
}


