/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.addNewBlockDevice;
import static org.jclouds.ec2.options.RegisterImageOptions.Builder.withDescription;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Constants;
import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.Region;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.Image.ImageType;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * Tests behavior of {@code AMIClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class AMIClientLiveTest {

   private AMIClient client;
   private String imageId = "ami-cdf819a4";
   private static final String DEFAULT_MANIFEST = "adrianimages/image.manifest.xml";
   private static final String DEFAULT_SNAPSHOT = "TODO";
   private RestContext<EC2Client, EC2AsyncClient> context;

   private Set<String> imagesToDeregister = Sets.newHashSet();

   protected String provider = "ec2";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new ComputeServiceContextFactory().createContext(provider,
               ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getProviderSpecificContext();
      client = context.getApi().getAMIServices();
   }

   public void testDescribeImageNotExists() {
      assertEquals(client.describeImagesInRegion(null, imageIds("ami-cdf819a3")).size(), 0);
   }

   @Test(expectedExceptions = AWSResponseException.class)
   public void testDescribeImageBadId() {
      client.describeImagesInRegion(null, imageIds("asdaasdsa"));
   }

   public void testDescribeImages() {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1,
               Region.AP_SOUTHEAST_1)) {
         Set<Image> allResults = Sets.newLinkedHashSet(client.describeImagesInRegion(region));
         assertNotNull(allResults);
         assert allResults.size() >= 2 : allResults.size();
         Iterator<Image> iterator = allResults.iterator();
         String id1 = iterator.next().getId();
         String id2 = iterator.next().getId();
         Set<Image> twoResults = Sets.newLinkedHashSet(client.describeImagesInRegion(region, imageIds(id1, id2)));
         assertNotNull(twoResults);
         assertEquals(twoResults.size(), 2);
         iterator = twoResults.iterator();
         assertEquals(iterator.next().getId(), id1);
         assertEquals(iterator.next().getId(), id2);
      }
   }

   @Test(enabled = false)
   public void testRegisterImageFromManifest() {
      String imageRegisteredId = client.registerImageFromManifestInRegion(null, "jcloudstest1", DEFAULT_MANIFEST);
      imagesToDeregister.add(imageRegisteredId);
      Image imageRegisteredFromManifest = Iterables.getOnlyElement(client.describeImagesInRegion(null,
               imageIds(imageRegisteredId)));
      assertEquals(imageRegisteredFromManifest.getName(), "jcloudstest1");
      assertEquals(imageRegisteredFromManifest.getImageLocation(), DEFAULT_MANIFEST);
      assertEquals(imageRegisteredFromManifest.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegisteredFromManifest.getRootDeviceType(), RootDeviceType.INSTANCE_STORE);
      assertEquals(imageRegisteredFromManifest.getRootDeviceName(), "/dev/sda1");
   }

   @Test(enabled = false)
   public void testRegisterImageFromManifestOptions() {
      String imageRegisteredWithOptionsId = client.registerImageFromManifestInRegion(null, "jcloudstest2",
               DEFAULT_MANIFEST, withDescription("adrian"));
      imagesToDeregister.add(imageRegisteredWithOptionsId);
      Image imageRegisteredFromManifestWithOptions = Iterables.getOnlyElement(client.describeImagesInRegion(null,
               imageIds(imageRegisteredWithOptionsId)));
      assertEquals(imageRegisteredFromManifestWithOptions.getName(), "jcloudstest2");
      assertEquals(imageRegisteredFromManifestWithOptions.getImageLocation(), DEFAULT_MANIFEST);
      assertEquals(imageRegisteredFromManifestWithOptions.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegisteredFromManifestWithOptions.getRootDeviceType(), RootDeviceType.INSTANCE_STORE);
      assertEquals(imageRegisteredFromManifestWithOptions.getRootDeviceName(), "/dev/sda1");
      assertEquals(imageRegisteredFromManifestWithOptions.getDescription(), "adrian");
   }

   @Test(enabled = false)
   // awaiting EBS functionality to be added to jclouds
   public void testRegisterImageBackedByEBS() {
      String imageRegisteredId = client.registerUnixImageBackedByEbsInRegion(null, "jcloudstest1", DEFAULT_MANIFEST);
      imagesToDeregister.add(imageRegisteredId);
      Image imageRegistered = Iterables
               .getOnlyElement(client.describeImagesInRegion(null, imageIds(imageRegisteredId)));
      assertEquals(imageRegistered.getName(), "jcloudstest1");
      assertEquals(imageRegistered.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegistered.getRootDeviceType(), RootDeviceType.EBS);
      assertEquals(imageRegistered.getRootDeviceName(), "/dev/sda1");
   }

   @Test(enabled = false)
   // awaiting EBS functionality to be added to jclouds
   public void testRegisterImageBackedByEBSOptions() {
      String imageRegisteredWithOptionsId = client.registerUnixImageBackedByEbsInRegion(null, "jcloudstest2",
               DEFAULT_SNAPSHOT, addNewBlockDevice("/dev/sda2", "myvirtual", 1).withDescription("adrian"));
      imagesToDeregister.add(imageRegisteredWithOptionsId);
      Image imageRegisteredWithOptions = Iterables.getOnlyElement(client.describeImagesInRegion(null,
               imageIds(imageRegisteredWithOptionsId)));
      assertEquals(imageRegisteredWithOptions.getName(), "jcloudstest2");
      assertEquals(imageRegisteredWithOptions.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegisteredWithOptions.getRootDeviceType(), RootDeviceType.EBS);
      assertEquals(imageRegisteredWithOptions.getRootDeviceName(), "/dev/sda1");
      assertEquals(imageRegisteredWithOptions.getDescription(), "adrian");
      assertEquals(imageRegisteredWithOptions.getEbsBlockDevices().entrySet(), ImmutableMap.of("/dev/sda1",
               new Image.EbsBlockDevice("/dev/sda1", 30, true), "/dev/sda2",
               new Image.EbsBlockDevice("/dev/sda2", 1, true)).entrySet());
   }

   @Test(enabled = false)
   public void testCreateImage() {
      // TODO client.createImageInRegion(null, name, instanceId, options);
   }

   @Test(enabled = false)
   public void testAddProductCodesToImage() {
      // TODO client.addProductCodesToImageInRegion(null, productCodes, imageId);
   }

   @Test(enabled = false)
   public void testAddLaunchPermissionsToImage() {
      // TODO client.addLaunchPermissionsToImageInRegion(null, userIds, userGroups,
      // imageId);
   }

   @Test(enabled = false)
   public void testRemoveLaunchPermissionsFromImage() {
      // TODO client.removeLaunchPermissionsFromImageInRegion(null, userIds, userGroups,
      // imageId);
   }

   @Test(enabled = false)
   public void testResetLaunchPermissionsOnImage() {
      // TODO client.resetLaunchPermissionsOnImageInRegion(null, imageId);
   }

   public void testGetLaunchPermissionForImage() {
      System.out.println(client.getLaunchPermissionForImageInRegion(null, imageId));
   }

   @Test(enabled = false)
   // awaiting ebs support
   public void testGetBlockDeviceMappingsForImage() {
      System.out.println(client.getBlockDeviceMappingsForImageInRegion(null, imageId));
   }

   @AfterTest
   public void deregisterImages() {
      for (String imageId : imagesToDeregister)
         client.deregisterImageInRegion(null, imageId);
   }
}
