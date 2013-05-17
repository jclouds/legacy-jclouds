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
package org.jclouds.route53.internal;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import org.jclouds.date.DateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.internal.BaseRestApiExpectTest;
import org.jclouds.route53.config.Route53HttpApiModule;

import com.google.inject.Module;
/**
 * 
 * @author Adrian Cole
 */
public class BaseRoute53ExpectTest<T> extends BaseRestApiExpectTest<T> {

   public BaseRoute53ExpectTest() {
      provider = "route53";
   }
   
   @ConfiguresHttpApi
   private static final class TestRoute53HttpApiModule extends Route53HttpApiModule {

      @Override
      protected String provideTimeStamp(final DateService dateService) {
         return "Mon, 21 Jan 02013 19:29:03 -0800";
      }
   }

   protected final HttpResponse notFound = HttpResponse.builder().statusCode(NOT_FOUND.getStatusCode()).build();
   protected final String authForDate = "AWS3-HTTPS AWSAccessKeyId=identity,Algorithm=HmacSHA256,Signature=pylxNiLcrsjNRZOsxyT161JCwytVPHyc2rFfmNCuZKI=";

   @Override
   protected Module createModule() {
      return new TestRoute53HttpApiModule();
   }
}
