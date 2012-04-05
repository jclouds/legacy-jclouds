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
package org.jclouds.aws.ec2;

import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.jclouds.aws.ec2.compute.AWSEC2ComputeServiceContext;
import org.jclouds.aws.ec2.compute.config.AWSEC2ComputeServiceContextModule;
import org.jclouds.aws.ec2.config.AWSEC2RestClientModule;
import org.jclouds.ec2.EC2ContextBuilder;
import org.jclouds.providers.ProviderMetadata;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class AWSEC2ContextBuilder extends
      EC2ContextBuilder<AWSEC2Client, AWSEC2AsyncClient, AWSEC2ComputeServiceContext, AWSEC2ApiMetadata> {
   public AWSEC2ContextBuilder() {
      this(new AWSEC2ProviderMetadata());
   }

   public AWSEC2ContextBuilder(
         ProviderMetadata<AWSEC2Client, AWSEC2AsyncClient, AWSEC2ComputeServiceContext, AWSEC2ApiMetadata> providerMetadata) {
      super(providerMetadata);
   }

   public AWSEC2ContextBuilder(AWSEC2ApiMetadata apiMetadata) {
      super(apiMetadata);
   }

   @Override
   public AWSEC2ContextBuilder overrides(Properties overrides) {
      super.overrides(warnAndReplaceIfUsingOldImageKey(overrides));
      return this;
   }

   // TODO: determine how to do conditional manipulation w/rocoto
   static Properties warnAndReplaceIfUsingOldImageKey(Properties props) {
      if (props.containsKey(PROPERTY_EC2_AMI_OWNERS)) {
         StringBuilder query = new StringBuilder();
         String owners = props.remove(PROPERTY_EC2_AMI_OWNERS).toString();
         if ("*".equals(owners))
            query.append("state=available;image-type=machine");
         else if (!"".equals(owners))
            query.append("owner-id=").append(owners).append(";state=available;image-type=machine");
         else if ("".equals(owners))
            query = new StringBuilder();
         props.setProperty(PROPERTY_EC2_AMI_QUERY, query.toString());
         Logger.getAnonymousLogger().warning(
               String.format("Property %s is deprecated, please use new syntax: %s=%s", PROPERTY_EC2_AMI_OWNERS,
                     PROPERTY_EC2_AMI_QUERY, query.toString()));
      }
      return props;
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new AWSEC2RestClientModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new AWSEC2ComputeServiceContextModule());
   }

   @VisibleForTesting
   public Properties getOverrides() {
      return overrides;
   }

}
