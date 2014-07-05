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

package jp.wda.azeda.core.logics;

import jp.wda.azeda.core.models.CheckAuthorizedResult;
import jp.wda.azeda.core.models.RegisteredTweets;
import jp.wda.azeda.core.models.Responses;
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
public interface TwitterService {

	/**
	 *
	 * @param account
	 * @param callbackURL
	 * @return
	 * @throws TwitterException
	 */
	public CheckAuthorizedResult checkAuthorized(String account, String callbackURL) throws TwitterException;

	/**
	 *
	 * @param requestID
	 * @param oauthVerifier
	 * @throws TwitterException
	 */
	public void registerAccessToken(String requestID, String oauthVerifier) throws TwitterException;

	/**
	 *
	 * @param account
	 * @param page
	 * @param limit
	 * @return
	 */
	public RegisteredTweets getRegisteredTweets(String account, int page, String limit);

	/**
	 *
	 * @param tweetID
	 * @return
	 */
	public Responses getResponses(long tweetID);

}
