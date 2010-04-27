package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

public class MetaData {
   @SerializedName("key_name")
   private String key;
   
   private String value;

   public void setKey(String key) {
      this.key = key;
   }

   public String getKey() {
      return key;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public String getValue() {
      return value;
   }
}
