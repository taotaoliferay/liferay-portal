/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.concurrent.ConcurrentHashSet;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.lang.reflect.Field;

import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class ReferenceRegistry {

	public static void registerReference(
		Class<?> clazz, Object object, String fieldName) {

		try {
			Field field = clazz.getDeclaredField(fieldName);

			ReferenceEntry referenceEntry = new ReferenceEntry(object, field);

			_referenceEntries.add(referenceEntry);
		}
		catch (SecurityException se) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Not allowed to get field " + fieldName + " for " + clazz);
			}
		}
		catch (Exception e) {
			_log.error("Unable to get field " + fieldName + " for " + clazz);
		}
	}

	public static void registerReference(Class<?> clazz, String fieldName) {
		registerReference(clazz, null, fieldName);
	}

	public static void registerReference(Field field) {
		ReferenceEntry referenceEntry = new ReferenceEntry(field);

		_referenceEntries.add(referenceEntry);
	}

	public static void registerReference(Object object, Field field) {
		ReferenceEntry referenceEntry = new ReferenceEntry(object, field);

		_referenceEntries.add(referenceEntry);
	}

	public static void releaseReferences() {
		for (ReferenceEntry referenceEntry : _referenceEntries) {
			try {
				referenceEntry.setValue(null);
			}
			catch (Exception e) {
				_log.error(
					"Failed to release reference for " + referenceEntry, e);
			}
		}

		_referenceEntries.clear();
	}

	private static Log _log = LogFactoryUtil.getLog(ReferenceRegistry.class);

	private static Set<ReferenceEntry> _referenceEntries =
		new ConcurrentHashSet<ReferenceEntry>();

}