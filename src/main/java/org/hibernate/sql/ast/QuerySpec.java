/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sql.ast;

import org.hibernate.sql.ast.from.FromClause;

/**
 * @author Steve Ebersole
 */
public class QuerySpec {
	private final FromClause fromClause = new FromClause( this );
	// select clause
	// where clause

	public FromClause getFromClause() {
		return fromClause;
	}
}
