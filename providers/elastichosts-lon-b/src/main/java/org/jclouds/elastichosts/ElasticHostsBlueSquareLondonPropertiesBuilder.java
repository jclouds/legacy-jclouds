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
package org.jclouds.elastichosts;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;

import java.util.Properties;

import org.jclouds.elasticstack.ElasticStackPropertiesBuilder;

/**
 * 
 * @author Adrian Cole
 */
public class ElasticHostsBlueSquareLondonPropertiesBuilder extends ElasticStackPropertiesBuilder {

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ISO3166_CODES, "GB-LND");
      properties.setProperty(PROPERTY_ENDPOINT, "https://api.lon-b.elastichosts.com");
      properties.setProperty(PROPERTY_API_VERSION, "2.0");
      return properties;
   }

   public ElasticHostsBlueSquareLondonPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
