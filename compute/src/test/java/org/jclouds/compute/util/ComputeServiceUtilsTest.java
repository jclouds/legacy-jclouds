/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.compute.util.ComputeServiceUtils.formatStatus;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsCommaDelimitedValue;
import static org.jclouds.compute.util.ComputeServiceUtils.metadataAndTagsAsValuesOfEmptyString;
import static org.jclouds.compute.util.ComputeServiceUtils.parseVersionOrReturnEmptyString;
import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.ComputeMetadataIncludingStatus;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Guice;

/**
 * Test the compute utils.
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit", testName = "ComputeServiceUtilsTest")
public class ComputeServiceUtilsTest {
   
   @SuppressWarnings("unchecked")
   @Test
   public void testFormatStatusWithBackendStatus() {
      ComputeMetadataIncludingStatus<Image.Status> resource = createMock(ComputeMetadataIncludingStatus.class);
      expect(resource.getStatus()).andReturn(Image.Status.PENDING);
      expect(resource.getBackendStatus()).andReturn("queued").anyTimes();
      replay(resource);
      assertEquals(formatStatus(resource), "PENDING[queued]");
      verify(resource);
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testFormatStatusWithoutBackendStatus() {
      ComputeMetadataIncludingStatus<Image.Status> resource = createMock(ComputeMetadataIncludingStatus.class);
      expect(resource.getStatus()).andReturn(Image.Status.PENDING);
      expect(resource.getBackendStatus()).andReturn(null).anyTimes();
      replay(resource);
      assertEquals(formatStatus(resource), "PENDING");
      verify(resource);
   }
   
   Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
   }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
         .getInstance(Json.class));

   @Test
   public void testMetadataAndTagsAsValuesOfEmptyString() {
      TemplateOptions options = TemplateOptions.Builder.tags(ImmutableSet.of("tag")).userMetadata(ImmutableMap.<String, String>of("foo", "bar"));
      assertEquals(metadataAndTagsAsValuesOfEmptyString(options), ImmutableMap.<String, String>of("foo", "bar", "tag", ""));
   }
   
   @Test
   public void testMetadataAndTagsAsCommaDelimitedValue() {
      TemplateOptions options = TemplateOptions.Builder.tags(ImmutableSet.of("tag")).userMetadata(ImmutableMap.<String, String>of("foo", "bar"));
      assertEquals(metadataAndTagsAsCommaDelimitedValue(options), ImmutableMap.<String, String>of("foo", "bar", "jclouds_tags", "tag"));
   }

   @Test
   public void testMetadataAndTagsAsValuesOfEmptyStringNoTags() {
      TemplateOptions options = TemplateOptions.Builder.userMetadata(ImmutableMap.<String, String>of("foo", "bar"));
      assertEquals(metadataAndTagsAsValuesOfEmptyString(options), ImmutableMap.<String, String>of("foo", "bar"));
   }
   
   @Test
   public void testMetadataAndTagsAsCommaDelimitedValueNoTags() {
      TemplateOptions options = TemplateOptions.Builder.userMetadata(ImmutableMap.<String, String>of("foo", "bar"));
      assertEquals(metadataAndTagsAsCommaDelimitedValue(options), ImmutableMap.<String, String>of("foo", "bar"));
   }
   
   @Test
   public void testParseVersionOrReturnEmptyStringUbuntu1004() {
      assertEquals(parseVersionOrReturnEmptyString(OsFamily.UBUNTU, "Ubuntu 10.04", map), "10.04");
   }

   @Test
   public void testParseVersionOrReturnEmptyStringUbuntu1104() {
      assertEquals(parseVersionOrReturnEmptyString(OsFamily.UBUNTU, "ubuntu 11.04 server (i386)", map), "11.04");
   }

   @Test
   public void testExecHttpResponse() {
      HttpRequest request = HttpRequest.builder()
                                       .method("GET")
                                       .endpoint("https://adriancolehappy.s3.amazonaws.com/java/install")
                                       .addHeader("Host", "adriancolehappy.s3.amazonaws.com")
                                       .addHeader("Date", "Sun, 12 Sep 2010 08:25:19 GMT")
                                       .addHeader("Authorization", "AWS 0ASHDJAS82:JASHFDA=").build();

      assertEquals(
            ComputeServiceUtils.execHttpResponse(request).render(org.jclouds.scriptbuilder.domain.OsFamily.UNIX),
            "curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET -H \"Host: adriancolehappy.s3.amazonaws.com\" -H \"Date: Sun, 12 Sep 2010 08:25:19 GMT\" -H \"Authorization: AWS 0ASHDJAS82:JASHFDA=\" https://adriancolehappy.s3.amazonaws.com/java/install |(bash)\n");

   }

   @Test
   public void testTarxzpHttpResponse() {
      HttpRequest request = HttpRequest.builder()
                                       .method("GET")
                                       .endpoint("https://adriancolehappy.s3.amazonaws.com/java/install")
                                       .addHeader("Host", "adriancolehappy.s3.amazonaws.com")
                                       .addHeader("Date", "Sun, 12 Sep 2010 08:25:19 GMT")
                                       .addHeader("Authorization", "AWS 0ASHDJAS82:JASHFDA=").build();
            
      assertEquals(
            ComputeServiceUtils.extractTargzIntoDirectory(request, "/stage/").render(
                  org.jclouds.scriptbuilder.domain.OsFamily.UNIX),
            "curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET -H \"Host: adriancolehappy.s3.amazonaws.com\" -H \"Date: Sun, 12 Sep 2010 08:25:19 GMT\" -H \"Authorization: AWS 0ASHDJAS82:JASHFDA=\" https://adriancolehappy.s3.amazonaws.com/java/install |(mkdir -p /stage/ &&cd /stage/ &&tar -xpzf -)\n");
   }

   @Test
   public void testGetPortRangesFromList() {
      Map<Integer, Integer> portRanges = Maps.newHashMap();
      portRanges.put(5, 7);
      portRanges.put(10, 11);
      portRanges.put(20, 20);
      assertEquals(portRanges, ComputeServiceUtils.getPortRangesFromList(5, 6, 7, 10, 11, 20));
   }
}
