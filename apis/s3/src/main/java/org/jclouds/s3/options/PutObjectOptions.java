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
package org.jclouds.s3.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.s3.reference.S3Headers.CANNED_ACL;
import static org.jclouds.s3.reference.S3Headers.DEFAULT_AMAZON_HEADERTAG;

import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.s3.domain.CannedAccessPolicy;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Contains options supported in the REST API for the PUT object operation.
 * <p/>
 * <h2>
 * Usage</h2> The recommended way to instantiate a PutObjectOptions object is to statically import
 * PutObjectOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.s3.commands.options.PutObjectOptions.Builder.*
 * import org.jclouds.s3.S3Client;
 * 
 * S3Client connection = // get connection
 * Future<Boolean> publicly readable = connection.putObject("bucketName",new S3Object("key","value"), withAcl(CannedAccessPolicy.PUBLIC_READ));
 * <code>
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectPUT.html?"
 *      />
 * 
 * @author Adrian Cole
 * 
 */
public class PutObjectOptions extends BaseHttpRequestOptions {
   public static final PutObjectOptions NONE = new PutObjectOptions();

   private CannedAccessPolicy acl = CannedAccessPolicy.PRIVATE;

   private String headerTag;

   @Inject
   public void setHeaderTag(@Named(PROPERTY_HEADER_TAG) String headerTag) {
      this.headerTag = headerTag;
   }

   @Override
   public Multimap<String, String> buildRequestHeaders() {
      checkState(headerTag != null, "headerTag should have been injected!");
      ImmutableMultimap.Builder<String, String> returnVal = ImmutableMultimap.builder();
      for (Entry<String, String> entry : headers.entries()) {
         returnVal.put(entry.getKey().replace(DEFAULT_AMAZON_HEADERTAG, headerTag), entry.getValue());
      }
      return returnVal.build();
   }

   /**
    * Override the default ACL (private) with the specified one.
    * 
    * @see CannedAccessPolicy
    */
   public PutObjectOptions withAcl(CannedAccessPolicy acl) {
      this.acl = checkNotNull(acl, "acl");
      if (!acl.equals(CannedAccessPolicy.PRIVATE))
         this.replaceHeader(CANNED_ACL, acl.toString());
      return this;
   }

   /**
    * @see PutObjectOptions#withAcl(CannedAccessPolicy)
    */
   public CannedAccessPolicy getAcl() {
      return acl;
   }

   public static class Builder {

      /**
       * @see PutObjectOptions#withAcl(CannedAccessPolicy)
       */
      public static PutObjectOptions withAcl(CannedAccessPolicy acl) {
         PutObjectOptions options = new PutObjectOptions();
         return options.withAcl(acl);
      }
   }
}
