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
package org.jclouds.smartos.compute.functions;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.smartos.compute.domain.DataSet;

import com.google.common.base.Function;

/**
 * @author Nigel Magnay
 */
@Singleton
public class DataSetToImage implements Function<DataSet, Image> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public Image apply(DataSet from) {

      ImageBuilder builder = new ImageBuilder();
      builder.ids(from.getUuid() + "");
      builder.name(from.getUrn());
      builder.description(from.getUrn());
      builder.status(Image.Status.AVAILABLE);

      OsFamily family;
      try {
         family = OsFamily.SOLARIS;
         builder.operatingSystem(new OperatingSystem.Builder().name(from.getUrn()).description(from.getUrn())
                  .family(family).build());
      } catch (IllegalArgumentException e) {
         logger.debug("<< didn't match os(%s)", from);
      }
      return builder.build();
   }

}
