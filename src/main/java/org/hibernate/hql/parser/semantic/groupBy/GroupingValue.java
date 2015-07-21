package org.hibernate.hql.parser.semantic.groupBy;

import org.hibernate.hql.parser.semantic.expression.Expression;

/**
 * Created by johara on 22/07/15.
 */
public class GroupingValue {

	private final Expression groupByExpression;
	private final String collation;


	public GroupingValue(Expression sortExpression, String collation) {
		this.groupByExpression = sortExpression;
		this.collation = collation;
	}

	public GroupingValue(Expression sortExpression) {
		this( sortExpression, null);
	}


	public Expression getSortExpression() {
		return groupByExpression;
	}

	public String getCollation() {
		return collation;
	}
}
