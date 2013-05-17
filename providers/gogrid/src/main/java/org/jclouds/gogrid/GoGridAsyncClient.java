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
package org.jclouds.gogrid;

import java.io.Closeable;

import org.jclouds.gogrid.services.GridImageAsyncClient;
import org.jclouds.gogrid.services.GridIpAsyncClient;
import org.jclouds.gogrid.services.GridJobAsyncClient;
import org.jclouds.gogrid.services.GridLoadBalancerAsyncClient;
import org.jclouds.gogrid.services.GridServerAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * @author Oleksiy Yarmula
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(GoGridClient.class)} as
 *             {@link GoGridAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface GoGridAsyncClient extends Closeable {

   /**
    * @see GoGridClient#getServerServices()
    */
   @Delegate
   GridServerAsyncClient getServerServices();

   /**
    * @see GoGridClient#getJobServices()
    */
   @Delegate
   GridJobAsyncClient getJobServices();

   /**
    * @see GoGridClient#getIpServices()
    */
   @Delegate
   GridIpAsyncClient getIpServices();

   /**
    * @see GoGridClient#getLoadBalancerServices()
    */
   @Delegate
   GridLoadBalancerAsyncClient getLoadBalancerServices();

   /**
    * @see GoGridClient#getImageServices()
    */
   @Delegate
   GridImageAsyncClient getImageServices();

}
