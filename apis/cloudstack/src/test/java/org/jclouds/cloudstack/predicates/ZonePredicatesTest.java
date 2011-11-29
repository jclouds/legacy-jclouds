package org.jclouds.cloudstack.predicates;

import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.Zone;
import org.testng.annotations.Test;

import static org.jclouds.cloudstack.predicates.ZonePredicates.supportsAdvancedNetworks;
import static org.jclouds.cloudstack.predicates.ZonePredicates.supportsSecurityGroups;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class ZonePredicatesTest {

   @Test
   public void testSupportsAdvancedNetworks() {
      assertTrue(supportsAdvancedNetworks().apply(
         Zone.builder().networkType(NetworkType.ADVANCED).build()
      ));
      assertFalse(supportsAdvancedNetworks().apply(
         Zone.builder().networkType(NetworkType.BASIC).build()
      ));
   }

   @Test
   public void testSupportsSecurityGroups() {
      assertTrue(supportsSecurityGroups().apply(
         Zone.builder().securityGroupsEnabled(true).build()
      ));
      assertFalse(supportsSecurityGroups().apply(
         Zone.builder().securityGroupsEnabled(false).build()
      ));
   }

}
