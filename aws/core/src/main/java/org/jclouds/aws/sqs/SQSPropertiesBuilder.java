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
package org.jclouds.aws.sqs;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_ACCESSKEYID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_EXPIREINTERVAL;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_SECRETACCESSKEY;
import static org.jclouds.aws.sqs.reference.SQSConstants.PROPERTY_SQS_ENDPOINT;
import static org.jclouds.aws.sqs.reference.SQSConstants.PROPERTY_SQS_ENDPOINT_AP_SOUTHEAST_1;
import static org.jclouds.aws.sqs.reference.SQSConstants.PROPERTY_SQS_ENDPOINT_EU_WEST_1;
import static org.jclouds.aws.sqs.reference.SQSConstants.PROPERTY_SQS_ENDPOINT_US_EAST_1;
import static org.jclouds.aws.sqs.reference.SQSConstants.PROPERTY_SQS_ENDPOINT_US_WEST_1;

import java.net.URI;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in SQS Clients
 * 
 * @author Adrian Cole
 */
public class SQSPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_SQS_ENDPOINT, "https://sqs.us-east-1.amazonaws.com");
      properties
               .setProperty(PROPERTY_SQS_ENDPOINT_US_EAST_1, "https://sqs.us-east-1.amazonaws.com");
      properties
               .setProperty(PROPERTY_SQS_ENDPOINT_US_WEST_1, "https://sqs.us-west-1.amazonaws.com");
      properties
               .setProperty(PROPERTY_SQS_ENDPOINT_EU_WEST_1, "https://sqs.eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_SQS_ENDPOINT_AP_SOUTHEAST_1,
               "https://sqs.ap-southeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_AWS_EXPIREINTERVAL, "60");
      return properties;
   }

   public SQSPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public SQSPropertiesBuilder(String id, String secret) {
      super();
      withCredentials(id, secret);
   }

   public SQSPropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(PROPERTY_AWS_ACCESSKEYID, checkNotNull(id, "awsAccessKeyId"));
      properties.setProperty(PROPERTY_AWS_SECRETACCESSKEY, checkNotNull(secret,
               "awsSecretAccessKey"));
      return this;
   }

   public SQSPropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(PROPERTY_SQS_ENDPOINT, checkNotNull(endpoint, "endpoint").toString());
      return this;
   }

   public SQSPropertiesBuilder withRequestExpiration(long seconds) {
      properties.setProperty(PROPERTY_AWS_EXPIREINTERVAL, seconds + "");
      return this;
   }
}
