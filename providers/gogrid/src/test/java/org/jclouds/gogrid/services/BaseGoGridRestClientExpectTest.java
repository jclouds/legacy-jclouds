package org.jclouds.gogrid.services;

import org.jclouds.date.TimeStamp;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.config.GoGridRestClientModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class BaseGoGridRestClientExpectTest extends BaseRestClientExpectTest<GoGridClient> {

   public BaseGoGridRestClientExpectTest() {
      provider = "gogrid";
   }

   @RequiresHttp
   @ConfiguresRestClient
   protected static final class TestGoGridRestClientModule extends GoGridRestClientModule {

      @Override
      protected Long provideTimeStamp(@TimeStamp Supplier<Long> cache) {
         return 1267243795L;
      }
   }

   @Override
   protected Module createModule() {
      return new TestGoGridRestClientModule();
   }
}
