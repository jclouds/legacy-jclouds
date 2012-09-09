package org.jclouds.openstack.swift.binders;

import org.jclouds.blobstore.binders.BindMapToHeadersWithPrefix;
import org.jclouds.openstack.swift.reference.SwiftHeaders;

public class BindMapToHeadersWithContainerMetadataPrefix extends BindMapToHeadersWithPrefix {

	public BindMapToHeadersWithContainerMetadataPrefix() {
		super(SwiftHeaders.CONTAINER_METADATA_PREFIX);
	}
}
