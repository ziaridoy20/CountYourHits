package fim.uni_passau.de.countyourhits.model;

import org.opencv.core.Point;

/**
 * Created by ziaridoy20 on 10-Jun-17.
 */

public class DetectedCircle {

    private boolean isOuterCircleIn = false;
    private Point cirCoordinate;
    private int cirRadius;

    public DetectedCircle(){

    }

    public int getCirRadius() {
        return cirRadius;
    }

    public void setCirRadius(int cirRadius) {
        this.cirRadius = cirRadius;
    }

    public Point getCirCoordinate() {
        return cirCoordinate;
    }

    public void setCirCoordinate(Point cirCoordinate) {
        this.cirCoordinate = cirCoordinate;
    }

    public boolean isOuterCircleIn() {
        return isOuterCircleIn;
    }

    public void setOuterCircleIn(boolean outerCircleIn) {
        isOuterCircleIn = outerCircleIn;
    }
}
