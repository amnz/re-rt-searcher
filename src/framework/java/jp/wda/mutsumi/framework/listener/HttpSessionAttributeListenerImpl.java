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

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

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
public class HttpSessionAttributeListenerImpl implements HttpSessionAttributeListener {

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	public HttpSessionAttributeListenerImpl() {
		super();
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	/** ロガー */
	protected static Logger logger = LoggerFactory.getLogger(HttpSessionAttributeListenerImpl.class);

	// インスタンスメソッド /////////////////////////////////////////////////////////////
	//                                                                Instance Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 * @param se
	 */
	public void attributeAdded(HttpSessionBindingEvent se){
		logger.debug("Bind to SESSION \"" + se.getName() + "\"=" + se.getValue() + " : sessionid=" + se.getSession().getId());
	}

	/**
	 *
	 * @param se
	 */
	public void attributeRemoved(HttpSessionBindingEvent se){
		logger.debug("Remove from SESSION \"" + se.getName() + "\"=" + se.getValue() + " : sessionid=" + se.getSession().getId());
	}

	/**
	 *
	 * @param se
	 */
	public void attributeReplaced(HttpSessionBindingEvent se){
		logger.debug("Replace SESSION from \"" + se.getName() + "\"=" + se.getValue());
		logger.debug("Replace SESSION to   \"" + se.getName() + "\"=" + se.getSession().getAttribute(se.getName()));
	}

}
