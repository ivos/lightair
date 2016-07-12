package net.sf.lightair.internal.db;

import net.sf.lightair.internal.Keywords;
import net.sf.lightair.internal.db.provider.Provider;
import org.apache.commons.lang3.StringUtils;

public class Providers implements Keywords {

	public static final String PROVIDER_CLASS_NAME_SUFFIX = "Provider";

	public static Provider createProvider(String profile, String dialect) {
		String providerClassName = Provider.class.getPackage().getName() + "." +
				StringUtils.capitalize(dialect) + PROVIDER_CLASS_NAME_SUFFIX;
		try {
			Class<?> providerClass = Class.forName(providerClassName);
			Object instance = providerClass.newInstance();
			if (!(instance instanceof Provider)) {
				throw new RuntimeException("Provider [" + providerClassName + "] must implement " +
						"the " + Provider.class.getName() + " interface.");
			}
			return (Provider) instance;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Cannot load provider class [" + providerClassName + "] for profile [" + profile + "]." +
							" Did you spell the dialect correctly?", e);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Cannot create new instance of provider class [" + providerClassName + "]." +
					" Does it have a public default constructor?", e);
		}
	}
}
