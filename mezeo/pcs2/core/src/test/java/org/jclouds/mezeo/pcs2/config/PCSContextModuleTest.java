package org.jclouds.mezeo.pcs2.config;

import static org.testng.Assert.assertEquals;

import org.jclouds.blobstore.BlobStoreMapsModule;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.http.handlers.CloseContentAndSetExceptionErrorHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;
import org.jclouds.mezeo.pcs2.PCSBlobStore;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.handlers.PCSClientErrorRetryHandler;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSContextModuleTest")
public class PCSContextModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new RestPCSBlobStoreModule(),
               new BlobStoreMapsModule<PCSBlobStore, ContainerMetadata, FileMetadata, PCSFile>(
                        new TypeLiteral<PCSBlobStore>() {
                        }, new TypeLiteral<ContainerMetadata>() {
                        }, new TypeLiteral<FileMetadata>() {
                        }, new TypeLiteral<PCSFile>() {
                        }), new PCSContextModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(Jsr330.named(PCSConstants.PROPERTY_PCS2_USER))
                              .to("user");
                     bindConstant()
                              .annotatedWith(Jsr330.named(PCSConstants.PROPERTY_PCS2_PASSWORD)).to(
                                       "key");
                     bindConstant()
                              .annotatedWith(Jsr330.named(PCSConstants.PROPERTY_PCS2_ENDPOINT)).to(
                                       "http://localhost");
                     super.configure();
                  }
               }, new ParserModule(), new JavaUrlHttpCommandExecutorServiceModule(),
               new ExecutorServiceModule(new WithinThreadExecutorService()));
   }

   @Test
   void testServerErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getServerErrorHandler().getClass(),
               CloseContentAndSetExceptionErrorHandler.class);
   }

   @Test
   void testClientErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getClientErrorHandler().getClass(),
               CloseContentAndSetExceptionErrorHandler.class);
   }

   @Test
   void testClientRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getClientErrorRetryHandler().getClass(),
               PCSClientErrorRetryHandler.class);
   }

   @Test
   void testRedirectionRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getRedirectionRetryHandler().getClass(), RedirectionRetryHandler.class);
   }

}