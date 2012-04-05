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
package org.jclouds.cloudwatch;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for Amazon's CloudWatch api.
 * 
 * <h3>note</h3>
 * <p/>
 * This class allows overriding of types {@code S}(client) and {@code A}(asyncClient), so that
 * children can add additional methods not declared here, such as new features
 * from AWS.
 * <p/>
 * 
 * This class is not setup to allow a different context than {@link RestContext}
 * . By doing so, it reduces the type complexity.
 * 
 * @author Adrian Cole
 */
public class CloudWatchApiMetadata<S extends CloudWatchClient, A extends CloudWatchAsyncClient> extends
      BaseRestApiMetadata<S, A, RestContext<S, A>, CloudWatchApiMetadata<S, A>> {

   @Override
   public Builder<S, A> toBuilder() {
      return new Builder<S, A>(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public CloudWatchApiMetadata() {
      this(new Builder<CloudWatchClient, CloudWatchAsyncClient>(CloudWatchClient.class, CloudWatchAsyncClient.class));
   }

   @SuppressWarnings("unchecked")
   protected CloudWatchApiMetadata(Builder<?, ?> builder) {
      super(Builder.class.cast(builder));
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseApiMetadata.Builder.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      return properties;
   }

   public static class Builder<S extends CloudWatchClient, A extends CloudWatchAsyncClient> extends
         BaseRestApiMetadata.Builder<S, A, RestContext<S, A>, CloudWatchApiMetadata<S, A>> {

      protected Builder(Class<S> client, Class<A> asyncClient) {
         super(client, asyncClient);
         id("cloudwatch")
         .type(ApiType.MONITOR)
         .name("Amazon CloudWatch Api")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .version(CloudWatchAsyncClient.VERSION)
         .defaultProperties(CloudWatchApiMetadata.defaultProperties())
         .defaultEndpoint("https://monitoring.us-east-1.amazonaws.com")
         .documentation(URI.create("http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/"));
      }

      @Override
      public CloudWatchApiMetadata<S, A> build() {
         return new CloudWatchApiMetadata<S, A>(this);
      }
      
      @Override
      public Builder<S, A> fromApiMetadata(CloudWatchApiMetadata<S, A> in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}