package org.jclouds.aws.s3.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BindNoBucketLoggingToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.BindNoBucketLoggingToXmlPayloadTest")
public class BindNoBucketLoggingToXmlPayloadTest extends BaseHandlerTest {

   public void testApplyInputStream() throws IOException {

      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindNoBucketLoggingToXmlPayload binder = injector
               .getInstance(BindNoBucketLoggingToXmlPayload.class);

      binder.bindToRequest(request, null);
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), "text/xml");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH), "70");
      assertEquals(request.getPayload().getRawContent(),
               "<BucketLoggingStatus xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"/>");

   }
}
