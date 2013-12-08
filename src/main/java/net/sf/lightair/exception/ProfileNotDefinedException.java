package net.sf.lightair.exception;

/**
 * Thrown when a specified profile is not defined in Light air properties.
 */
public class ProfileNotDefinedException extends AbstractException {

	/**
	 * Constructor.
	 * 
	 * @param profile
	 *            Name of profile
	 */
	public ProfileNotDefinedException(String profile) {
		super("Profile '" + profile
				+ "' is not defined in Ligt Air properties."
				+ " You can define it with profile.name=profile.properties.");
	}

	private static final long serialVersionUID = 1L;

}
