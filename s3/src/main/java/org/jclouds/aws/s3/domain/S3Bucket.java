/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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

import org.joda.time.DateTime;
import org.jclouds.aws.s3.domain.S3Owner;
import org.jclouds.aws.s3.domain.S3Object;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * A container that provides namespace, access control and aggregation of
 * {@link S3Object}s
 * 
 * @see http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html
 * @author Adrian Cole
 */
public class S3Bucket {

    public static class MetaData {
	public static enum LocationConstraint {
	    EU
	}

	private final String name;
	private DateTime creationDate;
	private S3Owner canonicalUser;
	private LocationConstraint locationConstraint;

	public MetaData(String name) {
	    this.name = checkNotNull(name, "name").toLowerCase();
	}

	public String getName() {
	    return name;
	}

	public DateTime getCreationDate() {
	    return creationDate;
	}

	public void setCreationDate(DateTime creationDate) {
	    this.creationDate = creationDate;
	}

	public S3Owner getCanonicalUser() {
	    return canonicalUser;
	}

	public void setCanonicalUser(S3Owner canonicalUser) {
	    this.canonicalUser = canonicalUser;
	}

	public LocationConstraint getLocationConstraint() {
	    return locationConstraint;
	}

	public void setLocationConstraint(LocationConstraint locationConstraint) {
	    this.locationConstraint = locationConstraint;
	}
    }

    public static final S3Bucket NOT_FOUND = new S3Bucket("NOT_FOUND");

    private Set<S3Object.MetaData> objects = new HashSet<S3Object.MetaData>();
    private final MetaData metaData;

    private boolean isComplete;

    public S3Bucket(String name) {
	this.metaData = new MetaData(name);
    }

    public String getName() {
	return this.metaData.getName();
    }

    public S3Bucket(MetaData metaData) {
	this.metaData = checkNotNull(metaData, "metaData");
    }

    public Set<S3Object.MetaData> getContents() {
	return objects;
    }

    public void setContents(Set<S3Object.MetaData> objects) {
	this.objects = objects;
    }

    public boolean isComplete() {
	return isComplete;
    }

    public void setComplete(boolean complete) {
	isComplete = complete;
    }

    public MetaData getMetaData() {
	return metaData;
    }

}
