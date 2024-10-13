
//DO ENEMY CAR AND FIREBALL LAST

import bagel.Image;

import java.util.Properties;
import java.util.Random;

public class EnemyCar {

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



    public EnemyCar(Properties props) {
        this.PROPS = props;

        this.fireRenderTimeout = 0;
        this.IMAGE = new Image(String.format(PROPS.getProperty("gameObjects.enemyCar.image")));

        // Get car properties
        this.RADIUS = Float.parseFloat(PROPS.getProperty("gameObjects.enemyCar.radius"));
        //this.DAMAGE_POINTS = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.damage"));
        this.DAMAGE_POINTS = Float.parseFloat(PROPS.getProperty("gameObjects.enemyCar.damage"));  // Parse as float
        //this.health = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.health"));
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
        //this.collisionTimeout = 0;
        this.isColliding = false;
        this.isDestroyed = false;
    }

    /**
     * Updates the car's position and checks for collisions.
     */





    /*
    public void update() {
        if (isDestroyed) {
            return;  // Do not update the car if it is destroyed
        }

        // If the car is colliding, handle collision behavior
        if (collisionTimeout > 0) {
            collisionTimeout--;
//            if (collisionTimeout <= COLLISION_KNOCKBACK_FRAMES) {
//                // Knockback logic - move the car away from the collided object
//                y += speedY * (collisionTimeout > 0 ? -1 : 1);
//            }

            // Apply knockback effect only during the first COLLISION_KNOCKBACK_FRAMES
            if (collisionTimeout > COLLISION_TIMEOUT - COLLISION_KNOCKBACK_FRAMES) {
                y += speedY > 0 ? -1 : 1;  // Move up or down based on speed direction
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
/*
    public void collide(Object entity) {
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
    public void collide(Object entity) {
        if (isDestroyed || collisionTimeout > 0) {
            return;  // Skip collisions if the car is already destroyed or in timeout
        }

        if (entity instanceof Taxi) {
            Taxi taxi = (Taxi) entity;
            //if (this.hasCollided(taxi)) {  // Ensure this check is accurate
            if (!taxi.isDestroyed() && hasCollided((Collidable) taxi)) {
                float entityDamage = taxi.getDamage();
                takeDamage(entityDamage);
                applyKnockback(taxi);
                taxi.takeDamage(DAMAGE_POINTS);
                collisionTimeout = COLLISION_TIMEOUT;
            }
        //collisionTimeout = COLLISION_TIMEOUT; // Reset collision timeout
//            float entityDamage = taxi.getDamage();
//            takeDamage(entityDamage);
//            /////isDestroyed = true;
//            taxi.takeDamage(DAMAGE_POINTS*100);

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
            //fireball.takeDamage(DAMAGE_POINTS);
        }

        // Start collision timeout
        //collisionTimeout = COLLISION_TIMEOUT;
    }





    /**
     * Takes damage and checks if the car is destroyed.
     */
    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            isDestroyed = true;
            fireRenderTimeout = 0;
//            if (fireRenderTimeout < FIRE_RENDER_TIMEOUT_FRAMES) {
//                System.out.println("hi");
//                Image fireImage = new Image("res/fire.png");
//                fireImage.draw(this.x, this.y);
//                fireRenderTimeout++;
//            }
            // Render fire effect or similar visual indicator (not implemented here)
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
    public void setY(int y) {
        this.y = y;
    }
    public int getCollisionTimeout() {
        return COLLISION_TIMEOUT;
    }

    public void setCollisionTimeout(int COLLISION_TIMEOUT) {
        this.collisionTimeout = collisionTimeout;
    }

    //public void setCollisionTimeout() {
        //this.COLLISION_TIMEOUT = collisionTimeout;
    //}

    protected boolean isInvincible() {
        return invincible;
    }
    boolean hasCollided () {
        return isColliding;
    }


    public boolean hasCollided(Collidable entity) {
        if (entity == null) return false;

        float distance = (float) Math.sqrt(Math.pow(this.getX() - entity.getX(), 2) + Math.pow(this.getY() - entity.getY(), 2));
        return distance <= (this.getRadius() + entity.getRadius());
    }

}







