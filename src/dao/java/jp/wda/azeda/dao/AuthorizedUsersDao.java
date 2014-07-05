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

import org.seasar.dao.annotation.tiger.NoPersistentProperty;
import org.seasar.dao.annotation.tiger.Query;
import org.seasar.dao.annotation.tiger.S2Dao;

/**
 *
 *
 *
 * $Id$
 * @author		$Author$
 * @revision	$Revision$
 * @date		$Date$
 */
@S2Dao(bean=AuthorizedUser.class)
public interface AuthorizedUsersDao {

	/**
	 *
	 * @param account
	 * @return
	 */
	@Query("RememberMe=?")
	public AuthorizedUser getUser(String rememberMe);

	/**
	 *
	 * @param dto
	 * @return
	 */
	@NoPersistentProperty( { "RegisteredAt" } )
	public int insert(AuthorizedUser dto);

	/**
	 *
	 * @param dto
	 * @return
	 */
	@Query("UserID=?")
	public int delete(long userID);

}
