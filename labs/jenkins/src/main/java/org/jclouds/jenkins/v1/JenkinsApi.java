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
package org.jclouds.jenkins.v1;

import org.jclouds.jenkins.v1.domain.Node;
import org.jclouds.jenkins.v1.features.ComputerApi;
import org.jclouds.jenkins.v1.features.JobApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to Jenkins.
 * <p/>
 * 
 * @see JenkinsAsyncApi
 * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Remote+access+API">api doc</a>
 * @author Adrian Cole
 */
public interface JenkinsApi {
   /**
    * @return the master computer
    */
   Node getMaster();
   
   /**
    * Provides synchronous access to Computer features.
    */
   @Delegate
   ComputerApi getComputerApi();
   
   /**
    * Provides synchronous access to Job features.
    */
   @Delegate
   JobApi getJobApi();
}
