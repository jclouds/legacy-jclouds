package org.jclouds.rackspace.cloudservers.options;

import static org.jclouds.rackspace.cloudservers.options.ListOptions.Builder.*;
import static org.testng.Assert.assertEquals;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "rackspace.ListOptionsTest")
public class ListOptionsTest {

   public void testWithDetails() {
      ListOptions options = new ListOptions().withDetails();
      assertEquals(options.buildPathSuffix(), "/detail");
   }

   public void testWithDetailsStatic() {
      ListOptions options = withDetails();
      assertEquals(options.buildPathSuffix(), "/detail");
   }

   public void testChangesSince() {
      DateTime ifModifiedSince = new DateTime();
      ListOptions options = new ListOptions().changesSince(ifModifiedSince);
      assertEquals(ImmutableList.of(ifModifiedSince.getMillis() / 1000 + ""), options
               .buildQueryParameters().get("changes-since"));
   }

   public void testStartAt() {
      long offset = 1;
      ListOptions options = new ListOptions().startAt(offset);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("offset"));
   }

   public void testMaxResults() {
      int limit = 1;
      ListOptions options = new ListOptions().maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }

   public void testChangesSinceStatic() {
      DateTime ifModifiedSince = new DateTime();
      ListOptions options = changesSince(ifModifiedSince);
      assertEquals(ImmutableList.of(ifModifiedSince.getMillis() / 1000 + ""), options
               .buildQueryParameters().get("changes-since"));
   }

   public void testStartAtStatic() {
      long offset = 1;
      ListOptions options = startAt(offset);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("offset"));
   }

   public void testMaxResultsStatic() {
      int limit = 1;
      ListOptions options = maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }
}
