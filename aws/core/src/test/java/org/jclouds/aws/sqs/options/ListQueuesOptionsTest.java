package org.jclouds.aws.sqs.options;

import static org.jclouds.aws.sqs.options.ListQueuesOptions.Builder.queuePrefix;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of ListQueuesOptions and ListQueuesOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class ListQueuesOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(ListQueuesOptions.class);
      assert !String.class.isAssignableFrom(ListQueuesOptions.class);
   }

   @Test
   public void testPrefix() {
      ListQueuesOptions options = new ListQueuesOptions();
      options.queuePrefix("test");
      assertEquals(options.buildFormParameters().get("QueueNamePrefix"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullPrefix() {
      ListQueuesOptions options = new ListQueuesOptions();
      assertEquals(options.buildFormParameters().get("QueueNamePrefix"), Collections.EMPTY_LIST);
   }

   @Test
   public void testPrefixStatic() {
      ListQueuesOptions options = queuePrefix("test");
      assertEquals(options.buildFormParameters().get("QueueNamePrefix"), Collections
               .singletonList("test"));
   }

   public void testNoPrefix() {
      queuePrefix(null);
   }
}
