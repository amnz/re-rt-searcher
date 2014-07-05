/* *****************************************************************************
 * Copyright (C) Movatoss co.,ltd.
 *      http://movatoss.jp/
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */

package jp.wda.mutsumi.framework;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.mvc.Viewable;
import org.glassfish.jersey.server.mvc.jsp.internal.LocalizationMessages;
import org.glassfish.jersey.server.mvc.spi.AbstractTemplateProcessor;
import org.glassfish.jersey.server.mvc.spi.ResolvedViewable;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Revision$
 * @date		$Date$
 */
@SuppressWarnings("all")
public class MayaaTemplateProcessor extends AbstractTemplateProcessor<String> {

	@Inject
	private Provider<Ref<HttpServletRequest>> requestProviderRef;
	@Inject
	private Provider<Ref<HttpServletResponse>> responseProviderRef;
	@Inject
	private Provider<ContainerRequest> containerRequestProvider;

	/**
	 * Create an instance of this processor with injected {@link Configuration config} and
	 * (optional) {@link ServletContext servlet context}.
	 *
	 * @param config configuration to configure this processor from.
	 * @param servletContext (optional) servlet context to obtain template resources from.
	 */
	@Inject
	public MayaaTemplateProcessor(final Configuration config, final ServletContext servletContext) {
		super(config, servletContext, "html", "html");
	}

	@Override
	protected String resolve(final String templatePath, final Reader reader) throws Exception {
		return templatePath;
	}

	@Override
	public void writeTo(String templateReference, Viewable viewable, MediaType mediaType,
			final MultivaluedMap<String, Object> httpHeaders, final OutputStream out) throws IOException {

		if (!(viewable instanceof ResolvedViewable)) {
			// This should not happen with default MVC message body writer implementation
			throw new IllegalArgumentException(LocalizationMessages.ERROR_VIEWABLE_INCORRECT_INSTANCE());
		}

		// SPI could supply instance of ResolvedViewable but we would like to keep the backward
		// compatibility, so the cast is here.
		ResolvedViewable resolvedViewable = (ResolvedViewable) viewable;


		TracingLogger tracingLogger = TracingLogger.getInstance(containerRequestProvider.get().getPropertiesDelegate());
		if (tracingLogger.isLogEnabled(MvcJspEvent.JSP_FORWARD)) {
			tracingLogger.log(MvcJspEvent.JSP_FORWARD, templateReference, resolvedViewable.getModel());
		}

		final RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(templateReference);
		if (dispatcher == null) {
			throw new ContainerException(LocalizationMessages.NO_REQUEST_DISPATCHER_FOR_RESOLVED_PATH(templateReference));
		}

		final RequestDispatcher wrapper = new MayaaRequestDispatcherWrapper(dispatcher, getBasePath(), resolvedViewable);

		// OutputStream and Writer for HttpServletResponseWrapper.
		final ServletOutputStream responseStream = new ServletOutputStream() {
			@Override
			public void write(final int b) throws IOException {
				out.write(b);
			}
		};
		final PrintWriter responseWriter = new PrintWriter(new OutputStreamWriter(responseStream));

		try {
			wrapper.forward(requestProviderRef.get().get(), new HttpServletResponseWrapper(responseProviderRef.get().get()) {

				@Override
				public ServletOutputStream getOutputStream() throws IOException {
					return responseStream;
				}

				@Override
				public PrintWriter getWriter() throws IOException {
					return responseWriter;
				}
			});
		} catch (Exception e) {
			throw new ContainerException(e);
		} finally {
			responseWriter.flush();
		}
	}

	/**
	 * MVC-JSP side tracing events.
	 */
	private static enum MvcJspEvent implements TracingLogger.Event {
		JSP_FORWARD(TracingLogger.Level.SUMMARY, "MVC", "Forwarding view to JSP page [%s], model %s");

		private final TracingLogger.Level level;
		private final String category;
		private final String messageFormat;

		private MvcJspEvent(TracingLogger.Level level, String category, String messageFormat) {
			this.level = level;
			this.category = category;
			this.messageFormat = messageFormat;
		}

		@Override
		public String category() {
			return category;
		}

		@Override
		public TracingLogger.Level level() {
			return level;
		}

		@Override
		public String messageFormat() {
			return messageFormat;
		}
	}

}
