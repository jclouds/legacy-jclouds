package org.jclouds.atmosonline.saas.options;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "emcsaas.ListOptionsTest")
public class ListOptionsTest {

   public void testToken() {
      ListOptions options = new ListOptions().token("a");
      assertEquals(ImmutableList.of("a"), options.buildRequestHeaders().get("x-emc-token"));
   }
   
   public void testLimit() {
      int limit = 1;
      ListOptions options = new ListOptions().limit(limit);
      assertEquals(ImmutableList.of("1"), options.buildRequestHeaders().get("x-emc-limit"));
   }

   public void testTokenStatic() {
      ListOptions options = ListOptions.Builder.token("a");
      assertEquals(ImmutableList.of("a"), options.buildRequestHeaders().get("x-emc-token"));
   }

   public void testLimitStatic() {
      int limit = 1;
      ListOptions options = ListOptions.Builder.limit(limit);
      assertEquals(ImmutableList.of("1"), options.buildRequestHeaders().get("x-emc-limit"));
   }
}
