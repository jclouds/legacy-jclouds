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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.predicates.internal.RefreshImageAndDoubleCheckOnFailUnlessStatusInvalid;
import org.jclouds.compute.strategy.GetImageStrategy;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * Tests to see if a image is available.
 * 
 * @author Adrian Cole
 */
@Singleton
public class AtomicImageAvailable extends RefreshImageAndDoubleCheckOnFailUnlessStatusInvalid {

   @Inject
   public AtomicImageAvailable(GetImageStrategy client) {
      super(Status.AVAILABLE, ImmutableSet.of(Status.ERROR, Status.DELETED), client);
   }
}
