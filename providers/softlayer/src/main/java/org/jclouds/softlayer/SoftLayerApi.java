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
import org.jclouds.softlayer.features.AccountApi;
import org.jclouds.softlayer.features.DatacenterApi;
import org.jclouds.softlayer.features.ProductPackageApi;
import org.jclouds.softlayer.features.VirtualGuestApi;

/**
 * Provides synchronous access to SoftLayer.
 * <p/>
 * 
 * @see <a href="http://sldn.softlayer.com/article/REST" />
 * @author Adrian Cole
 */
public interface SoftLayerApi extends Closeable {

   /**
    * Provides synchronous access to VirtualGuest features.
    */
   @Delegate
   VirtualGuestApi getVirtualGuestApi();

   /**
    * Provides synchronous access to Datacenter features.
    */
   @Delegate
   DatacenterApi getDatacenterApi();

   /**
    * Provides synchronous access to ProductPackage features.
    */
   @Delegate
   ProductPackageApi getProductPackageApi();

   /**
    * Provides synchronous access to Account features.
    */
   @Delegate
   AccountApi getAccountApi();
}
