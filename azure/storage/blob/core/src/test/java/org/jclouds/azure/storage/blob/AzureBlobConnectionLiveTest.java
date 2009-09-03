package org.jclouds.azure.storage.blob;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.security.SecureRandom;

import org.jclouds.azure.storage.AzureStorageResponseException;
import org.jclouds.azure.storage.blob.domain.ContainerMetadataList;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code AzureBlobConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "cloudservers.AzureBlobConnectionLiveTest")
public class AzureBlobConnectionLiveTest {

   protected static final String sysAzureStorageAccount = System
            .getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT);
   protected static final String sysAzureStorageKey = System
            .getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY);
   protected AzureBlobConnection connection;

   private String containerPrefix = System.getProperty("user.name") + "-azureblob";

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      Injector injector = AzureBlobContextBuilder.newBuilder(sysAzureStorageAccount,
               sysAzureStorageKey).withModules(new Log4JLoggingModule()).withSaxDebug()
               .buildInjector();
      connection = injector.getInstance(AzureBlobConnection.class);
   }

   @Test
   public void testListContainers() throws Exception {

      ContainerMetadataList response = connection.listContainers();
      assert null != response;
      long initialContainerCount = response.getContainerMetadata().size();
      assertTrue(initialContainerCount >= 0);

   }

   String privateContainer;
   String publicContainer;

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateContainer() throws Exception {
      boolean created = false;
      while (!created) {
         privateContainer = containerPrefix + new SecureRandom().nextInt();
         try {
            created = connection.createContainer(privateContainer, CreateContainerOptions.Builder
                     .withMetadata(ImmutableMultimap.of("foo", "bar")));
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }
      ContainerMetadataList response = connection.listContainers();
      assert null != response;
      long containerCount = response.getContainerMetadata().size();
      assertTrue(containerCount >= 1);
      // TODO ... check to see the container actually exists
   }

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreatePublicContainer() throws Exception {
      boolean created = false;
      while (!created) {
         publicContainer = containerPrefix + new SecureRandom().nextInt();
         try {
            created = connection.createContainer(publicContainer, CreateContainerOptions.Builder
                     .withPublicAcl());
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }

      URL url = new URL(String.format("http://%s.blob.core.windows.net/%s", sysAzureStorageAccount,
               publicContainer));
      Utils.toStringAndClose(url.openStream());
   }

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreatePublicRootContainer() throws Exception {
      try {
         connection.deleteRootContainer();
      } catch (Exception e) {
         // don't care.. we wish to recreate it.
      }
      boolean created = false;
      while (!created) {
         try {
            created = connection.createRootContainer();
         } catch (UndeclaredThrowableException e) {
            AzureStorageResponseException htpe = (AzureStorageResponseException) e.getCause()
                     .getCause();
            if (htpe.getError().getCode().equals("ContainerBeingDeleted")) {
               Thread.sleep(5000);
               continue;
            }
            throw e;
         }
      }
      // TODO check if it really exists.
   }

   @Test
   public void testListContainersWithOptions() throws Exception {

      ContainerMetadataList response = connection.listContainers(ListOptions.Builder.prefix(
               privateContainer).maxResults(1));
      assert null != response;
      long initialContainerCount = response.getContainerMetadata().size();
      assertTrue(initialContainerCount >= 0);
      assertEquals(privateContainer, response.getPrefix());
      assertEquals(1, response.getMaxResults());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateContainer",
            "testCreatePublicContainer" })
   public void testDeleteContainer() throws Exception {
      assert connection.deleteContainer(privateContainer);
      assert connection.deleteContainer(publicContainer);
      // TODO loop for up to 30 seconds checking if they are really gone
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreatePublicRootContainer" })
   public void testDeleteRootContainer() throws Exception {
      assert connection.deleteRootContainer();
      // TODO loop for up to 30 seconds checking if they are really gone
   }
}
