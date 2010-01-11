package org.jclouds.aws.sqs.options;

import static org.jclouds.aws.sqs.options.CreateQueueOptions.Builder.defaultVisibilityTimeout;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of CreateQueueOptions and CreateQueueOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class CreateQueueOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(CreateQueueOptions.class);
      assert !String.class.isAssignableFrom(CreateQueueOptions.class);
   }

   @Test
   public void testTimeout() {
      CreateQueueOptions options = new CreateQueueOptions();
      options.defaultVisibilityTimeout(1);
      assertEquals(options.buildFormParameters().get("DefaultVisibilityTimeout"), Collections
               .singletonList("1"));
   }

   @Test
   public void testNullTimeout() {
      CreateQueueOptions options = new CreateQueueOptions();
      assertEquals(options.buildFormParameters().get("DefaultVisibilityTimeout"), Collections.EMPTY_LIST);
   }

   @Test
   public void testTimeoutStatic() {
      CreateQueueOptions options = defaultVisibilityTimeout(1);
      assertEquals(options.buildFormParameters().get("DefaultVisibilityTimeout"), Collections
               .singletonList("1"));
   }

   public void testNoTimeout() {
      defaultVisibilityTimeout(0);
   }
}
