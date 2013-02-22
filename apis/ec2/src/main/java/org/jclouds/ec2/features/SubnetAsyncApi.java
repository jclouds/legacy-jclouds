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
package org.jclouds.ec2.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindFiltersToIndexedFormParams;
import org.jclouds.ec2.domain.Subnet;
import org.jclouds.ec2.xml.DescribeSubnetsResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptyFluentIterableOnNotFoundOr404;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon EC2 via the Query API
 * <p/>
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSubnets.html"
 *      >doc</a>
 * @see SubnetApi
 * @author Adrian Cole
 * @author Andrew Bayer
 */
@SinceApiVersion("2011-01-01")
@RequestFilters(FormSigner.class)
@VirtualHost
public interface SubnetAsyncApi {
   /**
    * @see SubnetApi#list()
    * @see <a
    *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSubnets.html">docs</a>
    */
   @Named("DescribeSubnets")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSubnets")
   @XMLResponseParser(DescribeSubnetsResponseHandler.class)
   @ExceptionParser(ReturnEmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<FluentIterable<Subnet>> list();

   /**
    * @see SubnetApi#filter
    * @see <a
    *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSubnets.html">docs</a>
    */
   @Named("DescribeSubnets")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSubnets")
   @XMLResponseParser(DescribeSubnetsResponseHandler.class)
   @ExceptionParser(ReturnEmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<FluentIterable<Subnet>> filter(
         @BinderParam(BindFiltersToIndexedFormParams.class) Multimap<String, String> filter);

}
