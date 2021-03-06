/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sql.ast.from;

import org.hibernate.sql.ast.predicate.Predicate;
import org.hibernate.sqm.query.JoinType;

/**
 * @author Steve Ebersole
 */
public class TableSpecificationGroupJoin {
	private final JoinType joinType;
	private final TableSpecificationGroup joinedGroup;
	private final Predicate predicate;

	public TableSpecificationGroupJoin(
			JoinType joinType,
			TableSpecificationGroup joinedGroup,
			Predicate predicate) {
		this.joinType = joinType;
		this.joinedGroup = joinedGroup;
		this.predicate = predicate;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public TableSpecificationGroup getJoinedGroup() {
		return joinedGroup;
	}

	public Predicate getPredicate() {
		return predicate;
	}
}
