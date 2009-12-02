package org.jclouds.vcloud.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULTCPUCOUNT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULTMEMORY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULTNETWORK;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToStringEntity;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindInstantiateVAppTemplateParamsToXmlEntity implements MapBinder {

   private final String xmlTemplate;
   private final BindToStringEntity stringBinder;
   protected final Map<String, String> defaultParams;

   @Inject
   public BindInstantiateVAppTemplateParamsToXmlEntity(
            @Named("InstantiateVAppTemplateParams") String xmlTemplate,
            BindToStringEntity stringBinder,
            @Named(PROPERTY_VCLOUD_DEFAULTNETWORK) String defaultNetwork,
            @Named(PROPERTY_VCLOUD_DEFAULTCPUCOUNT) String defaultCpuCount,
            @Named(PROPERTY_VCLOUD_DEFAULTMEMORY) String defaultMemory) {
      this.xmlTemplate = xmlTemplate;
      this.stringBinder = stringBinder;
      this.defaultParams = Maps.newHashMap();
      this.defaultParams.put("network", defaultNetwork);
      this.defaultParams.put("count", defaultCpuCount);
      this.defaultParams.put("megabytes", defaultMemory);
   }

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
               "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest gRequest = (GeneratedHttpRequest) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");
      postParams = new HashMap<String, String>(postParams);
      postParams.putAll(defaultParams);
      addOptionsToMap(postParams, gRequest);

      String entity = xmlTemplate;
      for (Entry<String, String> entry : postParams.entrySet()) {
         entity = entity.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
      }
      stringBinder.bindToRequest(request, entity);
   }

   protected void addOptionsToMap(Map<String, String> postParams, GeneratedHttpRequest<?> gRequest) {
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof InstantiateVAppTemplateOptions) {
            InstantiateVAppTemplateOptions options = (InstantiateVAppTemplateOptions) arg;
            if (options.getCpuCount() != null) {
               postParams.put("count", options.getCpuCount());
            }
            if (options.getMegabytes() != null) {
               postParams.put("megabytes", options.getMegabytes());
            }
            if (options.getNetwork() != null) {
               postParams.put("network", options.getNetwork());
            }
         }
      }
   }

   public void bindToRequest(HttpRequest request, Object input) {
      throw new IllegalStateException("InstantiateVAppTemplateParams is needs parameters");
   }

   String ifNullDefaultTo(String value, String defaultValue) {
      return value != null ? value : checkNotNull(defaultValue, "defaultValue");
   }
}
