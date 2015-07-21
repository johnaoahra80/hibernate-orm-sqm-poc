package org.hibernate.hql.parser.semantic.groupBy;


import org.hibernate.hql.parser.process.ParsingContext;
import org.hibernate.hql.parser.semantic.expression.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johara on 16/07/15.
 */
public class GroupByClause {
	private final ParsingContext parsingContext;

	private List<GroupingValue> groupBySpecifications;

	public GroupByClause(ParsingContext parsingContext) {
		this.parsingContext = parsingContext;
	}

	public GroupByClause addGroupBySpecification(GroupingValue groupBySpecification) {
		if ( groupBySpecifications == null ) {
			groupBySpecifications = new ArrayList<GroupingValue>();
		}
		groupBySpecifications.add( groupBySpecification );
		return this;
	}

	public GroupByClause addGroupBySpecification(Expression expression) {
		addGroupBySpecification( new GroupingValue( expression ) );
		return this;
	}

	public List<GroupingValue> getGroupBySpecifications() {
		return groupBySpecifications;
	}

	public ParsingContext getParsingContext() {
		return parsingContext;
	}
}
