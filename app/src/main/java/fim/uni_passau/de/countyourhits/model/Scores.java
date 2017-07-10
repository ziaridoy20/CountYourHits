package fim.uni_passau.de.countyourhits.model;

/**
 * Created by subash on 02/07/2017.
 */

public class Scores {
    private long scoreId;
    private long scorePlayer_Id;
    private long scoreRequestNo;
    private String scorePoint;
    private String scoreCo_ordinate_x;
    private String scoreCo_ordinate_y;
    private String scoreImageBlob;
    private String scoreDateTime;
    private String scoreNote;

    public long getScoreId() {
        return scoreId;
    }

    public void setScoreId(long scoreId) {
        this.scoreId = scoreId;
    }

    public long getScorePlayer_Id() {
        return scorePlayer_Id;
    }

    public void setScorePlayer_Id(long scorePlayer_Id) {
        this.scorePlayer_Id = scorePlayer_Id;
    }

    public long getScoreRequestNo() {
        return scoreRequestNo;
    }

    public void setScoreRequestNo(long scoreRequestNo) {
        this.scoreRequestNo = scoreRequestNo;
    }

    public String getScorePoint() {
        return scorePoint;
    }

    public void setScorePoint(String scorePoint) {
        this.scorePoint = scorePoint;
    }

    public String getScoreCo_ordinate_x() {
        return scoreCo_ordinate_x;
    }

    public void setScoreCo_ordinate_x(String scoreCo_ordinate_x) {
        this.scoreCo_ordinate_x = scoreCo_ordinate_x;
    }

    public String getScoreCo_ordinate_y() {
        return scoreCo_ordinate_y;
    }

    public void setScoreCo_ordinate_y(String scoreCo_ordinate_y) {
        this.scoreCo_ordinate_y = scoreCo_ordinate_y;
    }

    public String getScoreImageBlob() {
        return scoreImageBlob;
    }

    public void setScoreImageBlob(String scoreImageBlob) {
        this.scoreImageBlob = scoreImageBlob;
    }

    public String getScoreDateTime() {
        return scoreDateTime;
    }

    public void setScoreDateTime(String scoreDateTime) {
        this.scoreDateTime = scoreDateTime;
    }

    public String getScoreNote() {
        return scoreNote;
    }

    public void setScoreNote(String scoreNote) {
        this.scoreNote = scoreNote;
    }


}
