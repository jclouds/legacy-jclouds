package org.jclouds.snia.cdmi.v1.options;

/**
 * Optional get CDMI container operations
 * 
 * @author Kenneth Nagin
 */
public class GetContainerOptions extends GetCDMIObjectOptions {

   public GetContainerOptions() {
      super();
   }

   /**
    * Get CDMI container's field
    * 
    * @param fieldname
    * @return this
    */
   public GetContainerOptions field(String fieldname) {
      super.field(fieldname);
      return this;
   }

   /**
    * Get CDMI container's metadata
    * 
    * @return this
    */
   public GetContainerOptions metadata() {
      super.metadata();
      return this;
   }

   /**
    * Get CDMI container's metadata
    * 
    * @param prefix
    * @return this
    */
   public GetContainerOptions metadata(String prefix) {
      super.metadata(prefix);
      return this;
   }

   /**
    * Get CDMI container's children
    * 
    * @return this
    */
   public GetContainerOptions children() {
      this.pathSuffix = this.pathSuffix + "children;";
      return this;
   }

   /**
    * Get CDMI container's children in range
    * 
    * @param from
    * @param to
    * @return this
    */
   public GetContainerOptions children(int from, int to) {
      this.pathSuffix = this.pathSuffix + "children:" + from + "-" + to + ";";
      return this;
   }

   public static class Builder {
      public static GetContainerOptions field(String fieldname) {
         GetContainerOptions options = new GetContainerOptions();
         return (GetContainerOptions) options.field(fieldname);
      }

      public static GetContainerOptions metadata() {
         GetContainerOptions options = new GetContainerOptions();
         return (GetContainerOptions) options.metadata();
      }

      public static GetContainerOptions metadata(String prefix) {
         GetContainerOptions options = new GetContainerOptions();
         return (GetContainerOptions) options.metadata(prefix);
      }

      public static GetContainerOptions children() {
         GetContainerOptions options = new GetContainerOptions();
         return (GetContainerOptions) options.children();
      }

      public static GetContainerOptions children(int from, int to) {
         GetContainerOptions options = new GetContainerOptions();
         return (GetContainerOptions) options.children(from, to);
      }

   }
}
