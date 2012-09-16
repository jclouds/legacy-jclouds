package org.jclouds.sqs.options;

import static org.jclouds.sqs.options.ReceiveMessageOptions.Builder.attribute;
import static org.jclouds.sqs.options.ReceiveMessageOptions.Builder.visibilityTimeout;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ReceiveMessageOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ReceiveMessageOptionsTest")
public class ReceiveMessageOptionsTest {

   public void testVisibilityTimeout() {
      ReceiveMessageOptions options = new ReceiveMessageOptions().visibilityTimeout(2);
      assertEquals(ImmutableSet.of("2"), options.buildFormParameters().get("VisibilityTimeout"));
   }

   public void testVisibilityTimeoutStatic() {
      ReceiveMessageOptions options = visibilityTimeout(2);
      assertEquals(ImmutableSet.of("2"), options.buildFormParameters().get("VisibilityTimeout"));
   }

   public void testAttribute() {
      ReceiveMessageOptions options = new ReceiveMessageOptions().attribute("All");
      assertEquals(ImmutableSet.of("All"), options.buildFormParameters().get("AttributeName.1"));
   }

   public void testAttributeStatic() {
      ReceiveMessageOptions options = attribute("All");
      assertEquals(ImmutableSet.of("All"), options.buildFormParameters().get("AttributeName.1"));
   }

}
