package org.jclouds.aws.ec2.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.testng.annotations.Test;

/**
 * Tests behavior of {@code EncodedRSAPublicKeyToBase64}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class EncodedRSAPublicKeyToBase64Test {
   EncodedRSAPublicKeyToBase64 function = new EncodedRSAPublicKeyToBase64();

   public void testAllowedMarkers() throws IOException {
      assertEquals(function.apply("-----BEGIN CERTIFICATE-----"), "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0t");
      assertEquals(function.apply("ssh-rsa"), "c3NoLXJzYQ==");
      assertEquals(function.apply("---- BEGIN SSH2 PUBLIC KEY ----"), "LS0tLSBCRUdJTiBTU0gyIFBVQkxJQyBLRVkgLS0tLQ==");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testDisallowedMarkersIllegalArgument() throws IOException {
      function.apply("ssh-dsa");
   }
}
