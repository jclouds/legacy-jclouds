package org.jclouds.snia.cdmi.v1.queryparams;

/**
 * Generate CDMI data object query parameters Example: dataObject =
 * dataApi.get(containerName,dataObjectNameIn,DataObjectQueryParams.Builder.field("parentURI"));
 * dataObject = dataApi.get(containerName,dataObjectNameIn,DataObjectQueryParams.Builder.value());
 * 
 * @author Kenneth Nagin
 */
public class DataObjectQueryParams extends CDMIObjectQueryParams {

   public DataObjectQueryParams() {
      super();
   }

   /**
    * Get CDMI data object's field value
    * 
    * @param fieldname
    * @return this
    */
   public DataObjectQueryParams field(String fieldname) {
      super.field(fieldname);
      return this;
   }

   /**
    * Get CDMI data object's metadata
    * 
    * @return this
    */
   public DataObjectQueryParams metadata() {
      super.metadata();
      return this;
   }

   /**
    * Get CDMI data object's metadata associated with prefix
    * 
    * @param prefix
    * @return this
    */
   public DataObjectQueryParams metadata(String prefix) {
      super.metadata(prefix);
      return this;
   }

   /**
    * Get CDMI data object's value with range
    * 
    * @return this
    */
   public DataObjectQueryParams value() {
      queryParams = queryParams + "value;";
      return this;
   }

   /**
    * Get CDMI data object's value within range
    * 
    * @param from
    * @param to
    * @return this
    */
   public DataObjectQueryParams value(int from, int to) {
      queryParams = queryParams + "value:" + from + "-" + to + ";";
      return this;
   }

   public static class Builder {
      public static DataObjectQueryParams field(String fieldname) {
         DataObjectQueryParams options = new DataObjectQueryParams();
         return (DataObjectQueryParams) options.field(fieldname);
      }

      public static DataObjectQueryParams metadata() {
         DataObjectQueryParams options = new DataObjectQueryParams();
         return (DataObjectQueryParams) options.metadata();
      }

      public static DataObjectQueryParams metadata(String prefix) {
         DataObjectQueryParams options = new DataObjectQueryParams();
         return (DataObjectQueryParams) options.metadata(prefix);
      }

      public static DataObjectQueryParams value() {
         DataObjectQueryParams options = new DataObjectQueryParams();
         return (DataObjectQueryParams) options.value();
      }

      public static DataObjectQueryParams value(int from, int to) {
         DataObjectQueryParams options = new DataObjectQueryParams();
         return (DataObjectQueryParams) options.value(from, to);
      }

   }

}
