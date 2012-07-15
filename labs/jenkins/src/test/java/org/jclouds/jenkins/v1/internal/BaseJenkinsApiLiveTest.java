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
package org.jclouds.jenkins.v1.internal;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.jenkins.v1.JenkinsApiMetadata;
import org.jclouds.jenkins.v1.JenkinsAsyncApi;
import org.jclouds.jenkins.v1.JenkinsApi;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * Tests behavior of {@code JenkinsApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseJenkinsApiLiveTest extends BaseContextLiveTest<RestContext<JenkinsApi, JenkinsAsyncApi>> {

   public BaseJenkinsApiLiveTest() {
      provider = "jenkins";
   }

   protected RestContext<JenkinsApi, JenkinsAsyncApi> jenkinsContext;
   
   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      jenkinsContext = context;
   }

   @Override
   protected TypeToken<RestContext<JenkinsApi, JenkinsAsyncApi>> contextType() {
      return JenkinsApiMetadata.CONTEXT_TOKEN;
   }

}
