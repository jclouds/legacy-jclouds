package org.jclouds.rackspace.options;

import static org.jclouds.rackspace.options.BaseListOptions.Builder.changesSince;
import static org.jclouds.rackspace.options.BaseListOptions.Builder.maxResults;
import static org.jclouds.rackspace.options.BaseListOptions.Builder.startAt;
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
public class BaseListOptionsTest {

   public void testChangesSince() {
      DateTime ifModifiedSince = new DateTime();
      BaseListOptions options = new BaseListOptions().changesSince(ifModifiedSince);
      assertEquals(ImmutableList.of(ifModifiedSince.getMillis() / 1000 + ""), options
               .buildQueryParameters().get("changes-since"));
   }

   public void testStartAt() {
      long offset = 1;
      BaseListOptions options = new BaseListOptions().startAt(offset);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("offset"));
   }

   public void testMaxResults() {
      int limit = 1;
      BaseListOptions options = new BaseListOptions().maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }

   public void testChangesSinceStatic() {
      DateTime ifModifiedSince = new DateTime();
      BaseListOptions options = changesSince(ifModifiedSince);
      assertEquals(ImmutableList.of(ifModifiedSince.getMillis() / 1000 + ""), options
               .buildQueryParameters().get("changes-since"));
   }

   public void testStartAtStatic() {
      long offset = 1;
      BaseListOptions options = startAt(offset);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("offset"));
   }

   public void testMaxResultsStatic() {
      int limit = 1;
      BaseListOptions options = maxResults(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }
}
