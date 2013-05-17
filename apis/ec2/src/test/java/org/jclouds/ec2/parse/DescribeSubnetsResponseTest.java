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
package org.jclouds.ec2.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.ec2.domain.Subnet;
import org.jclouds.ec2.xml.DescribeSubnetsResponseHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 * @author Andrew Bayer
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeSubnetsResponseTest")
public class DescribeSubnetsResponseTest extends BaseHandlerTest {
    
    public void test() {
        InputStream is = getClass().getResourceAsStream("/describe_subnets.xml");
        
        FluentIterable<Subnet> expected = expected();
        
        DescribeSubnetsResponseHandler handler = injector.getInstance(DescribeSubnetsResponseHandler.class);
        FluentIterable<Subnet> result = factory.create(handler).parse(is);
        
        assertEquals(result.toString(), expected.toString());
        
    }
    public FluentIterable<Subnet> expected() {
        return FluentIterable.from(ImmutableSet.<Subnet>builder()
                                   .add(Subnet.builder()
                                        .subnetId("subnet-9d4a7b6c")
                                        .subnetState(Subnet.State.AVAILABLE)
                                        .vpcId("vpc-1a2b3c4d")
                                        .cidrBlock("10.0.1.0/24")
                                        .availableIpAddressCount(250)
                                        .availabilityZone("us-east-1a")
                                        .tag("Name", "ec2-o")
                                        .tag("Empty", "")
                                        .build())                
                                   .add(Subnet.builder()
                                        .subnetId("subnet-6e7f829e")
                                        .subnetState(Subnet.State.AVAILABLE)
                                        .vpcId("vpc-1a2b3c4d")
                                        .cidrBlock("10.0.0.0/24")
                                        .availableIpAddressCount(250)
                                        .availabilityZone("us-east-1a")
                                        .build()).build());
    }
}
