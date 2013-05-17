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
package org.jclouds.atmos.blobstore.functions;

import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.blobstore.domain.BlobMetadata;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobMetadataToObject implements Function<BlobMetadata, AtmosObject> {
   private final AtmosObject.Factory factory;
   private final BlobToContentMetadata blob2ContentMd;
   private final BlobToSystemMetadata blob2SysMd;

   @Inject
   protected BlobMetadataToObject(AtmosObject.Factory factory,
            BlobToContentMetadata blob2ContentMd, BlobToSystemMetadata blob2SysMd) {
      this.factory = factory;
      this.blob2ContentMd = blob2ContentMd;
      this.blob2SysMd = blob2SysMd;
   }

   public AtmosObject apply(BlobMetadata from) {
      if (from == null)
         return null;
      UserMetadata userMd = new UserMetadata();
      if (from.getUserMetadata() != null) {
         for (Entry<String, String> entry : from.getUserMetadata().entrySet())
            userMd.getMetadata().put(entry.getKey().toLowerCase(), entry.getValue());
      }
      return factory.create(blob2ContentMd.apply(from), blob2SysMd.apply(from), userMd);
   }

}
