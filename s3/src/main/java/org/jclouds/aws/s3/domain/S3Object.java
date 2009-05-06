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

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.DateTime;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class S3Object {
    public static final S3Object NOT_FOUND = new S3Object(MetaData.NOT_FOUND);

    public static class MetaData {
	public static final MetaData NOT_FOUND = new MetaData("NOT_FOUND");

	public static final String UNKNOWN_MIME_TYPE = "application/x-unknown-mime-type";

	private final String key;
	private DateTime lastModified;
	private String eTag;
	private long size = -1;
	private S3Owner owner;
	private String contentType = UNKNOWN_MIME_TYPE;
	private String storageClass = "STANDARD";
	private String contentMD5;
	private String server;

	public MetaData(String key) {
	    this.key = checkNotNull(key, "key");
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

	public String getETag() {
	    return eTag;
	}

	public void setETag(String tag) {
	    eTag = tag;
	}

	public long getSize() {
	    return size;
	}

	public void setSize(long size) {
	    this.size = size;
	}

	public S3Owner getOwner() {
	    return owner;
	}

	public void setOwner(S3Owner owner) {
	    this.owner = owner;
	}

	public String getContentType() {
	    return contentType;
	}

	public void setContentType(String contentType) {
	    this.contentType = contentType;
	}

	public String getStorageClass() {
	    return storageClass;
	}

	public void setStorageClass(String storageClass) {
	    this.storageClass = storageClass;
	}

	public String getContentMD5() {
	    return contentMD5;
	}

	public void setContentMD5(String contentMD5) {
	    this.contentMD5 = contentMD5;
	}

	public String getServer() {
	    return server;
	}

	public void setServer(String server) {
	    this.server = server;
	}
    }

    private Object data;
    private MetaData metaData;

    public S3Object(String key) {
	this(new MetaData(key));
    }

    public S3Object(MetaData metaData) {
	this.metaData = metaData;
    }

    public S3Object(MetaData metaData, Object data) {
	this(metaData);
	this.data = data;
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

    public void setMetaData(MetaData metaData) {
	this.metaData = metaData;
    }

    public MetaData getMetaData() {
	return metaData;
    }

}
