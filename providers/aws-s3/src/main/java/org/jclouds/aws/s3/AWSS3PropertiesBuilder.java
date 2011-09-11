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
package org.jclouds.aws.s3;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.domain.Region.AP_NORTHEAST_1;
import static org.jclouds.aws.domain.Region.AP_SOUTHEAST_1;
import static org.jclouds.aws.domain.Region.US_STANDARD;
import static org.jclouds.aws.domain.Region.US_WEST_1;
import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;

import java.util.Properties;

import org.jclouds.aws.domain.Region;

/**
 * Builds properties used in S3 Clients
 * 
 * @author Adrian Cole
 */
public class AWSS3PropertiesBuilder extends org.jclouds.s3.S3PropertiesBuilder {

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.putAll(Region.regionPropertiesS3());
      properties.setProperty(PROPERTY_ENDPOINT, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_STANDARD + "." + ENDPOINT, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_WEST_1 + "." + ENDPOINT, "https://s3-us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + "EU" + "." + ENDPOINT, "https://s3-eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_SOUTHEAST_1 + "." + ENDPOINT,
            "https://s3-ap-southeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_NORTHEAST_1 + "." + ENDPOINT,
            "https://s3-ap-northeast-1.amazonaws.com");
      return properties;
   }

   public AWSS3PropertiesBuilder() {
      super();
   }

   public AWSS3PropertiesBuilder(Properties properties) {
      super(properties);
   }

}
