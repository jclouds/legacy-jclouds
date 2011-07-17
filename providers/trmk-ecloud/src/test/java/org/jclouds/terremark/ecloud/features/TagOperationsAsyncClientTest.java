package org.jclouds.terremark.ecloud.features;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.functions.ReturnEmptyMapOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.terremark.ecloud.BaseTerremarkECloudAsyncClientTest;
import org.jclouds.terremark.ecloud.xml.TagNameToUsageCountHandler;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TagOperationsAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "TagOperationsAsyncClientTest")
public class TagOperationsAsyncClientTest extends BaseTerremarkECloudAsyncClientTest<TagOperationsAsyncClient> {

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TagOperationsAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TagOperationsAsyncClient>>() {
      };
   }

   public void testgetTagNameToUsageCount() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TagOperationsAsyncClient.class.getMethod("getTagNameToUsageCount", URI.class);
      HttpRequest request = processor
            .createRequest(
                  method,
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/deviceTags"));

      assertRequestLineEquals(request,
            "GET https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/deviceTags HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.tagsList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TagNameToUsageCountHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptyMapOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testgetTagNameToUsageCountInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TagOperationsAsyncClient.class.getMethod("getTagNameToUsageCountInOrg", URI.class);
      HttpRequest request = processor.createRequest(method,
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/deviceTags/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.tagsList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TagNameToUsageCountHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptyMapOnNotFoundOr404.class);

      checkFilters(request);
   }

}
