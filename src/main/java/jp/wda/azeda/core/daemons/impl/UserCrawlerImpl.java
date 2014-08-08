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

package jp.wda.azeda.core.daemons.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.wda.azeda.core.daemons.UserCrawler;
import jp.wda.azeda.dao.RetweetedTweet;
import jp.wda.azeda.dao.RetweetedTweetsDao;
import jp.wda.azeda.dao.StoredAccessToken;
import jp.wda.azeda.dao.StoredAccessTokensDao;

import org.seasar.framework.container.annotation.tiger.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Revision$
 * @date		$Date$
 */
public class UserCrawlerImpl extends Thread implements UserCrawler {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public UserCrawlerImpl() {
		super();
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/** ロガー */
	protected static Logger log = LoggerFactory.getLogger(RetweetsCrawlerImpl.class);

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXXX */
	private long interval = 60000;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public long getInterval(){ return interval; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setInterval(long s){ interval = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private long expired = 0;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public long getExpired(){ return expired; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setExpired(long s){ expired = s; }

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXXX */
	private RetweetedTweetsDao dao = null;
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setDao(RetweetedTweetsDao s){ dao = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private StoredAccessTokensDao accessTokensDao = null;
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setAccessTokensDao(StoredAccessTokensDao s){ accessTokensDao = s; }

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXXX */
	private long userID = 0;
	/** {@inheritDoc} */
	@Override
	public void attacheUser(long s){ userID = s; }

	/**
	 *
	 */
	public void run() {
		while(true) {
			try {
				crawl();
			} catch(TwitterException ex) {
				log.error("", ex);
			} catch(RuntimeException ex) {
				log.error("", ex);
			}

			try {
				sleep(interval);
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}

			if(!alive) {
				log.debug("UserCrawlerImpl terminated.");
				return;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void stopCrawl() {
		alive = false;
	}
	private boolean alive = true;


	/**
	 *
	 */
	@Aspect("aop.requiredTx")
	public void crawl() throws TwitterException {
		StoredAccessToken storedAccessToken = accessTokensDao.getAccessTokenByUserID(userID);
		if(storedAccessToken == null) { return; }
		if(!alive) { return; }
		log.debug("crawl user retweets!!");

		Twitter twitter = new TwitterFactory().getInstance();
		AccessToken  accessToken  = new AccessToken(storedAccessToken.getAccessToken(), storedAccessToken.getAccessTokenSecret());
		twitter.setOAuthAccessToken(accessToken);

		List<Status> retweets = twitter.getRetweetsOfMe();
		if(!alive) { return; }

		List<RetweetedTweet> registered = dao.getAll(userID, new Timestamp(System.currentTimeMillis() - 365*24*60*60*1000L));
		List<Long> registeredIDs = new ArrayList<Long>();
		for(RetweetedTweet t : registered) { registeredIDs.add(t.getTweetID()); }
		if(!alive) { return; }

		List<RetweetedTweet> dtos = new ArrayList<RetweetedTweet>();
		long oldest = System.currentTimeMillis() - expired*24*60*60*1000L;
		for(Status s : retweets) {
			if(registeredIDs.contains(s.getId()))   { continue; }
			if(s.getCreatedAt().getTime() < oldest) { continue; }

			RetweetedTweet dto = new RetweetedTweet();
			dto.setTweetID(s.getId());
			dto.setUserID(userID);
			dto.setTweettext(s.getText());
			dto.setCreatedat(new Timestamp(s.getCreatedAt().getTime()));
			dtos.add(dto);
		}
		if(!alive) { return; }
		dao.insertBatch(dtos);
	}

}
