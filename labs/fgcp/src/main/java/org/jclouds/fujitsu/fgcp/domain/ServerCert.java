/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.fujitsu.fgcp.domain;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * Describes a server certificate for use with a load balancer (SLB).
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "servercert")
public class ServerCert implements Comparable<ServerCert> {
    private int certNum;

    private String subject;

    private String issuer;

    private String validity;

    private int groupId;

    private String detail;

    /**
     * @return the certNum
     */
    public int getCertNum() {
        return certNum;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * @return the validity
     */
    public String getValidity() {
        return validity;
    }

    /**
     * @return the groupId
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(certNum, groupId, issuer, subject, validity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServerCert that = ServerCert.class.cast(obj);
        return Objects.equal(this.certNum, that.certNum)
                && Objects.equal(this.groupId, that.groupId)
                && Objects.equal(this.issuer, that.issuer)
                && Objects.equal(this.subject, that.subject)
                && Objects.equal(this.validity, that.validity);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("certNum", certNum).add("issuer", issuer)
                .add("subject", subject).add("validity", validity)
                .add("groupId", groupId).add("detail", detail).toString();
    }

    @Override
    public int compareTo(ServerCert o) {
        return (certNum - o.certNum) == 0 ? (groupId - o.groupId)
                : (certNum - o.certNum);
    }

}
