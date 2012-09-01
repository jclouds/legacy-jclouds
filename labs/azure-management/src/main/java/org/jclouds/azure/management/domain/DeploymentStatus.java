package org.jclouds.azure.management.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

public enum DeploymentStatus {
	RUNNING, SUSPENDED, RUNNING_TRANSITIONING, SUSPENDED_TRANSITIONING, STARTING, SUSPENDING, DEPLOYING, DELETING;

	public String value() {
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
	}

	@Override
	public String toString() {
		return value();
	}

	public static DeploymentStatus fromValue(String type) {
		try {
			return valueOf(CaseFormat.UPPER_CAMEL.to(
					CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
