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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

/**
 * A container that provides namespace, access control and aggregation of
 * {@link S3Object}s
 * 
 * @author Adrian Cole
 * @see http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html
 */
public class S3Bucket {
    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	sb.append("S3Bucket");
	sb.append("{metaData=").append(metaData);
	sb.append(", isComplete=").append(isComplete);
	sb.append('}');
	return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (!(o instanceof S3Bucket))
	    return false;

	S3Bucket s3Bucket = (S3Bucket) o;

	if (isComplete != s3Bucket.isComplete)
	    return false;
	if (!metaData.equals(s3Bucket.metaData))
	    return false;
	if (objects != null ? !objects.equals(s3Bucket.objects)
		: s3Bucket.objects != null)
	    return false;

	return true;
    }

    @Override
    public int hashCode() {
	int result = objects != null ? objects.hashCode() : 0;
	result = 31 * result + metaData.hashCode();
	result = 31 * result + (isComplete ? 1 : 0);
	return result;
    }

    public static class Metadata {
	@Override
	public String toString() {
	    final StringBuilder sb = new StringBuilder();
	    sb.append("MetaData");
	    sb.append("{name='").append(name).append('\'');
	    sb.append(", creationDate=").append(creationDate);
	    sb.append(", canonicalUser=").append(canonicalUser);
	    sb.append('}');
	    return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o)
		return true;
	    if (!(o instanceof Metadata))
		return false;

	    Metadata metaData = (Metadata) o;

	    if (canonicalUser != null ? !canonicalUser
		    .equals(metaData.canonicalUser)
		    : metaData.canonicalUser != null)
		return false;
	    if (creationDate != null ? !creationDate
		    .equals(metaData.creationDate)
		    : metaData.creationDate != null)
		return false;
	    if (!name.equals(metaData.name))
		return false;

	    return true;
	}

	@Override
	public int hashCode() {
	    int result = name.hashCode();
	    result = 31 * result
		    + (creationDate != null ? creationDate.hashCode() : 0);
	    result = 31 * result
		    + (canonicalUser != null ? canonicalUser.hashCode() : 0);
	    return result;
	}

	public static enum LocationConstraint {
	    EU
	}

	private final String name;
	private DateTime creationDate;
	private S3Owner canonicalUser;

	public Metadata(String name) {
	    this.name = checkNotNull(name, "name");
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

    }

    public static final S3Bucket NOT_FOUND = new S3Bucket("NOT_FOUND");

    private Set<S3Object.Metadata> objects = new HashSet<S3Object.Metadata>();
    private Set<String> commonPrefixes = new HashSet<String>();
    private String prefix;
    private String marker;
    private String delimiter;
    private long maxKeys;
    private final Metadata metaData;

    private boolean isComplete;

    public S3Bucket(String name) {
	this.metaData = new Metadata(name);
    }

    public String getName() {
	return this.metaData.getName();
    }

    public S3Bucket(Metadata metaData) {
	this.metaData = checkNotNull(metaData, "metaData");
    }

    public Set<S3Object.Metadata> getContents() {
	return objects;
    }

    public void setContents(Set<S3Object.Metadata> objects) {
	this.objects = objects;
    }

    public boolean isComplete() {
	return isComplete;
    }

    public void setComplete(boolean complete) {
	isComplete = complete;
    }

    public Metadata getMetaData() {
	return metaData;
    }

    public void setCommonPrefixes(Set<String> commonPrefixes) {
	this.commonPrefixes = commonPrefixes;
    }

    public Set<String> getCommonPrefixes() {
	return commonPrefixes;
    }

    public void setPrefix(String prefix) {
	this.prefix = prefix;
    }

    public String getPrefix() {
	return prefix;
    }

    public void setMaxKeys(long maxKeys) {
	this.maxKeys = maxKeys;
    }

    public long getMaxKeys() {
	return maxKeys;
    }

    public void setMarker(String marker) {
	this.marker = marker;
    }

    public String getMarker() {
	return marker;
    }

    public void setDelimiter(String delimiter) {
	this.delimiter = delimiter;
    }

    public String getDelimiter() {
	return delimiter;
    }

}
