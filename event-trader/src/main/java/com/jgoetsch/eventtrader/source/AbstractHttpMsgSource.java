/*
 * Copyright (c) 2012 Jeremy Goetsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jgoetsch.eventtrader.source;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParamBean;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpMsgSource extends UrlBasedMsgSource {

	private Logger log = LoggerFactory.getLogger(getClass());
	private String username;
	private String password;
	private boolean preemtiveAuth = false;
	private HttpClient httpClient;
	private String method = "GET";
	private HttpActionFilter loginAction;

	static class PreemptiveAuth implements HttpRequestInterceptor {
		public void process(
				final HttpRequest request, 
				final HttpContext context) throws HttpException, IOException {

			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

			// If no auth scheme avaialble yet, try to initialize it preemptively
			if (authState.getAuthScheme() == null) {
				CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

				Credentials creds = credsProvider.getCredentials(
						new AuthScope(
								targetHost.getHostName(), 
								targetHost.getPort()));
				if (creds == null) {
					throw new HttpException("No credentials for preemptive authentication");
				}
				authState.setAuthScheme(new BasicScheme());
				authState.setCredentials(creds);
			}
		}
	}

	protected HttpUriRequest createRequest() {
		List<NameValuePair> params = null;
		if (getRequestParameters() != null) {
			params = new ArrayList<NameValuePair>();
			for (Map.Entry<String, Object> p : getRequestParameters().entrySet())
				params.add(new BasicNameValuePair(p.getKey(), (String)p.getValue()));
		}

		HttpRequestBase req;
		if ("POST".equalsIgnoreCase(getMethod())) {
			req = new HttpPost(getUrl());
			if (params != null) {
				try {
					UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(params, "UTF-8");
					((HttpPost)req).setEntity(reqEntity);
				} catch (UnsupportedEncodingException e) {
					log.error("Error encoding request entity", e);
				}
			}
		}
		else if (params != null) {
			req = new HttpGet(getUrl() + (getUrl().contains("?") ? "&" : "?") + URLEncodedUtils.format(params, "UTF-8"));
		}
		else {
			req = new HttpGet(getUrl());
		}

		return req;
	}

	@Override
	protected final void receiveMsgs() {
		/*SchemeRegistry supportedSchemes = new SchemeRegistry();
		SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));

		ClientConnectionManager ccm = new SingleClientConnManager(httpParams, supportedSchemes);
		DefaultHttpClient client = new DefaultHttpClient(httpParams);

		if (getUsername() != null) {
			client.getCredentialsProvider().setCredentials(
				AuthScope.ANY,
				new UsernamePasswordCredentials(getUsername(), getPassword()));

			if (isPreemtiveAuth()) {
				client.addRequestInterceptor(new PreemptiveAuth(), 0);
			}

			if (loginAction != null)
				loginAction.doAction(client, this);
		}*/

		if (httpClient == null)
			httpClient = new DefaultHttpClient();

		if (getUsername() != null) {
			((DefaultHttpClient)httpClient).getCredentialsProvider().setCredentials(
				AuthScope.ANY,
				new UsernamePasswordCredentials(getUsername(), getPassword()));
		}

		receiveMsgs(httpClient);
	}

	protected abstract void receiveMsgs(HttpClient client);

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPreemtiveAuth() {
		return preemtiveAuth;
	}

	public void setPreemtiveAuth(boolean preemtiveAuth) {
		this.preemtiveAuth = preemtiveAuth;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setLoginAction(HttpActionFilter loginAction) {
		this.loginAction = loginAction;
	}

	public HttpActionFilter getLoginAction() {
		return loginAction;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

}
