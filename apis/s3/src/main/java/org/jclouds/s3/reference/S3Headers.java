/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.s3.reference;

/**
 * Additional headers specified by Amazon S3 REST API.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/latest/index.html?RESTAuthentication.html"
 *      />
 * @author Adrian Cole
 * 
 */
public interface S3Headers {

   /**
    * The canned ACL to apply to the object. Options include private, public-read,
    * public-read-write, and authenticated-read. For more information, see REST Access Control
    * Policy.
    */
   public static final String CANNED_ACL = "x-amz-acl";
   public static final String AMZ_MD5 = "x-amz-meta-object-eTag";

}
