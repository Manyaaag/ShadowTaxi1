
import bagel.Image;
import java.util.Properties;

public class Fireball implements Collidable {

    private final Image FIREBALL_IMAGE;
    private final float FIREBALL_RADIUS;
    private final float FIREBALL_DAMAGE;
    private final int FIREBALL_SPEED;

    protected int x, y;  // Fireball's current coordinates
    private boolean isDestroyed;

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
     * Update the fireball's position and check for collisions or reaching the top of the screen.
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
     * Renders the fireball on the screen.
     */
//    public void draw() {
//        if (!isDestroyed) {
//            FIREBALL_IMAGE.draw(x, y);
//        }
//    }
    public void draw() {
        FIREBALL_IMAGE.draw(x, y);
    }

    /**
     * Handles collision with game entities and applies damage.
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
     * Checks whether the fireball collides with a given entity.
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
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

    // Getter for fireball damage
    public float getDamage() {
        return FIREBALL_DAMAGE;
    }

    // Getter for fireball radius (for collision detection)
    public float getRadius() {
        return FIREBALL_RADIUS;
    }

    @Override
    public void setInvincible(int frames) {

    }

    public int getX() {
        return x;
    }

    // Getter for y-coordinate
    public int getY() {
        return y;
    }


    public boolean hasCollided(Collidable entity) {
        if (entity == null) return false;

        float distance = (float) Math.sqrt(Math.pow(this.getX() - entity.getX(), 2) + Math.pow(this.getY() - entity.getY(), 2));
        return distance <= (this.getRadius() + entity.getRadius());
    }

}
