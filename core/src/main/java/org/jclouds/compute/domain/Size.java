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
package org.jclouds.compute.domain;

import java.util.Map;

/**
 * Configured operating system used to start nodes.
 * 
 * @author Adrian Cole
 */
public interface Size {
   /**
    * provider-specific identifier (m1.small, etc)
    * 
    */
   String getId();

   /**
    * Name provided by the provider (Small CPU, etc)
    * 
    */
   String getName();

   /**
    * Amount of virtual or physical cores provided
    */
   Integer getCores();

   /**
    * Amount of RAM provided in MB (256M, 1740)
    */
   Long getRam();

   /**
    * Amount of boot disk provided in GB (200)
    */
   Long getDisk();

   /**
    * Amount of total transfer bandwidth in GB
    */
   Long getBandwidth();

   /**
    * 
    * Hourly price of this server in USD, estimated if monthly.
    * 
    * @return
    */
   Float getPrice();

   /**
    * Other variables present that the provider supports
    */
   Map<String, String> getExtra();

}