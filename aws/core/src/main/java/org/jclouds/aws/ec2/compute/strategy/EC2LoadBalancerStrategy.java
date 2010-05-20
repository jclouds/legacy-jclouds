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

package org.jclouds.aws.ec2.compute.strategy;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.config.EC2ComputeServiceContextModule.GetRegionFromLocation;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.services.ElasticLoadBalancerClient;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.LoadBalancerStrategy;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2LoadBalancerStrategy implements LoadBalancerStrategy
{
    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger          logger = Logger.NULL;
    protected final EC2Client ec2Client;
    protected final GetRegionFromLocation getRegionFromLocation;

    @Inject
    protected EC2LoadBalancerStrategy(EC2Client ec2Client, GetRegionFromLocation getRegionFromLocation)
    {
        this.ec2Client = ec2Client;
        this.getRegionFromLocation = getRegionFromLocation;
    }

    @Override
    public String execute(Location location, String name,
            String protocol, Integer loadBalancerPort, Integer instancePort,
            Set<String> instanceIds)
    {
        String region = getRegionFromLocation.apply(location);
        String dnsName = new String();

        // TODO: Fix temp hack
        String availabilityZone = new String();
        for (String az : AvailabilityZone.zones)
        {
            if (az.startsWith(region))
                availabilityZone = az;
        }

        ElasticLoadBalancerClient elbClient = ec2Client
                .getElasticLoadBalancerServices();

        dnsName = elbClient.createLoadBalancer(region, name, protocol,
                loadBalancerPort, instancePort, availabilityZone);

        Set<String> registeredInstanceIds = elbClient
                .registerInstancesWithLoadBalancer(region, name,
                        (String[]) instanceIds.toArray());

        // deregister instances
        boolean changed = registeredInstanceIds.removeAll(instanceIds);
        if (changed)
            elbClient.deregisterInstancesWithLoadBalancer(region, name,
                    (String[]) registeredInstanceIds.toArray());

        return dnsName;
    }
}