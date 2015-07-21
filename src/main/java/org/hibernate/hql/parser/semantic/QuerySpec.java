/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser.semantic;

import org.hibernate.hql.parser.process.ParsingContext;
import org.hibernate.hql.parser.semantic.from.FromClause;
import org.hibernate.hql.parser.semantic.from.FromClauseContainer;
import org.hibernate.hql.parser.semantic.groupBy.GroupByClause;
import org.hibernate.hql.parser.semantic.groupBy.GroupByClauseContainer;
import org.hibernate.hql.parser.semantic.predicate.WhereClause;
import org.hibernate.hql.parser.semantic.predicate.WhereClauseContainer;
import org.hibernate.hql.parser.semantic.select.SelectClause;

/**
 * Defines the commonality between a root query and a subquery.
 *
 * @author Steve Ebersole
 */
public class QuerySpec implements FromClauseContainer, WhereClauseContainer, GroupByClauseContainer {
	private final ParsingContext parsingContext;

	private final FromClause fromClause;
	private final SelectClause selectClause;
	private final WhereClause whereClause;
	// group-by
	private final GroupByClause groupByClause;

	// having


	public QuerySpec(
			ParsingContext parsingContext,
			FromClause fromClause,
			SelectClause selectClause,
			WhereClause whereClause,
			GroupByClause groupByClause) {
		this.parsingContext = parsingContext;
		this.fromClause = fromClause;
		this.selectClause = selectClause;
		this.whereClause = whereClause;
		this.groupByClause = groupByClause;
	}

	public SelectClause getSelectClause() {
		return selectClause;
	}

	@Override
	public FromClause getFromClause() {
		return fromClause;
	}

	@Override
	public WhereClause getWhereClause() {
		return whereClause;
	}

	@Override
	public GroupByClause getGroupByClause() {
		return groupByClause;
	}
}
