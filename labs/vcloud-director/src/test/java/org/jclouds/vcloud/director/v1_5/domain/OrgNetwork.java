package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.NS;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import org.jclouds.vcloud.director.v1_5.domain.Org.Builder;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

public class OrgNetwork extends EntityType<OrgNetwork> {
   
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromOrgNetwork(this);
   }

   public static class Builder extends EntityType.Builder<OrgNetwork> {

      private boolean allowedExternalIpAddresses;

      /**
       * @see Network#getAllowedExternalIpAddresses()
       */
      public Builder allowedExternalIpAddresses(boolean ExternalIpAddresses) {
         this.allowedExternalIpAddresses = allowedExternalIpAddresses;
         return this;
      }

      @Override
      public OrgNetwork build() {
         Org org = new OrgNetwork(href, name);
         org.setNetworkPool(network);
         org.setDescription(description);
         org.setId(id);
         org.setType(type);
         org.setLinks(links);
         org.setTasksInProgress(tasksInProgress);
         return org;
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder tasksInProgress(TasksInProgress tasksInProgress) {
         this.tasksInProgress = tasksInProgress;
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder fromEntityType(EntityType<OrgNetwork> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder fromOrgNetwork(OrgNetwork in) {
         return fromEntityType(in).fullName(in.getFullName());
      }
   }

   private OrgNetwork() {
      // For JAXB and builder use
   }

   private OrgNetwork(URI href, String name, String fullName) {
      super(href, name);
      this.fullName = fullName;
   }

   @XmlElement(namespace = NS, name = "FullName")
   private String fullName;

   /**
    * 
    * @return fullName of the org
    */
   public String getFullName() {
      return fullName;
   }

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      Org that = Org.class.cast(o);
      return super.equals(that) && equal(fullName, that.fullName);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(fullName);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("fullName", fullName);
   }

}
