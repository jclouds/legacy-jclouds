package org.jclouds.nirvanix.sdn.decorators;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.RuntimeDelegateImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code AddMetadataAsQueryParams}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sdn.AddMetadataAsQueryParamsTest")
public class AddMetadataAsQueryParamsTest {
   static {
      RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeMap() {
      AddMetadataAsQueryParams binder = new AddMetadataAsQueryParams();
      HttpRequest request = new HttpRequest(HttpMethod.POST, URI.create("http://localhost"));
      request = binder.decorateRequest(request, new File("foo"));
   }

   @Test
   public void testCorrect() {
      AddMetadataAsQueryParams binder = new AddMetadataAsQueryParams();
      HttpRequest request = new HttpRequest(HttpMethod.PUT, URI.create("http://localhost"));
      request = binder.decorateRequest(request, ImmutableMultimap.of("imageName", "foo", "serverId", "2") );
      assertEquals(request.getEndpoint().getQuery(), "metadata=imagename:foo&metadata=serverid:2");
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      AddMetadataAsQueryParams binder = new AddMetadataAsQueryParams();
      HttpRequest request = new HttpRequest(HttpMethod.PUT, URI.create("http://localhost"));
      request = binder.decorateRequest(request,null );
   }
}
