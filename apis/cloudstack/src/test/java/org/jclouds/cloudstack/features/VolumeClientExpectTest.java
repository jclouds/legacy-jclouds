package org.jclouds.cloudstack.features;

import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static org.testng.Assert.assertNotNull;

/**
* Test the CloudStack VolumeClient
*
* @author Adam Lowe
*/
@Test(groups = "unit", testName = "VolumeClientExpectTest")
public class VolumeClientExpectTest extends BaseCloudStackExpectTest<VolumeClient> {

   public void testCreateVolumeFromCustomDiskOffering() throws NoSuchAlgorithmException, CertificateException {
      VolumeClient client = requestSendsResponse(
              HttpRequest.builder()
                      .method("GET")
                      .endpoint(
                              URI.create("http://localhost:8080/client/api?response=json&" +
                                      "command=createVolume&name=VolumeClientExpectTest-jclouds-volume&diskofferingid=0473f5dd-bca5-4af4-a9b6-db9e8a88a2f6&zoneid=6f9a2921-b22a-4149-8b71-6ffc275a2177&size=1&apiKey=identity&signature=Y4%2BmdvhS/jlKRNSJ3nQqrjwg1CY%3D"))
                      .addHeader("Accept", "application/json")
                      .build(),
              HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResource("/queryasyncjobresultresponse-createvolume.json"))
                      .build());
      
      AsyncCreateResponse response = client.createVolumeFromCustomDiskOfferingInZone("VolumeClientExpectTest-jclouds-volume", "0473f5dd-bca5-4af4-a9b6-db9e8a88a2f6", "6f9a2921-b22a-4149-8b71-6ffc275a2177", 1);
      assertNotNull(response);
   }

   @Override
   protected VolumeClient clientFrom(CloudStackContext context) {
      return context.unwrap(CloudStackApiMetadata.CONTEXT_TOKEN).getApi().getVolumeClient();
   }
}
