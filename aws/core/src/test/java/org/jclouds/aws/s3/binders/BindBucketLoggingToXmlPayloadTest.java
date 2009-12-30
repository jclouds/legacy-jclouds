package org.jclouds.aws.s3.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.domain.BucketLogging;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.Grant;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code BindBucketLoggingToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.BindBucketLoggingToXmlPayloadTest")
public class BindBucketLoggingToXmlPayloadTest  extends BaseHandlerTest {

   public void testApplyInputStream() throws IOException {
      
      BucketLogging bucketLogging = new BucketLogging("mylogs", "access_log-", ImmutableSet
               .<Grant> of(new Grant(new EmailAddressGrantee("adrian@jclouds.org"),
                        Permission.FULL_CONTROL)));
     
      String expected = Utils.toStringAndClose(getClass().getResourceAsStream(
               "/s3/bucket_logging.xml"));
      
      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindBucketLoggingToXmlPayload binder = injector
               .getInstance(BindBucketLoggingToXmlPayload.class);

      binder.bindToRequest(request, bucketLogging);
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), "text/xml");
      assertEquals(request.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH), "433");
      assertEquals(request.getPayload().getRawContent(), expected);

   }
}
