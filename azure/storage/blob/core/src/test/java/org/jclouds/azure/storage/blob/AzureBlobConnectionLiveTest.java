package org.jclouds.azure.storage.blob;

import static org.testng.Assert.assertTrue;

import org.jclouds.azure.storage.blob.domain.ContainerMetadataList;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

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

}
