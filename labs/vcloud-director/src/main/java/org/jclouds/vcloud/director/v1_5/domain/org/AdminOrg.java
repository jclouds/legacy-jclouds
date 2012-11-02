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
package org.jclouds.vcloud.director.v1_5.domain.org;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Reference;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * The AdminOrg represents an administrative view of an organization.
 * It includes all members of the Org element, and adds several
 * elements that can be viewed and modified only by system administrators.
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
 */
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

   @Override
   public Builder<?> toBuilder() {
      return builder().fromAdminOrg(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Org.Builder<B> {
      
      private OrgSettings settings;
      private Set<Reference> users = Sets.newLinkedHashSet();
      private Set<Reference> groups = Sets.newLinkedHashSet();
      private Set<Reference> catalogs = Sets.newLinkedHashSet();
      private Set<Reference> vdcs = Sets.newLinkedHashSet();
      private Set<Reference> networks = Sets.newLinkedHashSet();
      
      /**
       * @see AdminOrg#getSettings()
       */
      public B settings(OrgSettings settings) {
         this.settings = settings;
         return self();
      }

      /**
       * @see AdminOrg#getUsers()
       */
      public B users(Iterable<Reference> users) {
         this.users = Sets.newLinkedHashSet(checkNotNull(users, "users"));
         return self();
      }
      
      /**
       * @see AdminOrg#getUsers()
       */
      public B user(Reference user) {
         users.add(checkNotNull(user, "user"));
         return self();
      }

      /**
       * @see AdminOrg#getGroups()
       */
      public B groups(Iterable<Reference> groups) {
         this.groups = Sets.newLinkedHashSet(checkNotNull(groups, "groups"));
         return self();
      }
      
      /**
       * @see AdminOrg#getGroups()
       */
      public B group(Reference group) {
         groups.add(checkNotNull(group, "group"));
         return self();
      }
      /**
       * @see AdminOrg#getCatalogs()
       */
      public B catalogs(Iterable<Reference> catalogReferences) {
         this.catalogs = Sets.newLinkedHashSet(checkNotNull(catalogs, "catalogs"));
         return self();
      }
      
      /**
       * @see AdminOrg#getCatalogs()
       */
      public B catalog(Reference catalog) {
         this.catalogs.add(checkNotNull(catalog, "catalog"));
         return self();
      }
      /**
       * @see AdminOrg#getVdcs()
       */
      public B vdcs(Iterable<Reference> vdcs) {
         this.vdcs = Sets.newLinkedHashSet(checkNotNull(vdcs, "vdcs"));
         return self();
      }
      
      /**
       * @see AdminOrg#getVdcs()
       */
      public B vdc(Reference vdc) {
         this.vdcs.add(checkNotNull(vdc, "vdc"));
         return self();
      }
      
      /**
       * @see AdminOrg#getNetworks()
       */
      public B networks(Iterable<Reference> networks) {
         this.networks = Sets.newLinkedHashSet(checkNotNull(networks, "networks"));
         return self();
      }
      
      /**
       * @see AdminOrg#getNetworks()
       */
      public B network(Reference network) {
         this.networks.add(checkNotNull(network, "network"));
         return self();
      }

      @Override
      public AdminOrg build() {
         return new AdminOrg(this);
      }

      public B fromAdminOrg(AdminOrg in) {
         return fromOrg(in)
            .settings(in.getSettings())
            .users(in.getUsers())
            .groups(in.getGroups())
            .catalogs(in.getCatalogs())
            .vdcs(in.getVdcs())
            .networks(in.getNetworks());
      }
   }
   
   protected AdminOrg() {
      // For JAXB
   }
   
   protected AdminOrg(Builder<?> builder) {
      super(builder);
      this.settings = builder.settings;
      this.users = builder.users == null ? Sets.<Reference>newLinkedHashSet() : ImmutableSet.copyOf(builder.users);
      this.groups = builder.groups == null ? Sets.<Reference>newLinkedHashSet() : ImmutableSet.copyOf(builder.groups);
      this.catalogs = builder.catalogs == null ? Sets.<Reference>newLinkedHashSet() : ImmutableSet.copyOf(builder.catalogs);
      this.vdcs = builder.vdcs == null ? Sets.<Reference>newLinkedHashSet() : ImmutableSet.copyOf(builder.vdcs);
      this.networks = builder.networks == null ? Sets.<Reference>newLinkedHashSet() : ImmutableSet.copyOf(builder.networks);
   }

    @XmlElement(name = "Settings", required = true)
    private OrgSettings settings;
    @XmlElementWrapper(name = "Users")
    @XmlElement(name = "UserReference")
    protected Set<Reference> users = Sets.newLinkedHashSet();
    @XmlElementWrapper(name = "Groups")
    @XmlElement(name = "GroupReference")
    protected Set<Reference> groups = Sets.newLinkedHashSet();
    @XmlElementWrapper(name = "Catalogs")
    @XmlElement(name = "CatalogReference")
    private Set<Reference> catalogs = Sets.newLinkedHashSet();
    @XmlElementWrapper(name = "Vdcs")
    @XmlElement(name = "Vdc")
    protected Set<Reference> vdcs = Sets.newLinkedHashSet();
    @XmlElementWrapper(name = "Networks")
    @XmlElement(name = "Network")
    protected Set<Reference> networks = Sets.newLinkedHashSet();

    /**
     * Gets the value of the settings property.
     */
    public OrgSettings getSettings() {
        return settings;
    }

    /**
     * Gets the value of the users property.
     */
    public Set<Reference> getUsers() {
        return users;
    }

    /**
     * Gets the value of the groups property.
     */
    public Set<Reference> getGroups() {
        return groups;
    }

    /**
     * Gets the value of the catalogs property.
     */
    public Set<Reference> getCatalogs() {
        return catalogs;
    }

    /**
     * Gets the value of the vdcs property.
     */
    public Set<Reference> getVdcs() {
        return vdcs;
    }

    /**
     * Gets the value of the networks property.
     */
    public Set<Reference> getNetworks() {
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
