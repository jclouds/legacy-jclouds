package org.jclouds.mezeo.pcs2;

import static org.testng.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.mezeo.pcs2.PCSCloud.Response;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.config.JaxrsModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code PCSDiscovery}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "pcs2.PCSCloudLiveTest")
public class PCSCloudLiveTest {

   String user = System.getProperty("jclouds.test.user");
   String password = System.getProperty("jclouds.test.key");

   private Injector injector;

   @Test
   public void testAuthentication() throws Exception {
      PCSCloud authentication = injector.getInstance(PCSCloud.class);
      Response response = authentication.authenticate();
      assertNotNull(response);
      assertNotNull(response.getContactsUrl());
      assertNotNull(response.getMetacontainersUrl());
      assertNotNull(response.getProjectsUrl());
      assertNotNull(response.getRecyclebinUrl());
      assertNotNull(response.getRootContainerUrl());
      assertNotNull(response.getSharesUrl());
      assertNotNull(response.getTagsUrl());
   }

   @BeforeClass
   void setupFactory() {
      injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(PCS.class).toInstance(
                     URI.create("https://pcsbeta.mezeo.net/v2"));
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         public BasicAuthentication provideBasicAuthentication()
                  throws UnsupportedEncodingException {
            return new BasicAuthentication(user, password);
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         protected PCSCloud provideCloud(RestClientFactory factory) {
            return factory.create(PCSCloud.class);
         }
      }, new JaxrsModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());
   }
}
