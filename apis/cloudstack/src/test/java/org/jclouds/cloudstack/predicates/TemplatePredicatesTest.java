package org.jclouds.cloudstack.predicates;

import org.jclouds.cloudstack.domain.Template;
import org.testng.annotations.Test;

import static org.jclouds.cloudstack.predicates.TemplatePredicates.isPasswordEnabled;
import static org.jclouds.cloudstack.predicates.TemplatePredicates.isReady;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class TemplatePredicatesTest {

   @Test
   public void testTemplateIsReady() {
      assertTrue(isReady().apply(
         Template.builder().ready(true).build()
      ));
      assertFalse(isReady().apply(
         Template.builder().ready(false).build()
      ));
   }

   @Test
   public void testTemplateIsPasswordEnabled() {
      assertTrue(isPasswordEnabled().apply(
         Template.builder().passwordEnabled(true).build()
      ));
      assertFalse(isPasswordEnabled().apply(
         Template.builder().passwordEnabled(false).build()
      ));
   }
}
