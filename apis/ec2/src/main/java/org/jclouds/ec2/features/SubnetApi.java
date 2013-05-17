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

import org.jclouds.ec2.domain.Subnet;
import org.jclouds.ec2.util.SubnetFilterBuilder;
import org.jclouds.rest.annotations.SinceApiVersion;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;

/**
 * To help you manage your Amazon EC2 instances, images, and other Amazon EC2 resources, you can assign your own
 * metadata to each resource in the form of tags.
 * 
 * @see <a href="http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Subnets.html" >doc</a>
 * @see SubnetAsyncApi
 * @author Adrian Cole
 * @author Andrew Bayer
 */
@SinceApiVersion("2011-01-01")
public interface SubnetApi {

   /**
    * Describes all of your subnets for your EC2 resources.
    * 
    * @return subnets or empty if there are none
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSubnets.html"
    *      >docs</href>
    */
   FluentIterable<Subnet> list();

   /**
    * Describes subnets for your EC2 resources qualified by a filter
    * 
    * <h4>example</h4>
    * 
    * <pre>
    * subnets = subnetApi.filter(new SubnetFilterBuilder().vpcId(&quot;vpc-1a2b3c4d&quot;).build());
    * </pre>
    * 
    * @param filter
    *           which is typically built by {@link SubnetFilterBuilder}
    * @return tags or empty if there are none that match the filter
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSubnets.html"
    *      >docs</href>
    */
   FluentIterable<Subnet> filter(Multimap<String, String> filter);

}
