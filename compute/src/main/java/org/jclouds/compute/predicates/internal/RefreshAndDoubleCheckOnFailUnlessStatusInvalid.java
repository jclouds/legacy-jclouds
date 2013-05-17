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

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ComputeMetadataIncludingStatus;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * Keep an atomic reference to a
 * resource, so as to eliminate a redundant {@link ComputeService#getNodeMetadata} call after the
 * predicate passes.
 * 
 * @author Adrian Cole
 */
@Singleton
public abstract class RefreshAndDoubleCheckOnFailUnlessStatusInvalid<S extends Enum<S>, C extends ComputeMetadataIncludingStatus<S>> implements Predicate<AtomicReference<C>> {

   private final S intended;
   private final Set<S> invalids;
   @Resource
   protected Logger logger = Logger.NULL;

   public RefreshAndDoubleCheckOnFailUnlessStatusInvalid(S intended, Set<S> invalids) {
      this.intended = checkNotNull(intended, "intended");
      this.invalids = ImmutableSet.copyOf(checkNotNull(invalids, "invalids"));
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
         return false;
      logger.trace("%s: looking for resource state %s: currently: %s", resource.getId(), intended, formatStatus(resource));
      if (invalids.contains(resource.getStatus()))
         throw new IllegalStateException("resource " + resource.getId() + " in location " + resource.getLocation()
                  + " is in invalid status " + formatStatus(resource));
      return resource.getStatus() == intended;
   }

   protected abstract C refreshOrNull(C resource);
}
