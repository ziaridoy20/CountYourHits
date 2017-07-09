package fim.uni_passau.de.countyourhits.model;

/**
 * Created by subash on 02/07/2017.
 */

public class Players {
    private long playerId;
    private String playerName;
    private String playerImage;
    private String playerNote;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerImage() {
        return playerImage;
    }

    public void setPlayerImage(String playerImage) {
        this.playerImage = playerImage;
    }

    public String getPlayerNote() {
        return playerNote;
    }

    public void setPlayerNote(String playerNote) {
        this.playerNote = playerNote;
    }


}
