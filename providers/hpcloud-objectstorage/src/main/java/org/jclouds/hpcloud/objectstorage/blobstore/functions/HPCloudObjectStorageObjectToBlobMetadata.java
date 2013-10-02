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
package org.jclouds.hpcloud.objectstorage.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.strategy.IfDirectoryReturnNameStrategy;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;

/**
 * @author Adrian Cole
 */
@Singleton
public class HPCloudObjectStorageObjectToBlobMetadata extends ObjectToBlobMetadata {

   private final PublicUriForObjectInfo publicUriForObjectInfo;

   @Inject
   public HPCloudObjectStorageObjectToBlobMetadata(IfDirectoryReturnNameStrategy ifDirectoryReturnName,
            PublicUriForObjectInfo publicUriForObjectInfo) {
      super(ifDirectoryReturnName);
      this.publicUriForObjectInfo = publicUriForObjectInfo;

   }

   public MutableBlobMetadata apply(ObjectInfo from) {
      if (from == null)
         return null;
      MutableBlobMetadata to = super.apply(from);
      to.setPublicUri(publicUriForObjectInfo.apply(from));

      return to;
   }
}
