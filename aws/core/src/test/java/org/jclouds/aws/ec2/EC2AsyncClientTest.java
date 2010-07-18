package org.jclouds.aws.ec2;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.aws.ec2.services.BaseEC2AsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code EC2Client}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.EC2ClientTest")
public class EC2AsyncClientTest extends BaseEC2AsyncClientTest<EC2AsyncClient> {

   private EC2AsyncClient asyncClient;
   private EC2Client syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getAMIServices() != null;
      assert syncClient.getAvailabilityZoneAndRegionServices() != null;
      assert syncClient.getElasticBlockStoreServices() != null;
      assert syncClient.getElasticIPAddressServices() != null;
      assert syncClient.getInstanceServices() != null;
      assert syncClient.getKeyPairServices() != null;
      assert syncClient.getMonitoringServices() != null;
      assert syncClient.getSecurityGroupServices() != null;
      assert syncClient.getPlacementGroupServices() != null;
      assert syncClient.getWindowsServices() != null;

   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert asyncClient.getAMIServices() != null;
      assert asyncClient.getAvailabilityZoneAndRegionServices() != null;
      assert asyncClient.getElasticBlockStoreServices() != null;
      assert asyncClient.getElasticIPAddressServices() != null;
      assert asyncClient.getInstanceServices() != null;
      assert asyncClient.getKeyPairServices() != null;
      assert asyncClient.getMonitoringServices() != null;
      assert asyncClient.getSecurityGroupServices() != null;
      assert asyncClient.getPlacementGroupServices() != null;
      assert asyncClient.getWindowsServices() != null;
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<EC2AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<EC2AsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(EC2AsyncClient.class);
      syncClient = injector.getInstance(EC2Client.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }

}