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
package org.jclouds.vcloud.director.v1_5.functions;

import org.jclouds.dmtf.ovf.SectionType;
import org.jclouds.vcloud.director.v1_5.domain.AbstractVAppType;

import com.google.common.base.Function;

/**
 * @author danikov
 */
public class SectionForVApp<S extends SectionType> implements Function<AbstractVAppType, S> {
   
   private final Class<? extends SectionType> sectionType;

   public SectionForVApp(Class<S> sectionType) {
      this.sectionType = sectionType;
   }

   @SuppressWarnings("unchecked")
   @Override
   public S apply(AbstractVAppType from) {
      for (SectionType section : from.getSections()) {
         if (sectionType.isAssignableFrom(section.getClass())) {
            return (S)section;
         }
      }
      return null;
   }
}