package org.jclouds.joyent.sdc.v6_5.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

public enum Type {
	VIRTUALMACHINE, SMARTMACHINE, UNRECOGNIZED;

	public static Type fromValue(String type) {
		try {
			return valueOf(CaseFormat.UPPER_CAMEL
					.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type,
							"type")));
		} catch (IllegalArgumentException e) {
			return UNRECOGNIZED;
		}
	}

	public String value() {
		return (CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
				name()));
	}

	@Override
	public String toString() {
		return value();
	}
}
