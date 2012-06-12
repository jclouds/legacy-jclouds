package org.jclouds.openstack.v2_0.predicates;

import static org.jclouds.openstack.v2_0.predicates.LinkPredicates.hrefEquals;
import static org.jclouds.openstack.v2_0.predicates.LinkPredicates.relationEquals;
import static org.jclouds.openstack.v2_0.predicates.LinkPredicates.typeEquals;

import java.net.URI;

import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "LinkPredicatesTest")
public class LinkPredicatesTest {
   Link ref = Link.builder().type("application/pdf").relation(Relation.DESCRIBEDBY).href(
            URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1")).build();

   @Test
   public void testRelationEqualsWhenEqual() {
      assert relationEquals(Relation.DESCRIBEDBY).apply(ref);
   }

   @Test
   public void testRelationEqualsWhenNotEqual() {
      assert !relationEquals(Relation.UNRECOGNIZED).apply(ref);
   }

   @Test
   public void testTypeEqualsWhenEqual() {
      assert typeEquals("application/pdf").apply(ref);
   }

   @Test
   public void testTypeEqualsWhenNotEqual() {
      assert !typeEquals("foo").apply(ref);
   }

   @Test
   public void testHrefEqualsWhenEqual() {
      assert hrefEquals(URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1")).apply(ref);
   }

   @Test
   public void testHrefEqualsWhenNotEqual() {
      assert !hrefEquals(URI.create("foo")).apply(ref);
   }
}
