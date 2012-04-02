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
package org.jclouds.apis;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * The Apis class provides static methods for accessing apis.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class Apis {

   /**
    * Returns the apis located on the classpath via
    * {@link java.util.ServiceLoader}.
    * 
    * @return all available apis loaded from classpath via ServiceLoader
    */
   private static Iterable<ApiMetadata> fromServiceLoader() {
      return ServiceLoader.load(ApiMetadata.class);
   }

   /**
    * Returns all available apis.
    * 
    * @return all available apis
    */
   public static Iterable<ApiMetadata> all() {
      return fromServiceLoader();
   }

   /**
    * Returns the first api with the provided id
    * 
    * @param id
    *           the id of the api to return
    * 
    * @return the api with the given id
    * 
    * @throws NoSuchElementException
    *            whenever there are no apis with the provided id
    */
   public static ApiMetadata withId(String id) throws NoSuchElementException {
      return find(all(), ApiPredicates.id(id));
   }

   /**
    * Returns the apis that are of type
    * {@link org.jclouds.apis.ApiMetadata#BLOBSTORE}.
    * 
    * @return the blobstore apis
    */
   public static Iterable<ApiMetadata> allBlobStore() {
      return filter(all(), ApiPredicates.type(ApiType.BLOBSTORE));
   }

   /**
    * Returns the apis that are of type
    * {@link org.jclouds.apis.ApiMetadata#COMPUTE}.
    * 
    * @return the compute service apis
    */
   public static Iterable<ApiMetadata> allCompute() {
      return filter(all(), ApiPredicates.type(ApiType.COMPUTE));
   }

   /**
    * Returns the apis that are of type
    * {@link org.jclouds.apis.ApiMetadata#QUEUE}.
    * 
    * @return the queue service apis
    */
   public static Iterable<ApiMetadata> allQueue() {
      return filter(all(), ApiPredicates.type(ApiType.QUEUE));
   }

   /**
    * Returns the apis that are of type
    * {@link org.jclouds.apis.ApiMetadata#TABLE}.
    * 
    * @return the table service apis
    */
   public static Iterable<ApiMetadata> allTable() {
      return filter(all(), ApiPredicates.type(ApiType.TABLE));
   }

   /**
    * Returns the apis that are of type
    * {@link org.jclouds.apis.ApiMetadata#LOADBALANCER}.
    * 
    * @return the load balancer service apis
    */
   public static Iterable<ApiMetadata> allLoadBalancer() {
      return filter(all(), ApiPredicates.type(ApiType.LOADBALANCER));
   }

   /**
    * Returns the apis that are of type
    * {@link org.jclouds.apis.ApiMetadata#MONITOR}.
    * 
    * @return the load balancer service apis
    */
   public static Iterable<ApiMetadata> allMonitor() {
      return filter(all(), ApiPredicates.type(ApiType.MONITOR));
   }

   /**
    * Returns the apis that are of the provided type.
    * 
    * @param type
    *           the type to apis to return
    * 
    * @return the apis of the provided type
    */
   public static Iterable<ApiMetadata> ofType(ApiType type) {
      return filter(all(), ApiPredicates.type(type));
   }
}
