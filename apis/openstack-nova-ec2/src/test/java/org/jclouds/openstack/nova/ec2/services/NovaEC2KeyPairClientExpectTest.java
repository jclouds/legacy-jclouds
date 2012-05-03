package org.jclouds.openstack.nova.ec2.services;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.ec2.internal.BaseNovaEC2RestClientExpectTest;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adam Lowe
 */
public class NovaEC2KeyPairClientExpectTest extends BaseNovaEC2RestClientExpectTest {

   public void testImportKeyPair() {
      NovaEC2KeyPairClient client = requestsSendResponses(
            describeAvailabilityZonesRequest,
            describeAvailabilityZonesResponse,
            HttpRequest.builder().method("POST")
                  .endpoint(URI.create("http://localhost:8773/services/Cloud/"))
                  .headers(ImmutableMultimap.of("Host", "localhost:8773"))
                  .payload(payloadFromStringWithContentType("Action=ImportKeyPair&KeyName=mykey&PublicKeyMaterial=c3NoLXJzYSBBQQ%3D%3D&Signature=wOOKOlDfJezRkx7NKcyOyaBQuY7PoVE3HFa9495RL7s%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2009-04-04&AWSAccessKeyId=identity", "application/x-www-form-urlencoded")).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/nova_ec2_import_keypair_response.xml")).build()
      ).getKeyPairServices();
      
      KeyPair result = client.importKeyPairInRegion(null, "mykey", "ssh-rsa AA");
      assertEquals(result.getKeyName(), "aplowe-nova-ec22");
      assertEquals(result.getSha1OfPrivateKey(), "e3:fd:de:f6:4c:36:7d:9b:8f:2f:4c:20:f8:ae:b0:ea");
   }

}
