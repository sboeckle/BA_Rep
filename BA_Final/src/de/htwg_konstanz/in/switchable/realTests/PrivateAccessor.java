package de.htwg_konstanz.in.switchable.realTests;

import java.lang.reflect.Field;

import org.junit.Assert;

/**
 * Provides access to private members in classes.
 */
public class PrivateAccessor {
	public static Object getPrivateField(Object o, String fieldName) {
		// Check we have valid arguments
		Assert.assertNotNull(o);
		Assert.assertNotNull(fieldName);
		// Go and find the private field...
		final Field fields[] = o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; ++i) {
			if (fieldName.equals(fields[i].getName())) {
				try {
					fields[i].setAccessible(true);
					return fields[i].get(o);
				} catch (IllegalAccessException ex) {
					Assert.fail("IllegalAccessException accessing " + fieldName);
				}
			}
		}
		Assert.fail("Field '" + fieldName + "' not found");
		return null;
	}
}