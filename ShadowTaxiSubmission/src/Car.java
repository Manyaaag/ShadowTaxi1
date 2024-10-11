
import bagel.Image;

import java.util.Properties;
import java.util.Random;

public class Car implements Collidable{

    private final Properties PROPS;
    private final Image IMAGE;
    private final float RADIUS;
    private final float DAMAGE_POINTS;
    private final int COLLISION_TIMEOUT = 200;
    private final int COLLISION_KNOCKBACK_FRAMES = 10;

    private int x, y;
    private int speedY;
    private float health;
    private int collisionTimeout;
    private boolean isDestroyed;
    private boolean isInvincible;

    public Car(Properties props) {
        this.PROPS = props;
        int carType = new Random().nextInt(Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.types"))) + 1;
        this.IMAGE = new Image(String.format(PROPS.getProperty("gameObjects.otherCar.image"), carType));
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

    public void draw() {
        if (!isDestroyed) {
            IMAGE.draw(x, y);
        }
    }



//    private void applyKnockback(Taxi taxi) {
//        for (int i = 0; i < 10; i++) {
//            if (this.y < taxi.getY()) {
//                this.y -= 1;
//                taxi.setY(taxi.getY() + 1);
//            } else {
//                this.y += 1;
//                taxi.setY(taxi.getY() - 1);
//            }
//        }
//    }
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


    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            isDestroyed = true;
            // Render fire effect if needed
        }
    }

    public void setCollisionTimeout(int timeout) {
        this.collisionTimeout = timeout;
    }


    private void resetSpeed() {
        int minSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.minSpeedY"));
        int maxSpeedY = Integer.parseInt(PROPS.getProperty("gameObjects.otherCar.maxSpeedY"));
        this.speedY = new Random().nextInt(maxSpeedY - minSpeedY + 1) + minSpeedY;
    }

    public float getDamage() {
        return DAMAGE_POINTS;
    }

    public float getRadius() {
        return RADIUS;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public boolean hasCollided() {
        return collisionTimeout > 0;
    }


/*
    @Override

    public void collide(Object entity) {
        if (entity instanceof Taxi) {
            collide((Taxi) entity);
        } else if (entity instanceof Car) {
            collideWithOtherCar((Car) entity);
        } else if (entity instanceof EnemyCar) {
            collideWithEnemyCar((EnemyCar) entity);
        } else if (entity instanceof Fireball) {
            collideWithFireball((Fireball) entity);
        }
    }

 */
    public void collide(Object entity) {
        if (entity instanceof Taxi) {
            Taxi taxi = (Taxi) entity;
            // Handle collision logic directly without calling another method
            if (!taxi.isDestroyed() && hasCollided((Collidable) taxi)) {
                takeDamage(taxi.getDamage());
                taxi.takeDamage(getDamage());
                applyKnockback(taxi);
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





    // Method to handle collision with another Car
    public void collideWithOtherCar(Car otherCar) {
        if (isDestroyed || collisionTimeout > 0 || otherCar.collisionTimeout > 0) return;

        float distance = (float) Math.sqrt(Math.pow(this.x - otherCar.getX(), 2) + Math.pow(this.y - otherCar.getY(), 2));
        if (distance < this.RADIUS + otherCar.getRadius()) {
            this.takeDamage(otherCar.getDamage());
            otherCar.takeDamage(this.DAMAGE_POINTS);
            this.collisionTimeout = COLLISION_TIMEOUT;
            otherCar.collisionTimeout = COLLISION_TIMEOUT;
            applyKnockback(otherCar);
        }
    }

    // Method to handle collision with an EnemyCar
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

    // Method to handle collision with a Fireball
    public void collideWithFireball(Fireball fireball) {
        if (isDestroyed || collisionTimeout > 0) return;

        float distance = (float) Math.sqrt(Math.pow(this.x - fireball.getX(), 2) + Math.pow(this.y - fireball.getY(), 2));
        if (distance < this.RADIUS + fireball.getRadius()) {
            this.takeDamage(fireball.getDamage());
            collisionTimeout = COLLISION_TIMEOUT;
            // Apply additional effects if needed
        }
    }

    @Override
    public boolean hasCollided(Collidable entity) {
        if (entity == null) return false;

        float distance = (float) Math.sqrt(Math.pow(this.getX() - entity.getX(), 2) + Math.pow(this.getY() - entity.getY(), 2));
        return distance <= (this.getRadius() + entity.getRadius());
    }


}



