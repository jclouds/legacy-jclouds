package org.jclouds.azure.storage.filters;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import static org.testng.Assert.assertTrue;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.Query;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.config.JaxrsModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "azure.SharedKeyAuthenticationLiveTest")
public class SharedKeyAuthenticationLiveTest {

   @RequestFilters(SharedKeyAuthentication.class)
   public interface IntegrationTestClient {

      @GET
      @Path("/")
      @Query(key = "comp", value = "list")
      String authenticate();

   }

   protected static final String sysAzureStorageAccount = System
            .getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT);
   protected static final String sysAzureStorageKey = System
            .getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY);
   private Injector injector;
   private IntegrationTestClient client;
   private String uri;

   @Test
   public void testAuthentication() throws Exception {
      String response = client.authenticate();
      assertTrue(response.contains(uri), String.format("expected %s to contain %s", response, uri));
   }

   @BeforeClass
   void setupFactory() {
      injector = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bindConstant().annotatedWith(
                     Names.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT)).to(
                     sysAzureStorageAccount);
            bindConstant().annotatedWith(
                     Names.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY)).to(
                     sysAzureStorageKey);
         }

      }, new JaxrsModule(), new Log4JLoggingModule(), new ExecutorServiceModule(
               new WithinThreadExecutorService()), new JavaUrlHttpCommandExecutorServiceModule());
      RestClientFactory factory = injector.getInstance(RestClientFactory.class);
      uri = "http://" + sysAzureStorageAccount + ".blob.core.windows.net";
      client = factory.create(URI.create(uri), IntegrationTestClient.class);
   }
}
