package org.jclouds.compute.internal;

import java.util.Set;

import org.jclouds.compute.util.ComputeUtils;
import org.testng.annotations.Test;
/**
 * Test the compute utils.
 * 
 * @author Ivan Meredith
 *
 */
@Test(groups = "unit")
public class ComputeUtilsTest {
	
	/**
	 * Test some of the currently supported clouds against compute.properties.
	 */
	@Test
	public void testSupportedProviders(){
		Set<String> providers = ComputeUtils.getSupportedProviders();
		assert providers.contains("rimuhosting");
		assert providers.contains("cloudservers");
		assert providers.contains("gogrid");		
	}
}
