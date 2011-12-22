package json;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 * @author JSON.org
 * @version 2008-09-18
 */
public class JSONException extends Exception {
	private static final long serialVersionUID = 4917590362036726380L;
	private Throwable cause;
	private String inputString;

    /**
     * Constructs a JSONException with an explanatory message.
     * @param message Detail about the reason for the exception.
     */
    public JSONException(String message) {
    	this(message, null);
    }

    public JSONException(Throwable t) {
        super(t.getMessage());
        this.cause = t;
    }

    public final Throwable getCause() {
        return this.cause;
    }
    
    public JSONException(String message, String input) {
        super(message);
        this.inputString = input;
    }
    
    public final String getInputString() {
    	return this.inputString;
    }
}
