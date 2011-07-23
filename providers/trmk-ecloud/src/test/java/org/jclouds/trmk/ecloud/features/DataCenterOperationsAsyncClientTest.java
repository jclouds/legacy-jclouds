package org.jclouds.trmk.ecloud.features;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.trmk.ecloud.BaseTerremarkECloudAsyncClientTest;
import org.jclouds.trmk.ecloud.features.DataCenterOperationsAsyncClient;
import org.jclouds.trmk.vcloud_0_8.xml.DataCentersHandler;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code DataCenterOperationsAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "DataCenterOperationsAsyncClientTest")
public class DataCenterOperationsAsyncClientTest extends
      BaseTerremarkECloudAsyncClientTest<DataCenterOperationsAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<DataCenterOperationsAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<DataCenterOperationsAsyncClient>>() {
      };
   }

   public void testlistDataCenters() throws SecurityException, NoSuchMethodException, IOException {
      Method method = DataCenterOperationsAsyncClient.class.getMethod("listDataCenters", URI.class);
      HttpRequest request = processor
            .createRequest(
                  method,
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/dataCenters"));

      assertRequestLineEquals(request,
            "GET https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/dataCenters HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.dataCentersList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DataCentersHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testlistDataCentersInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = DataCenterOperationsAsyncClient.class.getMethod("listDataCentersInOrg", URI.class);
      HttpRequest request = processor.createRequest(method,
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/datacentersList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.dataCentersList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DataCentersHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

}
