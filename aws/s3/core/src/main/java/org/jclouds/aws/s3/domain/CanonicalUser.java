/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.s3.domain;

/**
 * Every bucket and object in Amazon S3 has an owner, the user that created the
 * bucket or object. The owner of a bucket or object cannot be changed. However,
 * if the object is overwritten by another user (deleted and rewritten), the new
 * object will have a new owner.
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?
 *      RESTAccessPolicy.html" />
 * @author Adrian Cole
 */
public class CanonicalUser {
    private final String id;
    private String displayName;

    public CanonicalUser(String id) {
	this.id = id;
    }

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	sb.append("S3Owner");
	sb.append("{id='").append(id).append('\'');
	sb.append(", displayName='").append(displayName).append('\'');
	sb.append('}');
	return sb.toString();
    }

    /**
     * To locate the CanonicalUser ID for a user, the user must perform the
     * {@link org.jclouds.aws.s3.S3Connection#listBucket(String)} and retrieve
     * {@link S3Bucket.Metadata#getOwner()}
     */
    public String getId() {
	return id;
    }

    /**
     * read-only as is maintained by Amazon.
     */
    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (!(o instanceof CanonicalUser))
	    return false;

	CanonicalUser s3Owner = (CanonicalUser) o;

	if (displayName != null ? !displayName.equals(s3Owner.displayName)
		: s3Owner.displayName != null)
	    return false;
	if (!id.equals(s3Owner.id))
	    return false;

	return true;
    }

    @Override
    public int hashCode() {
	int result = id.hashCode();
	result = 31 * result
		+ (displayName != null ? displayName.hashCode() : 0);
	return result;
    }
}
