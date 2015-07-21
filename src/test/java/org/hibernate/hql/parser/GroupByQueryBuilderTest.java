/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.hibernate.hql.parser.antlr.HqlParser;
import org.hibernate.hql.parser.process.ExplicitFromClauseIndexer;
import org.hibernate.hql.parser.process.HqlParseTreeBuilder;
import org.hibernate.hql.parser.process.SemanticQueryBuilder;
import org.hibernate.hql.parser.semantic.QuerySpec;
import org.hibernate.hql.parser.semantic.SelectStatement;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * @author Steve Ebersole
 */
public class GroupByQueryBuilderTest {
	@Test
	public void simpleGroupByTest() {
		final ParsingContextTestingImpl parsingContext = new ParsingContextTestingImpl();

		final HqlParser parser = HqlParseTreeBuilder.INSTANCE.parseHql( "select cat.color, sum(cat.weight), count(cat) from Cat cat group by cat.color" );

		final ExplicitFromClauseIndexer explicitFromClauseIndexer = new ExplicitFromClauseIndexer( new ParsingContextTestingImpl() );
		ParseTreeWalker.DEFAULT.walk( explicitFromClauseIndexer, parser.statement() );

		parser.reset();

		SemanticQueryBuilder semanticQueryBuilder = new SemanticQueryBuilder( parsingContext, explicitFromClauseIndexer );
		SelectStatement selectStatement = semanticQueryBuilder.visitSelectStatement( parser.selectStatement() );
		QuerySpec querySpec = selectStatement.getQuerySpec();
		assertNotNull( querySpec );

		assertNotSame( 0, querySpec.getGroupByClause().getGroupBySpecifications().size() );
	}



}
