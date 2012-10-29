package org.jclouds.fujitsu.fgcp.domain;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

public class VServerWithVNICs extends VServer {

   @XmlElementWrapper(name = "vnics")
   @XmlElement(name = "vnic")
   protected Set<VNIC> vnics = new LinkedHashSet<VNIC>();

   public Set<VNIC> getVnics() {
      return vnics == null ? ImmutableSet.<VNIC> of() : ImmutableSet
            .copyOf(vnics);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id)
            .add("name", name).add("type", type).add("creator", creator)
            .add("diskimageId", diskimageId).add("vnics", vnics).toString();
   }
}
