package org.jclouds.azure.management.compute.functions;

import org.jclouds.azure.management.domain.RoleSize;
import org.jclouds.compute.domain.Hardware;

import com.google.common.base.Function;

public class RoleSizeToHardware implements Function<RoleSize, Hardware> {

	@Override
	public Hardware apply(RoleSize input) {
		return null;
	}

}
