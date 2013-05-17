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
package org.jclouds.openstack.swift.suppliers;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jclouds.openstack.swift.extensions.TemporaryUrlKeyApi;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class ReturnOrFetchTemporaryUrlKey implements Supplier<String> {

   private TemporaryUrlKeyApi client;

   @Inject
   public ReturnOrFetchTemporaryUrlKey(TemporaryUrlKeyApi client) {
      this.client = checkNotNull(client, "client");
   }

   @Override
   public String get() {
      String key = client.getTemporaryUrlKey();
      if (key == null) {
         client.setTemporaryUrlKey(UUID.randomUUID().toString());
         return client.getTemporaryUrlKey();
      }
      return key;
   }
}
