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
package org.jclouds.gogrid.services;

import org.jclouds.date.TimeStamp;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.config.GoGridRestClientModule;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class BaseGoGridRestClientExpectTest extends BaseRestClientExpectTest<GoGridClient> {

   public BaseGoGridRestClientExpectTest() {
      provider = "gogrid";
   }

      @ConfiguresRestClient
   protected static final class TestGoGridRestClientModule extends GoGridRestClientModule {

      @Override
      protected Long provideTimeStamp(@TimeStamp Supplier<Long> cache) {
         return 1267243795L;
      }
   }

   @Override
   protected Module createModule() {
      return new TestGoGridRestClientModule();
   }
}
