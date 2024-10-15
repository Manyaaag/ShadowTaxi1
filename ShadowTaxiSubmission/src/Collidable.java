
/**
 * The Collidable interface defines the methods for objects that can interact
 * with other entities in a collision-based system. It includes functionality
 * for checking collisions, applying knockback, handling damage, and managing
 * invincibility frames.
 */
public interface Collidable {

    /**
     * Checks if this entity has collided with another collidable entity.
     *
     * @param entity The other collidable entity to check for a collision.
     * @return true if a collision has occurred; false otherwise.
     */
    boolean hasCollided(Collidable entity);

    /**
     * Handles collision effects between this entity and another entity.
     *
     * @param entity The entity that this object is colliding with.
     */
    void collide(Object entity);

    /**
     * Retrieves the x-coordinate of this entity.
     *
     * @return The x-coordinate of this entity.
     */
    int getX();

    /**
     * Retrieves the y-coordinate of this entity.
     *
     * @return The y-coordinate of this entity.
     */
    int getY();

    /**
     * Retrieves the collision radius of this entity.
     *
     * @return The collision radius.
     */
    float getRadius();

    /**
     * Sets the number of frames for which this entity will be invincible.
     *
     * @param frames The number of invincibility frames.
     */
    void setInvincible(int frames);

    /**
     * Retrieves the damage points this entity can inflict upon collision.
     *
     * @return The damage points of this entity.
     */
    float getDamage(); // For damage points

    /**
     * Sets the x-coordinate of this entity, primarily used for knockback effects.
     *
     * @param x The x-coordinate to set.
     */
    void setX(int x); // For knockback

    /**
     * Sets the y-coordinate of this entity, primarily used for knockback effects.
     *
     * @param y The y-coordinate to set.
     */
    void setY(int y); // For knockback

    // float getDamagePoints();
    // void takeDamage();

}

