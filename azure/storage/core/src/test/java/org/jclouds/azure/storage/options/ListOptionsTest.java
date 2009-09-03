package org.jclouds.azure.storage.options;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azurestorage.ListOptionsTest")
public class ListOptionsTest {

   public void testPrefix() {
      ListOptions options = new ListOptions().prefix("a");
      assertEquals(ImmutableList.of("a"), options.buildQueryParameters().get("prefix"));
   }
   
   public void testMarker() {
      ListOptions options = new ListOptions().marker("a");
      assertEquals(ImmutableList.of("a"), options.buildQueryParameters().get("marker"));
   }
   
   public void testMaxResults() {
      int limit = 1;
      ListOptions options = new ListOptions().maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("maxresults"));
   }

   public void testPrefixStatic() {
      ListOptions options = ListOptions.Builder.prefix("a");
      assertEquals(ImmutableList.of("a"), options.buildQueryParameters().get("prefix"));
   }
   
   public void testMarkerStatic() {
      ListOptions options = ListOptions.Builder.marker("a");
      assertEquals(ImmutableList.of("a"), options.buildQueryParameters().get("marker"));
   }

   public void testMaxResultsStatic() {
      int limit = 1;
      ListOptions options = ListOptions.Builder.maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("maxresults"));
   }
}
