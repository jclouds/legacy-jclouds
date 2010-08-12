/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.slicehost.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.domain.Slice;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SliceTerminated implements Predicate<Slice> {

   private final SlicehostClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public SliceTerminated(SlicehostClient client) {
      this.client = client;
   }

   public boolean apply(Slice slice) {
      logger.trace("looking for state on slice %s", checkNotNull(slice, "slice"));
      slice = refresh(slice);
      if (slice == null)
         return true;
      logger.trace("%s: looking for slice state %s: currently: %s", slice.getId(), Slice.Status.TERMINATED, slice
            .getStatus());
      return slice.getStatus() == Slice.Status.TERMINATED;
   }

   private Slice refresh(Slice slice) {
      return client.getSlice(slice.getId());
   }
}
