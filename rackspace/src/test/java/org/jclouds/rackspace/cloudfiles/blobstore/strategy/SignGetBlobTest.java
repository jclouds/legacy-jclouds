package org.jclouds.rackspace.cloudfiles.blobstore.strategy;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesRestClientModule;
import org.jclouds.rackspace.cloudservers.TestRackspaceAuthenticationRestClientModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SignGetBlob}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.SignGetBlobTest")
public class SignGetBlobTest extends RestClientTest<CloudFilesAsyncClient> {

   public void testSignGetBlob() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      HttpRequest request = new SignGetObject(processor).apply("container", "blob");

      assertRequestLineEquals(request, "GET http://storageUrl/container/blob HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-Auth-Token: testtoken\n");
      assertPayloadEquals(request, null, null, false);

      assertEquals(request.getFilters().size(), 0);
   }

   protected Module createModule() {
      return new CloudFilesRestClientModule(new TestRackspaceAuthenticationRestClientModule());
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<CloudFilesAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CloudFilesAsyncClient>>() {
      };
   }

   @Override
   public ContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("cloudfiles", "identity", "credential", new Properties());
   }

}