package fim.uni_passau.de.countyourhits.model;

/**
 * Created by Nahid 002345 on 6/12/2017.
 */

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by Nahid 002345 on 6/11/2017.
 */

@JsonObject
public class ResultResponse {
    public ResultResponse() {
    }
    public ResultResponse(String description, String requestId, String playerId) {
        this.description = description;
        this.requestId = requestId;
        this.playerId = playerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgBlob() {
        return imgBlob;
    }

    public void setImgBlob(String imgBlob) {
        this.imgBlob = imgBlob;
    }

    public int getNonJsonField() {
        return nonJsonField;
    }

    public void setNonJsonField(int nonJsonField) {
        this.nonJsonField = nonJsonField;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Score getScorePoint() {
        return scorePoint;
    }

    public void setScorePoint(Score scorePoint) {
        this.scorePoint = scorePoint;
    }

    @JsonField
    public String requestId;

    @JsonField
    public String playerId;


    @JsonField
    public String description;

    @JsonField
    public String imgBlob;

    @JsonField
    public Score scorePoint;

    public int nonJsonField;
}
