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

    /*
     * Note that since this field isn't annotated as a
     * @JsonField, LoganSquare will ignore it when parsing
     * and serializing this class.
     */
    public int nonJsonField;
}