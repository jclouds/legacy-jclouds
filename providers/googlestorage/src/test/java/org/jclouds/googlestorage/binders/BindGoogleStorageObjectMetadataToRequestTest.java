package org.jclouds.googlestorage.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.s3.BaseS3AsyncClientTest;
import org.jclouds.s3.domain.S3Object;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code BindGoogleStorageObjectMetadataToRequest}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BindGoogleStorageObjectMetadataToRequestTest")
public class BindGoogleStorageObjectMetadataToRequestTest extends BaseS3AsyncClientTest {
 

   @Test
   public void testPassWithMinimumDetailsAndPayload5GB() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindGoogleStorageObjectMetadataToRequest binder = injector.getInstance(BindGoogleStorageObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object), HttpRequest.builder().method("PUT").endpoint(
               URI.create("http://localhost")).build());
   }

   @Test
   public void testExtendedPropertiesBind() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");
      object.getMetadata().getUserMetadata().putAll(ImmutableMap.of("foo", "bar"));

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindGoogleStorageObjectMetadataToRequest binder = injector.getInstance(BindGoogleStorageObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object), HttpRequest.builder().method("PUT").endpoint(
               URI.create("http://localhost")).headers(ImmutableMultimap.of("x-amz-meta-foo", "bar")).build());
   }

   public void testNoContentLengthIsChunked() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(null);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindGoogleStorageObjectMetadataToRequest binder = injector.getInstance(BindGoogleStorageObjectMetadataToRequest.class);

      assertEquals(binder.bindToRequest(request, object), HttpRequest.builder().method("PUT").endpoint(
               URI.create("http://localhost")).headers(ImmutableMultimap.of("Transfer-Encoding", "chunked")).build());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoNameIsBad() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5368709120000l);
      object.setPayload(payload);

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindGoogleStorageObjectMetadataToRequest binder = injector.getInstance(BindGoogleStorageObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testOver5GBIsBad() {
      S3Object object = injector.getInstance(S3Object.Factory.class).create(null);
      Payload payload = Payloads.newStringPayload("");
      payload.getContentMetadata().setContentLength(5 * 1024 * 1024 * 1024l + 1);
      object.setPayload(payload);
      object.getMetadata().setKey("foo");

      HttpRequest request = HttpRequest.builder().method("PUT").endpoint(URI.create("http://localhost")).build();
      BindGoogleStorageObjectMetadataToRequest binder = injector.getInstance(BindGoogleStorageObjectMetadataToRequest.class);
      binder.bindToRequest(request, object);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeS3Object() {
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      injector.getInstance(BindGoogleStorageObjectMetadataToRequest.class).bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      BindGoogleStorageObjectMetadataToRequest binder = injector.getInstance(BindGoogleStorageObjectMetadataToRequest.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(URI.create("http://momma")).build();
      binder.bindToRequest(request, null);
   }

}
