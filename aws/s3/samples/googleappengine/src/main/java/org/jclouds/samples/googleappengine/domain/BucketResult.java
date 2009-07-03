package org.jclouds.samples.googleappengine.domain;

public class BucketResult {
   private String name;
   private String size = "unknown";
   private String status = "ok";

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setSize(String size) {
      this.size = size;
   }

   public String getSize() {
      return size;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getStatus() {
      return status;
   }
}
