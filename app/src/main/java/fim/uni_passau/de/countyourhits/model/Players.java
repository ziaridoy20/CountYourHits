package fim.uni_passau.de.countyourhits.model;

/**
 * Created by subash on 02/07/2017.
 */

public class Players {
    private long id;
    private String playerName;
    private String score;
    private String scoreImage;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScoreImage() {
        return scoreImage;
    }

    public void setScoreImage(String scoreImage) {
        this.scoreImage = scoreImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
