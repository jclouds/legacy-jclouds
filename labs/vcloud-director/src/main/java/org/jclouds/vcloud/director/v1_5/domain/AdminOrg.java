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

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


/**
 * 
 *                 The AdminOrg represents an administrative view of an organization.
 *                 It includes all members of the Org element, and adds several
 *                 elements that can be viewed and modified only by system administrators.
 *             
 * 
 * <p>Java class for AdminOrg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AdminOrg">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}OrgType">
 *       &lt;sequence>
 *         &lt;element name="Settings" type="{http://www.vmware.com/vcloud/v1.5}OrgSettingsType"/>
 *         &lt;element name="Users" type="{http://www.vmware.com/vcloud/v1.5}UsersListType" minOccurs="0"/>
 *         &lt;element name="Groups" type="{http://www.vmware.com/vcloud/v1.5}GroupsListType" minOccurs="0"/>
 *         &lt;element name="Catalogs" type="{http://www.vmware.com/vcloud/v1.5}CatalogsListType" minOccurs="0"/>
 *         &lt;element name="Vdcs" type="{http://www.vmware.com/vcloud/v1.5}VdcsType" minOccurs="0"/>
 *         &lt;element name="Networks" type="{http://www.vmware.com/vcloud/v1.5}NetworksType" minOccurs="0"/>
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
@XmlRootElement(name = "AdminOrg")
@XmlType(propOrder = {
    "settings",
    "users",
    "groups",
    "catalogs",
    "vdcs",
    "networks"
})
public class AdminOrg extends Org {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }
   
   public static Builder<?> newBuilder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toNewBuilder() {
      return new ConcreteBuilder().fromAdminOrg(this);
   }
   
   public static abstract class Builder<T extends Builder<T>> extends Org.NewBuilder<T> {
      
      private OrgSettings settings;
      private UsersList users;
      private GroupsList groups;
      private CatalogsList catalogs;
      private Vdcs vdcs;
      private Networks networks;
      
      protected abstract T self();

      /**
       * @see AdminOrg#getSettings()
       */
      public T settings(OrgSettings settings) {
         this.settings = settings;
         return self();
      }

      /**
       * @see AdminOrg#getUsers()
       */
      public T users(UsersList users) {
         this.users = users;
         return self();
      }

      /**
       * @see AdminOrg#getGroups()
       */
      public T groups(GroupsList groups) {
         this.groups = groups;
         return self();
      }

      /**
       * @see AdminOrg#getCatalogs()
       */
      public T catalogs(CatalogsList catalogs) {
         this.catalogs = catalogs;
         return self();
      }

      /**
       * @see AdminOrg#getVdcs()
       */
      public T vdcs(Vdcs vdcs) {
         this.vdcs = vdcs;
         return self();
      }

      /**
       * @see AdminOrg#getNetworks()
       */
      public T networks(Networks networks) {
         this.networks = networks;
         return self();
      }

      public AdminOrg build() {
         return new AdminOrg(href, type, links, description, tasks, id, 
               name, fullName, isEnabled, settings, users, groups, catalogs, vdcs, networks);
      }

      public T fromAdminOrg(AdminOrg in) {
         return fromOrg(in)
            .settings(in.getSettings())
            .users(in.getUsers())
            .groups(in.getGroups())
            .catalogs(in.getCatalogs())
            .vdcs(in.getVdcs())
            .networks(in.getNetworks());
      }
   }
   
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   @Deprecated
   public Org.Builder toBuilder() {
      throw new UnsupportedOperationException("Use toNewBuilder() instead");
   }
   
   protected AdminOrg() {
      // For JAXB
   }
   
   protected AdminOrg(URI href, String type, Set<Link> links, String description, 
         Set<Task> tasks, String id, String name, String fullName, 
         Boolean enabled, OrgSettings settings, UsersList users, GroupsList groups, 
         CatalogsList catalogs, Vdcs vdcs, Networks networks) {
      super(href, type, links, description, tasks, id, name, fullName, enabled);
      this.settings = settings;
      this.users = users;
      this.groups = groups;
      this.catalogs = catalogs;
      this.vdcs = vdcs;
      this.networks = networks;
   }

    @XmlElement(name = "Settings", required = true)
    protected OrgSettings settings;
    @XmlElement(name = "Users")
    protected UsersList users;
    @XmlElement(name = "Groups")
    protected GroupsList groups;
    @XmlElement(name = "Catalogs")
    protected CatalogsList catalogs;
    @XmlElement(name = "Vdcs")
    protected Vdcs vdcs;
    @XmlElement(name = "Networks")
    protected Networks networks;

    /**
     * Gets the value of the settings property.
     * 
     * @return
     *     possible object is
     *     {@link OrgSettings }
     *     
     */
    public OrgSettings getSettings() {
        return settings;
    }

    /**
     * Gets the value of the users property.
     * 
     * @return
     *     possible object is
     *     {@link UsersList }
     *     
     */
    public UsersList getUsers() {
        return users;
    }

    /**
     * Gets the value of the groups property.
     * 
     * @return
     *     possible object is
     *     {@link GroupsList }
     *     
     */
    public GroupsList getGroups() {
        return groups;
    }

    /**
     * Gets the value of the catalogs property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogsList }
     *     
     */
    public CatalogsList getCatalogs() {
        return catalogs;
    }

    /**
     * Gets the value of the vdcs property.
     * 
     * @return
     *     possible object is
     *     {@link Vdcs }
     *     
     */
    public Vdcs getVdcs() {
        return vdcs;
    }

    /**
     * Gets the value of the networks property.
     * 
     * @return
     *     possible object is
     *     {@link Networks }
     *     
     */
    public Networks getNetworks() {
        return networks;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      AdminOrg that = AdminOrg.class.cast(o);
      return super.equals(that) && 
           equal(settings, that.settings) && 
           equal(users, that.users) && 
           equal(groups, that.groups) && 
           equal(catalogs, that.catalogs) && 
           equal(vdcs, that.vdcs) && 
           equal(networks, that.networks);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), 
           settings, 
           users, 
           groups, 
           catalogs, 
           vdcs, 
           networks);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("settings", settings)
            .add("users", users)
            .add("groups", groups)
            .add("catalogs", catalogs)
            .add("vdcs", vdcs)
            .add("networks", networks);
   }

}
