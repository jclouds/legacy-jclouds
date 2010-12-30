package org.jclouds.atmosonline.saas.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.blobstore.KeyAlreadyExistsException;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ReturnEndpointIfAlreadyExistsTest {

   @Test
   public void testFoundIsNullWhenEndpointNotSet() {
      assertEquals(new ReturnEndpointIfAlreadyExists().apply(new KeyAlreadyExistsException()), null);
   }

   @Test
   public void testFoundIsEndpointWhenSet() {
      assertEquals(
            new ReturnEndpointIfAlreadyExists().setEndpoint(URI.create("foo")).apply(new KeyAlreadyExistsException()),
            URI.create("foo"));
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testNotFoundPropagates() {
      new ReturnEndpointIfAlreadyExists().apply(new RuntimeException());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      new ReturnEndpointIfAlreadyExists().apply(null);
   }
}
