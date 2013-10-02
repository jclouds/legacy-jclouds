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
package org.jclouds.compute.predicates.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.formatStatus;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;

import org.jclouds.compute.domain.ComputeMetadataIncludingStatus;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

/**
 * 
 * 
 * @author Adrian Cole
 */
public abstract class TrueIfNullOrDeletedRefreshAndDoubleCheckOnFalse<S extends Enum<S>, C extends ComputeMetadataIncludingStatus<S>>
         implements Predicate<AtomicReference<C>> {
   protected final S deletedStatus;

   @Resource
   protected Logger logger = Logger.NULL;

   protected TrueIfNullOrDeletedRefreshAndDoubleCheckOnFalse(S deletedStatus) {
      this.deletedStatus = checkNotNull(deletedStatus, "deletedStatus");
   }

   public boolean apply(AtomicReference<C> atomicResource) {
      C resource = atomicResource.get();
      if (checkStatus(resource))
         return true;
      resource = refreshOrNull(resource);
      atomicResource.set(resource);
      return checkStatus(resource);
   }

   public boolean checkStatus(C resource) {
      if (resource == null)
         return true;
      logger.trace("%s: looking for resource status %s: currently: %s", resource.getId(), deletedStatus, formatStatus(resource));
      return resource.getStatus() == deletedStatus;
   }

   protected abstract C refreshOrNull(C resource);

}
