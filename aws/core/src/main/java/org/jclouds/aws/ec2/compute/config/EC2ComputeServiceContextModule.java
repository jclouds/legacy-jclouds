/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.compute.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.compute.domain.OsFamily.AMZN_LINUX;
import static org.jclouds.compute.domain.OsFamily.CENTOS;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.compute.EC2ComputeService;
import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.compute.strategy.EC2DestroyLoadBalancerStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2DestroyNodeStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2GetNodeMetadataStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2ListNodesStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2LoadBalanceNodesStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2RebootNodeStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2RunNodesAndAddToSetStrategy;
import org.jclouds.aws.ec2.compute.suppliers.EC2HardwareSupplier;
import org.jclouds.aws.ec2.compute.suppliers.EC2ImageSupplier;
import org.jclouds.aws.ec2.compute.suppliers.EC2LocationSupplier;
import org.jclouds.aws.ec2.compute.suppliers.RegionAndNameToImageSupplier;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.LoadBalanceNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.domain.Location;
import org.jclouds.rest.annotations.Provider;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;

/**
 * Configures the {@link ComputeServiceContext}; requires {@link EC2ComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class EC2ComputeServiceContextModule extends BaseComputeServiceContextModule {
   @Override
   protected void configure() {
      install(new EC2ComputeServiceDependenciesModule());
      super.configure();
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      String provider = injector.getInstance(Key.get(String.class, Provider.class));
      if ("eucalyptus".equals(provider))
         return template.osFamily(CENTOS);
      else if ("nova".equals(provider))
         return template.osFamily(UBUNTU);
      else
         return template.osFamily(AMZN_LINUX).os64Bit(true);
   }

   @Provides
   @Singleton
   protected Supplier<Map<RegionAndName, ? extends Image>> provideRegionAndNameToImageSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final RegionAndNameToImageSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<RegionAndName, ? extends Image>>(
               authException, seconds, new Supplier<Map<RegionAndName, ? extends Image>>() {
                  @Override
                  public Map<RegionAndName, ? extends Image> get() {
                     return supplier.get();
                  }
               });
   }

   @Override
   protected void bindLoadBalancerService() {
      bind(LoadBalanceNodesStrategy.class).to(EC2LoadBalanceNodesStrategy.class);
      bind(DestroyLoadBalancerStrategy.class).to(EC2DestroyLoadBalancerStrategy.class);
   }

   @Override
   protected Class<? extends RunNodesAndAddToSetStrategy> defineRunNodesAndAddToSetStrategy() {
      return EC2RunNodesAndAddToSetStrategy.class;
   }

   /**
    * not needed, as {@link EC2RunNodesAndAddToSetStrategy} is used and is already set-based.
    */
   @Override
   protected Class<? extends AddNodeWithTagStrategy> defineAddNodeWithTagStrategy() {
      return null;
   }

   /**
    * not needed, as {@link EC2RunNodesAndAddToSetStrategy} is used and is already set-based.
    */
   @Override
   protected void bindAddNodeWithTagStrategy(Class<? extends AddNodeWithTagStrategy> clazz) {
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return EC2DestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return EC2GetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier() {
      return EC2HardwareSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
      return EC2ImageSupplier.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return EC2ListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return EC2RebootNodeStrategy.class;
   }

   @Override
   protected Class<? extends Supplier<Location>> defineDefaultLocationSupplier() {
      return org.jclouds.aws.suppliers.DefaultLocationSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Location>>> defineLocationSupplier() {
      return EC2LocationSupplier.class;
   }
}
