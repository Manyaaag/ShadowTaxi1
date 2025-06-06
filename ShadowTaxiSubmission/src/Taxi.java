import bagel.Image;
import bagel.Input;
import bagel.Keys;
import java.util.Properties;

import java.util.Random;

/**
 * Class representing a Taxi in the game, implementing movement, collision handling, health, and power-ups.
 */

public class Taxi implements Collidable {

    private final Properties PROPS;
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
    public boolean isInvincible;
    protected final Trip[] TRIPS;
    private int tripCount;
    protected Coin coinPower;
    private Trip trip;


    private static final int SMOKE_RENDER_TIMEOUT_FRAMES = 20;  // Smoke render duration
    private static final int FIRE_RENDER_TIMEOUT_FRAMES = 20;
    private int smokeRenderTimeout;
    private int fireRenderTimeout;

    private static boolean isNewTaxiCreated = false;
    private boolean isNewTaxiActive = false; // Flag to control the new taxi after driver entry
    private Taxi newTaxiInstance = null;
    private Driver driver;
    private boolean shouldScrollDamagedTaxi = false;

    public boolean isOriginalTaxi;

    /**
     * Constructs a Taxi with specified position, maximum trip count, and properties.
     *
     * @param x           Initial x-coordinate of the Taxi.
     * @param y           Initial y-coordinate of the Taxi.
     * @param maxTripCount Maximum number of trips allowed for the Taxi.
     * @param props       Properties configuration for the Taxi.
     */

    public Taxi(int x, int y, int maxTripCount, Properties props) {
        this.PROPS = props;
        this.x = x;
        this.y = y;
        this.SPEED_X = Integer.parseInt(props.getProperty("gameObjects.taxi.speedX"));
        this.IMAGE = new Image(props.getProperty("gameObjects.taxi.image"));
        this.RADIUS = Float.parseFloat(props.getProperty("gameObjects.taxi.radius"));
        this.isDestroyed = false;
        TRIPS = new Trip[maxTripCount];
        this.collisionTimeout = 0;
        this.smokeRenderTimeout = 0;
        this.fireRenderTimeout = 0;

        this.isOriginalTaxi = true;
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

    // Getter for health
    public float getHealth() {
        return health;
    }

    public float getRadius() {
        return RADIUS;
    }

    @Override
    public void setInvincible(int frames) {
        invincibilityFrames = frames;

    }

    /**
     * Updates the Taxi's state, handling input, movement, and collision.
     *
     * @param input  The game input instance.
     * @param driver The driver associated with the Taxi.
     */

    public void update(Input input, Driver driver) {
        this.driver = driver;

        // Case 1: The original taxi is destroyed, triggering new taxi creation
        if (health <= 0) {
            isDestroyed = true;
            drawDamagedTaxi(); // Render the damaged taxi

            if (!isNewTaxiCreated) {
                stopAndEjectDriver(driver);
                spawnNewTaxi(driver);  // Create the new taxi once
                isNewTaxiCreated = true;
            }
        }

        // Case 2: The new taxi is created, and the driver is not inside
        if (isNewTaxiCreated && !isNewTaxiActive) {
            //health = 100;
            if (input != null && input.isDown(Keys.UP)) {
                newTaxiInstance.y += 5;  // Move the new taxi down by 5 pixels per frame when UP is pressed

            }
            newTaxiInstance.draw();  // Draw the new taxi at its updated position

        }

        // Case 1: Control the new taxi once it’s active
        if (isNewTaxiCreated && isNewTaxiActive && newTaxiInstance != null) {
            System.out.println("Controlling new taxi...");
            newTaxiInstance.adjustToInputMovement(input);  // Allow movement for new taxi
            newTaxiInstance.draw();
        }
        // Case 2: Driver has not yet entered new taxi
        else if (isNewTaxiCreated && !isNewTaxiActive) {
            driver.updateWithTaxi(input, newTaxiInstance);  // Allow driver movement towards new taxi
            newTaxiInstance.draw();
        }
        // Case 3: Control the original taxi if not destroyed and new taxi doesn’t exist
        else if (!isDestroyed && !isNewTaxiCreated) {
            adjustToInputMovement(input);
            draw();
        }


        // Move the damaged taxi downward only if up arrow key is pressed
        if (shouldScrollDamagedTaxi && input != null && input.isDown(Keys.UP)) {
            y += 5;
            drawDamagedTaxi();
        }

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

        if(trip != null && trip.hasReachedEnd()) {
            getTrip().end();
        }

        // the flag of the current trip renders to the screen
        if(tripCount > 0) {
            Trip lastTrip = TRIPS[tripCount - 1];
            if(!lastTrip.getPassenger().hasReachedFlag()) {
                lastTrip.getTripEndFlag().update(input);
            }
        }


        // Handle invincibility and collision timeout
        if (collisionTimeout > 0) collisionTimeout--;
        if (invincibilityFrames > 0) invincibilityFrames--;
    }

    /**
     * Draws the Taxi image on the screen if it is not destroyed.
     */

    public void draw() {
        if (health > 0 && !isDestroyed) {
            IMAGE.draw(this.x, this.y);
        }
    }

    /**
     * Activates the new Taxi, allowing it to be controlled.
     */
    public void activate() {
        isNewTaxiActive = true;
        System.out.println("New taxi now active");
    }
    /**
     * Stops and ejects the driver from the Taxi.
     *
     * @param driver The driver to be ejected.
     */


    private void stopAndEjectDriver(Driver driver) {
        // Eject driver at (x - 50, y)
        driver.setX(this.x - 50);
        driver.setY(this.y);
        driver.setInTaxi(false);
        shouldScrollDamagedTaxi = true;
    }
    /**
     * Draws the damaged Taxi on the screen, including a fire effect if applicable.
     */

    private void drawDamagedTaxi() {
        Image damagedImage = new Image("res/taxiDamaged.png");
        damagedImage.draw(this.x, this.y);

        if (fireRenderTimeout < FIRE_RENDER_TIMEOUT_FRAMES) {
            Image fireImage = new Image("res/fire.png");
            fireImage.draw(this.x, this.y + fireRenderTimeout * 5);
            fireRenderTimeout++;
        }
    }


    public boolean isMovingY() {
        return isMovingY;
    }

    public boolean isMovingX() {
        return isMovingX;
    }
    /**
     * Sets the current trip for the Taxi.
     *
     * @param trip The Trip to associate with the Taxi.
     */

    public void setTrip(Trip trip) {
        this.trip = trip;
        if(trip != null) {
            this.TRIPS[tripCount] = trip;
            tripCount++;
        }
    }
    /**
     * Retrieves the current trip of the Taxi.
     *
     * @return The current Trip associated with the Taxi.
     */

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
    /**
     * Adjusts Taxi movement based on user input.
     *
     * @param input The input used to adjust movement.
     */

    public void adjustToInputMovement(Input input) {
        if (isNewTaxiCreated && !isNewTaxiActive) {
            return;
        } else if (isNewTaxiActive || !isDestroyed) {
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

    }
    /**
     * Collects a coin power-up, applying its effect to the Taxi.
     *
     * @param coin The Coin power-up to collect.
     */

    public void collectPower(Coin coin) {
        coinPower = coin;
    }
    /**
     * Spawns a new Taxi at random coordinates.
     *
     * @param driver The Driver associated with the new Taxi.
     */

    // Method to spawn a new taxi at random coordinates
    public void spawnNewTaxi(Driver driver) {
        Random random = new Random();
        // Randomly select x-coordinate (either 360 or 620 for specified lanes)
        int newX = random.nextBoolean() ? 360 : 620;
        // Randomly select y-coordinate between 200 and 400
        int newY = random.nextInt(201) + 200;

        // Initialize the new taxi with randomized coordinates
        newTaxiInstance = new Taxi(newX, newY, TRIPS.length, PROPS);
        newTaxiInstance.isOriginalTaxi = false;
        //Taxi newTaxi = new Taxi(newX, newY, TRIPS.length, PROPS);

        // Logic to re-enter taxi if close enough
        if (driver.calculateDistance(newTaxiInstance) <= driver.getTaxiInRadius()) {
            driver.setInTaxi(true);
            driver.moveWithTaxi(newTaxiInstance);
        }
        //System.out.println("New Taxi Created at coordinates: (" + newX + ", " + newY + ")");
    }

    /**
     * Applies damage to the Taxi and checks if it is destroyed.
     *
     * @param damage The damage amount to apply.
     */

    public void takeDamage(float damage) {
        if (collisionTimeout == 0 && invincibilityFrames == 0) {
            health -= damage;
            if (health <= 0) {
                health = 0;
                isDestroyed = true;
                // Render fire effect here if needed
            }
            collisionTimeout = 200;
        }
    }


    /**
     * Activates invincibility for the Taxi for a set number of frames.
     */

    public void activateInvincibility() {
        invincibilityFrames = 1000; // Makes taxi invincible for 1000 frames
    }

    public float getDamage() {
        return DAMAGE_POINTS;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
    /**
     * Checks if the Taxi has collided with another entity.
     *
     * @param entity The entity to check collision against.
     * @return true if a collision occurred, false otherwise.
     */

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
        } //else if (entity instanceof InvinciblePower) {
        //collidewithInvinciblePower((InvinciblePower) entity);
        //}
    }
    /**
     * Handles collision with a Car entity.
     *
     * @param car The Car entity involved in the collision.
     */

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
    /**
     * Handles collision with an EnemyCar entity.
     *
     * @param enemyCar The EnemyCar entity involved in the collision.
     */

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
    /**
     * Handles collision with a Fireball entity.
     *
     * @param fireball The Fireball entity involved in the collision.
     */

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
    /**
     * Applies a knockback effect on the given entity.
     *
     * @param entity The entity to apply knockback on.
     */

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

    // Setter for health
//    public void setHealth(float health) {
//        this.health = health;
//        if (this.health <= 0) {
//            health = 0;
//        }
//    }

}

