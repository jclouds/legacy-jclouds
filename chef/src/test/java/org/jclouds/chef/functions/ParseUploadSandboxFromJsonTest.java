package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.jclouds.chef.domain.ChecksumStatus;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.Payloads;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Bytes;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseUploadSiteFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseUploadSiteFromJsonTest")
public class ParseUploadSandboxFromJsonTest {

   private ParseJson<UploadSandbox> handler;
   private Injector injector;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      injector = Guice.createInjector(new ParserModule());
      handler = injector.getInstance(Key
            .get(new TypeLiteral<ParseJson<UploadSandbox>>() {
            }));
   }

   public void test() {
      EncryptionService encryptionService = injector
            .getInstance(EncryptionService.class);
      assertEquals(
            handler.apply(new HttpResponse(200, "ok", Payloads
                  .newPayload(ParseUploadSandboxFromJsonTest.class
                        .getResourceAsStream("/upload-site.json")))),
            new UploadSandbox(
                  URI
                        .create("https://api.opscode.com/organizations/jclouds/sandboxes/d454f71e2a5f400c808d0c5d04c2c88c"),
                  ImmutableMap
                        .<List<Byte>, ChecksumStatus> of(
                              Bytes
                                    .asList(encryptionService
                                          .fromHex("0c5ecd7788cf4f6c7de2a57193897a6c")),
                              new ChecksumStatus(
                                    URI
                                          .create("https://s3.amazonaws.com/opscode-platform-production-data/organization-486ca3ac66264fea926aa0b4ff74341c/sandbox-d454f71e2a5f400c808d0c5d04c2c88c/checksum-0c5ecd7788cf4f6c7de2a57193897a6c?AWSAccessKeyId=AKIAJOZTD2N26S7W6APA&Expires=1277344702&Signature=FtKyqvYEjhhEKmRY%2B0M8aGPMM7g%3D"),
                                    true),
                              Bytes
                                    .asList(encryptionService
                                          .fromHex("0189e76ccc476701d6b374e5a1a27347")),
                              new ChecksumStatus(),
                              Bytes
                                    .asList(encryptionService
                                          .fromHex("1dda05ed139664f1f89b9dec482b77c0")),
                              new ChecksumStatus()),
                  "d454f71e2a5f400c808d0c5d04c2c88c"));
   }
}
