package marioGame;

public abstract class Collider {
    protected float height;
    protected float width;
    protected float xLocation;
    protected float yLocation;
    protected float startX;
    protected float startY;

    public Collider(float height, float width, float xLocation, float yLocation){
        this.height = height;
        this.width = width;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.startX = xLocation;
        this.startY = yLocation;
    }

    public int checkCollision(Collider other){
        if(xLocation + width > other.xLocation
                && xLocation < other.xLocation + other.width
                && yLocation + height > other.yLocation
                && yLocation < other.yLocation + other.height){
            return 1;
        }
        else if(yLocation + height <= other.yLocation
                && xLocation + width > other.xLocation
                && xLocation < other.xLocation + other.width){
            return 2;
        }
        else {
            return 0;
        }
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setxLocation(float xLocation) {
        this.xLocation = xLocation;
    }

    public void setyLocation(float yLocation) {
        this.yLocation = yLocation;
    }

    public float getxLocation() {
        return xLocation;
    }

    public float getyLocation() {
        return yLocation;
    }

    public abstract void show();

    public abstract void reset();
}
