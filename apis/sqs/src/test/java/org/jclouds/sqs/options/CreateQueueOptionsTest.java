package org.jclouds.sqs.options;

import static org.jclouds.sqs.options.CreateQueueOptions.Builder.attribute;
import static org.jclouds.sqs.options.CreateQueueOptions.Builder.visibilityTimeout;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code CreateQueueOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "CreateQueueOptionsTest")
public class CreateQueueOptionsTest {

   public void testVisibilityTimeout() {
      CreateQueueOptions options = new CreateQueueOptions().visibilityTimeout(2);
      assertEquals(ImmutableSet.of("VisibilityTimeout"), options.buildFormParameters().get("Attribute.1.Name"));
      assertEquals(ImmutableSet.of("2"), options.buildFormParameters().get("Attribute.1.Value"));
   }

   public void testVisibilityTimeoutStatic() {
      CreateQueueOptions options = visibilityTimeout(2);
      assertEquals(ImmutableSet.of("VisibilityTimeout"), options.buildFormParameters().get("Attribute.1.Name"));
      assertEquals(ImmutableSet.of("2"), options.buildFormParameters().get("Attribute.1.Value"));
   }

   public void testAttribute() {
      CreateQueueOptions options = new CreateQueueOptions().attribute("DelaySeconds", "1");
      assertEquals(ImmutableSet.of("DelaySeconds"), options.buildFormParameters().get("Attribute.1.Name"));
      assertEquals(ImmutableSet.of("1"), options.buildFormParameters().get("Attribute.1.Value"));
   }

   public void testAttributeStatic() {
      CreateQueueOptions options = attribute("DelaySeconds", "1");
      assertEquals(ImmutableSet.of("DelaySeconds"), options.buildFormParameters().get("Attribute.1.Name"));
      assertEquals(ImmutableSet.of("1"), options.buildFormParameters().get("Attribute.1.Value"));
   }

}
