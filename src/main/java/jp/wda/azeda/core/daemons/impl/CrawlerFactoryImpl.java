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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wda.azeda.core.daemons.Crawler;
import jp.wda.azeda.core.daemons.CrawlerFactory;
import jp.wda.azeda.core.daemons.RetweetsCrawler;
import jp.wda.azeda.core.daemons.UserCrawler;
import jp.wda.azeda.dao.StoredAccessToken;
import jp.wda.azeda.dao.StoredAccessTokensDao;

import org.seasar.framework.container.S2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Revision$
 * @date		$Date$
 */
public class CrawlerFactoryImpl implements CrawlerFactory {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public CrawlerFactoryImpl() {
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/** ロガー */
	protected static Logger log = LoggerFactory.getLogger(CrawlerFactoryImpl.class);

	private HashMap<Long, Crawler> userCrawlers = new HashMap<Long, Crawler>();
	private HashMap<Long, Crawler> retweetsCrawlers = new HashMap<Long, Crawler>();

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
	private S2Container container = null;
	/**
	 * XXXXを設定します。<BR>
	 * @param s 設定値<BR>
	 */
	public void setContainer(S2Container s){ container = s; }

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public void start() {
		List<StoredAccessToken> tokens = accessTokensDao.getAll();
		for(StoredAccessToken token : tokens) {
			startCrawl(token.getUserID());
		}
	}


	/** {@inheritDoc} */
	@Override
	public void startCrawl(long userID) {
		startCrawl(userID, "USCrawler-", UserCrawler.class,     userCrawlers);
		startCrawl(userID, "RTCrawler-", RetweetsCrawler.class, retweetsCrawlers);
	}

	/**
	 *
	 * @param userID
	 */
	private void startCrawl(long userID, String threadNamePrefix, Class<? extends Crawler> crawlerClazz, Map<Long, Crawler> map) {
		if(map.containsKey(userID)) {
			Crawler crawler = map.remove(userID);
			crawler.stopCrawl();
			crawler = null;
		}

		Crawler crawler = (Crawler)container.getComponent(crawlerClazz);
		crawler.setName(threadNamePrefix + userID);
		crawler.setDaemon(true);
		crawler.attacheUser(userID);
		crawler.setExpired(expired);
		map.put(userID, crawler);

		crawler.start();
	}

}
