/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.codegen.ec2.queryapi.transform;

import org.jclouds.codegen.ec2.queryapi.AmazonEC2QueryAPI;
import org.jclouds.codegen.model.API;

/**
 * Converts object models representing AWS API beans into Java classes.
 * <p>
 * This implementation is designed to perform the following steps:
 * <ul>
 * <li>Parse the JSON object representation produced by the <tt>parse_ec2.pl</tt> perl script</li>
 * <li>Convert the JSON into Java object models (@see org.jclouds.aws.codegen.models)</li>
 * 
 * @author Adrian Cole
 */
public class AmazonEC2QueryAPITransformer {

   public API transform(AmazonEC2QueryAPI amazonAPI) {
      API api = new API();
      
      return api;
   }
   
 
}
