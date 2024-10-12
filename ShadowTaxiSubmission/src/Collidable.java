public interface Collidable {
    boolean hasCollided(Collidable entity);

    void collide(Object entity);
   // boolean checkCollision(Collidable other);

    //float getDamagePoints();
    //void takeDamage();
    int getX();
    int getY();
    float getRadius();
    void setInvincible(int frames);


}
