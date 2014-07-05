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
import java.util.HashMap;
import java.util.List;

import jp.wda.azeda.core.daemons.RetweetsCrawler;
import jp.wda.azeda.dao.Retweet;
import jp.wda.azeda.dao.RetweetedTweet;
import jp.wda.azeda.dao.RetweetedTweetsDao;
import jp.wda.azeda.dao.Retweeter;
import jp.wda.azeda.dao.RetweetersDao;
import jp.wda.azeda.dao.RetweetsDao;

import org.seasar.framework.container.annotation.tiger.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Paging;
import twitter4j.ResponseList;
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
public class RetweetsCrawlerImpl extends Thread implements RetweetsCrawler {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public RetweetsCrawlerImpl() {
		super();
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/** ロガー */
	protected static Logger log = LoggerFactory.getLogger(RetweetsCrawlerImpl.class);

	private List<RetweetedTweet> tweets;

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXXX */
	private long retweetsInterval = 60000;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public long getRetweetsInterval(){ return retweetsInterval; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setRetweetsInterval(long s){ retweetsInterval = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private long timelineInterval = 5000;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public long getTimelineInterval(){ return timelineInterval; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setTimelineInterval(long s){ timelineInterval = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private int timelinePages = 5;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public int getTimelinePages(){ return timelinePages; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setTimelinePages(int s){ timelinePages = s; }

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

	/* ***********************************************************************>> */
	/** XXXX */
	private long retweetExpired = 60;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public long getRetweetExpired(){ return retweetExpired; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setRetweetExpired(long s){ retweetExpired = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private int maxStores = 0;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public int getMaxStores(){ return maxStores; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setMaxStores(int s){ maxStores = s; }

	// プロパティ ///////////////////////////////////////////////////////////////////////
	//                                                                      Properties //
	/////////////////////////////////////////////////////////////////////////////////////

	/* ***********************************************************************>> */
	/** XXXX */
	private RetweetedTweetsDao dao = null;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public RetweetedTweetsDao getDao(){ return dao; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setDao(RetweetedTweetsDao s){ dao = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private RetweetersDao retweetersDao = null;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public RetweetersDao getRetweetersDao(){ return retweetersDao; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setRetweetersDao(RetweetersDao s){ retweetersDao = s; }

	/* ***********************************************************************>> */
	/** XXXX */
	private RetweetsDao rtDao = null;
	/**
	 * XXXXを取得します。<BR>
	 * @return XXXX
	 */
	public RetweetsDao getRTDao(){ return rtDao; }
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setRTDao(RetweetsDao s){ rtDao = s; }

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
		tweets = new ArrayList<RetweetedTweet>();

		while(true) {
			try {
				crawl();
			} catch(TwitterException ex) {
				log.error("", ex);
			} catch(RuntimeException ex) {
				log.error("", ex);
			}

			try {
				sleep(retweetsInterval);
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
			if(!alive) {
				log.debug("RetweetsCrawler terminated." + tweets.size());
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
		log.debug("crawl retweets!!" + tweets.size());
		if(tweets.size() == 0) { loadTweets(); }
		if(tweets.size() == 0) { return; }

		RetweetedTweet tweet = tweets.remove(0);
		log.debug("check tweet " + tweet.getTweetID());

		Twitter  twitter = new TwitterFactory().getInstance();
		AccessToken  accessToken  = new AccessToken(tweet.getAccessToken().getAccessToken(), tweet.getAccessToken().getAccessTokenSecret());
		twitter.setOAuthAccessToken(accessToken);

		ResponseList<Status> rt = twitter.getRetweets(tweet.getTweetID());
		if(!alive) { return; }
		if(rt == null || rt.size() == 0) { return; }

		HashMap<Long, Status> retweets = new HashMap<Long, Status>();
		for(Status status : rt) {
			retweets.put(status.getUser().getId(), status);
		}

		List<Retweeter> registered = retweetersDao.getRetweeterIDs(tweet.getTweetID());
		for(Retweeter retweeter : registered) {
			long registeredUserID = retweeter.getRetweeterID();
			if(retweets.containsKey(registeredUserID)) {
				retweets.remove(registeredUserID);
				continue;
			}
		}
		if(retweets.size() == 0) { return; }



		List<Retweet> dtos = new ArrayList<Retweet>();
		for(Status status : retweets.values()) {
			long retweeter = status.getUser().getId();
			long oldest = System.currentTimeMillis() - retweetExpired*60*1000;
			if(status.getCreatedAt().getTime() < oldest) {
				log.info(tweet.getTweetID() + " retweet by " + status.getUser().getScreenName() + " は " + retweetExpired + "分以上経過しているので除外します。");
				insertRetweeter(twitter.getId(), tweet.getTweetID(), retweeter);
				continue;
			}



			try {
				sleep(timelineInterval);
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
			if(!alive) { return; }



			ResponseList<Status> userTimeline = loadRetweetersTweets(twitter, tweet.getTweetID(), retweeter, 1);
			if(!alive) { return; }
			if(userTimeline == null) { continue; }

			int responses = -1;
			for(int i = userTimeline.size() - 1; i >= 0; i--) {
				Status s = userTimeline.get(i);
				if(responses < 0 && s.isRetweet() && s.getRetweetedStatus().getId() == tweet.getTweetID()) {
					responses = 0;
					continue;
				}
				if(responses < 0) { continue; }

				log.debug(s.getId() + ":" + s.getCreatedAt() + "(" + s.isRetweet() + ") " + s.getText());

				Retweet retweet = new Retweet();
				retweet.setTweetID(s.getId());
				retweet.setRetweetTo(tweet.getTweetID());
				retweet.setRetweeterID(retweeter);
				retweet.setScreenName(status.getUser().getScreenName());
				dtos.add(retweet);

				if(responses++ > maxStores) { break; }
			}

			if(responses > 0) {
				insertRetweeter(twitter.getId(), tweet.getTweetID(), retweeter);
			}
		}

		if(dtos.size() > 0) { rtDao.insertBatch(dtos); }
 	}

	/**
	 *
	 */
	private void loadTweets() {
		log.debug("get retweets.");
		tweets = dao.getAll(userID, new Timestamp(System.currentTimeMillis() - expired*24*60*60*1000));
	}

	/**
	 *
	 * @param twitter
	 * @param retweetTo
	 * @param retweeter
	 * @param page
	 * @return
	 * @throws TwitterException
	 */
	private ResponseList<Status> loadRetweetersTweets(Twitter twitter, long retweetTo, long retweeter, int page) throws TwitterException {
		if(!alive) { return null; }
		if(page > timelinePages) {
			log.info(retweetTo + " retweet by " + retweeter + " は " + (timelinePages*100) + "件以上検索しても発見できないので除外します。");
			insertRetweeter(twitter.getId(), retweetTo, retweeter);
			return null;
		}

		Paging paging = new Paging(retweetTo);
		paging.setPage(page);
		paging.setCount(100);
		ResponseList<Status> userTimeline = twitter.getUserTimeline(retweeter, paging);

		if(userTimeline.size() == 0) { return null; }

		for(Status s : userTimeline) {
			if(s.isRetweet()) {
				if(s.getRetweetedStatus().getId() != retweetTo) { continue; }
				log.debug("found!!");
				return userTimeline;
			}
		}

		try {
			sleep(timelineInterval);
		} catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		if(!alive) { return null; }

		return loadRetweetersTweets(twitter, retweetTo, retweeter, page + 1);
	}

	/**
	 *
	 * @param userID
	 * @param retweetTo
	 * @param retweeter
	 * @throws TwitterException
	 */
	private void insertRetweeter(long userID, long retweetTo, long retweeter) throws TwitterException {
		if(!alive) { return; }
		Retweeter dto = new Retweeter();
		dto.setRetweeterID(retweeter);
		dto.setRetweetTo(retweetTo);
		dto.setUserID(userID);

		try {
			retweetersDao.insert(dto);
		} catch(RuntimeException ex) {
			log.error(userID + "を登録済みに設定できません。" , ex);
		}
	}

}
