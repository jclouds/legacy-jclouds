package org.jclouds.rimuhosting.miro;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.rimuhosting.miro.binder.CreateServerOptions;
import org.jclouds.rimuhosting.miro.filters.RimuHostingAuthentication;
import org.jclouds.rimuhosting.miro.functions.ParseRimuHostingException;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code RimuHostingAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "rimuhosting.RimuHostingAsyncClientTest")
public class RimuHostingAsyncClientTest extends RestClientTest<RimuHostingAsyncClient> {

   public void testCreateServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = RimuHostingAsyncClient.class.getMethod("createServer", String.class, String.class, String.class,
            CreateServerOptions[].class);
      GeneratedHttpRequest<RimuHostingAsyncClient> httpRequest = processor.createRequest(method, "test.ivan.api.com",
            "lenny", "MIRO1B");

      assertRequestLineEquals(httpRequest, "POST https://rimuhosting.com/r/orders/new-vps HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(
            httpRequest,
            "{\"request\":{\"instantiation_options\":{\"distro\":\"lenny\",\"domain_name\":\"test.ivan.api.com\"},\"pricing_plan_code\":\"MIRO1B\",\"meta_data\":[]}}",
            "application/json", false);
      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ParseRimuHostingException.class);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), RimuHostingAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<RimuHostingAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<RimuHostingAsyncClient>>() {
      };
   }

   @Override
   public ContextSpec<RimuHostingClient, RimuHostingAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("rimuhosting", "apikey", "null", new Properties());
   }
}
