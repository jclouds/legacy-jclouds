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
 * Describes an intermediate CA certificate for use with a load balancer (SLB).
 * 
 * @author Dies Koper
 */
@XmlRootElement(name = "ccacert")
public class IntermediateCACert implements Comparable<IntermediateCACert> {
    private int ccacertNum;

    private String description;

    private String subject;

    private String issuer;

    private String validity;

    private String detail;

    /**
     * @return the ccacertNum
     */
    public int getCcacertNum() {
        return ccacertNum;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
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
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ccacertNum, issuer, subject, validity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IntermediateCACert that = IntermediateCACert.class.cast(obj);
        return Objects.equal(this.ccacertNum, that.ccacertNum)
                && Objects.equal(this.issuer, that.issuer)
                && Objects.equal(this.subject, that.subject)
                && Objects.equal(this.validity, that.validity);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("ccacertNum", ccacertNum).add("issuer", issuer)
                .add("subject", subject).add("validity", validity)
                .add("description", description).add("detail", detail)
                .toString();
    }

    @Override
    public int compareTo(IntermediateCACert o) {
        return ccacertNum - o.ccacertNum;
    }

}
