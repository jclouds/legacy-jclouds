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

package org.jclouds.tools.ebsresize.facade;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.predicates.InstanceStateRunning;
import org.jclouds.aws.ec2.predicates.InstanceStateStopped;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.predicates.RetryablePredicate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Aggregates several methods of jClouds' EC2 functionality
 *  to work with instance client (instance store). These
 *  features are specific to instances with EBS root device.
 *
 * @author Oleksiy Yarmula
 */
public class InstanceFacade {

    final private InstanceClient instanceServices;
    final private Predicate<RunningInstance> instanceRunning;
    final private Predicate<RunningInstance> instanceStopped;

    public InstanceFacade(InstanceClient instanceServices) {
        this.instanceServices = instanceServices;
        this.instanceRunning =
                new RetryablePredicate<RunningInstance>(new InstanceStateRunning(instanceServices),
                                      600, 10, TimeUnit.SECONDS);
        this.instanceStopped =
                new RetryablePredicate<RunningInstance>(new InstanceStateStopped(instanceServices),
                        600, 10, TimeUnit.SECONDS);
    }

    /**
     * Starts an instance, given that it has an EBS volume attached.
     * This command is only available for EC2 instances with
     *  EBS root device.
     *
     * This method blocks until the operations are fully completed.
     *
     * @param instance instance to start
     * @see #stopInstance(org.jclouds.aws.ec2.domain.RunningInstance)
     */
    public void startInstance(RunningInstance instance) {
        instanceServices.startInstancesInRegion(instance.getRegion(), instance.getId());
        checkState(instanceRunning.apply(instance),
                /*or throw*/ "Couldn't start the instance");
    }

     /**
     * Stops an instance.
     * This command is only available for EC2 instances with
     *  EBS root device.
     *
     * This method blocks until the operations are fully completed.
     *
     * @param instance instance to stop
     * @see #startInstance(org.jclouds.aws.ec2.domain.RunningInstance)
     */
    public void stopInstance(RunningInstance instance) {
        instanceServices.stopInstancesInRegion(instance.getRegion(), false, instance.getId());
        checkState(instanceStopped.apply(instance),
                            /*or throw*/ "Couldn't stop the instance");
    }

    /**
     * Given an instance id and its {@link Region}, returns a {@link RunningInstance}.
     * 
     * @param instanceId id of instance
     * @param region region of the instance
     * @return instance, corresponding to instanceId and region
     */
    public RunningInstance getInstanceByIdAndRegion(String instanceId, Region region) {
        Set<Reservation> reservations = instanceServices.describeInstancesInRegion(region, instanceId);
        Reservation reservation = checkNotNull(Iterables.getOnlyElement(reservations));
        return Iterables.getOnlyElement(reservation);
    }
    
}
