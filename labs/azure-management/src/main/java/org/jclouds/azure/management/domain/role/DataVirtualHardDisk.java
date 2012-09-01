package org.jclouds.azure.management.domain.role;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DataVirtualHardDisk")
public class DataVirtualHardDisk extends VirtualHardDisk {

   @XmlElement(name = "Lun")
   private Integer lun;

   @XmlElement(name = "LogicalDiskSizeInGB")
   private Integer logicalDiskSizeInGB;

   public DataVirtualHardDisk() {

   }

   public Integer getLun() {
      return lun;
   }

   public void setLun(Integer lun) {
      this.lun = lun;
   }

   public Integer getLogicalDiskSizeInGB() {
      return logicalDiskSizeInGB;
   }

   public void setLogicalDiskSizeInGB(Integer logicalDiskSizeInGB) {
      this.logicalDiskSizeInGB = logicalDiskSizeInGB;
   }

   @Override
   public String toString() {
      return "DataVirtualHardDisk [lun=" + lun + ", logicalDiskSizeInGB=" + logicalDiskSizeInGB + ", hostCaching="
               + hostCaching + ", diskLabel=" + diskLabel + ", diskName=" + diskName + ", mediaLink=" + mediaLink + "]";
   }

}
