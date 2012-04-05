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
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.loadbalancer.internal.BaseLoadBalancerServiceApiMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Amazon's Elastic Load Balancing api.
 * <h3>note</h3>
 * <p/>
 * This class allows overriding of types {@code S}(client) and {@code A}(asyncClient), so that
 * children can add additional methods not declared here, such as new features
 * from AWS.
 * <p/>
 * 
 * This class is not setup to allow a different context than {@link LoadBalancerServiceContext}
 * . By doing so, it reduces the type complexity.
 * 
 * @author Adrian Cole
 */
public class ELBApiMetadata<S extends ELBClient, A extends ELBAsyncClient> extends
      BaseLoadBalancerServiceApiMetadata<S, A, LoadBalancerServiceContext<S, A>, ELBApiMetadata<S, A>> {

   @Override
   public Builder<S, A> toBuilder() {
      return new Builder<S, A>(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public ELBApiMetadata() {
      this(new Builder<ELBClient, ELBAsyncClient>(ELBClient.class, ELBAsyncClient.class));
   }

   protected ELBApiMetadata(Builder<?, ?> builder) {
      super(builder);
   }
   
   protected static Properties defaultProperties() {
      Properties properties = BaseApiMetadata.Builder.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_HEADER_TAG, "amz");
      return properties;
   }
   
   public static class Builder<S extends ELBClient, A extends ELBAsyncClient> extends
         BaseLoadBalancerServiceApiMetadata.Builder<S, A, LoadBalancerServiceContext<S, A>, ELBApiMetadata<S, A>> {

      protected Builder(Class<S> client, Class<A> asyncClient) {
         id("elb")
         .name("Amazon Elastic Load Balancing Api")
         .identityName("Access Key ID")
         .credentialName("Secret Access Key")
         .version(ELBAsyncClient.VERSION)
         .defaultProperties(ELBApiMetadata.defaultProperties())
         .defaultEndpoint("https://elasticloadbalancing.us-east-1.amazonaws.com")
         .documentation(URI.create("http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference"))
         .javaApi(client, asyncClient)
         .contextBuilder(new TypeToken<ELBContextBuilder<S, A>>(getClass()){
            private static final long serialVersionUID = 1L;
            });
      }

      @Override
      public ELBApiMetadata<S, A> build() {
         return new ELBApiMetadata<S, A>(this);
      }
      
      @Override
      public Builder<S, A> fromApiMetadata(ELBApiMetadata<S, A> in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}