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

package org.jclouds.ec2.compute.strategy;

import javax.inject.Singleton;

import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 */
@ImplementedBy(ReviseParsedImage.NoopReviseParsedImage.class)
public interface ReviseParsedImage {
   void reviseParsedImage(org.jclouds.ec2.domain.Image from, ImageBuilder builder, OsFamily family,
            OperatingSystem.Builder osBuilder);

   @Singleton
   public static class NoopReviseParsedImage implements ReviseParsedImage {

      @Override
      public void reviseParsedImage(org.jclouds.ec2.domain.Image from, ImageBuilder builder, OsFamily family,
               OperatingSystem.Builder osBuilder) {
      }
   }
}
