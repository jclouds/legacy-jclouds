package org.jclouds.azure.management.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

public enum RoleSize {
	EXTRA_SMALL, SMALL, MEDIUM, LARGE, EXTRA_LARGE;

	public String value() {
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
	}

	@Override
	public String toString() {
		return value();
	}

	public static RoleSize fromValue(String type) {
		try {
			return valueOf(CaseFormat.UPPER_CAMEL.to(
					CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
