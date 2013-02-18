package org.jclouds.dynect.v3.predicates;

import static org.jclouds.dynect.v3.predicates.RecordPredicates.typeEquals;

import org.jclouds.dynect.v3.domain.RecordId;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RecordPredicatesTest {
   RecordId recordId = RecordId.builder()
                               .zone("adrianc.zone.dynecttest.jclouds.org")
                               .fqdn("adrianc.zone.dynecttest.jclouds.org")
                               .type("SOA")
                               .id(50976579l).build();

   @Test
   public void testTypeEqualsWhenEqual() {
      assert typeEquals("SOA").apply(recordId);
   }

   @Test
   public void testTypeEqualsWhenNotEqual() {
      assert !typeEquals("NS").apply(recordId);
   }
}
