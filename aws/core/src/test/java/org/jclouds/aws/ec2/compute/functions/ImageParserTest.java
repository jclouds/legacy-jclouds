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
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.aws.ec2.compute.strategy.EC2PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.xml.BaseEC2HandlerTest;
import org.jclouds.aws.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "compute.ImageParserTest")
public class ImageParserTest extends BaseEC2HandlerTest {

   public void testParseAlesticCanonicalImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/alestic_canonical.xml");

      Set<Image> result = parseImages(is);
      assertEquals(result.size(), 6);

      ImageParser parser = new ImageParser(
               new EC2PopulateDefaultLoginCredentialsForImageStrategy(), ImmutableMap
                        .<String, Location> of());
      org.jclouds.compute.domain.Image ubuntuHardy = parser.apply(Iterables.get(result, 0));

      assertEquals(ubuntuHardy.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(ubuntuHardy.getDescription(),
               "ubuntu-images-us/ubuntu-hardy-8.04-i386-server-20091130.manifest.xml");
      assertEquals(ubuntuHardy.getId(), "ami-7e28ca17");
      assertEquals(ubuntuHardy.getLocation(), null);
      assertEquals(ubuntuHardy.getName(), "8.04");
      assertEquals(ubuntuHardy.getOsDescription(),
               "ubuntu-images-us/ubuntu-hardy-8.04-i386-server-20091130.manifest.xml");
      assertEquals(ubuntuHardy.getOsFamily(), OsFamily.UBUNTU);
      assertEquals(ubuntuHardy.getUserMetadata(), ImmutableMap.<String, String> of("owner",
               "099720109477"));
      assertEquals(ubuntuHardy.getVersion(), "20091130");

      org.jclouds.compute.domain.Image alesticKarmic = parser.apply(Iterables.get(result, 1));

      assertEquals(alesticKarmic.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(alesticKarmic.getDescription(),
               "alestic/ubuntu-9.10-karmic-base-20090623.manifest.xml");
      assertEquals(alesticKarmic.getId(), "ami-19a34270");
      assertEquals(alesticKarmic.getLocation(), null);
      assertEquals(alesticKarmic.getName(), "9.10");
      assertEquals(alesticKarmic.getOsDescription(),
               "alestic/ubuntu-9.10-karmic-base-20090623.manifest.xml");
      assertEquals(alesticKarmic.getOsFamily(), OsFamily.UBUNTU);
      assertEquals(alesticKarmic.getUserMetadata(), ImmutableMap.<String, String> of("owner",
               "063491364108"));
      assertEquals(alesticKarmic.getVersion(), "20090623");

      org.jclouds.compute.domain.Image ubuntuKarmic = parser.apply(Iterables.get(result, 2));

      assertEquals(ubuntuKarmic.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(ubuntuKarmic.getDescription(),
               "ubuntu-images-us/ubuntu-karmic-9.10-i386-server-20100121.manifest.xml");
      assertEquals(ubuntuKarmic.getId(), "ami-bb709dd2");
      assertEquals(ubuntuKarmic.getLocation(), null);
      assertEquals(ubuntuKarmic.getName(), "9.10");
      assertEquals(ubuntuKarmic.getOsDescription(),
               "ubuntu-images-us/ubuntu-karmic-9.10-i386-server-20100121.manifest.xml");
      assertEquals(ubuntuKarmic.getOsFamily(), OsFamily.UBUNTU);
      assertEquals(ubuntuKarmic.getUserMetadata(), ImmutableMap.<String, String> of("owner",
               "099720109477"));
      assertEquals(ubuntuKarmic.getVersion(), "20100121");

      // should skip testing image
      assert parser.apply(Iterables.get(result, 3)) == null;

      org.jclouds.compute.domain.Image alesticHardy = parser.apply(Iterables.get(result, 4));

      assertEquals(alesticHardy.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(alesticHardy.getDescription(),
               "alestic/ubuntu-8.04-hardy-base-20080905.manifest.xml");
      assertEquals(alesticHardy.getId(), "ami-c0fa1ea9");
      assertEquals(alesticHardy.getLocation(), null);
      assertEquals(alesticHardy.getName(), "8.04");
      assertEquals(alesticHardy.getOsDescription(),
               "alestic/ubuntu-8.04-hardy-base-20080905.manifest.xml");
      assertEquals(alesticHardy.getOsFamily(), OsFamily.UBUNTU);
      assertEquals(alesticHardy.getUserMetadata(), ImmutableMap.<String, String> of("owner",
               "063491364108"));
      assertEquals(alesticHardy.getVersion(), "20080905");

      // should skip kernel
      assert parser.apply(Iterables.get(result, 5)) == null;
   }

   public void testParseVostokImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/vostok.xml");

      Set<Image> result = parseImages(is);

      ImageParser parser = new ImageParser(
               new EC2PopulateDefaultLoginCredentialsForImageStrategy(), ImmutableMap
                        .<String, Location> of());

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(image.getDescription(),
               "vostok-builds/vostok-0.95-5622/vostok-0.95-5622.manifest.xml");
      assertEquals(image.getId(), "ami-870de2ee");
      assertEquals(image.getLocation(), null);
      assertEquals(image.getName(), "");
      assertEquals(image.getOsDescription(),
               "vostok-builds/vostok-0.95-5622/vostok-0.95-5622.manifest.xml");
      assertEquals(image.getOsFamily(), null);
      assertEquals(image.getUserMetadata(), ImmutableMap.<String, String> of("owner",
               "133804938231"));
      assertEquals(image.getVersion(), "");

   }

   private Set<Image> parseImages(InputStream is) {
      DescribeImagesResponseHandler handler = injector
               .getInstance(DescribeImagesResponseHandler.class);
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
