/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.cloudloadbalancers.v1;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.rackspace.cloudidentity.v2_0.ServiceType;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationApiModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;
import org.jclouds.rackspace.cloudloadbalancers.v1.config.CloudLoadBalancersHttpApiModule;
import org.jclouds.rackspace.cloudloadbalancers.v1.loadbalancer.config.CloudLoadBalancersLoadBalancerContextModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
/**
 * Implementation of {@link ApiMetadata} for CloudLoadBalancers 1.0 API
 * 
 * @author Adrian Cole
 */
public class CloudLoadBalancersApiMetadata extends BaseHttpApiMetadata<CloudLoadBalancersApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CloudLoadBalancersApiMetadata() {
      this(new Builder());
   }

   protected CloudLoadBalancersApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.LOAD_BALANCERS);
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<CloudLoadBalancersApi, Builder> {

      protected Builder() {
         id("rackspace-cloudloadbalancers")
               .name("Rackspace Cloud Load Balancers API")
               .identityName("Username")
               .credentialName("API Key")
               .documentation(URI.create("http://docs.rackspace.com/loadbalancers/api/clb-devguide-latest/index.html"))
               .version("1.0")
               .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
               .defaultProperties(CloudLoadBalancersApiMetadata.defaultProperties())
               .view(typeToken(LoadBalancerServiceContext.class))
               .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                            .add(CloudIdentityAuthenticationApiModule.class)
                            .add(CloudIdentityAuthenticationModule.class)
                            .add(ZoneModule.class)
                            .add(CloudLoadBalancersHttpApiModule.class)
                            .add(CloudLoadBalancersLoadBalancerContextModule.class).build());
      }

      @Override
      public CloudLoadBalancersApiMetadata build() {
         return new CloudLoadBalancersApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
