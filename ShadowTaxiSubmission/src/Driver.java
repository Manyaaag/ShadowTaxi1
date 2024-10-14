
import java.util.Properties;
import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class Driver {

    private final Properties PROPS;
    private final Image IMAGE;

    private final float RADIUS;
    private final int WALK_SPEED_X;
    private final int WALK_SPEED_Y;
    private int x, y;  // Driver's current coordinates
    private int health;
    private int moveY;
    private int walkDirectionX;
    private int walkDirectionY;
    private final int SPEED_Y;

    public boolean inTaxi = true;
    public boolean inNewTaxi = false;
    private final int DRIVER_INTAXI_RADIUS;



    public Driver(int x, int y, Properties props) {
        this.PROPS = props;
        this.IMAGE = new Image(props.getProperty("gameObjects.driver.image"));
        // Parse properties with fallbacks
        this.WALK_SPEED_X = parseIntProperty(props, "gameObjects.driver.walkSpeedX", 2);
        this.WALK_SPEED_Y = parseIntProperty(props, "gameObjects.driver.walkSpeedY", 2);
        this.RADIUS = parseFloatProperty(props, "gameObjects.driver.radius", 10.0f);
        this.DRIVER_INTAXI_RADIUS = parseIntProperty(props, "gameObjects.driver.taxiGetInRadius", 10);
        this.SPEED_Y = parseIntProperty(props, "gameObjects.taxi.speedY", 5);
        this.health = parseIntProperty(props, "gameObjects.driver.health", 100);

        this.x = x;
        this.y = y;
        this.moveY = 0;
    }

    private int parseIntProperty(Properties props, String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private float parseFloatProperty(Properties props, String key, float defaultValue) {
        try {
            return Float.parseFloat(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

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

    public int getTaxiInRadius() {
        return DRIVER_INTAXI_RADIUS;
    }

    // Getter for health
    public float getHealth() {
        return health;
    }

    public void setInTaxi(boolean inTaxi) {
        this.inTaxi = inTaxi;
    }
    public void setInNewTaxi(boolean inNewTaxi) {
        this.inNewTaxi = inNewTaxi;

    }


    // Setter for health
    public void setHealth(int health) {
        this.health = health;
        if (this.health <= 0) {
            // Implement logic to render blood effect here if needed
        }
    }


    public void updateWithTaxi(Input input, Taxi newTaxi) {
        if (!inTaxi) {  // Control driver movement only when outside the taxi
            if (input != null) {
                adjustToInputMovement(input);
            }
            walk();



            // Check if close enough to enter the new taxi
            if (!newTaxi.isOriginalTaxi && calculateDistance(newTaxi) <= DRIVER_INTAXI_RADIUS) {    //currDistance <= DRIVER_INTAXI_RADIUS
                enterTaxi(newTaxi);
                //inTaxi = true;
                //moveWithTaxi(newTaxi);
            } else {
                draw();  // Draw the driver if outside the taxi
            }

            // Game loss condition: Driver goes out of bounds at top of screen
            if (y < 0) {
                System.out.println("Game Over: Driver moved out of bounds.");
                // Implement game over logic here
            }
        } else {
            //moveWithTaxi(taxi);
            moveWithTaxi(newTaxi);  // Keep driver moving with the taxi when inside
        }
    }


    private void adjustToInputMovement(Input input) {
        if (input.wasPressed(Keys.UP)) {
            moveY = 1;
        } else if (input.wasReleased(Keys.UP)) {
            moveY = 0;
        }

        if (input.isDown(Keys.LEFT)) {
            walkDirectionX = -1;
        } else if (input.isDown(Keys.RIGHT)) {
            walkDirectionX = 1;
        } else {
            walkDirectionX = 0;
        }

        if (input.isDown(Keys.UP)) {
            walkDirectionY = -1;
        } else if (input.isDown(Keys.DOWN)) {
            walkDirectionY = 1;
        } else {
            walkDirectionY = 0;
        }
        //System.out.println("Current Walk Directions -> X: " + walkDirectionX + " Y: " + walkDirectionY);

    }

    private void move() {
        this.y += SPEED_Y * moveY;
    }

    public void draw() {
        IMAGE.draw(x, y);
    }


    private void walk() {
        x += WALK_SPEED_X * walkDirectionX;
        y += WALK_SPEED_Y * walkDirectionY;

        //System.out.println("Driver Position -> X: " + x + " Y: " + y);  // Debug for position

    }

    public void enterTaxi(Taxi newTaxi) {
        System.out.println("attempting to enter taxi");
        if (!inTaxi && !newTaxi.isOriginalTaxi && calculateDistance(newTaxi) <= DRIVER_INTAXI_RADIUS) {
            System.out.println("Driver is now entering the taxi.");  // Debug log

            inTaxi = true;
            inNewTaxi = true;
            newTaxi.activate();
            moveWithTaxi(newTaxi);
        }
    }

    public void moveWithTaxi(Taxi taxi) {
        x = taxi.getX();
        y = taxi.getY();
    }

    public double calculateDistance(Taxi newTaxi) {

        float currDistance = (float) Math.sqrt(Math.pow(newTaxi.getX() - x, 2) + Math.pow(newTaxi.getY() - y, 2));
//        int dx = this.x - newTaxi.getX();
//        int dy = this.y - newTaxi.getY();
//        return Math.sqrt(dx * dx + dy * dy);
        return currDistance;

    }




    //float currDistance = (float) Math.sqrt(Math.pow(taxi.getX() - x, 2) + Math.pow(taxi.getY() - y, 2));
    //return currDistance <= TAXI_DETECT_RADIUS


}
