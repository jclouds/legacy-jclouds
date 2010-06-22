/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.domain;

/**
 * 
 * The FenceMode element contains one of the following strings that specify how
 * a network is connected to its parent network.
 * 
 * @author Adrian Cole
 */
public interface FenceMode {
   /**
    * The two networks are bridged.
    */
   public static final String BRIDGED = "bridged";
   /**
    * The two networks are not connected.
    */
   public static final String ISOLATED = "isolated";
   /**
    * The two networks are connected as specified in their NatService elements.
    */
   public static final String NAT_ROUTED = "natRouted";
}