package org.jclouds.vcloud.binders;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULTCPUCOUNT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULTMEMORY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULTNETWORK;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.io.IOUtils;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.util.Providers;

/**
 * Tests behavior of {@code BindInstantiateVAppTemplateParamsToXmlEntity}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.BindInstantiateVAppTemplateParamsToXmlEntityTest")
public class BindInstantiateVAppTemplateParamsToXmlEntityTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         bind(String.class).annotatedWith(Jsr330.named(PROPERTY_VCLOUD_DEFAULTCPUCOUNT))
                  .toProvider(Providers.<String> of(null));
         bind(String.class).annotatedWith(Jsr330.named(PROPERTY_VCLOUD_DEFAULTMEMORY)).toProvider(
                  Providers.<String> of(null));
         bind(String.class).annotatedWith(Jsr330.named(PROPERTY_VCLOUD_DEFAULTNETWORK)).toProvider(
                  Providers.<String> of(null));
      }

      @SuppressWarnings("unused")
      @Singleton
      @Provides
      @Named("InstantiateVAppTemplateParams")
      String provideInstantiateVAppTemplateParams() throws IOException {
         InputStream is = getClass().getResourceAsStream("/InstantiateVAppTemplateParams.xml");
         return Utils.toStringAndClose(is);
      }
   });

   public void testApplyInputStream() throws IOException {
      String expected = IOUtils.toString(getClass().getResourceAsStream("/newvapp-hosting.xml"));
      Multimap<String, String> headers = Multimaps.synchronizedMultimap(HashMultimap
               .<String, String> create());
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(new Object[] {}).atLeastOnce();
      expect(request.getFirstHeaderOrNull("Content-Type")).andReturn(null).atLeastOnce();
      expect(request.getHeaders()).andReturn(headers).atLeastOnce();
      request.setEntity(expected);
      replay(request);

      BindInstantiateVAppTemplateParamsToXmlEntity binder = injector
               .getInstance(BindInstantiateVAppTemplateParamsToXmlEntity.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("name", "CentOS 01");
      map.put("template", "https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/3");
      map.put("count", "1");
      map.put("megabytes", "512");
      map.put("network", "https://vcloud.safesecureweb.com/network/1990");
      binder.bindToRequest(request, map);
      assertEquals(headers.get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals(headers.get(HttpHeaders.CONTENT_LENGTH), Collections.singletonList("901"));

   }
}
