package org.jclouds.greenqloud.compute;

import org.jclouds.providers.BaseProviderMetadataTest;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "GreenQloudComputeProviderTest")
public class GreenQloudComputeProviderTest extends BaseProviderMetadataTest {
	public GreenQloudComputeProviderTest() {
		super(new GreenQloudComputeProviderMetadata(),
				ProviderMetadata.COMPUTE_TYPE);
	}
}
