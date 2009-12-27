/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.jclouds.aws.ec2.options.RegisterImageOptions.Builder.withDescription;
import static org.jclouds.aws.ec2.options.RegisterImageBackedByEbsOptions.Builder.addNewBlockDevice;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.EC2ContextFactory;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.Image.ImageType;
import org.jclouds.aws.ec2.domain.Image.RootDeviceType;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.internal.ImmutableMap;

/**
 * Tests behavior of {@code AMIClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.AMIClientLiveTest")
public class AMIClientLiveTest {

   private AMIClient client;
   private String user;
   private String imageId = "ami-cdf819a4";
   private static final String DEFAULT_MANIFEST = "adrianimages/image.manifest.xml";
   private static final String DEFAULT_SNAPSHOT = "TODO";
   private RestContext<EC2AsyncClient, EC2Client> context;

   private Set<String> imagesToDeregister = Sets.newHashSet();

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      context = EC2ContextFactory.createContext(user, password, new Log4JLoggingModule());
      client = context.getApi().getAMIServices();
   }

   public void testDescribeImages() {
      SortedSet<Image> allResults = Sets.newTreeSet(client.describeImages());
      assertNotNull(allResults);
      assert allResults.size() >= 2 : allResults.size();
      Iterator<Image> iterator = allResults.iterator();
      String id1 = iterator.next().getImageId();
      String id2 = iterator.next().getImageId();
      SortedSet<Image> twoResults = Sets.newTreeSet(client.describeImages(imageIds(id1, id2)));
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 2);
      iterator = twoResults.iterator();
      assertEquals(iterator.next().getImageId(), id1);
      assertEquals(iterator.next().getImageId(), id2);
   }

   public void testRegisterImageFromManifest() {
      String imageRegisteredId = client.registerImageFromManifest("jcloudstest1", DEFAULT_MANIFEST);
      imagesToDeregister.add(imageRegisteredId);
      Image imageRegisteredFromManifest = Iterables.getOnlyElement(client
               .describeImages(imageIds(imageRegisteredId)));
      assertEquals(imageRegisteredFromManifest.getName(), "jcloudstest1");
      assertEquals(imageRegisteredFromManifest.getImageLocation(), DEFAULT_MANIFEST);
      assertEquals(imageRegisteredFromManifest.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegisteredFromManifest.getRootDeviceType(), RootDeviceType.INSTANCE_STORE);
      assertEquals(imageRegisteredFromManifest.getRootDeviceName(), "/dev/sda1");
   }

   public void testRegisterImageFromManifestOptions() {
      String imageRegisteredWithOptionsId = client.registerImageFromManifest("jcloudstest2",
               DEFAULT_MANIFEST, withDescription("adrian"));
      imagesToDeregister.add(imageRegisteredWithOptionsId);
      Image imageRegisteredFromManifestWithOptions = Iterables.getOnlyElement(client
               .describeImages(imageIds(imageRegisteredWithOptionsId)));
      assertEquals(imageRegisteredFromManifestWithOptions.getName(), "jcloudstest2");
      assertEquals(imageRegisteredFromManifestWithOptions.getImageLocation(), DEFAULT_MANIFEST);
      assertEquals(imageRegisteredFromManifestWithOptions.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegisteredFromManifestWithOptions.getRootDeviceType(),
               RootDeviceType.INSTANCE_STORE);
      assertEquals(imageRegisteredFromManifestWithOptions.getRootDeviceName(), "/dev/sda1");
      assertEquals(imageRegisteredFromManifestWithOptions.getDescription(), "adrian");
   }

   @Test(enabled = false)
   // awaiting EBS functionality to be added to jclouds
   public void testRegisterImageBackedByEBS() {
      String imageRegisteredId = client.registerImageBackedByEbs("jcloudstest1", DEFAULT_MANIFEST);
      imagesToDeregister.add(imageRegisteredId);
      Image imageRegistered = Iterables.getOnlyElement(client
               .describeImages(imageIds(imageRegisteredId)));
      assertEquals(imageRegistered.getName(), "jcloudstest1");
      assertEquals(imageRegistered.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegistered.getRootDeviceType(), RootDeviceType.EBS);
      assertEquals(imageRegistered.getRootDeviceName(), "/dev/sda1");
   }

   @Test(enabled = false)
   // awaiting EBS functionality to be added to jclouds
   public void testRegisterImageBackedByEBSOptions() {
      String imageRegisteredWithOptionsId = client.registerImageBackedByEbs("jcloudstest2",
               DEFAULT_SNAPSHOT, addNewBlockDevice("/dev/sda2", "myvirtual", 1).withDescription(
                        "adrian"));
      imagesToDeregister.add(imageRegisteredWithOptionsId);
      Image imageRegisteredWithOptions = Iterables.getOnlyElement(client
               .describeImages(imageIds(imageRegisteredWithOptionsId)));
      assertEquals(imageRegisteredWithOptions.getName(), "jcloudstest2");
      assertEquals(imageRegisteredWithOptions.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegisteredWithOptions.getRootDeviceType(), RootDeviceType.EBS);
      assertEquals(imageRegisteredWithOptions.getRootDeviceName(), "/dev/sda1");
      assertEquals(imageRegisteredWithOptions.getDescription(), "adrian");
      assertEquals(imageRegisteredWithOptions.getEbsBlockDevices().entrySet(), ImmutableMap.of(
               "/dev/sda1", new Image.EbsBlockDevice("/dev/sda1", 30, true), "/dev/sda2",
               new Image.EbsBlockDevice("/dev/sda2", 1, true)).entrySet());
   }

   @Test(enabled = false)
   public void testCreateImage() {
      // TODO client.createImage(name, instanceId, options);
   }

   @Test(enabled = false)
   public void testAddLaunchPermissionsToImage() {
      // TODO client.addLaunchPermissionsToImage(userIds, userGroups, imageId);
   }

   @Test(enabled = false)
   public void testAddProductCodesToImage() {
      // TODO client.addProductCodesToImage(productCodes, imageId);
   }

   @Test(enabled = false)
   public void testRemoveLaunchPermissionsFromImage() {
      // TODO client.removeLaunchPermissionsFromImage(userIds, userGroups, imageId);
   }

   @Test(enabled = false)
   public void testResetLaunchPermissionsOnImage() {
      // TODO client.resetLaunchPermissionsOnImage(imageId);
   }

   public void testGetLaunchPermissionForImage() {
      System.out.println(client.getLaunchPermissionForImage(imageId));
   }

   public void testGetProductCodesForImage() {
      System.out.println(client.getProductCodesForImage(imageId));
   }

   @Test(enabled = false)
   // awaiting ebs support
   public void testGetBlockDeviceMappingsForImage() {
      System.out.println(client.getBlockDeviceMappingsForImage(imageId));
   }

   @AfterTest
   public void deregisterImages() {
      for (String imageId : imagesToDeregister)
         client.deregisterImage(imageId);
   }
}
