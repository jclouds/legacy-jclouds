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
package org.jclouds.aws.ec2.commands.options;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.CommonEC2Parameters.EXPIRES;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.http.HttpRequest;
import org.jclouds.util.DateService;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import org.jclouds.util.Jsr330;

@Test(groups = "unit", testName = "s3.EC2QuerySignerTest")
public class EC2QuerySignerTest {

   @Test
   void testExpires() {
      UriBuilder builder = UriBuilder.fromUri(URI.create("https://ec2.amazonaws.com/"));
      builder.queryParam(ACTION,"DescribeImages");
      builder.queryParam(EXPIRES,"2008-02-10T12%3A00%3A00Z");
      builder.queryParam("ImageId.1","ami-2bb65342");
      HttpRequest request = new HttpRequest(HttpMethod.GET,builder.build());
      createFilter();
   }

   @Test
   void testAclQueryString() {
      URI host = URI.create("http://s3.amazonaws.com:80/?acl");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      StringBuilder builder = new StringBuilder();
      createFilter().appendUriPath(request, builder);
      assertEquals(builder.toString(), "/?acl");
   }

   // "?acl", "?location", "?logging", or "?torrent"

   @Test
   void testAppendBucketNameHostHeaderService() {
      URI host = URI.create("http://s3.amazonaws.com:80");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      request.getHeaders().put(HttpHeaders.HOST, "s3.amazonaws.com");
      StringBuilder builder = new StringBuilder();
      createFilter().appendBucketName(request, builder);
      assertEquals(builder.toString(), "");
   }

   @Test
   void testAppendBucketNameURIHost() {
      URI host = URI.create("http://adriancole.s3int5.s3-external-3.amazonaws.com:80");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      StringBuilder builder = new StringBuilder();
      createFilter().appendBucketName(request, builder);
      assertEquals(builder.toString(), "/adriancole.s3int5");
   }



   private EC2QuerySigner createFilter() {
      return Guice.createInjector(new AbstractModule() {

         protected void configure() {
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "foo");
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_SECRETACCESSKEY)).to(
                     "bar");
            bind(DateService.class);

         }
      }).getInstance(EC2QuerySigner.class);
   }

}