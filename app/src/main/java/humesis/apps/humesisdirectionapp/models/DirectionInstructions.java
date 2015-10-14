package humesis.apps.humesisdirectionapp.models;

/**
 * Created by dhanraj on 14/10/15.
 */
public class DirectionInstructions {

    String instruction;
    String distanceText;
    String durationText;

    public DirectionInstructions() {
    }

    public DirectionInstructions(String instruction, String distanceText, String durationText) {
        this.instruction = instruction;
        this.distanceText = distanceText;
        this.durationText = durationText;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }
}
