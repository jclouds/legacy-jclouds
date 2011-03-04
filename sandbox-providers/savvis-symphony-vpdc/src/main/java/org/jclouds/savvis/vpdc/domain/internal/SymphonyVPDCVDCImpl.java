package org.jclouds.savvis.vpdc.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.savvis.vpdc.domain.SymphonyVPDCVDC;
import org.jclouds.vcloud.domain.AllocationModel;
import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VDCStatus;
import org.jclouds.vcloud.domain.internal.VDCImpl;

/**
 * Locations of resources in SymphonyVPDC vDC
 * 
 * @author Adrian Cole
 * 
 */
public class SymphonyVPDCVDCImpl extends VDCImpl implements SymphonyVPDCVDC {
   private final String offeringTag;

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public SymphonyVPDCVDCImpl(String name, String type, URI id, VDCStatus status, ReferenceType org,
         @Nullable String description, Iterable<Task> tasks, AllocationModel allocationModel,
         @Nullable Capacity storageCapacity, @Nullable Capacity cpuCapacity, @Nullable Capacity memoryCapacity,
         Map<String, ReferenceType> resourceEntities, Map<String, ReferenceType> availableNetworks, int nicQuota,
         int networkQuota, int vmQuota, boolean isEnabled, String offeringTag) {
      super(name, type, id, status, org, description, tasks, allocationModel, storageCapacity, cpuCapacity,
            memoryCapacity, resourceEntities, availableNetworks, nicQuota, networkQuota, vmQuota, isEnabled);
      this.offeringTag = checkNotNull(offeringTag, "offeringTag");
   }

   @Override
   public String getOfferingTag() {
      return offeringTag;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((offeringTag == null) ? 0 : offeringTag.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      SymphonyVPDCVDCImpl other = (SymphonyVPDCVDCImpl) obj;
      if (offeringTag == null) {
         if (other.offeringTag != null)
            return false;
      } else if (!offeringTag.equals(other.offeringTag))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getHref() + ", name=" + getName() + ", description=" + getDescription() + ", offeringTag="
            + offeringTag + "]";
   }
}