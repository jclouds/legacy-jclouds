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
package org.jclouds.openstack.nova.ec2.xml;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Image.ImageType;
import org.jclouds.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.location.Region;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Adjusted to filter out non-MACHINE images
 *
 * @author Adam Lowe
 */
public class NovaDescribeImagesResponseHandler extends DescribeImagesResponseHandler {
   @Inject
   public NovaDescribeImagesResponseHandler(@Region Supplier<String> defaultRegion) {
      super(defaultRegion);
   }

   public Set<Image> getResult() {
      return ImmutableSet.copyOf(Iterables.filter(contents, new Predicate<Image>() {
         @Override
         public boolean apply(Image image) {
            return image.getImageType() == ImageType.MACHINE;
         }
      }));
   }
}
