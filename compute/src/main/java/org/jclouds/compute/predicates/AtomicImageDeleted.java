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
package org.jclouds.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.predicates.internal.TrueIfNullOrDeletedRefreshAndDoubleCheckOnFalse;
import org.jclouds.compute.strategy.GetImageStrategy;

import com.google.inject.Inject;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class AtomicImageDeleted extends TrueIfNullOrDeletedRefreshAndDoubleCheckOnFalse<Image.Status, Image> {

   private final GetImageStrategy client;

   @Inject
   public AtomicImageDeleted(GetImageStrategy client) {
      super(Status.DELETED);
      this.client = checkNotNull(client, "client");
   }
   
   @Override
   protected Image refreshOrNull(Image resource) {
      if (resource == null || resource.getId() == null)
         return null;
      return client.getImage(resource.getId());
   }
}
