package org.jclouds.azure.storage.xml;

import org.jclouds.azure.storage.xml.config.AzureStorageParserModule;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.DateService;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class BaseHandlerTest {

   protected AzureStorageParserFactory parserFactory = null;
   protected DateService dateService = null;

   private Injector injector;

   @BeforeTest
   protected void setUpInjector() {
      injector = Guice.createInjector(new AzureStorageParserModule(), new ParserModule());
      parserFactory = injector.getInstance(AzureStorageParserFactory.class);
      dateService = injector.getInstance(DateService.class);
      assert parserFactory != null;
   }

   @AfterTest
   protected void tearDownInjector() {
      parserFactory = null;
      dateService = null;
      injector = null;
   }

}