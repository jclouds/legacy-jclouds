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

import static org.jclouds.aws.reference.FormParameters.ACTION;

import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindFiltersToIndexedFormParams;
import org.jclouds.ec2.domain.Subnet;
import org.jclouds.ec2.xml.DescribeSubnetsResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
/**
 * Provides access to Amazon EC2 via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSubnets.html"
 *      >doc</a>
 * @author Adrian Cole
 * @author Andrew Bayer
 */
@SinceApiVersion("2011-01-01")
@RequestFilters(FormSigner.class)
@VirtualHost
public interface SubnetApi {
   /**
    * Describes all of your subnets for your EC2 resources.
    * 
    * @return subnets or empty if there are none
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSubnets.html"
    *      >docs</href>
    */
   @Named("DescribeSubnets")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSubnets")
   @XMLResponseParser(DescribeSubnetsResponseHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
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
   @Named("DescribeSubnets")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSubnets")
   @XMLResponseParser(DescribeSubnetsResponseHandler.class)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Subnet> filter(
         @BinderParam(BindFiltersToIndexedFormParams.class) Multimap<String, String> filter);

}
