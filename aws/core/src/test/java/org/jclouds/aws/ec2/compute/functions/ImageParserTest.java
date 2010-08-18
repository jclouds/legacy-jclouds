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
      assertEquals(result.size(), 7);

      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
            .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
            .ofInstance(defaultLocation));
      org.jclouds.compute.domain.Image ubuntuHardy = parser.apply(Iterables.get(result, 0));

      assertEquals(ubuntuHardy.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(ubuntuHardy.getDescription(), "ubuntu-images-us/ubuntu-hardy-8.04-i386-server-20091130.manifest.xml");
      assertEquals(ubuntuHardy.getProviderId(), "ami-7e28ca17");
      assertEquals(ubuntuHardy.getLocation(), defaultLocation);
      assertEquals(ubuntuHardy.getName(), "8.04");
      assertEquals(ubuntuHardy.getOsDescription(),
            "ubuntu-images-us/ubuntu-hardy-8.04-i386-server-20091130.manifest.xml");
      assertEquals(ubuntuHardy.getOsFamily(), OsFamily.UBUNTU);
      assertEquals(ubuntuHardy.getUserMetadata(), ImmutableMap.<String, String> of("owner", "099720109477"));
      assertEquals(ubuntuHardy.getVersion(), "20091130");

      org.jclouds.compute.domain.Image alesticKarmic = parser.apply(Iterables.get(result, 1));

      assertEquals(alesticKarmic.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(alesticKarmic.getDescription(), "alestic/ubuntu-9.10-karmic-base-20090623.manifest.xml");
      assertEquals(alesticKarmic.getProviderId(), "ami-19a34270");
      assertEquals(alesticKarmic.getLocation(), defaultLocation);
      assertEquals(alesticKarmic.getName(), "9.10");
      assertEquals(alesticKarmic.getOsDescription(), "alestic/ubuntu-9.10-karmic-base-20090623.manifest.xml");
      assertEquals(alesticKarmic.getOsFamily(), OsFamily.UBUNTU);
      assertEquals(alesticKarmic.getUserMetadata(), ImmutableMap.<String, String> of("owner", "063491364108"));
      assertEquals(alesticKarmic.getVersion(), "20090623");

      org.jclouds.compute.domain.Image ubuntuKarmic = parser.apply(Iterables.get(result, 2));

      assertEquals(ubuntuKarmic.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(ubuntuKarmic.getDescription(),
            "ubuntu-images-us/ubuntu-karmic-9.10-i386-server-20100121.manifest.xml");
      assertEquals(ubuntuKarmic.getProviderId(), "ami-bb709dd2");
      assertEquals(ubuntuKarmic.getLocation(), defaultLocation);
      assertEquals(ubuntuKarmic.getName(), "9.10");
      assertEquals(ubuntuKarmic.getOsDescription(),
            "ubuntu-images-us/ubuntu-karmic-9.10-i386-server-20100121.manifest.xml");
      assertEquals(ubuntuKarmic.getOsFamily(), OsFamily.UBUNTU);
      assertEquals(ubuntuKarmic.getUserMetadata(), ImmutableMap.<String, String> of("owner", "099720109477"));
      assertEquals(ubuntuKarmic.getVersion(), "20100121");

      // should skip testing image
      assert parser.apply(Iterables.get(result, 3)) == null;

      org.jclouds.compute.domain.Image alesticHardy = parser.apply(Iterables.get(result, 4));

      assertEquals(alesticHardy.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(alesticHardy.getDescription(), "alestic/ubuntu-8.04-hardy-base-20080905.manifest.xml");
      assertEquals(alesticHardy.getProviderId(), "ami-c0fa1ea9");
      assertEquals(alesticHardy.getLocation(), defaultLocation);
      assertEquals(alesticHardy.getName(), "8.04");
      assertEquals(alesticHardy.getOsDescription(), "alestic/ubuntu-8.04-hardy-base-20080905.manifest.xml");
      assertEquals(alesticHardy.getOsFamily(), OsFamily.UBUNTU);
      assertEquals(alesticHardy.getUserMetadata(), ImmutableMap.<String, String> of("owner", "063491364108"));
      assertEquals(alesticHardy.getVersion(), "20080905");

      org.jclouds.compute.domain.Image ubuntuLucid = parser.apply(Iterables.get(result, 5));

      assertEquals(ubuntuLucid.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(ubuntuLucid.getDescription(),
            "ubuntu-images-us-west-1/ubuntu-lucid-10.04-i386-server-20100427.1.manifest.xml");
      assertEquals(ubuntuLucid.getProviderId(), "ami-c597c680");
      assertEquals(ubuntuLucid.getLocation(), defaultLocation);
      assertEquals(ubuntuLucid.getName(), "10.04");
      assertEquals(ubuntuLucid.getOsDescription(),
            "ubuntu-images-us-west-1/ubuntu-lucid-10.04-i386-server-20100427.1.manifest.xml");
      assertEquals(ubuntuLucid.getOsFamily(), OsFamily.UBUNTU);
      assertEquals(ubuntuLucid.getUserMetadata(), ImmutableMap.<String, String> of("owner", "099720109477"));
      assertEquals(ubuntuLucid.getVersion(), "20100427.1");

      // should skip kernel
      assert parser.apply(Iterables.get(result, 6)) == null;
   }

   private Location defaultLocation = new LocationImpl(LocationScope.REGION, "us-east-1", "us-east-1", null);

   public void testParseVostokImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/vostok.xml");

      Set<Image> result = parseImages(is);

      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
            .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
            .ofInstance(defaultLocation));

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(image.getDescription(), "vostok-builds/vostok-0.95-5622/vostok-0.95-5622.manifest.xml");
      assertEquals(image.getProviderId(), "ami-870de2ee");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), "");
      assertEquals(image.getOsDescription(), "vostok-builds/vostok-0.95-5622/vostok-0.95-5622.manifest.xml");
      assertEquals(image.getOsFamily(), OsFamily.UNKNOWN);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "133804938231"));
      assertEquals(image.getVersion(), "5622");

   }

   public void testParseRightScaleImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/rightscale_images.xml");

      Set<Image> result = parseImages(is);

      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
            .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
            .ofInstance(defaultLocation));

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_64);
      assertEquals(image.getDescription(), "rightscale-us-east/CentOS_5.4_x64_v4.4.10.manifest.xml");
      assertEquals(image.getProviderId(), "ami-ccb35ea5");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), "5.4");
      assertEquals(image.getOsDescription(), "rightscale-us-east/CentOS_5.4_x64_v4.4.10.manifest.xml");
      assertEquals(image.getOsFamily(), OsFamily.CENTOS);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "411009282317"));
      assertEquals(image.getVersion(), "4.4.10");

      image = parser.apply(Iterables.get(result, 1));

      assertEquals(image.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_64);
      assertEquals(image.getDescription(), "RightImage_Ubuntu_9.10_x64_v4.5.3_EBS_Alpha");
      assertEquals(image.getProviderId(), "ami-c19db6b5");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), "9.10");
      assertEquals(image.getOsDescription(), "411009282317/RightImage_Ubuntu_9.10_x64_v4.5.3_EBS_Alpha");
      assertEquals(image.getOsFamily(), OsFamily.UBUNTU);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "411009282317"));
      assertEquals(image.getVersion(), "4.5.3_EBS_Alpha");

   }

   public void testParseEucalyptusImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/eucalyptus_images.xml");

      Set<Image> result = parseImages(is);
      assertEquals(result.size(), 4);
      ImageParser parser = new ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), Suppliers
            .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
            .ofInstance(defaultLocation));

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_64);
      assertEquals(image.getDescription(), "centos-5.3-x86_64/centos.5-3.x86-64.img.manifest.xml");
      assertEquals(image.getProviderId(), "emi-9ACB1363");
      assertEquals(image.getLocation(), defaultLocation);
      assertEquals(image.getName(), "5.3");
      assertEquals(image.getOsDescription(), "centos-5.3-x86_64/centos.5-3.x86-64.img.manifest.xml");
      assertEquals(image.getOsFamily(), OsFamily.CENTOS);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner", "admin"));
      assertEquals(image.getVersion(), "");

      // should skip test images
      image = parser.apply(Iterables.get(result, 3));
      assertEquals(image, null);

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
