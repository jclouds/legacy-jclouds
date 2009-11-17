/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.predicates;

import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if an address is reachable.
 * 
 * @author Adrian Cole
 */
@Singleton
public class AddressReachable implements Predicate<InetAddress> {

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named("org.jclouds.address_timeout")
   private int timeout = 2000;

   @Override
   public boolean apply(InetAddress address) {
      try {
         logger.trace("testing address %s", address);
         return address.isReachable(timeout);
      } catch (IOException e) {
         return false;
      }
   }

}
