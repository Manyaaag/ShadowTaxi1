/*
import bagel.Image;
import bagel.Input;

import java.util.Properties;

public abstract class Objects {
    protected int x;
    protected int y;
    protected Image image;
    protected int speedY;
    protected float RADIUS;
    protected Properties PROPS;

    protected float health;
    protected boolean invincible = false;


    public Objects(int x, int y, String imagePath, int speedY, float radius) {
        this.x = x;
        this.y = y;
        this.image = new Image(imagePath);
        this.speedY = speedY;
        this.RADIUS = radius;
        this.health = 100; // Default health, you can override in subclasses
    }

    // Implement Collidable method to check for collision
    @Override
    public boolean hasCollided(Objects other) {
        float collisionDistance = this.RADIUS + other.getRadius();
        float currDistance = (float) Math.sqrt(Math.pow(this.x - other.getX(), 2) + Math.pow(this.y - other.getY(), 2));
        return currDistance <= collisionDistance;
    }

    // Abstract method to handle what happens during a collision
    @Override
    public abstract void onCollision(Objects other);

    // Abstract method for updating the object (specific implementation will vary)
    public abstract void update(Input input);

    // Move the object in y-direction based on speed
    public void move() {
        this.y += speedY;
    }

    // Draw the object on screen
    public void draw() {
        image.draw(x, y);
    }

    // Method to handle damage (can accept both int and float)
    public void takeDamage(float damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public float getRadius() {

        return RADIUS;

    }



    public float getHealth() {

        return health;

    }



    // Check if the object is invincible
    public boolean isInvincible() {
        return invincible;
    }

    // Set invincibility status
    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }


    // Getters and Setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHealth(float health) {
        this.health = health;
    }
}

 */
import bagel.Image;
import bagel.Input;

public abstract class Objects {
    protected int x;
    protected int y;
    protected Image image;
    protected int speedY;  // Make final if unchanging
    protected float RADIUS;  // Make final if unchanging
    protected float health;

    public Objects(int x, int y, String imagePath, int speedY, float radius) {
        this.x = x;
        this.y = y;
        this.image = new Image(imagePath);
        this.speedY = speedY;
        this.RADIUS = radius;
        this.health = 100; // Default health, can be overridden in subclasses
    }

    public boolean hasCollided(Objects other) {
        float collisionDistance = this.RADIUS + other.getRadius();
        float currDistance = (float) Math.sqrt(Math.pow(this.x - other.getX(), 2) + Math.pow(this.y - other.getY(), 2));
        return currDistance <= collisionDistance;
    }

    public abstract void onCollision(Objects other);
    public abstract void update(Input input);

    public void move() {
        this.y += speedY;
    }

    public void draw() {
        image.draw(x, y);
    }

    public void takeDamage(float damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    // Getters
    public float getRadius() {
        return RADIUS;
    }

    public float getHealth() {
        return health;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Setters
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHealth(float health) {
        this.health = health;
    }
}

