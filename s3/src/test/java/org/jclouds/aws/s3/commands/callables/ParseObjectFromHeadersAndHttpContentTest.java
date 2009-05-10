package org.jclouds.aws.s3.commands.callables;

import static org.testng.Assert.*;
import static org.easymock.classextension.EasyMock.*;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Object.Metadata;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpHeaders;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 *
 */
@Test
public class ParseObjectFromHeadersAndHttpContentTest {
    ParseObjectFromHeadersAndHttpContent callable;
    ParseMetadataFromHeaders metadataParser;

    @BeforeMethod
    void setUp() {
	metadataParser = createMock(ParseMetadataFromHeaders.class);
	callable = new ParseObjectFromHeadersAndHttpContent(metadataParser);
    }

    @AfterMethod
    void tearDown() {
	callable = null;
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCall() throws HttpException {
	HttpResponse response = createMock(HttpResponse.class);
	expect(response.getStatusCode()).andReturn(409).atLeastOnce();
	expect(response.getContent()).andReturn(null);
	replay(response);
	callable.setResponse(response);
	callable.call();
    }

    @Test
    public void testParseContentLengthWhenContentRangeSet()
	    throws HttpException {
	HttpResponse response = createMock(HttpResponse.class);
	metadataParser.setResponse(response);
	Metadata meta = createMock(Metadata.class);
	expect(metadataParser.call()).andReturn(meta);
	expect(meta.getSize()).andReturn(-1l);
	meta.setSize(-1l);
	expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH))
		.andReturn("10485760").atLeastOnce();
	expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_RANGE))
		.andReturn("0-10485759/20232760").atLeastOnce();
	meta.setSize(20232760l);
	expect(meta.getSize()).andReturn(20232760l);

	expect(response.getStatusCode()).andReturn(200).atLeastOnce();
	expect(response.getContent()).andReturn(IOUtils.toInputStream("test"));
	replay(response);
	replay(metadataParser);
	replay(meta);

	callable.setResponse(response);
	S3Object object = callable.call();
	assertEquals(object.getContentLength(), 10485760);
	assertEquals(object.getMetadata().getSize(), 20232760);
	assertEquals(object.getContentRange(), "0-10485759/20232760");

    }

}
