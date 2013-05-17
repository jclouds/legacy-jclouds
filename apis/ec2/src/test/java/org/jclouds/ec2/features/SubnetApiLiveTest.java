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
package org.jclouds.ec2.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.ec2.domain.Subnet;
import org.jclouds.ec2.internal.BaseEC2ApiLiveTest;
import org.jclouds.ec2.util.SubnetFilterBuilder;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * tests ability to list/filter subnets
 * 
 * @author Adrian Cole
 * @author Andrew Bayer
 */
@Test(groups = "live")
public class SubnetApiLiveTest extends BaseEC2ApiLiveTest {
    
    private void checkSubnet(Subnet subnet) {
        getAnonymousLogger().info(format("subnet %s vpc: %s", subnet.getSubnetId(), subnet.getVpcId()));
        
        checkNotNull(subnet.getSubnetId(), "Id: Subnet %s", subnet);
        checkNotNull(subnet.getVpcId(), "VPC: Subnet %s", subnet);
        checkNotNull(subnet.getSubnetState(), "SubnetState: Subnet %s", subnet);
        checkNotNull(subnet.getCidrBlock(), "CIDR Block: %s", subnet);
        checkNotNull(subnet.getAvailabilityZone(), "Availability Zone: %s", subnet);
    }
    
    @Test
    public void testListSubnets() {
        ImmutableSet<Subnet> subnets = api().list().toSet();
        getAnonymousLogger().info("subnets: " + subnets.size());
        
        for (Subnet subnet : subnets) {
            checkSubnet(subnet);
            assertEquals(api().filter(new SubnetFilterBuilder().subnetId(subnet.getSubnetId()).build()).get(0), subnet);
      }
    }

    @Test
    public void testFilterWhenNotFound() {
        assertTrue(retry(new Predicate<Iterable<Subnet>>() {
                    public boolean apply(Iterable<Subnet> input) {
                        return api().filter(new SubnetFilterBuilder().subnetId("subnet-pants").build())
                            .toSet().equals(input);
                    }
                }, 600, 200, 200, MILLISECONDS).apply(ImmutableSet.<Subnet> of()));
    }

    private SubnetApi api() {
        Optional<? extends SubnetApi> subnetOption = api.getSubnetApi();
        if (!subnetOption.isPresent())
            throw new SkipException("subnet api not present");
        return subnetOption.get();
    }

}
