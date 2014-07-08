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

package jp.wda.azeda.core.actions;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import jp.wda.azeda.core.logics.TwitterService;
import jp.wda.mutsumi.framework.ActionBase;
import twitter4j.TwitterException;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Revision$
 * @date		$Date$
 */
@Path("/")
public class RootAction extends ActionBase {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public RootAction() {
		super();
	}

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXXX */
	private TwitterService twitterService;
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setTwitterService(TwitterService s){ twitterService = s; }

	// インスタンス変数 /////////////////////////////////////////////////////////////////
	//                                                                 Instance Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	@Context
	public HttpServletRequest request;

	/**
	 *
	 */
	@Context
	public HttpServletResponse response;

	/**
	 *
	 */
	@Context
	public UriInfo uriInfo;

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	@GET
	@Path("/check/{account}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkAuthorized(
			  @PathParam("account")				String account
			  ) throws TwitterException {
		URI uri = uriInfo.getBaseUriBuilder().path("/authorized/").build();
		return Response.ok(twitterService.checkAuthorized(account, uri.toString())).build();
	}

	/**
	 *
	 * @return
	 * @throws TwitterException
	 */
	@GET
	@Path("/authorized/{requestid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response authorized(
			  @PathParam("requestid")			String requestid
			, @QueryParam("oauth_verifier")		String oauthVerifier
			) throws TwitterException {

		twitterService.registerAccessToken(requestid, oauthVerifier);
		URI uri = uriInfo.getBaseUriBuilder().path("../index.html").build();
		return Response.seeOther(uri).build();
	}

	@GET
	@Path("/tweets/{account}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegisteredTweets(
			  @PathParam("account")				String account
			, @QueryParam("limit")				String limit
			) {
		return Response.ok(twitterService.getRegisteredTweets(account, 0, limit)).build();
	}

	@GET
	@Path("/tweets/{account}/{page:[0-9]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegisteredTweets(
			  @PathParam("account")				String account
			, @PathParam("page")				int page
			, @QueryParam("limit")				String limit
			) {
		return Response.ok(twitterService.getRegisteredTweets(account, page, limit)).build();
	}

	@GET
	@Path("/responses/{id:[0-9]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResponses(
			  @PathParam("id")					long tweetID
			){
		return Response.ok(twitterService.getResponses(tweetID)).build();
	}

}
