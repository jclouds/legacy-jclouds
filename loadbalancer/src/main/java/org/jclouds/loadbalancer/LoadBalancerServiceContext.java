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
package org.jclouds.loadbalancer;

import java.io.Closeable;

import org.jclouds.View;
import org.jclouds.loadbalancer.internal.LoadBalancerServiceContextImpl;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.Utils;

import com.google.inject.ImplementedBy;

/**
 * Represents a cloud that has LoadBalancer functionality.
 * 
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(LoadBalancerServiceContextImpl.class)
public interface LoadBalancerServiceContext extends Closeable, View {

   LoadBalancerService getLoadBalancerService();


   Utils getUtils();

   /**
    * @see #getUtils
    */
   Utils utils();
   
   /**
    * will be removed in jclouds 1.6
    * 
    * @see View#getInputType
    * @see View#unwrap
    */
   @Deprecated
   <S, A> RestContext<S, A> getProviderSpecificContext();

   @Override
   void close();
}
