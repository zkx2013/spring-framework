/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link DefaultResponseErrorHandler}.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Denys Ivano
 */
public class DefaultResponseErrorHandlerTests {

	private final DefaultResponseErrorHandler handler = new DefaultResponseErrorHandler();

	private final ClientHttpResponse response = mock(ClientHttpResponse.class);


	@Test
	public void hasErrorTrue() throws Exception {
		given(response.getRawStatusCode()).willReturn(HttpStatus.NOT_FOUND.value());
		assertTrue(handler.hasError(response));
	}

	@Test
	public void hasErrorFalse() throws Exception {
		given(response.getRawStatusCode()).willReturn(HttpStatus.OK.value());
		assertFalse(handler.hasError(response));
	}

	@Test
	public void handleError() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		given(response.getRawStatusCode()).willReturn(HttpStatus.NOT_FOUND.value());
		given(response.getStatusText()).willReturn("Not Found");
		given(response.getHeaders()).willReturn(headers);
		given(response.getBody()).willReturn(new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8)));

		assertThatExceptionOfType(HttpClientErrorException.class).isThrownBy(() ->
				handler.handleError(response))
			.satisfies(ex -> assertThat(ex.getResponseHeaders()).isSameAs(headers));
	}

	@Test
	public void handleErrorIOException() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		given(response.getRawStatusCode()).willReturn(HttpStatus.NOT_FOUND.value());
		given(response.getStatusText()).willReturn("Not Found");
		given(response.getHeaders()).willReturn(headers);
		given(response.getBody()).willThrow(new IOException());

		assertThatExceptionOfType(HttpClientErrorException.class).isThrownBy(() ->
				handler.handleError(response));
	}

	@Test
	public void handleErrorNullResponse() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		given(response.getRawStatusCode()).willReturn(HttpStatus.NOT_FOUND.value());
		given(response.getStatusText()).willReturn("Not Found");
		given(response.getHeaders()).willReturn(headers);

		assertThatExceptionOfType(HttpClientErrorException.class).isThrownBy(() ->
				handler.handleError(response));
	}

	@Test  // SPR-16108
	public void hasErrorForUnknownStatusCode() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		given(response.getRawStatusCode()).willReturn(999);
		given(response.getStatusText()).willReturn("Custom status code");
		given(response.getHeaders()).willReturn(headers);

		assertFalse(handler.hasError(response));
	}

	@Test // SPR-9406
	public void handleErrorUnknownStatusCode() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		given(response.getRawStatusCode()).willReturn(999);
		given(response.getStatusText()).willReturn("Custom status code");
		given(response.getHeaders()).willReturn(headers);

		assertThatExceptionOfType(UnknownHttpStatusCodeException.class).isThrownBy(() ->
				handler.handleError(response));
	}

	@Test  // SPR-17461
	public void hasErrorForCustomClientError() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		given(response.getRawStatusCode()).willReturn(499);
		given(response.getStatusText()).willReturn("Custom status code");
		given(response.getHeaders()).willReturn(headers);

		assertTrue(handler.hasError(response));
	}

	@Test
	public void handleErrorForCustomClientError() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		given(response.getRawStatusCode()).willReturn(499);
		given(response.getStatusText()).willReturn("Custom status code");
		given(response.getHeaders()).willReturn(headers);

		assertThatExceptionOfType(UnknownHttpStatusCodeException.class).isThrownBy(() ->
				handler.handleError(response));
	}

	@Test  // SPR-17461
	public void hasErrorForCustomServerError() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		given(response.getRawStatusCode()).willReturn(599);
		given(response.getStatusText()).willReturn("Custom status code");
		given(response.getHeaders()).willReturn(headers);

		assertTrue(handler.hasError(response));
	}

	@Test
	public void handleErrorForCustomServerError() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		given(response.getRawStatusCode()).willReturn(599);
		given(response.getStatusText()).willReturn("Custom status code");
		given(response.getHeaders()).willReturn(headers);

		assertThatExceptionOfType(UnknownHttpStatusCodeException.class).isThrownBy(() ->
				handler.handleError(response));
	}

	@Test  // SPR-16604
	public void bodyAvailableAfterHasErrorForUnknownStatusCode() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		TestByteArrayInputStream body = new TestByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8));

		given(response.getRawStatusCode()).willReturn(999);
		given(response.getStatusText()).willReturn("Custom status code");
		given(response.getHeaders()).willReturn(headers);
		given(response.getBody()).willReturn(body);

		assertFalse(handler.hasError(response));
		assertFalse(body.isClosed());
		assertEquals("Hello World", StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
	}


	private static class TestByteArrayInputStream extends ByteArrayInputStream {

		private boolean closed;

		public TestByteArrayInputStream(byte[] buf) {
			super(buf);
			this.closed = false;
		}

		public boolean isClosed() {
			return closed;
		}

		@Override
		public boolean markSupported() {
			return false;
		}

		@Override
		public synchronized void mark(int readlimit) {
			throw new UnsupportedOperationException("mark/reset not supported");
		}

		@Override
		public synchronized void reset() {
			throw new UnsupportedOperationException("mark/reset not supported");
		}

		@Override
		public void close() throws IOException {
			super.close();
			this.closed = true;
		}
	}

}
