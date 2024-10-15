/**
 * Class representing weather conditions in the game, including start and end frames, and rain status.
 */
public class WeatherCondition {

    private final int startFrame; // Frame at which the weather condition starts
    private final int endFrame; // Frame at which the weather condition ends
    private final boolean isRaining; // Flag indicating if it is raining

    /**
     * Constructs a WeatherCondition with specified start frame, end frame, and rain status.
     *
     * @param startFrame The frame at which the weather condition starts.
     * @param endFrame   The frame at which the weather condition ends.
     * @param isRaining  Indicates if the condition involves rain.
     */
    public WeatherCondition(int startFrame, int endFrame, boolean isRaining) {
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.isRaining = isRaining;
    }

    /**
     * Gets the frame at which the weather condition starts.
     *
     * @return The start frame of the weather condition.
     */
    public int getStartFrame() {
        return startFrame;
    }

    /**
     * Gets the frame at which the weather condition ends.
     *
     * @return The end frame of the weather condition.
     */
    public int getEndFrame() {
        return endFrame;
    }

    /**
     * Checks if the weather condition involves rain.
     *
     * @return true if it is raining, false otherwise.
     */
    public boolean isRaining() {
        return isRaining;
    }
}
