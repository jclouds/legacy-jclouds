/*
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
package org.jclouds.vcloud.director.v1_5.domain.org;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Used when OrgLdapMode=CUSTOM to define connection details for
 * the organization's LDAP service.
 *             
 * <pre>
 * &lt;complexType name="CustomOrgLdapSettings">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="HostName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Port" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IsSsl" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="IsSslAcceptAll" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Realm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SearchBase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AuthenticationMechanism" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GroupSearchBase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsGroupSearchBaseEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ConnectorType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="UserAttributes" type="{http://www.vmware.com/vcloud/v1.5}OrgLdapUserAttributesType"/>
 *         &lt;element name="GroupAttributes" type="{http://www.vmware.com/vcloud/v1.5}OrgLdapGroupAttributesType"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "CustomOrgLdapSettings", propOrder = {
    "hostName",
    "port",
    "isSsl",
    "isSslAcceptAll",
    "realm",
    "searchBase",
    "userName",
    "password",
    "authenticationMechanism",
    "groupSearchBase",
    "isGroupSearchBaseEnabled",
    "connectorType",
    "userAttributes",
    "groupAttributes"
})
public class CustomOrgLdapSettings {
   @XmlType
   @XmlEnum(String.class)
   public static enum AuthenticationMechanism {
      @XmlEnumValue("simple") SIMPLE("simple"),
      @XmlEnumValue("kerberos") KERBEROS("kerberos"),
      @XmlEnumValue("md5digest") MD5DIGEST("md5digest"),
      @XmlEnumValue("ntlm") NTLM("ntlm"),
      UNRECOGNIZED("unrecognized");
      
      public static final List<AuthenticationMechanism> ALL = ImmutableList.of(
            SIMPLE, KERBEROS, MD5DIGEST, NTLM);

      protected final String stringValue;

      AuthenticationMechanism(String stringValue) {
         this.stringValue = stringValue;
      }

      public String value() {
         return stringValue;
      }

      protected static final Map<String, AuthenticationMechanism> AUTHENTICATION_MECHANISM_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(AuthenticationMechanism.values()), new Function<AuthenticationMechanism, String>() {
               @Override
               public String apply(AuthenticationMechanism input) {
                  return input.stringValue;
               }
            });

      public static AuthenticationMechanism fromValue(String value) {
         AuthenticationMechanism authMechanism = AUTHENTICATION_MECHANISM_BY_ID.get(checkNotNull(value, "stringValue"));
         return authMechanism == null ? UNRECOGNIZED : authMechanism;
      }
   }
   
   @XmlType
   @XmlEnum(String.class)
   public static enum ConnectorType {
      @XmlEnumValue("ACTIVE_DIRECTORY") ACTIVE_DIRECTORY("ACTIVE_DIRECTORY"),
      @XmlEnumValue("OPEN_LDAP") OPEN_LDAP("OPEN_LDAP"),
      UNRECOGNIZED("unrecognized");
      
      public static final List<ConnectorType> ALL = ImmutableList.of(
            ACTIVE_DIRECTORY, OPEN_LDAP);

      protected final String stringValue;

      ConnectorType(String stringValue) {
         this.stringValue = stringValue;
      }

      public String value() {
         return stringValue;
      }

      protected static final Map<String, ConnectorType> CONNECTOR_TYPE_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(ConnectorType.values()), new Function<ConnectorType, String>() {
               @Override
               public String apply(ConnectorType input) {
                  return input.stringValue;
               }
            });

      public static ConnectorType fromValue(String value) {
         ConnectorType type = CONNECTOR_TYPE_BY_ID.get(checkNotNull(value, "stringValue"));
         return type == null ? UNRECOGNIZED : type;
      }
   }
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCustomOrgLdapSettings(this);
   }
   
   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();
      
      private String hostName;
      private int port;
      private Boolean isSsl;
      private Boolean isSslAcceptAll;
      private String realm;
      private String searchBase;
      private String userName;
      private String password;
      private String authenticationMechanism;
      private String groupSearchBase;
      private boolean isGroupSearchBaseEnabled;
      private String connectorType;
      private OrgLdapUserAttributes userAttributes;
      private OrgLdapGroupAttributes groupAttributes;

      /**
       * @see CustomOrgLdapSettings#getHostName()
       */
      public T hostName(String hostName) {
         this.hostName = hostName;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getPort()
       */
      public T port(int port) {
         this.port = port;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getIsSsl()
       */
      public T isSsl(Boolean isSsl) {
         this.isSsl = isSsl;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getIsSslAcceptAll()
       */
      public T isSslAcceptAll(Boolean isSslAcceptAll) {
         this.isSslAcceptAll = isSslAcceptAll;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getRealm()
       */
      public T realm(String realm) {
         this.realm = realm;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getSearchBase()
       */
      public T searchBase(String searchBase) {
         this.searchBase = searchBase;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getUserName()
       */
      public T userName(String userName) {
         this.userName = userName;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getPassword()
       */
      public T password(String password) {
         this.password = password;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getAuthenticationMechanism()
       */
      public T authenticationMechanism(String authenticationMechanism) {
         this.authenticationMechanism = authenticationMechanism;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getGroupSearchBase()
       */
      public T groupSearchBase(String groupSearchBase) {
         this.groupSearchBase = groupSearchBase;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getIsGroupSearchBaseEnabled()
       */
      public T isGroupSearchBaseEnabled(boolean isGroupSearchBaseEnabled) {
         this.isGroupSearchBaseEnabled = isGroupSearchBaseEnabled;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getConnectorType()
       */
      public T connectorType(String connectorType) {
         this.connectorType = connectorType;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getUserAttributes()
       */
      public T userAttributes(OrgLdapUserAttributes userAttributes) {
         this.userAttributes = userAttributes;
         return self();
      }

      /**
       * @see CustomOrgLdapSettings#getGroupAttributes()
       */
      public T groupAttributes(OrgLdapGroupAttributes groupAttributes) {
         this.groupAttributes = groupAttributes;
         return self();
      }


      public CustomOrgLdapSettings build() {
         return new CustomOrgLdapSettings(hostName, port, isSsl, isSslAcceptAll, 
               realm, searchBase, userName, password, authenticationMechanism, 
               groupSearchBase, isGroupSearchBaseEnabled, connectorType, 
               userAttributes, groupAttributes);
      }


      public T fromCustomOrgLdapSettings(CustomOrgLdapSettings in) {
         return hostName(in.getHostName())
            .port(in.getPort())
            .isSsl(in.isSsl())
            .isSslAcceptAll(in.isSslAcceptAll())
            .realm(in.getRealm())
            .searchBase(in.getSearchBase())
            .userName(in.getUserName())
            .password(in.getPassword())
            .authenticationMechanism(in.getAuthenticationMechanism())
            .groupSearchBase(in.getGroupSearchBase())
            .isGroupSearchBaseEnabled(in.isGroupSearchBaseEnabled())
            .connectorType(in.getConnectorType())
            .userAttributes(in.getUserAttributes())
            .groupAttributes(in.getGroupAttributes());
      }

   }

   public static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected CustomOrgLdapSettings() {
      // For JAXB
   }

   private CustomOrgLdapSettings(String hostName, int port, Boolean isSsl, 
         Boolean isSslAcceptAll, String realm, String searchBase, String userName, 
         String password, String authenticationMechanism, String groupSearchBase, 
         boolean isGroupSearchBaseEnabled, String connectorType, 
         OrgLdapUserAttributes userAttributes, OrgLdapGroupAttributes groupAttributes) {
         this.hostName = hostName;
         this.port = port;
         this.isSsl = isSsl;
         this.password = password;
         this.authenticationMechanism = authenticationMechanism;
         
   }



    @XmlElement(name = "HostName", required = true)
    protected String hostName;
    @XmlElement(name = "Port")
    protected int port;
    @XmlElement(name = "IsSsl")
    protected Boolean isSsl;
    @XmlElement(name = "IsSslAcceptAll")
    protected Boolean isSslAcceptAll;
    @XmlElement(name = "Realm")
    protected String realm;
    @XmlElement(name = "SearchBase")
    protected String searchBase;
    @XmlElement(name = "UserName")
    protected String userName;
    @XmlElement(name = "Password")
    protected String password;
    @XmlElement(name = "AuthenticationMechanism", required = true)
    protected String authenticationMechanism;
    @XmlElement(name = "GroupSearchBase")
    protected String groupSearchBase;
    @XmlElement(name = "IsGroupSearchBaseEnabled")
    protected boolean isGroupSearchBaseEnabled;
    @XmlElement(name = "ConnectorType", required = true)
    protected String connectorType;
    @XmlElement(name = "UserAttributes", required = true)
    protected OrgLdapUserAttributes userAttributes;
    @XmlElement(name = "GroupAttributes", required = true)
    protected OrgLdapGroupAttributes groupAttributes;

    /**
     * Gets the value of the hostName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Gets the value of the port property.
     * 
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the value of the isSsl property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSsl() {
        return isSsl;
    }


    /**
     * Gets the value of the isSslAcceptAll property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSslAcceptAll() {
        return isSslAcceptAll;
    }

    /**
     * Gets the value of the realm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Gets the value of the searchBase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchBase() {
        return searchBase;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
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

    /**
     * Gets the value of the authenticationMechanism property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    /**
     * Gets the value of the groupSearchBase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupSearchBase() {
        return groupSearchBase;
    }

    /**
     * Gets the value of the isGroupSearchBaseEnabled property.
     * 
     */
    public boolean isGroupSearchBaseEnabled() {
        return isGroupSearchBaseEnabled;
    }

    /**
     * Gets the value of the connectorType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectorType() {
        return connectorType;
    }

    /**
     * Gets the value of the userAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link OrgLdapUserAttributes }
     *     
     */
    public OrgLdapUserAttributes getUserAttributes() {
        return userAttributes;
    }

    /**
     * Gets the value of the groupAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link OrgLdapGroupAttributes }
     *     
     */
    public OrgLdapGroupAttributes getGroupAttributes() {
        return groupAttributes;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CustomOrgLdapSettings that = CustomOrgLdapSettings.class.cast(o);
      return super.equals(that) && 
           equal(hostName, that.hostName) && 
           equal(port, that.port) && 
           equal(isSsl, that.isSsl) && 
           equal(isSslAcceptAll, that.isSslAcceptAll) && 
           equal(realm, that.realm) && 
           equal(searchBase, that.searchBase) && 
           equal(userName, that.userName) && 
           equal(password, that.password) && 
           equal(authenticationMechanism, that.authenticationMechanism) && 
           equal(groupSearchBase, that.groupSearchBase) && 
           equal(isGroupSearchBaseEnabled, that.isGroupSearchBaseEnabled) && 
           equal(connectorType, that.connectorType) && 
           equal(userAttributes, that.userAttributes) && 
           equal(groupAttributes, that.groupAttributes);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), 
           hostName, 
           port, 
           isSsl, 
           isSslAcceptAll, 
           realm, 
           searchBase, 
           userName, 
           password, 
           authenticationMechanism, 
           groupSearchBase, 
           isGroupSearchBaseEnabled, 
           connectorType, 
           userAttributes, 
           groupAttributes);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("hostName", hostName)
            .add("port", port)
            .add("isSsl", isSsl)
            .add("isSslAcceptAll", isSslAcceptAll)
            .add("realm", realm)
            .add("searchBase", searchBase)
            .add("userName", userName)
            .add("password", password)
            .add("authenticationMechanism", authenticationMechanism)
            .add("groupSearchBase", groupSearchBase)
            .add("isGroupSearchBaseEnabled", isGroupSearchBaseEnabled)
            .add("connectorType", connectorType)
            .add("userAttributes", userAttributes)
            .add("groupAttributes", groupAttributes);
   }

}
