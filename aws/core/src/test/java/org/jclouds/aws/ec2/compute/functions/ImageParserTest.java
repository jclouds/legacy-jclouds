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

package org.jclouds.aws.ec2.compute.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.aws.ec2.compute.strategy.EC2PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.xml.BaseEC2HandlerTest;
import org.jclouds.aws.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "compute.ImageParserTest")
public class ImageParserTest extends BaseEC2HandlerTest {

   public void testParseAlesticCanonicalImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/alestic_canonical.xml");

      Set<Image> result = parseImages(is);
      assertEquals(result.size(), 8);

      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
               .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
               .ofInstance(defaultLocation), "ec2");
      org.jclouds.compute.domain.Image ubuntuHardy = parser.apply(Iterables.get(result, 0));

      assertEquals(ubuntuHardy.getDescription(), "ubuntu-images-us/ubuntu-hardy-8.04-i386-server-20091130.manifest.xml");
      assertEquals(ubuntuHardy.getId(), "us-east-1/ami-7e28ca17");
      assertEquals(ubuntuHardy.getProviderId(), "ami-7e28ca17");
      assertEquals(ubuntuHardy.getLocation(), defaultLocation);
      assertEquals(ubuntuHardy.getName(), null);
      assertEquals(ubuntuHardy.getOperatingSystem().getName(), null);
      assertEquals(ubuntuHardy.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(ubuntuHardy.getOperatingSystem().getVersion(), "8.04");
      assertEquals(ubuntuHardy.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(ubuntuHardy.getOperatingSystem().getDescription(),
               "ubuntu-images-us/ubuntu-hardy-8.04-i386-server-20091130.manifest.xml");
      assertEquals(ubuntuHardy.getOperatingSystem().is64Bit(), false);
      assertEquals(ubuntuHardy.getUserMetadata(), ImmutableMap.<String, String> of("owner", "099720109477",
               "rootDeviceType", "instance-store"));
      assertEquals(ubuntuHardy.getVersion(), "20091130");

      org.jclouds.compute.domain.Image alesticKarmic = parser.apply(Iterables.get(result, 1));

      assertEquals(alesticKarmic.getOperatingSystem().is64Bit(), false);
      assertEquals(alesticKarmic.getDescription(), "alestic/ubuntu-9.10-karmic-base-20090623.manifest.xml");
      assertEquals(alesticKarmic.getId(), "us-east-1/ami-19a34270");
      assertEquals(alesticKarmic.getProviderId(), "ami-19a34270");
      assertEquals(alesticKarmic.getLocation(), defaultLocation);
      assertEquals(alesticKarmic.getName(), null);
      assertEquals(alesticKarmic.getOperatingSystem().getName(), null);
      assertEquals(alesticKarmic.getOperatingSystem().getVersion(), "9.10");
      assertEquals(alesticKarmic.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(alesticKarmic.getOperatingSystem().getDescription(),
               "alestic/ubuntu-9.10-karmic-base-20090623.manifest.xml");
      assertEquals(alesticKarmic.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(alesticKarmic.getUserMetadata(), ImmutableMap.<String, String> of("owner", "063491364108",
               "rootDeviceType", "instance-store"));
      assertEquals(alesticKarmic.getVersion(), "20090623");

      org.jclouds.compute.domain.Image ubuntuKarmic = parser.apply(Iterables.get(result, 2));

      assertEquals(ubuntuKarmic.getOperatingSystem().is64Bit(), false);
      assertEquals(ubuntuKarmic.getDescription(),
               "ubuntu-images-us/ubuntu-karmic-9.10-i386-server-20100121.manifest.xml");
      assertEquals(ubuntuKarmic.getId(), "us-east-1/ami-bb709dd2");
      assertEquals(ubuntuKarmic.getProviderId(), "ami-bb709dd2");
      assertEquals(ubuntuKarmic.getLocation(), defaultLocation);
      assertEquals(ubuntuKarmic.getName(), null);
      assertEquals(ubuntuKarmic.getOperatingSystem().getName(), null);
      assertEquals(ubuntuKarmic.getOperatingSystem().getVersion(), "9.10");
      assertEquals(ubuntuKarmic.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(ubuntuKarmic.getOperatingSystem().getDescription(),
               "ubuntu-images-us/ubuntu-karmic-9.10-i386-server-20100121.manifest.xml");
      assertEquals(ubuntuKarmic.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(ubuntuKarmic.getUserMetadata(), ImmutableMap.<String, String> of("owner", "099720109477",
               "rootDeviceType", "instance-store"));
      assertEquals(ubuntuKarmic.getVersion(), "20100121");

      org.jclouds.compute.domain.Image testing = parser.apply(Iterables.get(result, 3));

      assertEquals(testing.getOperatingSystem().is64Bit(), true);
      assertEquals(testing.getDescription(),
               "ubuntu-images-testing-us/ubuntu-lucid-daily-amd64-desktop-20100317.manifest.xml");
      assertEquals(testing.getId(), "us-east-1/ami-190fe070");
      assertEquals(testing.getProviderId(), "ami-190fe070");
      assertEquals(testing.getLocation(), defaultLocation);
      assertEquals(testing.getName(), null);
      assertEquals(testing.getOperatingSystem().getName(), null);
      assertEquals(testing.getOperatingSystem().getVersion(), "10.04");
      assertEquals(testing.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(testing.getOperatingSystem().getDescription(),
               "ubuntu-images-testing-us/ubuntu-lucid-daily-amd64-desktop-20100317.manifest.xml");
      assertEquals(testing.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(testing.getUserMetadata(), ImmutableMap.<String, String> of("owner", "099720109477",
               "rootDeviceType", "instance-store"));
      assertEquals(testing.getVersion(), "20100317");

      org.jclouds.compute.domain.Image alesticHardy = parser.apply(Iterables.get(result, 4));

      assertEquals(alesticHardy.getOperatingSystem().is64Bit(), false);
      assertEquals(alesticHardy.getDescription(), "alestic/ubuntu-8.04-hardy-base-20080905.manifest.xml");
      assertEquals(alesticHardy.getId(), "us-east-1/ami-c0fa1ea9");
      assertEquals(alesticHardy.getProviderId(), "ami-c0fa1ea9");
      assertEquals(alesticHardy.getLocation(), defaultLocation);
      assertEquals(alesticHardy.getName(), null);
      assertEquals(alesticHardy.getOperatingSystem().getName(), null);
      assertEquals(alesticHardy.getOperatingSystem().getVersion(), "8.04");
      assertEquals(alesticHardy.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(alesticHardy.getOperatingSystem().getDescription(),
               "alestic/ubuntu-8.04-hardy-base-20080905.manifest.xml");
      assertEquals(alesticHardy.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(alesticHardy.getUserMetadata(), ImmutableMap.<String, String> of("owner", "063491364108",
               "rootDeviceType", "instance-store"));
      assertEquals(alesticHardy.getVersion(), "20080905");

      org.jclouds.compute.domain.Image ubuntuLucid = parser.apply(Iterables.get(result, 5));

      assertEquals(ubuntuLucid.getOperatingSystem().is64Bit(), false);
      assertEquals(ubuntuLucid.getDescription(),
               "ubuntu-images-us-west-1/ubuntu-lucid-10.04-i386-server-20100427.1.manifest.xml");
      assertEquals(ubuntuLucid.getId(), "us-east-1/ami-c597c680");
      assertEquals(ubuntuLucid.getProviderId(), "ami-c597c680");
      assertEquals(ubuntuLucid.getLocation(), defaultLocation);
      assertEquals(ubuntuLucid.getName(), null);
      assertEquals(ubuntuLucid.getOperatingSystem().getName(), null);
      assertEquals(ubuntuLucid.getOperatingSystem().getVersion(), "10.04");
      assertEquals(ubuntuLucid.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(ubuntuLucid.getOperatingSystem().getDescription(),
               "ubuntu-images-us-west-1/ubuntu-lucid-10.04-i386-server-20100427.1.manifest.xml");
      assertEquals(ubuntuLucid.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(ubuntuLucid.getUserMetadata(), ImmutableMap.<String, String> of("owner", "099720109477",
               "rootDeviceType", "instance-store"));
      assertEquals(ubuntuLucid.getVersion(), "20100427.1");

      // should skip kernel
      assert parser.apply(Iterables.get(result, 6)) == null;

      org.jclouds.compute.domain.Image ubuntuEbs = parser.apply(Iterables.get(result, 7));

      assertEquals(ubuntuEbs.getOperatingSystem().is64Bit(), false);
      assertEquals(ubuntuEbs.getDescription(), "099720109477/ebs/ubuntu-images/ubuntu-lucid-10.04-i386-server-20100827");
      assertEquals(ubuntuEbs.getId(), "us-east-1/ami-10f3a255");
      assertEquals(ubuntuEbs.getProviderId(), "ami-10f3a255");
      assertEquals(ubuntuEbs.getLocation(), defaultLocation);
      assertEquals(ubuntuEbs.getName(), null);
      assertEquals(ubuntuEbs.getOperatingSystem().getName(), null);
      assertEquals(ubuntuEbs.getOperatingSystem().getVersion(), "10.04");
      assertEquals(ubuntuEbs.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(ubuntuEbs.getOperatingSystem().getDescription(),
               "099720109477/ebs/ubuntu-images/ubuntu-lucid-10.04-i386-server-20100827");
      assertEquals(ubuntuEbs.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(ubuntuEbs.getUserMetadata(), ImmutableMap.<String, String> of("owner", "099720109477",
               "rootDeviceType", "ebs"));
      assertEquals(ubuntuEbs.getVersion(), "20100827");

   }

   private Location defaultLocation = new LocationImpl(LocationScope.REGION, "us-east-1", "us-east-1", null);

   public void testParseVostokImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/vostok.xml");

      Set<Image> result = parseImages(is);

      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
               .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
               .ofInstance(defaultLocation), "ec2");

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getOperatingSystem().is64Bit(), false);
      assertEquals(image.getDescription(), "vostok-builds/vostok-0.95-5622/vostok-0.95-5622.manifest.xml");
      assertEquals(image.getId(), "us-east-1/ami-870de2ee");
      assertEquals(image.getProviderId(), "ami-870de2ee");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(),
               "vostok-builds/vostok-0.95-5622/vostok-0.95-5622.manifest.xml");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UNKNOWN);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "133804938231", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), "5622");

   }

   public void testParseCCImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/describe_images_cc.xml");

      Set<Image> result = parseImages(is);

      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
               .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
               .ofInstance(defaultLocation), "ec2");

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "EC2 CentOS 5.4 HVM AMI");
      assertEquals(image.getId(), "us-east-1/ami-7ea24a17");
      assertEquals(image.getProviderId(), "ami-7ea24a17");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "5.4");
      assertEquals(image.getOperatingSystem().getArch(), "hvm");
      assertEquals(image.getOperatingSystem().getDescription(), "amazon/EC2 CentOS 5.4 HVM AMI");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "206029621532", "rootDeviceType",
               "ebs"));
      assertEquals(image.getVersion(), null);

   }

   public void testParseRightScaleImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/rightscale_images.xml");

      Set<Image> result = parseImages(is);

      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
               .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
               .ofInstance(defaultLocation), "ec2");

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "rightscale-us-east/CentOS_5.4_x64_v4.4.10.manifest.xml");
      assertEquals(image.getId(), "us-east-1/ami-ccb35ea5");
      assertEquals(image.getProviderId(), "ami-ccb35ea5");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "5.4");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(),
               "rightscale-us-east/CentOS_5.4_x64_v4.4.10.manifest.xml");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "411009282317", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), "4.4.10");

      image = parser.apply(Iterables.get(result, 1));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "RightImage_Ubuntu_9.10_x64_v4.5.3_EBS_Alpha");
      assertEquals(image.getId(), "us-east-1/ami-c19db6b5");
      assertEquals(image.getProviderId(), "ami-c19db6b5");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "9.10");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(),
               "411009282317/RightImage_Ubuntu_9.10_x64_v4.5.3_EBS_Alpha");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "411009282317", "rootDeviceType",
               "ebs"));
      assertEquals(image.getVersion(), "4.5.3_EBS_Alpha");

   }

   public void testParseEucalyptusImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/eucalyptus_images.xml");

      Set<Image> result = parseImages(is);
      assertEquals(result.size(), 4);
      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
               .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
               .ofInstance(defaultLocation), "ec2");

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "centos-5.3-x86_64/centos.5-3.x86-64.img.manifest.xml");
      assertEquals(image.getId(), "us-east-1/emi-9ACB1363");
      assertEquals(image.getProviderId(), "emi-9ACB1363");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "5.3");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "centos-5.3-x86_64/centos.5-3.x86-64.img.manifest.xml");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "admin", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

   }

   public void testParseAmznmage() {
      InputStream is = getClass().getResourceAsStream("/ec2/amzn_images.xml");

      Set<Image> result = parseImages(is);
      assertEquals(result.size(), 4);
      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
               .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
               .ofInstance(defaultLocation), "ec2");

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getOperatingSystem().is64Bit(), false);
      assertEquals(image.getDescription(), "Amazon");
      assertEquals(image.getId(), "us-east-1/ami-82e4b5c7");
      assertEquals(image.getProviderId(), "ami-82e4b5c7");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "0.9.7-beta");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "137112412989/amzn-ami-0.9.7-beta.i386-ebs");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.AMZN_LINUX);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "137112412989", "rootDeviceType",
               "ebs"));
      assertEquals(image.getVersion(), "0.9.7-beta");

      image = parser.apply(Iterables.get(result, 1));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "Amazon");
      assertEquals(image.getId(), "us-east-1/ami-8ce4b5c9");
      assertEquals(image.getProviderId(), "ami-8ce4b5c9");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "0.9.7-beta");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "137112412989/amzn-ami-0.9.7-beta.x86_64-ebs");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.AMZN_LINUX);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "137112412989", "rootDeviceType",
               "ebs"));
      assertEquals(image.getVersion(), "0.9.7-beta");

      image = parser.apply(Iterables.get(result, 2));

      assertEquals(image.getOperatingSystem().is64Bit(), false);
      assertEquals(image.getDescription(), "Amazon Linux AMI i386 S3");
      assertEquals(image.getId(), "us-east-1/ami-f0e4b5b5");
      assertEquals(image.getProviderId(), "ami-f0e4b5b5");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "0.9.7-beta");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(),
               "amzn-ami-us-west-1/amzn-ami-0.9.7-beta.i386.manifest.xml");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.AMZN_LINUX);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "137112412989", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), "0.9.7-beta");

      image = parser.apply(Iterables.get(result, 3));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "Amazon Linux AMI x86_64 S3");
      assertEquals(image.getId(), "us-east-1/ami-f2e4b5b7");
      assertEquals(image.getProviderId(), "ami-f2e4b5b7");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);

      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "0.9.7-beta");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(),
               "amzn-ami-us-west-1/amzn-ami-0.9.7-beta.x86_64.manifest.xml");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.AMZN_LINUX);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "137112412989", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), "0.9.7-beta");

   }

   public void testParseNovaImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/nova_images.xml");

      Set<Image> result = parseImages(is);
      assertEquals(result.size(), 19);
      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
               .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
               .ofInstance(defaultLocation), "nebula");

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "nasacms/image.manifest.xml");
      assertEquals(image.getId(), "us-east-1/ami-h30p5im0");
      assertEquals(image.getProviderId(), "ami-h30p5im0");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "nasacms/image.manifest.xml");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "foo", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      assertEquals(parser.apply(Iterables.get(result, 1)), null);

      image = parser.apply(Iterables.get(result, 2));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "nebula/tiny");
      assertEquals(image.getId(), "us-east-1/ami-tiny");
      assertEquals(image.getProviderId(), "ami-tiny");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "nebula/tiny");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "vishvananda", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      image = parser.apply(Iterables.get(result, 3));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "demos/mediawiki");
      assertEquals(image.getId(), "us-east-1/ami-630A130F");
      assertEquals(image.getProviderId(), "ami-630A130F");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "demos/mediawiki");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "admin", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      assertEquals(parser.apply(Iterables.get(result, 4)), null);
      assertEquals(parser.apply(Iterables.get(result, 5)), null);
      assertEquals(parser.apply(Iterables.get(result, 6)), null);

      image = parser.apply(Iterables.get(result, 7));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "pinglet/instances");
      assertEquals(image.getId(), "us-east-1/ami-pinginst");
      assertEquals(image.getProviderId(), "ami-pinginst");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "pinglet/instances");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "admin", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      image = parser.apply(Iterables.get(result, 8));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "bucket/testbuntu.manifest.xml");
      assertEquals(image.getId(), "us-east-1/ami-alqbihe2");
      assertEquals(image.getProviderId(), "ami-alqbihe2");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "bucket/testbuntu.manifest.xml");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "rkumar2", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      image = parser.apply(Iterables.get(result, 9));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "gfortran-bucket/gfortran.manifest.xml");
      assertEquals(image.getId(), "us-east-1/ami-i0aemtfp");
      assertEquals(image.getProviderId(), "ami-i0aemtfp");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "gfortran-bucket/gfortran.manifest.xml");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "ykliu", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      assertEquals(parser.apply(Iterables.get(result, 10)), null);

      image = parser.apply(Iterables.get(result, 11));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "grinder/grinder-analyzer.manifest.xml");
      assertEquals(image.getId(), "us-east-1/ami-2ig7w1bh");
      assertEquals(image.getProviderId(), "ami-2ig7w1bh");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "grinder/grinder-analyzer.manifest.xml");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "foo", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      assertEquals(parser.apply(Iterables.get(result, 12)), null);

      image = parser.apply(Iterables.get(result, 13));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "nebula/lucid");
      assertEquals(image.getId(), "us-east-1/ami-lucid");
      assertEquals(image.getProviderId(), "ami-lucid");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "10.04");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "nebula/lucid");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "vishvananda", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      image = parser.apply(Iterables.get(result, 14));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "nebula/karmic-large");
      assertEquals(image.getId(), "us-east-1/ami-karmiclg");
      assertEquals(image.getProviderId(), "ami-karmiclg");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "9.10");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "nebula/karmic-large");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "admin", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      image = parser.apply(Iterables.get(result, 15));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "jo/qa-grinder.manifest.xml");
      assertEquals(image.getId(), "us-east-1/ami-8jen8kdn");
      assertEquals(image.getProviderId(), "ami-8jen8kdn");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "jo/qa-grinder.manifest.xml");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "jyothi", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      image = parser.apply(Iterables.get(result, 16));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "nebula/lucid-large");
      assertEquals(image.getId(), "us-east-1/ami-lucidlg");
      assertEquals(image.getProviderId(), "ami-lucidlg");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "10.04");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "nebula/lucid-large");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "vishvananda", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      image = parser.apply(Iterables.get(result, 17));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "demos/wordpress");
      assertEquals(image.getId(), "us-east-1/ami-6CD61336");
      assertEquals(image.getProviderId(), "ami-6CD61336");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "demos/wordpress");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "admin", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

      image = parser.apply(Iterables.get(result, 18));

      assertEquals(image.getOperatingSystem().is64Bit(), true);
      assertEquals(image.getDescription(), "nebula/ubuntu-karmic");
      assertEquals(image.getId(), "us-east-1/ami-25CB1213");
      assertEquals(image.getProviderId(), "ami-25CB1213");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), null);
      assertEquals(image.getOperatingSystem().getName(), null);
      assertEquals(image.getOperatingSystem().getVersion(), "9.10");
      assertEquals(image.getOperatingSystem().getArch(), "paravirtual");
      assertEquals(image.getOperatingSystem().getDescription(), "nebula/ubuntu-karmic");
      assertEquals(image.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "admin", "rootDeviceType",
               "instance-store"));
      assertEquals(image.getVersion(), null);

   }

   private Set<Image> parseImages(InputStream is) {
      DescribeImagesResponseHandler handler = injector.getInstance(DescribeImagesResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Set<Image> result = factory.create(handler).parse(is);
      return result;
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(new Object[] { null }).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
