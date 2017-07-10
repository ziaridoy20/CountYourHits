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
public class ScoresMsg {

    /*
     * Annotate a field that you want sent with the @JsonField marker.
     */
    @JsonField
    public String scoreId;
    @JsonField
    public String scorePlayer_Id;
    @JsonField
    public String scoreRequestNo;
    @JsonField
    public String scorePoint;
    @JsonField
    public String scoreCo_ordinate_x;
    @JsonField
    public String scoreCo_ordinate_y;
    @JsonField
    public String scoreImagePath;
    @JsonField
    public String scoreDateTime;
    @JsonField
    public String scoreNote;

    @JsonField
    public String imgBlob;
    /*
     * Note that since this field isn't annotated as a
     * @JsonField, LoganSquare will ignore it when parsing
     * and serializing this class.
     */
    public int nonJsonField;
}