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
public class Message{

    /*
     * Annotate a field that you want sent with the @JsonField marker.
     */
    @JsonField
    public String description;

    @JsonField
    public String imgBlob;
    /*
     * Note that since this field isn't annotated as a
     * @JsonField, LoganSquare will ignore it when parsing
     * and serializing this class.
     */
    @JsonField
    public String scorePoint;

    @JsonField
    public long playerId;

    @JsonField
    public long requestId;

    @JsonField
    public Double coOrdinateX;

    @JsonField
    public Double coOrdinateY;

    public int nonJsonField;
}