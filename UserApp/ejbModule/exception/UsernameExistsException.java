package exception;
/**
 * 
 * @author nina
 *
 */
public class UsernameExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UsernameExistsException(String string) {
		super(string);
	}

}
