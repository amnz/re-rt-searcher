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

package jp.wda.mutsumi.framework.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

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
public class HttpSessionListenerImpl implements HttpSessionListener {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public HttpSessionListenerImpl() {
		super();
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/** ロガー */
	protected static Logger logger = LoggerFactory.getLogger(HttpSessionListenerImpl.class);

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 * @param se
	 */
	public void sessionCreated(HttpSessionEvent se){
		logger.debug("---------------------------------------------------------------------------------");
		logger.debug("SESSION started. : sessionid=" + se.getSession().getId() + ":" + se.getSession().getMaxInactiveInterval());
		logger.debug("---------------------------------------------------------------------------------");
	}

	/**
	 *
	 * @param se
	 */
	public void sessionDestroyed(HttpSessionEvent se){
		logger.debug("---------------------------------------------------------------------------------");
		logger.debug("SESSION destroyed. : sessionid=" + se.getSession().getId());
		logger.debug("---------------------------------------------------------------------------------");
	}

}
