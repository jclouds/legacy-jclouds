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
package org.jclouds.softlayer;

import java.io.Closeable;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.softlayer.features.AccountAsyncClient;
import org.jclouds.softlayer.features.DatacenterAsyncClient;
import org.jclouds.softlayer.features.ProductPackageAsyncClient;
import org.jclouds.softlayer.features.VirtualGuestAsyncClient;

/**
 * Provides asynchronous access to SoftLayer via their REST API.
 * <p/>
 * 
 * @see SoftLayerClient
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(SoftLayerClient.class)} as
 *             {@link SoftLayerAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface SoftLayerAsyncClient extends Closeable {

   /**
    * Provides asynchronous access to VirtualGuest features.
    */
   @Delegate
   VirtualGuestAsyncClient getVirtualGuestClient();

   /**
    * Provides asynchronous access to Datacenter features.
    */
   @Delegate
   DatacenterAsyncClient getDatacenterClient();
   
   /**
    * Provides asynchronous access to ProductPackage features.
    */
   @Delegate
   ProductPackageAsyncClient getProductPackageClient();

   /**
    * Provides asynchronous access to Account features.
    */
   @Delegate
   AccountAsyncClient getAccountClient();
}
