/*
import java.util.Properties;
import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class Driver {

    private final Properties PROPS;
    private final Image IMAGE;
    private final int DRIVER_INTAXI_RADIUS;
    private final float RADIUS;
    private final int WALK_SPEED_X;
    private final int WALK_SPEED_Y;
    private int x, y;  // Driver's current coordinates
    private int health;
    private boolean inTaxi = true;
    private int moveY;
    private int walkDirectionX;
    private int walkDirectionY;
    private final int SPEED_Y;

    public Driver(int x, int y, Properties props) {

        this.IMAGE = new Image(props.getProperty("gameObjects.driver.image"));
        this.WALK_SPEED_X = Integer.parseInt(props.getProperty("gameObjects.driver.walkSpeedX"));
        this.WALK_SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.driver.walkSpeedY"));
        this.PROPS = props;
        this.RADIUS = Float.parseFloat(props.getProperty("gameObjects.driver.radius"));
        this.DRIVER_INTAXI_RADIUS = Integer.parseInt(props.getProperty("gameObjects.driver.taxiGetInRadius"));
        this.x = x;
        this.y = y;
        this.moveY = 0;
        this.SPEED_Y = Integer.parseInt(props.getProperty("gameObjects.taxi.speedY"));
        this.health = Integer.parseInt(props.getProperty("gameObjects.driver.health"));

    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void UpdateWithTaxi(Input input, Taxi taxi) {
        // if the passenger is not in the taxi or the trip is completed, update the passenger status based on keyboard
        // input. This means the passenger is go down when taxi moves up.
        if(!inTaxi) {
            if(input != null) {
                adjustToInputMovement(input);
            }

            move();
            draw();
        }
        private void adjustToInputMovement(Input input) {
            if (input.wasPressed(Keys.UP)) {
                moveY = 1;
            }  else if(input.wasReleased(Keys.UP)) {
                moveY = 0;
            }
        }


         // Move the GameObject object in the y-direction based on the speedY attribute.

        private void move() {
            this.y += SPEED_Y * moveY;
        }


         // Draw the GameObject object into the screen.

        private void draw() {
            IMAGE.draw(x, y);
        }


         // Walk the people object based on the walk direction and speed.

        private void walk() {
            x += + WALK_SPEED_X * walkDirectionX;
            y += + WALK_SPEED_Y * walkDirectionY;
        }


         // Move the people object along with taxi when the people object is in the taxi.
         // @param taxi Active taxi in the game play

        private void moveWithTaxi(Taxi taxi) {
            x = taxi.getX();
            y = taxi.getY();
        }



}
*/


import java.util.Properties;
import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Keys;

public class Driver {

    private final Properties PROPS;
    private final Image IMAGE;
    private final int DRIVER_INTAXI_RADIUS;
    private final float RADIUS;
    private final int WALK_SPEED_X;
    private final int WALK_SPEED_Y;
    private int x, y;  // Driver's current coordinates
    private int health;
    private boolean inTaxi = true;
    private int moveY;
    private int walkDirectionX;
    private int walkDirectionY;
    private final int SPEED_Y;

    public Driver(int x, int y, Properties props) {
        this.PROPS = props;

        // Load image, default to a placeholder if missing
        //String driverImagePath = props.getProperty("gameObjects.driver.image", "default_image.png");
       //this.IMAGE = new Image(driverImagePath);
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

    public void updateWithTaxi(Input input, Taxi taxi) {
        // Update movement only if the driver is not in the taxi
        if (!inTaxi) {
            if (input != null) {
                adjustToInputMovement(input);
            }

            move();
            draw();
        } else {
            // Move with the taxi when inside
            moveWithTaxi(taxi);
            //System.out.println("Driver Health: " + health + " | Driver is inside the taxi.");
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
    }

    private void moveWithTaxi(Taxi taxi) {
        x = taxi.getX();
        y = taxi.getY();
    }
}
