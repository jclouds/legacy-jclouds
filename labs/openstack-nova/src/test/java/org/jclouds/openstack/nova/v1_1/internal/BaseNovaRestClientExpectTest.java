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
package org.jclouds.openstack.nova.v1_1.internal;

import java.net.URI;
import java.util.Date;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.RequiresHttp;
import org.jclouds.openstack.config.OpenStackAuthenticationModule;
import org.jclouds.openstack.filters.AddTimestampQuery;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.config.NovaRestClientModule;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Module;

/**
 * Base class for writing Nova Rest Client Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseNovaRestClientExpectTest extends BaseRestClientExpectTest<NovaClient> {
   
   
   public BaseNovaRestClientExpectTest() {
      provider = "openstack-nova";
   }

   
   //TODO: change to keystone
   protected HttpRequest initialAuth = HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost:5000/v1.1"))
            .headers(
            ImmutableMultimap.<String, String> builder()
            .put("X-Auth-User", "identity")
            .put("X-Auth-Key", "credential")
            .put("Accept", "*/*").build()).build();

   protected String authToken = "d6245d35-22a0-47c0-9770-2c5097da25fc";

   // FIXME: KeyStone
   protected HttpResponse responseWithUrls = HttpResponse
            .builder()
            .statusCode(204)
            .message("HTTP/1.1 204 No Content")
            .headers(ImmutableMultimap.<String,String>builder()
            .put("Server", "Apache/2.2.3 (Red Hat)")
            .put("vary", "X-Auth-Token,X-Auth-Key,X-Storage-User,X-Storage-Pass")
            .put("Cache-Control", "s-maxage=86399")
            .put("Content-Type", "text/xml")
            .put("Date", "Tue, 10 Jan 2012 22:08:47 GMT")
            .put("X-Auth-Token", authToken)
            .put("X-Server-Management-Url","http://localhost:8774/v1.1/identity")
            .put("Connection", "Keep-Alive")
            .put("Content-Length", "0")
            .build()).build();
   
   
   protected static final String CONSTANT_DATE = "2009-11-08T15:54:08.897Z";

   /**
    * override so that we can control the timestamp used in {@link AddTimestampQuery}
    */
   static class TestOpenStackAuthenticationModule extends OpenStackAuthenticationModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      public Supplier<Date> provideCacheBusterDate() {
         return new Supplier<Date>() {
            public Date get() {
               return new SimpleDateFormatDateService().iso8601DateParse(CONSTANT_DATE);
            }
         };
      }

   }

   @Override
   protected Module createModule() {
      return new TestNovaRestClientModule();
   }

   @ConfiguresRestClient
   @RequiresHttp
   protected static class TestNovaRestClientModule extends NovaRestClientModule {
      private TestNovaRestClientModule() {
         super(new TestOpenStackAuthenticationModule());
      }
   }

}
