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
package org.jclouds.aws.ec2.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.compute.functions.ImageParser;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "compute.PropertiesTest")
public class ImageParserTest extends BaseHandlerTest {

   public void testParseVostokImage() {
      InputStream is = getClass().getResourceAsStream("/ec2/vostok.xml");

      Set<Image> result = parseImages(is);

      ImageParser parser = new ImageParser();

      org.jclouds.compute.domain.Image image = parser.apply(Iterables.get(result, 0));

      assertEquals(image.getArchitecture(), org.jclouds.compute.domain.Architecture.X86_32);
      assertEquals(image.getDescription(),
               "vostok-builds/vostok-0.95-5622/vostok-0.95-5622.manifest.xml");
      assertEquals(image.getId(), "ami-870de2ee");
      assertEquals(image.getLocationId(), "default");
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
      expect(request.getArgs()).andReturn(new Object[] { Region.DEFAULT });
      replay(request);
      handler.setContext(request);
   }
}
