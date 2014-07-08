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

package jp.wda.azeda.dao;

import java.sql.Timestamp;
import java.util.List;

import org.seasar.dao.annotation.tiger.NoPersistentProperty;
import org.seasar.dao.annotation.tiger.Query;
import org.seasar.dao.annotation.tiger.S2Dao;
import org.seasar.dao.annotation.tiger.Sql;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Revision$
 * @date		$Date$
 */
@S2Dao(bean=RetweetedTweet.class)
public interface RetweetedTweetsDao {

	/**
	 *
	 * @param oldest
	 * @return
	 */
	@Query("RetweetedTweets.UserID=? and RetweetedTweets.RegisteredAt > ?")
	public List<RetweetedTweet> getAll(long userID, Timestamp oldest);

	/**
	 *
	 * @param oldest
	 * @return
	 */
	/*@Query("RetweetedTweets.UserID=? order by TweetID desc limit ? offset ?")*/
	@Sql("select"
			+ "	  t.tweetid "
			+ "	, t.userid "
			+ "	, t.registeredat "
			+ "	, r.lastretweetedAt "
			+ " from "
			+ "	retweetedtweets t "
			+ "		left join (select retweetto, max(registeredat) as lastretweetedAt from retweets group by retweetto) r on t.tweetid=r.retweetto "
			+ " where "
			+ "	t.userid=?"
			+ " order by "
			+ "	  r.lastretweetedAt desc NULLS LAST "
			+ "	, tweetid desc "
			+ " limit ? offset ? "
			)
	public List<RetweetedTweet> getTweets(long userID, int limit, int offset);

	/**
	 *
	 * @param dtos
	 * @return
	 */
	@NoPersistentProperty({ "RegisteredAt" })
	public int insertBatch(List<RetweetedTweet> dtos);

}
