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

package org.jclouds.ec2.compute;

import java.util.Properties;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.common.base.Function;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * 
 * @author David Alves
 */
public abstract class BaseEC2ComputeServiceExpectTest<T> extends BaseRestClientExpectTest<T> implements
         Function<ComputeServiceContext, T> {

   protected static final String CONSTANT_DATE = "2012-04-16T15:54:08.897Z";
   protected DateService dateService = new SimpleDateFormatDateService();

   public BaseEC2ComputeServiceExpectTest() {
      provider = "ec2";
   }

   @ConfiguresRestClient
   private static final class TestEC2RestClientModule extends EC2RestClientModule<EC2Client, EC2AsyncClient> {
      @Override
      @Provides
      protected String provideTimeStamp(DateService dateService) {
         return CONSTANT_DATE;
      }
   }

   @Override
   public T createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return apply(createComputeServiceContext(fn, module, props));
   }

   private ComputeServiceContext createComputeServiceContext(Function<HttpRequest, HttpResponse> fn, Module module,
            Properties props) {
      return createInjector(fn, module, props).getInstance(ComputeServiceContext.class);
   }

   @Override
   protected Module createModule() {
      return new TestEC2RestClientModule();
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new EC2ApiMetadata();
   }

}
