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

package jp.wda.azeda.core.models;

import java.io.Serializable;

import jp.wda.azeda.dao.Retweet;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Revision$
 * @date		$Date$
 */
public class Tweet implements Serializable {
	/** */
	private static final long serialVersionUID = -7431667688601211263L;

	public Tweet() {
		super();
	}

	public Tweet(Retweet t) {
		super();

		this.screenName = t.getScreenName();
		this.ID = "" + t.getTweetID();
	}

	public String screenName;
	public String ID;

}
