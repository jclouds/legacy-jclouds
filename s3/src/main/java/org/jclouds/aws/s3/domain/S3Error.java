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

public class S3Error {
    private String code;
    private String message;
    private String resource;
    private String requestId;
    private String hostId;
    private String header;
    private String signatureProvided;
    private String stringToSign;
    private String stringSigned;
    private String stringToSignBytes;

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	sb.append("S3Error");
	sb.append("{code='").append(code).append('\'');
	sb.append(", message='").append(message).append('\'');
	sb.append(", resource='").append(resource).append('\'');
	sb.append(", requestId='").append(requestId).append('\'');
	sb.append(", hostId='").append(hostId).append('\'');
	sb.append(", header='").append(header).append('\'');
	sb.append(", signatureProvided='").append(signatureProvided).append(
		'\'');
	sb.append(", stringToSign='").append(stringToSign).append('\'');
	sb.append(", stringSigned='").append(getStringSigned()).append('\'');
	sb.append(", stringToSignBytes='").append(stringToSignBytes).append(
		'\'');
	sb.append('}');
	return sb.toString();
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public String getMessage() {
	return message;
    }

    public void setResource(String resource) {
	this.resource = resource;
    }

    public String getResource() {
	return resource;
    }

    public void setRequestId(String requestId) {
	this.requestId = requestId;
    }

    public String getRequestId() {
	return requestId;
    }

    public void setHostId(String hostId) {
	this.hostId = hostId;
    }

    public String getHostId() {
	return hostId;
    }

    public void setSignatureProvided(String signatureProvided) {
	this.signatureProvided = signatureProvided;
    }

    public String getSignatureProvided() {
	return signatureProvided;
    }

    public void setStringToSign(String stringToSign) {
	this.stringToSign = stringToSign;
    }

    public String getStringToSign() {
	return stringToSign;
    }

    public void setStringToSignBytes(String stringToSignBytes) {
	this.stringToSignBytes = stringToSignBytes;
    }

    public String getStringToSignBytes() {
	return stringToSignBytes;
    }

    public void setHeader(String header) {
	this.header = header;
    }

    public String getHeader() {
	return header;
    }

    public void setStringSigned(String stringSigned) {
	this.stringSigned = stringSigned;
    }

    public String getStringSigned() {
	return stringSigned;
    }
}
