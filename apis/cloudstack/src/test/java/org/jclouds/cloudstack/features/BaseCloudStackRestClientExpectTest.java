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
package org.jclouds.cloudstack.features;

import org.jclouds.cloudstack.CloudStackContextBuilder;
import org.jclouds.cloudstack.CloudStackPropertiesBuilder;
import org.jclouds.rest.BaseRestClientExpectTest;

import java.util.Properties;

/**
 * Base class for writing CloudStack Rest Client Expect tests
 *
 * @author Andrei Savu
 */
public class BaseCloudStackRestClientExpectTest<S> extends BaseRestClientExpectTest<S> {

   public BaseCloudStackRestClientExpectTest() {
      provider = "cloudstack";
   }

   @Override
   public Properties setupRestProperties() {
      Properties overrides = new Properties();
      overrides.put("cloudstack.contextbuilder", CloudStackContextBuilder.class.getName());
      overrides.put("cloudstack.propertiesbuilder", CloudStackPropertiesBuilder.class.getName());
      return overrides;
   }

   @Override
   public Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.put("jclouds.endpoint", "http://localhost:8080/client/api");
      return overrides;
   }

}
