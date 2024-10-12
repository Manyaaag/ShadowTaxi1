public class WeatherCondition {
    private final int startFrame;
    private final int endFrame;
    private final boolean isRaining;

    public WeatherCondition(int startFrame, int endFrame, boolean isRaining) {
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.isRaining = isRaining;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public boolean isRaining() {
        return isRaining;
    }
}