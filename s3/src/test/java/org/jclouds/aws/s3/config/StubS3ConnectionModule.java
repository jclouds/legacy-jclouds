package org.jclouds.aws.s3.config;

import com.google.inject.AbstractModule;
import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.StubS3Connection;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
@S3ConnectionModule
public class StubS3ConnectionModule extends AbstractModule {
    protected void configure() {
        bind(S3Connection.class).to(StubS3Connection.class);
    }
}
