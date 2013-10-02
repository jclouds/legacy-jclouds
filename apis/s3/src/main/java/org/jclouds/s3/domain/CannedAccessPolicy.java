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
package org.jclouds.s3.domain;

/**
 * Description from Amazon's documentation:
 * 
 * <p />
 * Because of restrictions in what can be sent via http headers, Amazon S3
 * supports the concept of canned access policies for REST. A canned access
 * policy can be included with the x-amz-acl header as part of a PUT operation
 * to provide shorthand representation of a full access policy. When Amazon S3
 * sees the x-amz-acl header as part of a PUT operation, it will assign the
 * respective access policy to the resource created as a result of the PUT. If
 * no x-amz-acl header is included with a PUT request, then the bucket or object
 * is written with the private access control policy (even if, in the case of an
 * object, the object already exists with some other pre-existing access control
 * policy).
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?
 *      RESTAccessPolicy.html" />
 * @author Adrian Cole
 * 
 */
public enum CannedAccessPolicy {

    /**
     * Owner gets FULL_CONTROL. No one else has access rights (default).
     */
    PRIVATE("private"),
    /**
     * Owner gets FULL_CONTROL and the anonymous identity is granted READ
     * access. If this policy is used on an object, it can be read from a
     * browser with no authentication.
     */
    PUBLIC_READ("public-read"),
    /**
     * Owner gets FULL_CONTROL, the anonymous identity is granted READ and
     * WRITE access. This can be a useful policy to apply to a bucket, but is
     * generally not recommended.
     */
    PUBLIC_READ_WRITE("public-read-write"),
    /**
     * Owner gets FULL_CONTROL, and any identity authenticated as a registered
     * Amazon S3 user is granted READ access.
     */
    AUTHENTICATED_READ("authenticated-read");

    private String policyName;

    CannedAccessPolicy(String policyName) {
	this.policyName = policyName;
    }

    @Override
    public String toString() {
	return policyName;
    }
    
    /**
     * @param capHeader
     * The value of the x-amz-acl HTTP Header returned by S3 when an
     * object has a canned access policy.
     * 
     * @return
     * the canned access policy object corresponding to the header value,
     * or null if the given header value does not represent a valid canned 
     * policy.
     */
    public static CannedAccessPolicy fromHeader(String capHeader) {
       if ("private".equals(capHeader)) {
          return CannedAccessPolicy.PRIVATE;
       } else if ("public-read".equals(capHeader)) {
          return CannedAccessPolicy.PUBLIC_READ;
       } else if ("public-read-write".equals(capHeader)) {
          return CannedAccessPolicy.PUBLIC_READ_WRITE;
       } else if ("authenticated-read".equals(capHeader)) {
          return CannedAccessPolicy.AUTHENTICATED_READ;
       } else {
          return null;
       }
    }
}
