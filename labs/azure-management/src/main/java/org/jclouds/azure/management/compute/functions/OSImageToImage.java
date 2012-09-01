package org.jclouds.azure.management.compute.functions;

import org.jclouds.azure.management.domain.OSImage;
import org.jclouds.compute.domain.Image;

import com.google.common.base.Function;

public class OSImageToImage implements Function<OSImage, Image>{

	@Override
	public Image apply(OSImage input) {
		return null;
	}

}
