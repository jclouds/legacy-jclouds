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
package org.jclouds.elb;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.elb.config.ELBRestClientModule;
import org.jclouds.elb.loadbalancer.config.ELBLoadBalancerContextModule;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Amazon's Elastic Load Balancing api.
 * 
 * @author Adrian Cole
 */
public class ELBApiMetadata extends BaseRestApiMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -7077953935392202824L;
   
   public static final TypeToken<RestContext<ELBClient, ELBAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<ELBClient, ELBAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public ELBApiMetadata() {
      this(new Builder(ELBClient.class, ELBAsyncClient.class));
   }

   protected ELBApiMetadata(Builder builder) {
      super(builder);
   }
   
   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      return properties;
   }
   
   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder(Class<?> client, Class<?> asyncClient) {
         super(client, asyncClient);
         id("elb")
         .name("Amazon Elastic Load Balancing Api")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .version(ELBAsyncClient.VERSION)
         .defaultProperties(ELBApiMetadata.defaultProperties())
         .defaultEndpoint("https://elasticloadbalancing.us-east-1.amazonaws.com")
         .documentation(URI.create("http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference"))
         .view(LoadBalancerServiceContext.class)
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(ELBRestClientModule.class, ELBLoadBalancerContextModule.class));
      }

      @Override
      public ELBApiMetadata build() {
         return new ELBApiMetadata(this);
      }
      
      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}