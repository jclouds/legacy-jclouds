package org.jclouds.azure.storage.blob.options;

import static org.testng.Assert.assertEquals;

import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code CreateContainerOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azurestorage.CreateContainerOptionsTest")
public class CreateContainerOptionsTest {

   public void testPublicAcl() {
      CreateContainerOptions options = new CreateContainerOptions().withPublicAcl();
      assertEquals(ImmutableList.of("true"), options.buildRequestHeaders().get(
               "x-ms-prop-publicaccess"));
   }

   public void testPublicAclStatic() {
      CreateContainerOptions options = CreateContainerOptions.Builder.withPublicAcl();
      assertEquals(ImmutableList.of("true"), options.buildRequestHeaders().get(
               "x-ms-prop-publicaccess"));
   }

   public void testMetadata() {
      CreateContainerOptions options = new CreateContainerOptions().withMetadata(ImmutableMultimap
               .of("test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
               AzureStorageHeaders.USER_METADATA_PREFIX + "test"));
   }

   public void testMetadataAlreadyPrefixed() {
      CreateContainerOptions options = new CreateContainerOptions().withMetadata(ImmutableMultimap
               .of(AzureStorageHeaders.USER_METADATA_PREFIX + "test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
               AzureStorageHeaders.USER_METADATA_PREFIX + "test"));
   }

   public void testMetadataStatic() {
      CreateContainerOptions options = CreateContainerOptions.Builder
               .withMetadata(ImmutableMultimap.of("test", "foo"));
      assertEquals(ImmutableList.of("foo"), options.buildRequestHeaders().get(
               AzureStorageHeaders.USER_METADATA_PREFIX + "test"));
   }

}
