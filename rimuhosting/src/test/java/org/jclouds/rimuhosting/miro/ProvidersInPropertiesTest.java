package org.jclouds.rimuhosting.miro;

import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit")
public class ProvidersInPropertiesTest {

   @Test
   public void testSupportedProviders() {
      Iterable<String> providers = Utils.getSupportedProviders();
      assert Iterables.contains(providers, "rimuhosting") : providers;
   }

   @Test
   public void testSupportedComputeServiceProviders() {
      Iterable<String> providers = ComputeServiceUtils.getSupportedProviders();
      assert Iterables.contains(providers, "rimuhosting") : providers;
   }

}
