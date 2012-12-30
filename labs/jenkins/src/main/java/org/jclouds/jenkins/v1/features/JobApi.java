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
package org.jclouds.jenkins.v1.features;

import java.util.Map;
import org.jclouds.jenkins.v1.domain.JobDetails;
import org.jclouds.jenkins.v1.domain.LastBuild;

/**
 * Job Services
 * 
 * @see JobAsyncApi
 * @author Adrian Cole
 * @see <a href= "http://ci.jruby.org/computer/api/" >api doc</a>
 */
public interface JobApi {

   /**
    * creates a job, given the payload
    * 
    * @param displayName
    * @param xml
    */
   void createFromXML(String displayName, String xml);

   JobDetails get(String displayName);

   void delete(String displayName);
   
   /**
    * Build a job via API
    * 
    * If security is enabled, provide username/password of an account with build permission in the request.
    * Another alternative (but deprecated) is to configure the 'Trigger builds remotely' section in the job configuration.
    * 
    * @param displayName
    */
   void build(String displayName);

   void buildWithParameters(String displayName, Map<String, String> parameters);
   
   String fetchConfigXML(String displayName);
   
   LastBuild lastBuild(String displayName);
}
