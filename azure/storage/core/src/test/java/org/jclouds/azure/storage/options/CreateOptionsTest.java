package org.jclouds.azure.storage.options;

import static org.testng.Assert.assertEquals;

import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code CreateOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azurequeue.CreateOptionsTest")
public class CreateOptionsTest {

   public void testMetadata() {
      CreateOptions options = new CreateOptions().withMetadata(ImmutableMultimap.of(
               "test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
               AzureStorageHeaders.USER_METADATA_PREFIX + "test"));
   }

   public void testMetadataAlreadyPrefixed() {
      CreateOptions options = new CreateOptions().withMetadata(ImmutableMultimap.of(
               AzureStorageHeaders.USER_METADATA_PREFIX + "test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
               AzureStorageHeaders.USER_METADATA_PREFIX + "test"));
   }

   public void testMetadataStatic() {
      CreateOptions options = CreateOptions.Builder.withMetadata(ImmutableMultimap.of(
               "test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
               AzureStorageHeaders.USER_METADATA_PREFIX + "test"));
   }

}
