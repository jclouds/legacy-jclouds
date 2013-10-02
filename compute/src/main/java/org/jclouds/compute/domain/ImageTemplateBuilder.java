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
package org.jclouds.compute.domain;

import org.jclouds.compute.domain.internal.ImageTemplateImpl;

/**
 * A builder for {@link ImageTemplate}s. Includes sub-builders to build specific
 * {@link ImageTemplate}s for different purposes, such as cloning, creating from iso, creating from
 * netboot.
 * 
 * @author David Alves
 * 
 */
public abstract class ImageTemplateBuilder {

   String name;

   private ImageTemplateBuilder() {
   }

   public ImageTemplateBuilder name(String name) {
      this.name = name;
      return this;
   }

   public static class CloneImageTemplateBuilder extends ImageTemplateBuilder {

      String nodeId;

      @Override
      public CloneImageTemplateBuilder name(String name) {
         return CloneImageTemplateBuilder.class.cast(super.name(name));
      }

      public CloneImageTemplateBuilder nodeId(String nodeId) {
         this.nodeId = nodeId;
         return this;
      }

      public CloneImageTemplate build() {
         return new ImageTemplateImpl.CloneImageTemplateImpl(name, nodeId);
      }
   }
}
