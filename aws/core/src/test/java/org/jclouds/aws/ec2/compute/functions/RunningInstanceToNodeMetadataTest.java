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
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.testng.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Provider;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.compute.domain.RegionAndName;
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
   private static class ImageProvider implements
            Provider<Set<? extends org.jclouds.compute.domain.Image>> {
      private final Set<? extends org.jclouds.compute.domain.Image> images;

      private ImageProvider(org.jclouds.compute.domain.Image jcImage) {
         this.images = ImmutableSet.<org.jclouds.compute.domain.Image> of(jcImage);
      }

      @Override
      public Set<? extends org.jclouds.compute.domain.Image> get() {
         return images;
      }
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testImageNotFoundAndLazyReturnsNull() throws UnknownHostException {
      AMIClient amiClient = createMock(AMIClient.class);
      Map<RegionAndName, KeyPair> credentialsMap = createMock(Map.class);
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);

      ConcurrentMap<RegionAndName, org.jclouds.compute.domain.Image> imageMap = createMock(ConcurrentMap.class);

      Location location = new LocationImpl(LocationScope.ZONE, "us-east-1a", "description", null);
      Set<Location> locations = ImmutableSet.<Location> of(location);
      PopulateDefaultLoginCredentialsForImageStrategy credentialProvider = createMock(PopulateDefaultLoginCredentialsForImageStrategy.class);
      RunningInstance instance = createMock(RunningInstance.class);

      expect(instance.getId()).andReturn("id").atLeastOnce();
      expect(instance.getGroupIds()).andReturn(ImmutableSet.<String> of()).atLeastOnce();
      expect(instance.getKeyName()).andReturn(null).atLeastOnce();
      expect(instance.getInstanceState()).andReturn(InstanceState.RUNNING);

      expect(instance.getIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 12, 10, 10, 1 }));
      expect(instance.getPrivateIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 10, 10, 10, 1 }));

      expect(instance.getAvailabilityZone()).andReturn(AvailabilityZone.US_EAST_1A).atLeastOnce();

      expect(instance.getImageId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("notImageId").atLeastOnce();
      expect(instance.getRegion()).andReturn("us-east-1").atLeastOnce();

      expect(imageMap.get(new RegionAndName("us-east-1", "imageId"))).andReturn(null);

      expect(instance.getInstanceType()).andReturn(InstanceType.C1_XLARGE).atLeastOnce();

      replay(imageMap);
      replay(jcImage);
      replay(amiClient);
      replay(credentialsMap);
      replay(credentialProvider);
      replay(instance);

      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(amiClient,
               credentialsMap, credentialProvider, new ImageProvider(jcImage), imageMap, locations,
               new RunningInstanceToStorageMappingUnix());

      NodeMetadata metadata = parser.apply(instance);
      assertEquals(metadata.getLocation(), locations.iterator().next());
      assertEquals(metadata.getImage(), null);
      assertEquals(metadata.getTag(), "NOTAG-id");
      assertEquals(metadata.getCredentials(), null);

      verify(imageMap);
      verify(jcImage);
      verify(amiClient);
      verify(credentialsMap);
      verify(credentialProvider);
      verify(instance);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testImageNotFoundAndLazyFailsWithNPE() throws UnknownHostException {
      AMIClient amiClient = createMock(AMIClient.class);
      Map<RegionAndName, KeyPair> credentialsMap = createMock(Map.class);
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);

      ConcurrentMap<RegionAndName, org.jclouds.compute.domain.Image> imageMap = createMock(ConcurrentMap.class);

      Location location = new LocationImpl(LocationScope.ZONE, "us-east-1a", "description", null);
      Set<Location> locations = ImmutableSet.<Location> of(location);
      PopulateDefaultLoginCredentialsForImageStrategy credentialProvider = createMock(PopulateDefaultLoginCredentialsForImageStrategy.class);
      RunningInstance instance = createMock(RunningInstance.class);

      expect(instance.getId()).andReturn("id").atLeastOnce();
      expect(instance.getGroupIds()).andReturn(ImmutableSet.<String> of()).atLeastOnce();
      expect(instance.getKeyName()).andReturn(null).atLeastOnce();
      expect(instance.getInstanceState()).andReturn(InstanceState.RUNNING);

      expect(instance.getIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 12, 10, 10, 1 }));
      expect(instance.getPrivateIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 10, 10, 10, 1 }));

      expect(instance.getAvailabilityZone()).andReturn(AvailabilityZone.US_EAST_1A).atLeastOnce();

      expect(instance.getImageId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("notImageId").atLeastOnce();
      expect(instance.getRegion()).andReturn("us-east-1").atLeastOnce();

      expect(imageMap.get(new RegionAndName("us-east-1", "imageId"))).andThrow(
               new NullPointerException()).atLeastOnce();

      expect(instance.getInstanceType()).andReturn(InstanceType.C1_XLARGE).atLeastOnce();

      replay(imageMap);
      replay(jcImage);
      replay(amiClient);
      replay(credentialsMap);
      replay(credentialProvider);
      replay(instance);

      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(amiClient,
               credentialsMap, credentialProvider, new ImageProvider(jcImage), imageMap, locations,
               new RunningInstanceToStorageMappingUnix());

      NodeMetadata metadata = parser.apply(instance);
      assertEquals(metadata.getLocation(), locations.iterator().next());
      assertEquals(metadata.getImage(), null);
      assertEquals(metadata.getTag(), "NOTAG-id");
      assertEquals(metadata.getCredentials(), null);

      verify(imageMap);
      verify(jcImage);
      verify(amiClient);
      verify(credentialsMap);
      verify(credentialProvider);
      verify(instance);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testImageNotFoundAndLazySucceeds() throws UnknownHostException {
      AMIClient amiClient = createMock(AMIClient.class);
      Map<RegionAndName, KeyPair> credentialsMap = createMock(Map.class);
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);

      ConcurrentMap<RegionAndName, org.jclouds.compute.domain.Image> imageMap = createMock(ConcurrentMap.class);

      Location location = new LocationImpl(LocationScope.ZONE, "us-east-1a", "description", null);
      Set<Location> locations = ImmutableSet.<Location> of(location);
      PopulateDefaultLoginCredentialsForImageStrategy credentialProvider = createMock(PopulateDefaultLoginCredentialsForImageStrategy.class);
      RunningInstance instance = createMock(RunningInstance.class);

      expect(instance.getId()).andReturn("id").atLeastOnce();
      expect(instance.getGroupIds()).andReturn(ImmutableSet.<String> of()).atLeastOnce();
      expect(instance.getKeyName()).andReturn(null).atLeastOnce();
      expect(instance.getInstanceState()).andReturn(InstanceState.RUNNING);

      expect(instance.getIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 12, 10, 10, 1 }));
      expect(instance.getPrivateIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 10, 10, 10, 1 }));

      expect(instance.getAvailabilityZone()).andReturn(AvailabilityZone.US_EAST_1A).atLeastOnce();

      expect(instance.getImageId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("notImageId").atLeastOnce();
      expect(instance.getRegion()).andReturn("us-east-1").atLeastOnce();

      org.jclouds.compute.domain.Image lateImage = createMock(org.jclouds.compute.domain.Image.class);

      expect(imageMap.get(new RegionAndName("us-east-1", "imageId"))).andReturn(lateImage)
               .atLeastOnce();

      expect(instance.getInstanceType()).andReturn(InstanceType.C1_XLARGE).atLeastOnce();

      replay(lateImage);
      replay(imageMap);
      replay(jcImage);
      replay(amiClient);
      replay(credentialsMap);
      replay(credentialProvider);
      replay(instance);

      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(amiClient,
               credentialsMap, credentialProvider, new ImageProvider(jcImage), imageMap, locations,
               new RunningInstanceToStorageMappingUnix());

      NodeMetadata metadata = parser.apply(instance);
      assertEquals(metadata.getLocation(), locations.iterator().next());
      assertEquals(metadata.getImage(), lateImage);
      assertEquals(metadata.getTag(), "NOTAG-id");
      assertEquals(metadata.getCredentials(), null);

      verify(lateImage);
      verify(imageMap);
      verify(jcImage);
      verify(amiClient);
      verify(credentialsMap);
      verify(credentialProvider);
      verify(instance);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testApplyWithNoSecurityGroupCreatesTagOfIdPrefixedByTagAndNullCredentials()
            throws UnknownHostException {
      AMIClient amiClient = createMock(AMIClient.class);
      Map<RegionAndName, KeyPair> credentialsMap = createMock(Map.class);
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);

      ConcurrentMap<RegionAndName, org.jclouds.compute.domain.Image> imageMap = createMock(ConcurrentMap.class);

      Location location = new LocationImpl(LocationScope.ZONE, "us-east-1a", "description", null);
      Set<Location> locations = ImmutableSet.<Location> of(location);
      PopulateDefaultLoginCredentialsForImageStrategy credentialProvider = createMock(PopulateDefaultLoginCredentialsForImageStrategy.class);
      RunningInstance instance = createMock(RunningInstance.class);

      expect(instance.getId()).andReturn("id").atLeastOnce();
      expect(instance.getRegion()).andReturn("us-east-1").atLeastOnce();
      expect(instance.getGroupIds()).andReturn(ImmutableSet.<String> of()).atLeastOnce();
      expect(instance.getKeyName()).andReturn(null).atLeastOnce();
      expect(instance.getInstanceState()).andReturn(InstanceState.RUNNING);

      expect(instance.getIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 12, 10, 10, 1 }));
      expect(instance.getPrivateIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 10, 10, 10, 1 }));

      expect(instance.getAvailabilityZone()).andReturn(AvailabilityZone.US_EAST_1A).atLeastOnce();

      expect(instance.getImageId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getLocation()).andReturn(location).atLeastOnce();

      expect(instance.getInstanceType()).andReturn(InstanceType.C1_XLARGE).atLeastOnce();

      replay(imageMap);
      replay(jcImage);
      replay(amiClient);
      replay(credentialsMap);
      replay(credentialProvider);
      replay(instance);

      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(amiClient,
               credentialsMap, credentialProvider, new ImageProvider(jcImage), imageMap, locations,
               new RunningInstanceToStorageMappingUnix());

      NodeMetadata metadata = parser.apply(instance);
      assertEquals(metadata.getLocation(), locations.iterator().next());
      assertEquals(metadata.getImage(), jcImage);
      assertEquals(metadata.getTag(), "NOTAG-id");
      assertEquals(metadata.getCredentials(), null);

      verify(imageMap);
      verify(jcImage);
      verify(amiClient);
      verify(credentialsMap);
      verify(credentialProvider);
      verify(instance);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testApplyWithNoKeyPairCreatesTagOfParsedSecurityGroupAndNullCredentials()
            throws UnknownHostException {
      AMIClient amiClient = createMock(AMIClient.class);
      Map<RegionAndName, KeyPair> credentialsMap = createMock(Map.class);
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);
      ConcurrentMap<RegionAndName, org.jclouds.compute.domain.Image> imageMap = createMock(ConcurrentMap.class);

      Location location = new LocationImpl(LocationScope.ZONE, "us-east-1a", "description", null);
      Set<Location> locations = ImmutableSet.<Location> of(location);
      PopulateDefaultLoginCredentialsForImageStrategy credentialProvider = createMock(PopulateDefaultLoginCredentialsForImageStrategy.class);
      RunningInstance instance = createMock(RunningInstance.class);

      expect(instance.getId()).andReturn("id").atLeastOnce();
      expect(instance.getRegion()).andReturn("us-east-1").atLeastOnce();
      expect(instance.getGroupIds()).andReturn(ImmutableSet.of("jclouds#tag")).atLeastOnce();
      expect(instance.getKeyName()).andReturn(null).atLeastOnce();
      expect(instance.getInstanceState()).andReturn(InstanceState.RUNNING);

      expect(instance.getIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 12, 10, 10, 1 }));
      expect(instance.getPrivateIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 10, 10, 10, 1 }));

      expect(instance.getAvailabilityZone()).andReturn(AvailabilityZone.US_EAST_1A).atLeastOnce();

      expect(instance.getImageId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getLocation()).andReturn(location).atLeastOnce();

      expect(instance.getInstanceType()).andReturn(InstanceType.C1_XLARGE).atLeastOnce();

      replay(imageMap);
      replay(jcImage);
      replay(amiClient);
      replay(credentialsMap);
      replay(credentialProvider);
      replay(instance);

      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(amiClient,
               credentialsMap, credentialProvider, new ImageProvider(jcImage), imageMap, locations,
               new RunningInstanceToStorageMappingUnix());

      NodeMetadata metadata = parser.apply(instance);
      assertEquals(metadata.getLocation(), locations.iterator().next());
      assertEquals(metadata.getImage(), jcImage);
      assertEquals(metadata.getTag(), "tag");
      assertEquals(metadata.getCredentials(), null);

      verify(imageMap);
      verify(jcImage);
      verify(amiClient);
      verify(credentialsMap);
      verify(credentialProvider);
      verify(instance);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testApplyWithKeyPairCreatesTagOfParsedSecurityGroupAndCredentialsBasedOnIt()
            throws UnknownHostException {
      AMIClient amiClient = createMock(AMIClient.class);
      Map<RegionAndName, KeyPair> credentialsMap = createMock(Map.class);
      ConcurrentMap<RegionAndName, org.jclouds.compute.domain.Image> imageMap = createMock(ConcurrentMap.class);

      PopulateDefaultLoginCredentialsForImageStrategy credentialProvider = createMock(PopulateDefaultLoginCredentialsForImageStrategy.class);
      RunningInstance instance = createMock(RunningInstance.class);
      Image image = createMock(Image.class);

      expect(instance.getId()).andReturn("id").atLeastOnce();
      expect(instance.getGroupIds()).andReturn(ImmutableSet.of("jclouds#tag")).atLeastOnce();
      expect(instance.getKeyName()).andReturn("jclouds#keyName").atLeastOnce();
      expect(instance.getInstanceState()).andReturn(InstanceState.RUNNING);

      Location location = new LocationImpl(LocationScope.ZONE, "us-east-1a", "description", null);
      Set<Location> locations = ImmutableSet.<Location> of(location);
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);

      expect(instance.getIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 12, 10, 10, 1 }));
      expect(instance.getPrivateIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 10, 10, 10, 1 }));

      expect(instance.getRegion()).andReturn(Region.US_EAST_1).atLeastOnce();

      expect(instance.getImageId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getLocation()).andReturn(location).atLeastOnce();

      expect(amiClient.describeImagesInRegion(Region.US_EAST_1, imageIds("imageId"))).andReturn(
               ImmutableSet.<Image> of(image));

      expect(credentialProvider.execute(image)).andReturn(new Credentials("user", "pass"));

      expect(credentialsMap.get(new RegionAndName(Region.US_EAST_1, "jclouds#keyName"))).andReturn(
               new KeyPair(Region.US_EAST_1, "jclouds#keyName", "keyFingerprint", "pass"));

      expect(instance.getAvailabilityZone()).andReturn(AvailabilityZone.US_EAST_1A).atLeastOnce();

      expect(instance.getInstanceType()).andReturn(InstanceType.C1_XLARGE).atLeastOnce();

      replay(imageMap);
      replay(amiClient);
      replay(credentialsMap);
      replay(credentialProvider);
      replay(instance);
      replay(jcImage);

      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(amiClient,
               credentialsMap, credentialProvider, new ImageProvider(jcImage), imageMap, locations,
               new RunningInstanceToStorageMappingUnix());
      NodeMetadata metadata = parser.apply(instance);

      assertEquals(metadata.getTag(), "tag");
      assertEquals(metadata.getLocation(), location);
      assertEquals(metadata.getImage(), jcImage);

      assertEquals(metadata.getCredentials(), new Credentials("user", "pass"));

      verify(imageMap);
      verify(jcImage);
      verify(amiClient);
      verify(credentialsMap);
      verify(credentialProvider);
      verify(instance);

   }

   @SuppressWarnings("unchecked")
   @Test
   public void testApplyWithTwoSecurityGroups() throws UnknownHostException {
      AMIClient amiClient = createMock(AMIClient.class);
      Map<RegionAndName, KeyPair> credentialsMap = createMock(Map.class);
      ConcurrentMap<RegionAndName, org.jclouds.compute.domain.Image> imageMap = createMock(ConcurrentMap.class);

      PopulateDefaultLoginCredentialsForImageStrategy credentialProvider = createMock(PopulateDefaultLoginCredentialsForImageStrategy.class);
      RunningInstance instance = createMock(RunningInstance.class);
      Image image = createMock(Image.class);

      expect(instance.getId()).andReturn("id").atLeastOnce();
      expect(instance.getGroupIds()).andReturn(ImmutableSet.of("jclouds#tag", "jclouds#tag2"))
               .atLeastOnce();
      expect(instance.getKeyName()).andReturn("jclouds#keyName").atLeastOnce();
      expect(instance.getInstanceState()).andReturn(InstanceState.RUNNING);

      Location location = new LocationImpl(LocationScope.ZONE, "us-east-1a", "description", null);
      Set<Location> locations = ImmutableSet.<Location> of(location);
      org.jclouds.compute.domain.Image jcImage = createMock(org.jclouds.compute.domain.Image.class);

      expect(instance.getIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 12, 10, 10, 1 }));
      expect(instance.getPrivateIpAddress()).andReturn(
               InetAddress.getByAddress(new byte[] { 10, 10, 10, 1 }));

      expect(instance.getRegion()).andReturn(Region.US_EAST_1).atLeastOnce();

      expect(instance.getImageId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getProviderId()).andReturn("imageId").atLeastOnce();
      expect(jcImage.getLocation()).andReturn(location).atLeastOnce();

      expect(amiClient.describeImagesInRegion(Region.US_EAST_1, imageIds("imageId"))).andReturn(
               ImmutableSet.<Image> of(image));

      expect(credentialProvider.execute(image)).andReturn(new Credentials("user", "pass"));

      expect(credentialsMap.get(new RegionAndName(Region.US_EAST_1, "jclouds#keyName"))).andReturn(
               new KeyPair(Region.US_EAST_1, "jclouds#keyName", "keyFingerprint", "pass"));

      expect(instance.getAvailabilityZone()).andReturn(AvailabilityZone.US_EAST_1A).atLeastOnce();

      expect(instance.getInstanceType()).andReturn(InstanceType.C1_XLARGE).atLeastOnce();

      replay(imageMap);
      replay(amiClient);
      replay(credentialsMap);
      replay(credentialProvider);
      replay(instance);
      replay(jcImage);

      RunningInstanceToNodeMetadata parser = new RunningInstanceToNodeMetadata(amiClient,
               credentialsMap, credentialProvider, new ImageProvider(jcImage), imageMap, locations,
               new RunningInstanceToStorageMappingUnix());

      NodeMetadata metadata = parser.apply(instance);

      assertEquals(metadata.getTag(), "NOTAG-id");
      assertEquals(metadata.getLocation(), location);
      assertEquals(metadata.getImage(), jcImage);

      assertEquals(metadata.getCredentials(), new Credentials("user", "pass"));

      verify(imageMap);
      verify(jcImage);
      verify(amiClient);
      verify(credentialsMap);
      verify(credentialProvider);
      verify(instance);

   }
}
