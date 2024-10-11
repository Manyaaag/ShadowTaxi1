

import bagel.Image;
import bagel.Input;
import bagel.Keys;
import java.util.Properties;

public class Taxi implements Collidable {
    private final Image IMAGE;
    private final int SPEED_X;
    private final float RADIUS;
    private float DAMAGE_POINTS = 100.0f;
    private float health = 100;
    protected boolean isDestroyed;
    private int collisionTimeout;
    private int invincibilityFrames = 0; // For invincibility power-up
    //private Coin coinPower;
    private int x;
    private int y;
    private boolean isMovingY;
    private boolean isMovingX;
    private boolean isInvincible;
    protected final Trip[] TRIPS;
    private int tripCount;
    protected Coin coinPower;
    private Trip trip;

    public Taxi(int x, int y, int maxTripCount, Properties props) {
        this.x = x;
        this.y = y;
        this.SPEED_X = Integer.parseInt(props.getProperty("gameObjects.taxi.speedX"));
        this.IMAGE = new Image(props.getProperty("gameObjects.taxi.image"));
        this.RADIUS = Float.parseFloat(props.getProperty("gameObjects.taxi.radius"));
        this.isDestroyed = false;
        this.collisionTimeout = 0;
        TRIPS = new Trip[maxTripCount];
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

    public void setX(int x) {
        this.x = x;
    }

    public float getRadius() {
        return RADIUS;
    }

    public void update(Input input) {
        // if the taxi has coin power, apply the effect of the coin on the priority of the passenger
        // (See the logic in TravelPlan class)
        if (trip != null && coinPower != null) {
            TravelPlan tp = trip.getPassenger().getTravelPlan();
            int newPriority = tp.getPriority();
            if(!tp.getCoinPowerApplied()) {
                newPriority = coinPower.applyEffect(tp.getPriority());
            }
            if(newPriority < tp.getPriority()) {
                tp.setCoinPowerApplied();
            }
            tp.setPriority(newPriority);
        }

        if(input != null) {
            adjustToInputMovement(input);
        }

        if(trip != null && trip.hasReachedEnd()) {
            getTrip().end();
        }

        draw();

        // the flag of the current trip renders to the screen
        if(tripCount > 0) {
            Trip lastTrip = TRIPS[tripCount - 1];
            if(!lastTrip.getPassenger().hasReachedFlag()) {
                lastTrip.getTripEndFlag().update(input);
            }
        }

        if (input != null) adjustToInputMovement(input);
        if (!isDestroyed) draw();
        if (collisionTimeout > 0) collisionTimeout--;
        if (invincibilityFrames > 0) invincibilityFrames--; // Reduce invincibility duration
    }

    public void draw() {
        IMAGE.draw(x, y);
    }

    public boolean isMovingY() {
        return isMovingY;
    }

    public boolean isMovingX() {
        return isMovingX;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
        if(trip != null) {
            this.TRIPS[tripCount] = trip;
            tripCount++;
        }
    }

    public Trip getTrip() {
        return this.trip;
    }
    /**
     * Get the last trip from the list of trips.
     * @return Trip object
     */
    public Trip getLastTrip() {
        if (tripCount == 0) {
            return null;
        }
        return TRIPS[tripCount - 1];
    }

        public void adjustToInputMovement(Input input) {
        if (input.wasPressed(Keys.UP)) {
            isMovingY = true;
        } else if (input.wasReleased(Keys.UP)) {
            isMovingY = false;
        } else if (input.isDown(Keys.LEFT)) {
            x -= SPEED_X;
            isMovingX = true;
        } else if (input.isDown(Keys.RIGHT)) {
            x += SPEED_X;
            isMovingX = true;
        } else if (input.wasReleased(Keys.LEFT) || input.wasReleased(Keys.RIGHT)) {
            isMovingX = false;
        }
    }

    public void collectPower(Coin coin) {
        coinPower = coin;
    }





    public void takeDamage(float damage) {
        if (collisionTimeout == 0 && invincibilityFrames == 0) {
            health -= damage;
            if (health <= 0) {
                isDestroyed = true;
                // Render fire effect here if needed
            }
        }
    }

    public void activateInvincibility() {
        invincibilityFrames = 1000; // Makes taxi invincible for 1000 frames
    }

    public float getDamage() {
        return DAMAGE_POINTS;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }


//    public boolean hasCollided() {
//        //return collisionTimeout > 0;
//    }

    public boolean hasCollided(Collidable entity) {
        if (entity == null) return false;

        float distance = (float) Math.sqrt(Math.pow(this.getX() - entity.getX(), 2) + Math.pow(this.getY() - entity.getY(), 2));
        return distance <= (this.getRadius() + entity.getRadius());
    }

    @Override
    public void collide(Object entity) {
        if (entity instanceof Car) {
            collideWithCar((Car) entity);
        } else if (entity instanceof EnemyCar) {
            collideWithEnemyCar((EnemyCar) entity);
        } else if (entity instanceof Fireball) {
            collideWithFireball((Fireball) entity);
        }
    }

    // Method to handle collision with a Car
    public void collideWithCar(Car car) {
        if (isDestroyed || collisionTimeout > 0 || invincibilityFrames > 0 || car.hasCollided()) return;

        float distance = (float) Math.sqrt(Math.pow(this.x - car.getX(), 2) + Math.pow(this.y - car.getY(), 2));
        if (distance < this.RADIUS + car.getRadius()) {
            takeDamage(car.getDamage());
            car.takeDamage(DAMAGE_POINTS);
            collisionTimeout = 200;
            car.setCollisionTimeout(200);
            applyKnockback(car);
            // Render smoke effect for 20 frames, using smoke.png at the taxi’s coordinates
        }
    }

    // Method to handle collision with an EnemyCar
    public void collideWithEnemyCar(EnemyCar enemyCar) {
        if (isDestroyed || collisionTimeout > 0 || invincibilityFrames > 0 || enemyCar.isInvincible() || enemyCar.hasCollided()) return;

        float distance = (float) Math.sqrt(Math.pow(this.x - enemyCar.getX(), 2) + Math.pow(this.y - enemyCar.getY(), 2));
        if (distance < this.RADIUS + enemyCar.getRadius()) {
            takeDamage(enemyCar.getDamage());
            enemyCar.takeDamage(DAMAGE_POINTS);
            collisionTimeout = 200;
            enemyCar.setCollisionTimeout(200);
            applyKnockback(enemyCar);
        }
    }

    // Method to handle collision with a Fireball
    public void collideWithFireball(Fireball fireball) {
        if (isDestroyed || collisionTimeout > 0 || invincibilityFrames > 0) return;

        float distance = (float) Math.sqrt(Math.pow(this.x - fireball.getX(), 2) + Math.pow(this.y - fireball.getY(), 2));
        if (distance < this.RADIUS + fireball.getRadius()) {
            takeDamage(fireball.getDamage());
            collisionTimeout = 200;
            // Render smoke effect here as well, if needed
        }
    }

//    // Apply knockback effect for 10 frames when colliding with another car or enemy car
//    private void applyKnockback(Object entity) {
//        for (int i = 0; i < 10; i++) {
//            if (this.y < entity.getY()) {
//                this.y -= 1;
//                entity.setY(entity.getY() + 1);
//            } else {
//                this.y += 1;
//                entity.setY(entity.getY() - 1);
//            }
//        }
//    }

    private void applyKnockback(Object entity) {
        for (int i = 0; i < 10; i++) {
            if (entity instanceof Taxi) {
                Taxi taxi = (Taxi) entity;
                if (this.y < taxi.getY()) {
                    this.y -= 1;
                    taxi.setY(taxi.getY() + 1);
                } else {
                    this.y += 1;
                    taxi.setY(taxi.getY() - 1);
                }
            } else if (entity instanceof Car) {
                Car car = (Car) entity;
                if (this.y < car.getY()) {
                    this.y -= 1;
                    car.setY(car.getY() + 1);
                } else {
                    this.y += 1;
                    car.setY(car.getY() - 1);
                }
            } else if (entity instanceof EnemyCar) {
                EnemyCar enemyCar = (EnemyCar) entity;
                if (this.y < enemyCar.getY()) {
                    this.y -= 1;
                    enemyCar.setY(enemyCar.getY() + 1);
                } else {
                    this.y += 1;
                    enemyCar.setY(enemyCar.getY() - 1);
                }
            }
        }
    }

    /**
     * Calculate total earnings. (See how fee is calculated for each trip in Trip class.)
     * @return int, total earnings
     */
    public float calculateTotalEarnings() {
        float totalEarnings = 0;
        for(Trip trip : TRIPS) {
            if (trip != null) {
                totalEarnings += trip.getFee();
            }
        }
        return totalEarnings;
    }


}


