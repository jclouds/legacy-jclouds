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
package org.jclouds.ec2.internal;

import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.date.DateService;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.base.Supplier;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public abstract class BaseEC2ClientExpectTest<T> extends BaseEC2ExpectTest<T> {

   @ConfiguresRestClient
   protected static class TestEC2RestClientModule extends EC2RestClientModule<EC2Client, EC2AsyncClient> {

      @Override
      protected void configure() {
         super.configure();
         // predicatable node names
         final AtomicInteger suffix = new AtomicInteger();
         bind(new TypeLiteral<Supplier<String>>() {
         }).toInstance(new Supplier<String>() {

            @Override
            public String get() {
               return suffix.getAndIncrement() + "";
            }

         });
      }

      @Override
      @Provides
      protected String provideTimeStamp(DateService dateService) {
         return CONSTANT_DATE;
      }
   }

   @Override
   protected Module createModule() {
      return new TestEC2RestClientModule();
   }
}
