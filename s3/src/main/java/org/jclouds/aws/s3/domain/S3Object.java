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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.jclouds.http.ContentTypes;
import org.joda.time.DateTime;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class S3Object {
    public static final S3Object NOT_FOUND = new S3Object(Metadata.NOT_FOUND);

    private Object data;
    private Metadata metaData;

    public S3Object(String key) {
	this(new Metadata(key));
    }

    public S3Object(Metadata metaData) {
	this.metaData = metaData;
    }

    public S3Object(Metadata metaData, Object data) {
	this(metaData);
	this.data = data;
    }

    public S3Object(String key, Object data) {
	this(key);
	this.data = data;
    }

    public static class Metadata {
	public static final Metadata NOT_FOUND = new Metadata("NOT_FOUND");
	
	// parsed during list, head, or get
	private final String key;
	private byte[] md5;
	private long size = -1;
	
	// only parsed during head or get
	private Multimap<String, String> userMetadata = HashMultimap.create();
	private DateTime lastModified;
	private String contentType = ContentTypes.UNKNOWN_MIME_TYPE;
	private String cacheControl;
	private String contentDisposition;
	private String contentEncoding;
	
	// only parsed on list
	private S3Owner owner = null;
	private String storageClass = null;

	public Metadata(String key) {
	    checkNotNull(key, "key");
	    checkArgument(!key.startsWith("/"), "keys cannot start with /");
	    this.key = key;
	}

	@Override
	public String toString() {
	    final StringBuilder sb = new StringBuilder();
	    sb.append("MetaData");
	    sb.append("{key='").append(key).append('\'');
	    sb.append(", lastModified=").append(lastModified);
	    sb.append(", md5=").append(
		    getMd5() == null ? "null" : Arrays.asList(getMd5())
			    .toString());
	    sb.append(", size=").append(size);
	    sb.append(", contentType='").append(contentType).append('\'');
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

	    if (size != metaData.size)
		return false;
	    if (contentType != null ? !contentType.equals(metaData.contentType)
		    : metaData.contentType != null)
		return false;
	    if (!key.equals(metaData.key))
		return false;
	    if (lastModified != null ? !lastModified
		    .equals(metaData.lastModified)
		    : metaData.lastModified != null)
		return false;
	    if (!Arrays.equals(getMd5(), metaData.getMd5()))
		return false;
	    return true;
	}

	@Override
	public int hashCode() {
	    int result = key.hashCode();
	    result = 31 * result
		    + (lastModified != null ? lastModified.hashCode() : 0);
	    result = 31 * result
		    + (getMd5() != null ? Arrays.hashCode(getMd5()) : 0);
	    result = 31 * result + (int) (size ^ (size >>> 32));
	    result = 31 * result
		    + (contentType != null ? contentType.hashCode() : 0);
	    return result;
	}

	public String getKey() {
	    return key;
	}

	public DateTime getLastModified() {
	    return lastModified;
	}

	public void setLastModified(DateTime lastModified) {
	    this.lastModified = lastModified;
	}

	public long getSize() {
	    return size;
	}

	public void setSize(long size) {
	    this.size = size;
	}

	public String getContentType() {
	    return contentType;
	}

	public void setContentType(String contentType) {
	    this.contentType = contentType;
	}

	public void setMd5(byte[] md5) {
	    this.md5 = md5;
	}

	public byte[] getMd5() {
	    return md5;
	}

	public void setUserMetadata(Multimap<String, String> userMetadata) {
	    this.userMetadata = userMetadata;
	}

	public Multimap<String, String> getUserMetadata() {
	    return userMetadata;
	}

	public void setOwner(S3Owner owner) {
	    this.owner = owner;
	}

	public S3Owner getOwner() {
	    return owner;
	}

	public void setStorageClass(String storageClass) {
	    this.storageClass = storageClass;
	}

	public String getStorageClass() {
	    return storageClass;
	}

	public void setCacheControl(String cacheControl) {
	    this.cacheControl = cacheControl;
	}

	public String getCacheControl() {
	    return cacheControl;
	}

	public void setContentDisposition(String contentDisposition) {
	    this.contentDisposition = contentDisposition;
	}

	public String getContentDisposition() {
	    return contentDisposition;
	}

	public void setContentEncoding(String contentEncoding) {
	    this.contentEncoding = contentEncoding;
	}

	public String getContentEncoding() {
	    return contentEncoding;
	}
    }

    public String getKey() {
	return metaData.getKey();
    }

    public void setData(Object data) {
	this.data = data;
    }

    public Object getData() {
	return data;
    }

    public void setMetaData(Metadata metaData) {
	this.metaData = metaData;
    }

    public Metadata getMetaData() {
	return metaData;
    }

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	sb.append("S3Object");
	sb.append("{metaData=").append(metaData);
	sb.append('}');
	return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (!(o instanceof S3Object))
	    return false;

	S3Object s3Object = (S3Object) o;

	if (data != null ? !data.equals(s3Object.data) : s3Object.data != null)
	    return false;
	if (!metaData.equals(s3Object.metaData))
	    return false;

	return true;
    }

    @Override
    public int hashCode() {
	int result = data != null ? data.hashCode() : 0;
	result = 31 * result + metaData.hashCode();
	return result;
    }

}
