/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.aws.s3.binders;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.inject.Provider;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.binders.BindAsHostPrefix;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.uri.UriBuilderImpl;

/**
 * Tests behavior of
 * {@code AssignCorrectHostnameAndBindAsHostPrefixIfConfigured}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "AssignCorrectHostnameAndBindAsHostPrefixIfConfiguredTest")
public class AssignCorrectHostnameAndBindAsHostPrefixIfConfiguredTest {
   Provider<UriBuilder> uriBuilderProvider = new Provider<UriBuilder>() {

      @Override
      public UriBuilder get() {
         return new UriBuilderImpl();
      }

   };

   public void testWhenNoBucketRegionMappingInCache() {
      HttpRequest request = new HttpRequest("GET", URI.create("https://s3.amazonaws.com"));

      AssignCorrectHostnameAndBindAsHostPrefixIfConfigured binder = new AssignCorrectHostnameAndBindAsHostPrefixIfConfigured(
            new BindAsHostPrefix(uriBuilderProvider), new RegionToEndpointOrProviderIfNull("aws-s3",
                  URI.create("https://s3.amazonaws.com"), ImmutableMap.of("us-standard",
                        URI.create("https://s3.amazonaws.com"), "us-west-1",
                        URI.create("https://s3-us-west-1.amazonaws.com"))), uriBuilderProvider,
            ImmutableMap.<String, String> of());

      request = binder.bindToRequest(request, "bucket");
      assertEquals(request.getRequestLine(), "GET https://bucket.s3.amazonaws.com HTTP/1.1");

   }

   public void testWhenBucketRegionMappingInCache() {
      HttpRequest request = new HttpRequest("GET", URI.create("https://s3.amazonaws.com"));

      AssignCorrectHostnameAndBindAsHostPrefixIfConfigured binder = new AssignCorrectHostnameAndBindAsHostPrefixIfConfigured(
            new BindAsHostPrefix(uriBuilderProvider), new RegionToEndpointOrProviderIfNull("aws-s3",
                  URI.create("https://s3.amazonaws.com"), ImmutableMap.of("us-standard",
                        URI.create("https://s3.amazonaws.com"), "us-west-1",
                        URI.create("https://s3-us-west-1.amazonaws.com"))), uriBuilderProvider,
            ImmutableMap.<String, String> of("bucket", "us-west-1"));

      request = binder.bindToRequest(request, "bucket");
      assertEquals(request.getRequestLine(), "GET https://bucket.s3-us-west-1.amazonaws.com HTTP/1.1");

   }
}
