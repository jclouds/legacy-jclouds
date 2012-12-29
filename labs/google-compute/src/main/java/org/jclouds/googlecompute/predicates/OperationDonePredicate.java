/*
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

package org.jclouds.googlecompute.predicates;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import org.jclouds.googlecompute.GoogleComputeApi;
import org.jclouds.googlecompute.config.UserProject;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.features.OperationApi;

import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Tests that an Operation is done, returning the completed Operation when it is.
 *
 * @author David Alves
 */
public class OperationDonePredicate implements Predicate<AtomicReference<Operation>> {

   private OperationApi api;

   @Inject
   OperationDonePredicate(GoogleComputeApi api, @UserProject String project) {
      this.api = api.getOperationApiForProject(project);
   }

   @Override
   public boolean apply(AtomicReference<Operation> input) {
      checkNotNull(input, "input");
      Operation current = api.get(input.get().getName());
      switch (current.getStatus()) {
         case DONE:
            input.set(current);
            return true;
         case PENDING:
         case RUNNING:
         default:
            return false;
      }
   }
}
