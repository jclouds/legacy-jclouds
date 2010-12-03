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

package org.jclouds.aws.s3.blobstore.config;

import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class ScaleUpCloudBlobStoreContextModule extends S3BlobStoreContextModule {

   @SuppressWarnings("rawtypes")
   @Override
   protected void bindBucketLocationStrategy() {
      bind(new TypeLiteral<Function<BucketMetadata, Location>>() {
      }).toInstance((Function)Functions.constant(null));
   }
}
