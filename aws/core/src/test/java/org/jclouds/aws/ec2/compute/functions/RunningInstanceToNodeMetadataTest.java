/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.compute.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.testng.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.compute.domain.RegionTag;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.functions.RunningInstanceToStorageMappingUnix;
import org.jclouds.aws.ec2.services.AMIClient;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.RunningInstanceToNodeMetadataTest")
public class RunningInstanceToNodeMetadataTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testApplyWithNoKeyPairCreatesTagOfIdPrefixedByTagAndNullCredentials()
            throws UnknownHostException {
      AMIClient amiClient = createMock(AMIClient.class);
      Map<RegionTag, KeyPair> credentialsMap = createMock(Map.class);
      Map<String, org.jclouds.compute.domain.Image> images = createMock(Map.class);
      Map<String, Location> locations = createMock(Map.class);
      PopulateDefaultLoginCredentialsForImageStrategy credentialProvider = createMock(PopulateDefaultLoginCredentialsForImageStrategy.class);
      RunningInstance instance = createMock(RunningInstance.class);

      expect(instance.getId()).andReturn("id").atLeastOnce();
      expect(instance.getKeyName()).andReturn(null).atLeastOnce();
      expect(instance.getInstanceState()).andReturn(InstanceState.RUNNING);
      Location location = new LocationImpl(LocationScope.ZONE, "us-east-1a", "description", null);
      expect(locations.get("us-east-1a")).andReturn(location);

      org.jclouds.compute.domain.Image jcImage = createNiceMock(org.jclouds.compute.domain.Image.class);
      expect(images.get("imageId")).andReturn(jcImage);

      expect(instance.getIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 12, 10, 10, 1 }));
      expect(instance.getPrivateIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 10, 10, 10, 1 }));

      expect(instance.getAvailabilityZone()).andReturn(AvailabilityZone.US_EAST_1A).atLeastOnce();

      expect(instance.getImageId()).andReturn("imageId").atLeastOnce();

      expect(instance.getInstanceType()).andReturn(InstanceType.C1_XLARGE).atLeastOnce();

      replay(amiClient);
      replay(credentialsMap);
      replay(credentialProvider);
      replay(instance);
      replay(images);
      replay(locations);

      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(amiClient,
               credentialsMap, credentialProvider, images, locations,
               new RunningInstanceToStorageMappingUnix());

      NodeMetadata metadata = parser.apply(instance);
      assertEquals(metadata.getLocation(), location);
      assertEquals(metadata.getImage(), jcImage);
      assertEquals(metadata.getTag(), "NOTAG-id");
      assertEquals(metadata.getCredentials(), null);

      verify(amiClient);
      verify(credentialsMap);
      verify(credentialProvider);
      verify(instance);
      verify(images);
      verify(locations);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testApplyWithKeyPairCreatesTagOfParsedKeyPairAndCredentialsBasedOnIt()
            throws UnknownHostException {
      AMIClient amiClient = createMock(AMIClient.class);
      Map<RegionTag, KeyPair> credentialsMap = createMock(Map.class);
      Map<String, org.jclouds.compute.domain.Image> images = createMock(Map.class);
      Map<String, Location> locations = createMock(Map.class);

      PopulateDefaultLoginCredentialsForImageStrategy credentialProvider = createMock(PopulateDefaultLoginCredentialsForImageStrategy.class);
      RunningInstance instance = createMock(RunningInstance.class);
      Image image = createMock(Image.class);

      expect(instance.getId()).andReturn("id").atLeastOnce();
      expect(instance.getKeyName()).andReturn("keyName-100").atLeastOnce();
      expect(instance.getInstanceState()).andReturn(InstanceState.RUNNING);

      Location location = new LocationImpl(LocationScope.ZONE, "us-east-1a", "description", null);
      expect(locations.get("us-east-1a")).andReturn(location);

      org.jclouds.compute.domain.Image jcImage = createNiceMock(org.jclouds.compute.domain.Image.class);
      expect(images.get("imageId")).andReturn(jcImage);

      expect(instance.getIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 12, 10, 10, 1 }));
      expect(instance.getPrivateIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 10, 10, 10, 1 }));

      expect(instance.getRegion()).andReturn(Region.US_EAST_1).atLeastOnce();

      expect(instance.getImageId()).andReturn("imageId").atLeastOnce();

      expect(amiClient.describeImagesInRegion(Region.US_EAST_1, imageIds("imageId"))).andReturn(
               ImmutableSet.<Image> of(image));

      expect(credentialProvider.execute(image)).andReturn(new Credentials("user", "pass"));

      expect(credentialsMap.get(new RegionTag(Region.US_EAST_1, "keyName-100"))).andReturn(
               new KeyPair(Region.US_EAST_1, "keyName-100", "keyFingerprint", "pass"));

      expect(instance.getAvailabilityZone()).andReturn(AvailabilityZone.US_EAST_1A).atLeastOnce();

      expect(instance.getInstanceType()).andReturn(InstanceType.C1_XLARGE).atLeastOnce();

      replay(amiClient);
      replay(credentialsMap);
      replay(credentialProvider);
      replay(instance);
      replay(images);
      replay(locations);

      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(amiClient,
               credentialsMap, credentialProvider, images, locations,
               new RunningInstanceToStorageMappingUnix());

      NodeMetadata metadata = parser.apply(instance);

      assertEquals(metadata.getTag(), "keyName");
      assertEquals(metadata.getLocation(), location);
      assertEquals(metadata.getImage(), jcImage);

      assertEquals(metadata.getCredentials(), new Credentials("user", "pass"));

      verify(amiClient);
      verify(credentialsMap);
      verify(credentialProvider);
      verify(instance);
      verify(images);
      verify(locations);
   }

}
