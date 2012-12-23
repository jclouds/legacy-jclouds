package org.jclouds.snia.cdmi.v1.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Optional get CDMI object options Note: We use BaseHttpRequestOptions.pathSuffix to include the
 * CDMI query parameters rather than queryParam because the CDMI specification is not
 * following the standard usage. This is the summary of the CDMI specification: To read one or more
 * requested fields from an existing CDMI container object, one of the following requests shall be
 * performed: GET <root URI>/<ContainerName>/<TheContainerName>/?<fieldname>;<fieldname>;... GET
 * <root URI>/<ContainerName>/<TheContainerName>/?children:<range>;... GET <root
 * URI>/<ContainerName>/<TheContainerName>/?metadata:<prefix>;...
 * 
 * For example: GET /MyContainer/?parentURI;children HTTP/1.1 GET
 * /MyContainer/?childrenrange;children:0-2 HTTP/1.1
 * 
 * To read one or more requested fields from an existing data object, one of the following requests
 * shall be performed: GET <root URI>/<ContainerName>/<DataObjectName>?<fieldname>;<fieldname>;...
 * GET <root URI>/<ContainerName>/<DataObjectName>?value:<range>;... GET <root
 * URI>/<ContainerName>/<DataObjectName>?metadata:<prefix>;...
 * 
 * @author Kenneth Nagin
 */
public class GetCDMIObjectOptions extends BaseHttpRequestOptions {

   public GetCDMIObjectOptions() {
      this.pathSuffix = "?";
   }

   /**
    * Get CDMI data object's field
    * 
    * @param fieldname
    * @return this
    */
   public GetCDMIObjectOptions field(String fieldname) {
      this.pathSuffix = this.pathSuffix + fieldname + ";";
      return this;
   }

   /**
    * Get CDMI data object's metadata
    * 
    * @return this
    */
   public GetCDMIObjectOptions metadata() {
      this.pathSuffix = this.pathSuffix + "metadata;";
      return this;
   }

   /**
    * Get CDMI data object's metadata
    * 
    * @param prefix
    * @return this
    */
   public GetCDMIObjectOptions metadata(String prefix) {
      this.pathSuffix = this.pathSuffix + "metadata:" + prefix + ";";
      return this;
   }

   public static class Builder {
      public static GetCDMIObjectOptions field(String fieldname) {
         GetCDMIObjectOptions options = new GetCDMIObjectOptions();
         return (GetCDMIObjectOptions) options.field(fieldname);
      }

      public static GetCDMIObjectOptions metadata() {
         GetCDMIObjectOptions options = new GetCDMIObjectOptions();
         return (GetCDMIObjectOptions) options.metadata();
      }

      public static GetCDMIObjectOptions metadata(String prefix) {
         GetCDMIObjectOptions options = new GetCDMIObjectOptions();
         return (GetCDMIObjectOptions) options.metadata(prefix);
      }

   }
}
