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
package org.jclouds.aws.ec2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_ENDPOINT;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_ACCESSKEYID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_EXPIREINTERVAL;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_SECRETACCESSKEY;

import java.net.URI;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in EC2 Clients
 * 
 * @author Adrian Cole
 */
public class EC2PropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_EC2_ENDPOINT, "https://ec2.us-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_AWS_EXPIREINTERVAL, "60");
      // alestic and canonical
      properties.setProperty(PROPERTY_EC2_AMI_OWNERS, "063491364108,099720109477");
      // auth fail sometimes happens in EC2, as the rc.local script that injects the
      // authorized key executes after ssh has started
      properties.setProperty("jclouds.ssh.max_retries", "6");
      properties.setProperty("jclouds.ssh.retryable_messages",
               "Auth fail,invalid data,End of IO Stream Read,Connection reset");
      return properties;
   }

   public EC2PropertiesBuilder(Properties properties) {
      super(properties);
   }

   public EC2PropertiesBuilder(String id, String secret) {
      super();
      withCredentials(id, secret);
   }

   public EC2PropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(PROPERTY_AWS_ACCESSKEYID, checkNotNull(id, "awsAccessKeyId"));
      properties.setProperty(PROPERTY_AWS_SECRETACCESSKEY, checkNotNull(secret,
               "awsSecretAccessKey"));
      return this;
   }

   public EC2PropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(PROPERTY_EC2_ENDPOINT, checkNotNull(endpoint, "endpoint").toString());
      return this;
   }

   public EC2PropertiesBuilder withRequestExpiration(long seconds) {
      properties.setProperty(PROPERTY_AWS_EXPIREINTERVAL, seconds + "");
      return this;
   }
}
