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
package org.jclouds.ec2;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.compute.config.ComputeServiceProperties.RESOURCENAME_DELIMITER;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AUTO_ALLOCATE_ELASTIC_IPS;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_TIMEOUT_SECURITYGROUP_PRESENT;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;
import org.jclouds.ec2.compute.EC2ComputeServiceContext;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Amazon's EC2 api.
 * 
 * <h3>note</h3>
 * <p/>
 * This class allows overriding of types {@code S}(client) and {@code A}
 * (asyncClient), so that children can add additional methods not declared here,
 * such as new features from AWS.
 * <p/>
 * 
 * As this is a popular api, we also allow overrides for type {@code C}
 * (context). This allows subtypes to add in new feature groups or extensions,
 * not present in the base api. For example, you could make a subtype for
 * context, that exposes admin operations.
 * 
 * @author Adrian Cole
 */
public class EC2ApiMetadata<S extends EC2Client, A extends EC2AsyncClient, C extends EC2ComputeServiceContext<S, A>, M extends EC2ApiMetadata<S, A, C, M>>
      extends BaseComputeServiceApiMetadata<S, A, C, M> {

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public Builder<S, A, C, M> toBuilder() {
      return (Builder<S, A, C, M>) new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public EC2ApiMetadata() {
      this(new Builder(EC2Client.class, EC2AsyncClient.class));
   }

   protected EC2ApiMetadata(Builder<?, ?, ?, ?> builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseComputeServiceApiMetadata.Builder.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      properties.setProperty(PROPERTY_EC2_AMI_OWNERS, "*");
      properties.setProperty(PROPERTY_EC2_TIMEOUT_SECURITYGROUP_PRESENT, "500");
      properties.setProperty(PROPERTY_EC2_AUTO_ALLOCATE_ELASTIC_IPS, "false");
      properties.setProperty(RESOURCENAME_DELIMITER, "#");
      return properties;
   }

   public static class Builder<S extends EC2Client, A extends EC2AsyncClient, C extends EC2ComputeServiceContext<S, A>, M extends EC2ApiMetadata<S, A, C, M>>
         extends BaseComputeServiceApiMetadata.Builder<S, A, C, M> {

      protected Builder(Class<S> syncClient, Class<A> asyncClient) {
         id("ec2")
         .name("Amazon Elastic Compute Cloud (EC2) API")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .defaultEndpoint("https://ec2.us-east-1.amazonaws.com")
         .documentation(URI.create("http://docs.amazonwebservices.com/AWSEC2/latest/APIReference"))
         .version(EC2AsyncClient.VERSION)
         .defaultProperties(EC2ApiMetadata.defaultProperties())
         .javaApi(syncClient, asyncClient)
         .contextBuilder(new TypeToken<EC2ContextBuilder<S, A, C, M>>(getClass()) {
                  private static final long serialVersionUID = 1L;
               });
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      @SuppressWarnings("rawtypes")
      protected TypeToken contextToken(TypeToken<S> clientToken, TypeToken<A> asyncClientToken) {
         return new TypeToken<EC2ComputeServiceContext<S, A>>() {
            private static final long serialVersionUID = 1L;
         }.where(new TypeParameter<S>() {
         }, clientToken).where(new TypeParameter<A>() {
         }, asyncClientToken);
      }
      @SuppressWarnings({ "rawtypes", "unchecked" })
      @Override
      public M build() {
         return (M) new EC2ApiMetadata(this);
      }
      
      @Override
      public Builder<S, A, C, M> fromApiMetadata(M in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}