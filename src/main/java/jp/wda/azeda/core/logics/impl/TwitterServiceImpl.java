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

package jp.wda.azeda.core.logics.impl;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import jp.wda.azeda.core.daemons.CrawlerFactory;
import jp.wda.azeda.core.logics.TwitterService;
import jp.wda.azeda.core.models.CheckAuthorizedResult;
import jp.wda.azeda.core.models.RegisteredTweets;
import jp.wda.azeda.core.models.Responses;
import jp.wda.azeda.core.models.Tweet;
import jp.wda.azeda.dao.Retweet;
import jp.wda.azeda.dao.RetweetedTweet;
import jp.wda.azeda.dao.RetweetedTweetsDao;
import jp.wda.azeda.dao.RetweetsDao;
import jp.wda.azeda.dao.StoredAccessToken;
import jp.wda.azeda.dao.StoredAccessTokensDao;
import jp.wda.mutsumi.framework.ServiceBase;

import org.seasar.dao.NotSingleRowUpdatedRuntimeException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Revision$
 * @date		$Date$
 */
public class TwitterServiceImpl extends ServiceBase implements TwitterService {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public TwitterServiceImpl() {
	}

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXXX */
	private StoredAccessTokensDao accessTokensDao = null;
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setAccessTokensDao(StoredAccessTokensDao s){ accessTokensDao = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private CrawlerFactory crawlerFactory = null;
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setCrawlerFactory(CrawlerFactory s){ crawlerFactory = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private RetweetedTweetsDao twDao = null;
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setTwDao(RetweetedTweetsDao s){ twDao = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private RetweetsDao resDao = null;
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setResDao(RetweetsDao s){ resDao = s; }

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/** {@inheritDoc} */
	@Override
	public CheckAuthorizedResult checkAuthorized(String account, String callbackURL) throws TwitterException {
		CheckAuthorizedResult result  = new CheckAuthorizedResult();
		StoredAccessToken at = accessTokensDao.getAccessToken(account);
		if(at != null) { return result; }

		Twitter  twitter = new TwitterFactory().getInstance();
		String requestID = UUID.randomUUID().toString();
		RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL + requestID);
		requestTokens.put(requestID, requestToken);

		result.status = -1;
		result.redirectTo = requestToken.getAuthorizationURL();

		return result;
	}
	private HashMap<String, RequestToken> requestTokens = new HashMap<String, RequestToken>();

	/** {@inheritDoc} */
	@Override
	public void registerAccessToken(String requestID, String oauthVerifier) throws TwitterException {
		Twitter twitter = new TwitterFactory().getInstance();
		RequestToken requestToken = requestTokens.get(requestID);
		AccessToken  accessToken  = twitter.getOAuthAccessToken(requestToken, oauthVerifier);

		StoredAccessToken dbToken = new StoredAccessToken();
		dbToken.setUserID(accessToken.getUserId());
		dbToken.setAccount(accessToken.getScreenName());
		dbToken.setAccessToken(accessToken.getToken());
		dbToken.setAccessTokenSecret(accessToken.getTokenSecret());
		dbToken.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

		try {
			accessTokensDao.update(dbToken);
		} catch(NotSingleRowUpdatedRuntimeException ex) {
			accessTokensDao.insert(dbToken);
		}

		crawlerFactory.startCrawl(dbToken.getUserID());
	}


	/** {@inheritDoc} */
	@Override
	public RegisteredTweets getRegisteredTweets(String account, int page, String limitRaw) {
		RegisteredTweets result = new RegisteredTweets();
		result.screenName = account;

		StoredAccessToken at = accessTokensDao.getAccessToken(account);
		if(at == null) { return result; }

		int limit = 20;
		if(limitRaw != null) {
			try {
				limit = Integer.parseInt(limitRaw);
			} catch(NumberFormatException ex) {
				limit = 20;
			}
		}

		List<RetweetedTweet> tweets = twDao.getTweets(at.getUserID(), limit, page * limit);
		for(RetweetedTweet t : tweets) {
			result.tweets.add("" + t.getTweetID());
		}

		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Responses getResponses(long tweetID) {
		Responses result = new Responses();

		List<Retweet> tweets = resDao.getResponses(tweetID);
		for(Retweet t : tweets) {
			result.tweets.add(new Tweet(t));
		}

		return result;
	}

}
