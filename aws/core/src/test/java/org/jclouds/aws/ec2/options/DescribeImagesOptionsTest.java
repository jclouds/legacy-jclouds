package org.jclouds.aws.ec2.options;

import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.executableBy;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.ownedBy;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of DescribeImagesOptions and DescribeImagesOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class DescribeImagesOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(DescribeImagesOptions.class);
      assert !String.class.isAssignableFrom(DescribeImagesOptions.class);
   }

   @Test
   public void testExecutableBy() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      options.executableBy("test");
      assertEquals(options.buildFormParameters().get("ExecutableBy"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullExecutableBy() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("ExecutableBy"), Collections.EMPTY_LIST);
   }

   @Test
   public void testExecutableByStatic() {
      DescribeImagesOptions options = executableBy("test");
      assertEquals(options.buildFormParameters().get("ExecutableBy"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testExecutableByNPE() {
      executableBy(null);
   }

   @Test
   public void testOwners() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      options.ownedBy("test");
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.singletonList("test"));
   }

   @Test
   public void testMultipleOwners() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      options.ownedBy("test", "trouble");
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.singletonList("test"));
      assertEquals(options.buildFormParameters().get("Owner.2"), Collections
               .singletonList("trouble"));
   }

   @Test
   public void testNullOwners() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.EMPTY_LIST);
   }

   @Test
   public void testOwnersStatic() {
      DescribeImagesOptions options = ownedBy("test");
      assertEquals(options.buildFormParameters().get("Owner.1"), Collections.singletonList("test"));
   }

   public void testNoOwners() {
      ownedBy();
   }

   @Test
   public void testImageIds() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      options.imageIds("test");
      assertEquals(options.buildFormParameters().get("ImageId.1"), Collections
               .singletonList("test"));
   }

   @Test
   public void testMultipleImageIds() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      options.imageIds("test", "trouble");
      assertEquals(options.buildFormParameters().get("ImageId.1"), Collections
               .singletonList("test"));
      assertEquals(options.buildFormParameters().get("ImageId.2"), Collections
               .singletonList("trouble"));
   }

   @Test
   public void testNullImageIds() {
      DescribeImagesOptions options = new DescribeImagesOptions();
      assertEquals(options.buildFormParameters().get("ImageId.1"), Collections.EMPTY_LIST);
   }

   @Test
   public void testImageIdsStatic() {
      DescribeImagesOptions options = imageIds("test");
      assertEquals(options.buildFormParameters().get("ImageId.1"), Collections
               .singletonList("test"));
   }

   public void testNoImageIds() {
      imageIds();
   }
}
