package org.jclouds.opscodeplatform;

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
      assert Iterables.contains(providers, "opscodeplatform") : providers;
   }

}
