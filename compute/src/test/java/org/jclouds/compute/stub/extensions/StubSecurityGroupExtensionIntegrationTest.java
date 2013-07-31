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
package org.jclouds.compute.stub.extensions;

import java.util.Properties;

import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.extensions.internal.BaseSecurityGroupExtensionLiveTest;
import org.testng.annotations.Test;


/**
 * Base test for {@link SecurityGroupExtension} implementations.
 * 
 * @author Andrew Bayer
 * 
 */
@Test(groups = { "integration", "live" }, singleThreaded = true, testName="StubSecurityGroupExtensionIntegrationTest")
public class StubSecurityGroupExtensionIntegrationTest extends BaseSecurityGroupExtensionLiveTest {

   public StubSecurityGroupExtensionIntegrationTest() {
      provider = "stub";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      // This is a hack to make sure we get a different set of node IDs, nodes, groups, etc from StubComputeServiceIntegrationTest.
      overrides.setProperty(provider + ".identity", "sec-stub");

      return overrides;
   }
}
