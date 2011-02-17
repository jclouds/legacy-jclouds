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

package org.jclouds.s3.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.reference.S3Headers;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Contains options supported in the REST API for the COPY object operation.
 * <p/>
 * <h2>Usage</h2> The recommended way to instantiate a CopyObjectOptions object is to statically
 * import CopyObjectOptions.Builder.* and invoke a static creation method followed by an instance
 * mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.s3.commands.options.CopyObjectOptions.Builder.*
 * <p/>
 * S3Client connection = // get connection
 * <p/>
 * Multimap<String,String> metadata = LinkedHashMultimap.create();
 * metadata.put("x-amz-meta-adrian", "foo");
 * <p/>
 * // this will copy the object, provided it wasn't modified since yesterday.
 * // it will not use metadata from the source, and instead use what we pass in.
 * Future<S3Object.Metadata> object = connection.copyObject("sourceBucket", "objectName",
 * "destinationBucket", "destinationName",
 * overrideMetadataWith(meta).
 * ifSourceModifiedSince(new Date().minusDays(1))
 * );
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectCOPY.html?"
 *      />
 */
public class CopyObjectOptions extends BaseHttpRequestOptions {
   private final static DateService dateService = new SimpleDateFormatDateService();
   public static final CopyObjectOptions NONE = new CopyObjectOptions();
   private Map<String, String> metadata;
   private CannedAccessPolicy acl = CannedAccessPolicy.PRIVATE;

   private String metadataPrefix;

   @Inject
   public void setMetadataPrefix(@Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix) {
      this.metadataPrefix = metadataPrefix;
   }

   private String headerTag;

   @Inject
   public void setHeaderTag(@Named(PROPERTY_HEADER_TAG) String headerTag) {
      this.headerTag = headerTag;
   }

   /**
    * Override the default ACL (private) with the specified one.
    * 
    * @see CannedAccessPolicy
    */
   public CopyObjectOptions overrideAcl(CannedAccessPolicy acl) {
      this.acl = checkNotNull(acl, "acl");
      if (!acl.equals(CannedAccessPolicy.PRIVATE))
         this.replaceHeader(S3Headers.CANNED_ACL, acl.toString());
      return this;
   }

   /**
    * @see CopyObjectOptions#overrideAcl(CannedAccessPolicy)
    */
   public CannedAccessPolicy getAcl() {
      return acl;
   }

   /**
    * For use in the header x-amz-copy-source-if-unmodified-since
    * <p/>
    * Copies the object if it hasn't been modified since the specified time; otherwise returns a 412
    * (precondition failed).
    * <p/>
    * This header can be used with x-amz-copy-source-if-match, but cannot be used with other
    * conditional copy headers.
    * 
    * @return valid HTTP date
    * @see <a href="http://rfc.net/rfc2616.html?s3.3"/>
    * @see CopyObjectOptions#ifSourceModifiedSince(Date)
    */
   public String getIfModifiedSince() {
      return getFirstHeaderOrNull("x-amz-copy-source-if-modified-since");
   }

   /**
    * For use in the header x-amz-copy-source-if-modified-since
    * <p/>
    * Copies the object if it has been modified since the specified time; otherwise returns a 412
    * (failed condition).
    * <p/>
    * This header can be used with x-amz-copy-source-if-none-match, but cannot be used with other
    * conditional copy headers.
    * 
    * @return valid HTTP date
    * @see <a href="http://rfc.net/rfc2616.html?s3.3"/>
    * @see CopyObjectOptions#ifSourceUnmodifiedSince(Date)
    */
   public String getIfUnmodifiedSince() {
      return getFirstHeaderOrNull("x-amz-copy-source-if-unmodified-since");
   }

   /**
    * For use in the request header: x-amz-copy-source-if-match
    * <p/>
    * Copies the object if its payload tag (ETag) matches the specified tag; otherwise return a 412
    * (precondition failed).
    * <p/>
    * This header can be used with x-amz-copy-source-if-unmodified-since, but cannot be used with
    * other conditional copy headers.
    * 
    * @see CopyObjectOptions#ifSourceETagMatches(String)
    */
   public String getIfMatch() {
      return getFirstHeaderOrNull("x-amz-copy-source-if-match");
   }

   /**
    * For use in the request header: x-amz-copy-source-if-none-match
    * <p/>
    * Copies the object if its payload tag (ETag) is different than the specified Etag; otherwise
    * returns a 412 (failed condition).
    * <p/>
    * This header can be used with x-amz-copy-source-if-modified-since, but cannot be used with
    * other conditional copy headers.
    * 
    * @see CopyObjectOptions#ifSourceETagDoesntMatch(String)
    */
   public String getIfNoneMatch() {
      return getFirstHeaderOrNull("x-amz-copy-source-if-none-match");
   }

   /**
    * When not null, contains the header [x-amz-copy-source-if-unmodified-since] -> [REPLACE] and
    * metadata headers passed in from the users.
    * 
    * @see #overrideMetadataWith(Multimap)
    */
   public Map<String, String> getMetadata() {
      return metadata;
   }

   /**
    * Only return the object if it has changed since this time.
    * <p/>
    * Not compatible with {@link #ifSourceETagMatches(String)} or
    * {@link #ifSourceUnmodifiedSince(Date)}
    */
   public CopyObjectOptions ifSourceModifiedSince(Date ifModifiedSince) {
      checkState(getIfMatch() == null, "ifETagMatches() is not compatible with ifModifiedSince()");
      checkState(getIfUnmodifiedSince() == null,
               "ifUnmodifiedSince() is not compatible with ifModifiedSince()");
      replaceHeader("x-amz-copy-source-if-modified-since", dateService
               .rfc822DateFormat(checkNotNull(ifModifiedSince, "ifModifiedSince")));
      return this;
   }

   /**
    * Only return the object if it hasn't changed since this time.
    * <p/>
    * Not compatible with {@link #ifSourceETagDoesntMatch(String)} or
    * {@link #ifSourceModifiedSince(Date)}
    */
   public CopyObjectOptions ifSourceUnmodifiedSince(Date ifUnmodifiedSince) {
      checkState(getIfNoneMatch() == null,
               "ifETagDoesntMatch() is not compatible with ifUnmodifiedSince()");
      checkState(getIfModifiedSince() == null,
               "ifModifiedSince() is not compatible with ifUnmodifiedSince()");
      replaceHeader("x-amz-copy-source-if-unmodified-since", dateService
               .rfc822DateFormat(checkNotNull(ifUnmodifiedSince, "ifUnmodifiedSince")));
      return this;
   }

   /**
    * The object's eTag hash should match the parameter <code>eTag</code>.
    * <p/>
    * <p/>
    * Not compatible with {@link #ifSourceETagDoesntMatch(String)} or
    * {@link #ifSourceModifiedSince(Date)}
    * 
    * @param eTag
    *           hash representing the payload
    */
   public CopyObjectOptions ifSourceETagMatches(String eTag) throws UnsupportedEncodingException {
      checkState(getIfNoneMatch() == null,
               "ifETagDoesntMatch() is not compatible with ifETagMatches()");
      checkState(getIfModifiedSince() == null,
               "ifModifiedSince() is not compatible with ifETagMatches()");
      replaceHeader("x-amz-copy-source-if-match", String.format("\"%1$s\"", checkNotNull(eTag,
               "eTag")));
      return this;
   }

   /**
    * The object should not have a eTag hash corresponding with the parameter <code>eTag</code>.
    * <p/>
    * Not compatible with {@link #ifSourceETagMatches(String)} or
    * {@link #ifSourceUnmodifiedSince(Date)}
    * 
    * @param eTag
    *           hash representing the payload
    * @throws UnsupportedEncodingException
    *            if there was a problem converting this into an S3 eTag string
    */
   public CopyObjectOptions ifSourceETagDoesntMatch(String eTag)
            throws UnsupportedEncodingException {
      checkState(getIfMatch() == null, "ifETagMatches() is not compatible with ifETagDoesntMatch()");
      Preconditions.checkState(getIfUnmodifiedSince() == null,
               "ifUnmodifiedSince() is not compatible with ifETagDoesntMatch()");
      replaceHeader("x-amz-copy-source-if-none-match", String.format("\"%1$s\"", checkNotNull(eTag,
               "ifETagDoesntMatch")));
      return this;
   }

   @Override
   public Multimap<String, String> buildRequestHeaders() {
      checkState(headerTag != null, "headerTag should have been injected!");
      checkState(metadataPrefix != null, "metadataPrefix should have been injected!");
      Multimap<String, String> returnVal = LinkedHashMultimap.create();
      for (Entry<String, String> entry : headers.entries()) {
         returnVal.put(entry.getKey().replace("amz", headerTag), entry.getValue());
      }
      if (metadata != null) {
         for (String key : metadata.keySet()) {
            returnVal.put(key.startsWith(metadataPrefix) ? key : metadataPrefix + key, metadata
                     .get(key));
         }
         returnVal.put("x-" + headerTag + "-metadata-directive", "REPLACE");
      }
      return returnVal;
   }

   /**
    * Use the provided metadata instead of what is on the source object.
    */
   public CopyObjectOptions overrideMetadataWith(Map<String, String> metadata) {
      checkNotNull(metadata, "metadata");
      this.metadata = metadata;
      return this;
   }

   public static class Builder {
      /**
       * @see CopyObjectOptions#overrideAcl(CannedAccessPolicy)
       */
      public static CopyObjectOptions overrideAcl(CannedAccessPolicy acl) {
         CopyObjectOptions options = new CopyObjectOptions();
         return options.overrideAcl(acl);
      }

      /**
       * @see CopyObjectOptions#getIfModifiedSince()
       */
      public static CopyObjectOptions ifSourceModifiedSince(Date ifModifiedSince) {
         CopyObjectOptions options = new CopyObjectOptions();
         return options.ifSourceModifiedSince(ifModifiedSince);
      }

      /**
       * @see CopyObjectOptions#ifSourceUnmodifiedSince(Date)
       */
      public static CopyObjectOptions ifSourceUnmodifiedSince(Date ifUnmodifiedSince) {
         CopyObjectOptions options = new CopyObjectOptions();
         return options.ifSourceUnmodifiedSince(ifUnmodifiedSince);
      }

      /**
       * @see CopyObjectOptions#ifSourceETagMatches(String)
       */
      public static CopyObjectOptions ifSourceETagMatches(String eTag)
               throws UnsupportedEncodingException {
         CopyObjectOptions options = new CopyObjectOptions();
         return options.ifSourceETagMatches(eTag);
      }

      /**
       * @see CopyObjectOptions#ifSourceETagDoesntMatch(String)
       */
      public static CopyObjectOptions ifSourceETagDoesntMatch(String eTag)
               throws UnsupportedEncodingException {
         CopyObjectOptions options = new CopyObjectOptions();
         return options.ifSourceETagDoesntMatch(eTag);
      }

      /**
       * @see #overrideMetadataWith(Multimap)
       */
      public static CopyObjectOptions overrideMetadataWith(Map<String, String> metadata) {
         CopyObjectOptions options = new CopyObjectOptions();
         return options.overrideMetadataWith(metadata);
      }
   }
}
