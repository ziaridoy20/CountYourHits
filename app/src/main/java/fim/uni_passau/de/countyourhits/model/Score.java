package fim.uni_passau.de.countyourhits.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.opencv.core.Point;

/**
 * Created by Nahid 002345 on 6/21/2017.
 */
@JsonObject
public class Score {
    public Point getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(Point centerPoint) {
        this.centerPoint = centerPoint;
    }

    public double getScorePoint() {
        return scorePoint;
    }

    public void setScorePoint(double scorePoint) {
        this.scorePoint = scorePoint;
    }

    public Point getTargetCenter() {
        return targetCenter;
    }

    public void setTargetCenter(Point targetCenter) {
        this.targetCenter = targetCenter;
    }

    @JsonField
    public double scorePoint;

    @JsonField
    public Point centerPoint;

    @JsonField
    public Point targetCenter;
}
