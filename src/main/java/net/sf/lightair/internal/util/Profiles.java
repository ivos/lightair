package net.sf.lightair.internal.util;

import org.apache.commons.lang.StringUtils;

public class Profiles {

	public static final String DEFAULT_PROFILE = "";

	public static String getProfile(String profile) {
		return StringUtils.defaultIfBlank(profile, DEFAULT_PROFILE);
	}

}
