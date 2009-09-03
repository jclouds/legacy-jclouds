package org.jclouds.azure.storage.queue;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.UndeclaredThrowableException;
import java.security.SecureRandom;

import org.jclouds.azure.storage.domain.MetadataList;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code AzureQueueConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "cloudservers.AzureQueueConnectionLiveTest")
public class AzureQueueConnectionLiveTest {

   protected static final String sysAzureStorageAccount = System
            .getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT);
   protected static final String sysAzureStorageKey = System
            .getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY);
   protected AzureQueueConnection connection;

   private String queuePrefix = System.getProperty("user.name") + "-azurequeue";

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      Injector injector = AzureQueueContextBuilder.newBuilder(sysAzureStorageAccount,
               sysAzureStorageKey).withModules(new Log4JLoggingModule()).withSaxDebug()
               .buildInjector();
      connection = injector.getInstance(AzureQueueConnection.class);
   }

   @Test
   public void testListQueues() throws Exception {

      MetadataList<QueueMetadata> response = connection.listQueues();
      assert null != response;
      long initialQueueCount = response.getMetadata().size();
      assertTrue(initialQueueCount >= 0);

   }

   String privateQueue;

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateQueue() throws Exception {
      boolean created = false;
      while (!created) {
         privateQueue = queuePrefix + new SecureRandom().nextInt();
         try {
            created = connection.createQueue(privateQueue, CreateOptions.Builder
                     .withMetadata(ImmutableMultimap.of("foo", "bar")));
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }
      MetadataList<QueueMetadata> response = connection.listQueues();
      assert null != response;
      long queueCount = response.getMetadata().size();
      assertTrue(queueCount >= 1);
      // TODO ... check to see the queue actually exists
   }

   @Test
   public void testListQueuesWithOptions() throws Exception {

      MetadataList<QueueMetadata> response = connection.listQueues(ListOptions.Builder.prefix(
               privateQueue).maxResults(1));
      assert null != response;
      long initialQueueCount = response.getMetadata().size();
      assertTrue(initialQueueCount >= 0);
      assertEquals(privateQueue, response.getPrefix());
      assertEquals(1, response.getMaxResults());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateQueue" })
   public void testDeleteQueue() throws Exception {
      assert connection.deleteQueue(privateQueue);
      // TODO loop for up to 30 seconds checking if they are really gone
   }

}
