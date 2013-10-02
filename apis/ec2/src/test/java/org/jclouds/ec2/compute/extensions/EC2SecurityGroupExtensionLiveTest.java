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
package org.jclouds.ec2.compute.extensions;

import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.extensions.internal.BaseSecurityGroupExtensionLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Live test for ec2 {@link SecurityGroupExtension} implementation
 * 
 * @author Andrew Bayer
 * 
 */
@Test(groups = "live", singleThreaded = true, testName = "EC2SecurityGroupExtensionLiveTest")
public class EC2SecurityGroupExtensionLiveTest extends BaseSecurityGroupExtensionLiveTest {

   public EC2SecurityGroupExtensionLiveTest() {
      provider = "ec2";
   }

}
