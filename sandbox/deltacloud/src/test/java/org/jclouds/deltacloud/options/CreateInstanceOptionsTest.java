package org.jclouds.deltacloud.options;

import static org.jclouds.deltacloud.options.CreateInstanceOptions.Builder.named;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of CreateInstanceOptions and CreateInstanceOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class CreateInstanceOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(CreateInstanceOptions.class);
      assert !String.class.isAssignableFrom(CreateInstanceOptions.class);
   }

   @Test
   public void testWithNamed() {
      CreateInstanceOptions options = new CreateInstanceOptions();
      options.named("test");
      assertEquals(options.buildFormParameters().get("name"), Collections.singletonList("test"));
   }

   @Test
   public void testNullWithNamed() {
      CreateInstanceOptions options = new CreateInstanceOptions();
      assertEquals(options.buildFormParameters().get("name"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithNamedStatic() {
      CreateInstanceOptions options = named("test");
      assertEquals(options.buildFormParameters().get("name"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithNamedNPE() {
      named(null);
   }

}
