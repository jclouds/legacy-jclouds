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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


/**
 * 
 *                 Specifies connection details for the organization s SMTP server.
 *                 If IsDefaultSmtpServer (in OrgEmailSettings) is false, the SmtpServerSettings
 *                 element is taken into account.
 *             
 * 
 * <p>Java class for SmtpServerSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SmtpServerSettings">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="IsUseAuthentication" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Host" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SmtpServerSettings", propOrder = {
    "useAuthentication",
    "host",
    "username",
    "password"
})
public class SmtpServerSettings {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromSmtpServerSettings(this);
   }

   public static class Builder {
      
      private boolean useAuthentication;
      private String host;
      private String username;
      private String password;

      /**
       * @see SmtpServerSettings#getIsUseAuthentication()
       */
      public Builder useAuthentication(boolean useAuthentication) {
         this.useAuthentication = useAuthentication;
         return this;
      }

      /**
       * @see SmtpServerSettings#getHost()
       */
      public Builder host(String host) {
         this.host = host;
         return this;
      }

      /**
       * @see SmtpServerSettings#getUsername()
       */
      public Builder username(String username) {
         this.username = username;
         return this;
      }

      /**
       * @see SmtpServerSettings#getPassword()
       */
      public Builder password(String password) {
         this.password = password;
         return this;
      }


      public SmtpServerSettings build() {
         return new SmtpServerSettings(useAuthentication, host, username, password);
      }


      public Builder fromSmtpServerSettings(SmtpServerSettings in) {
         return useAuthentication(in.useAuthentication())
            .host(in.getHost())
            .username(in.getUsername())
            .password(in.getPassword());
      }
   }

   @SuppressWarnings("unused")
   private SmtpServerSettings() {
      // For JAXB
   }

    public SmtpServerSettings(boolean useAuthentication, String host,
         String username, String password) {
      this.useAuthentication = useAuthentication;
      this.host = host;
      this.username = username;
   }

   @XmlElement(name = "IsUseAuthentication")
    protected boolean useAuthentication;
    @XmlElement(name = "Host", required = true)
    protected String host;
    @XmlElement(name = "Username", required = true)
    protected String username;
    @XmlElement(name = "Password")
    protected String password;

    /**
     * Gets the value of the isUseAuthentication property.
     * 
     */
    public boolean useAuthentication() {
        return useAuthentication;
    }

    /**
     * Gets the value of the host property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      SmtpServerSettings that = SmtpServerSettings.class.cast(o);
      return equal(useAuthentication, that.useAuthentication) && 
           equal(host, that.host) && 
           equal(username, that.username) && 
           equal(password, that.password);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(useAuthentication, 
           host, 
           username, 
           password);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   public ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("isUseAuthentication", useAuthentication)
            .add("host", host)
            .add("username", username)
            .add("password", password);
   }

}
