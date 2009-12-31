/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.s3.domain;

import java.util.Date;
import java.util.Map;

/**
 * /** Amazon S3 is designed to store objects. Objects are stored in {@link S3BucketListing buckets}
 * and consist of a {@link org.jclouds.aws.s3.domain.S3Object#getData() value}, a
 * {@link S3Object#getKey key}, {@link ObjectMetadata#getUserMetadata() metadata}, and an access
 * control policy.
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?UsingObjects.html"
 * 
 * @see <a href= "http://docs.amazonwebservices.com/AmazonS3/2006-03-01/UsingMetadata.html" />
 */
public interface ObjectMetadata extends Comparable<ObjectMetadata> {

   public enum StorageClass {
      STANDARD
   }

   /**
    * The key is the handle that you assign to an object that allows you retrieve it later. A key is
    * a sequence of Unicode characters whose UTF-8 encoding is at most 1024 bytes long. Each object
    * in a bucket must have a unique key.
    * 
    * @see <a href= "http://docs.amazonwebservices.com/AmazonHTTP/2006-03-01/UsingKeys.html" />
    */
   String getKey();

   /**
    * Every bucket and object in Amazon S3 has an owner, the user that created the bucket or object.
    * The owner of a bucket or object cannot be changed. However, if the object is overwritten by
    * another user (deleted and rewritten), the new object will have a new owner.
    */
   CanonicalUser getOwner();

   /**
    * Currently defaults to 'STANDARD' and not used.
    */
   StorageClass getStorageClass();

   /**
    * Can be used to specify caching behavior along the request/reply chain.
    * 
    * @link http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.9.
    */
   String getCacheControl();

   /**
    * Specifies presentational information for the object.
    * 
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html?sec19.5.1."/>
    */
   String getContentDisposition();

   /**
    * Specifies what content encodings have been applied to the object and thus what decoding
    * mechanisms must be applied in order to obtain the media-type referenced by the Content-Type
    * header field.
    * 
    * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.11" />
    */
   String getContentEncoding();

   /**
    * 
    * A standard MIME type describing the format of the contents. If none is provided, the default
    * is binary/octet-stream.
    * 
    * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17"/>
    */
   String getContentType();

   /**
    * The base64 decoded 128-bit MD5 digest of the message (without the headers) according to RFC
    * 1864. This header can be used as a message integrity check to verify that the data is the same
    * data that was originally sent. Although it is optional, we recommend using the Content-MD5
    * mechanism as an end-to-end integrity check.
    * 
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/RESTAuthentication.html"/>
    */
   byte[] getContentMD5();

   Date getLastModified();

   String getETag();

   Long getSize();

   Map<String, String> getUserMetadata();

}