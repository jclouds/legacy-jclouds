package org.jclouds.gq.compute;

import org.jclouds.providers.BaseProviderMetadataTest;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "GQComputeProviderTest")
public class GQComputeProviderTest extends BaseProviderMetadataTest {
	public GQComputeProviderTest() {
		super(new GQComputeProviderMetadata(),
				ProviderMetadata.COMPUTE_TYPE);
	}
}
