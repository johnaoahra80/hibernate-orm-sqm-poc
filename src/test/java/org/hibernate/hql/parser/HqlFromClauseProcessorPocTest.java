/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser;

import org.hibernate.hql.parser.antlr.HqlParser;
import org.hibernate.hql.parser.process.ExplicitFromClauseIndexer;
import org.hibernate.hql.parser.process.builder.HqlParseTreeBuilder;
import org.hibernate.hql.parser.process.ImplicitAliasGenerator;
import org.hibernate.hql.parser.semantic.JoinType;
import org.hibernate.hql.parser.semantic.from.FromClause;
import org.hibernate.hql.parser.semantic.from.FromElement;
import org.hibernate.hql.parser.semantic.from.QualifiedAttributeJoinFromElement;
import org.hibernate.hql.parser.semantic.from.FromElementSpace;

import org.junit.Test;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Initial work on a "from clause processor"
 *
 * @author Steve Ebersole
 */
public class HqlFromClauseProcessorPocTest {
	@Test
	public void testSimpleFrom() throws Exception {
		final HqlParser parser = HqlParseTreeBuilder.INSTANCE.parseHql( "select a.b from Something a" );
		final ExplicitFromClauseIndexer explicitFromClauseIndexer = processFromClause( parser );
		final FromClause fromClause1 = explicitFromClauseIndexer.getRootFromClause();
		assertNotNull( fromClause1 );
		assertEquals( 0, fromClause1.getChildFromClauses().size() );
		assertEquals( 1, fromClause1.getFromElementSpaces().size() );
		FromElementSpace space1 = fromClause1.getFromElementSpaces().get( 0 );
		assertNotNull( space1 );
		assertNotNull( space1.getRoot() );
		assertEquals( 0, space1.getJoins().size() );
		FromElement fromElement = fromClause1.findFromElementByAlias( "a" );
		assertNotNull( fromElement );
		assertSame( fromElement, space1.getRoot() );
	}

	private ExplicitFromClauseIndexer processFromClause(HqlParser parser) {
		final ExplicitFromClauseIndexer explicitFromClauseIndexer = new ExplicitFromClauseIndexer( new ParsingContextTestingImpl() );
		ParseTreeWalker.DEFAULT.walk( explicitFromClauseIndexer, parser.statement() );
		return explicitFromClauseIndexer;
	}

	@Test
	public void testMultipleSpaces() throws Exception {
		final HqlParser parser = HqlParseTreeBuilder.INSTANCE.parseHql( "select a.b from Something a, SomethingElse b" );
		final ExplicitFromClauseIndexer explicitFromClauseIndexer = processFromClause( parser );
		final FromClause fromClause1 = explicitFromClauseIndexer.getRootFromClause();
		assertNotNull( fromClause1 );
		assertEquals( 0, fromClause1.getChildFromClauses().size() );
		assertEquals( 2, fromClause1.getFromElementSpaces().size() );
		FromElementSpace space1 = fromClause1.getFromElementSpaces().get( 0 );
		FromElementSpace space2 = fromClause1.getFromElementSpaces().get( 1 );
		assertNotNull( space1.getRoot() );
		assertEquals( 0, space1.getJoins().size() );
		assertNotNull( space2.getRoot() );
		assertEquals( 0, space2.getJoins().size() );
		FromElement fromElementA = fromClause1.findFromElementByAlias( "a" );
		assertNotNull( fromElementA );
		FromElement fromElementB = fromClause1.findFromElementByAlias( "b" );
		assertNotNull( fromElementB );
		assertNotEquals( fromElementA, fromElementB );
	}

	@Test
	public void testImplicitAlias() throws Exception {
		final HqlParser parser = HqlParseTreeBuilder.INSTANCE.parseHql( "select b from Something" );
		final ExplicitFromClauseIndexer explicitFromClauseIndexer = processFromClause( parser );
		final FromClause fromClause1 = explicitFromClauseIndexer.getRootFromClause();
		assertNotNull( fromClause1 );
		assertEquals( 0, fromClause1.getChildFromClauses().size() );
		assertEquals( 1, fromClause1.getFromElementSpaces().size() );
		FromElementSpace space1 = fromClause1.getFromElementSpaces().get( 0 );
		assertNotNull( space1 );
		assertNotNull( space1.getRoot() );
		assertEquals( 0, space1.getJoins().size() );
		assertTrue( ImplicitAliasGenerator.isImplicitAlias( space1.getRoot().getAlias() ) );
		FromElement fromElement = fromClause1.findFromElementByAlias( space1.getRoot().getAlias() );
		assertSame( space1.getRoot(), fromElement );
	}

	@Test
	public void testCrossJoin() throws Exception {
		final HqlParser parser = HqlParseTreeBuilder.INSTANCE.parseHql( "select a.b from Something a cross join SomethingElse b" );
		final ExplicitFromClauseIndexer explicitFromClauseIndexer = processFromClause( parser );
		final FromClause fromClause1 = explicitFromClauseIndexer.getRootFromClause();
		assertNotNull( fromClause1 );
		assertEquals( 0, fromClause1.getChildFromClauses().size() );
		assertEquals( 1, fromClause1.getFromElementSpaces().size() );
		FromElementSpace space1 = fromClause1.getFromElementSpaces().get( 0 );
		assertNotNull( space1 );
		assertNotNull( space1.getRoot() );
		assertEquals( 1, space1.getJoins().size() );

		FromElement fromElementA = fromClause1.findFromElementByAlias( "a" );
		assertNotNull( fromElementA );
		assertSame( space1.getRoot(), fromElementA );

		FromElement fromElementB = fromClause1.findFromElementByAlias( "b" );
		assertNotNull( fromElementB );
		assertSame( space1.getJoins().get( 0 ), fromElementB );
	}

	@Test
	public void testSimpleImplicitInnerJoin() throws Exception {
		simpleJoinAssertions(
				HqlParseTreeBuilder.INSTANCE.parseHql( "select a.basic from Something a join a.entity c" ),
				JoinType.INNER
		);
	}

	private void simpleJoinAssertions(HqlParser parser, JoinType joinType) {
		final ExplicitFromClauseIndexer explicitFromClauseIndexer = processFromClause( parser );
		final FromClause fromClause1 = explicitFromClauseIndexer.getRootFromClause();
		assertNotNull( fromClause1 );
		assertEquals( 0, fromClause1.getChildFromClauses().size() );
		assertEquals( 1, fromClause1.getFromElementSpaces().size() );
		FromElementSpace space1 = fromClause1.getFromElementSpaces().get( 0 );
		assertNotNull( space1 );
		assertNotNull( space1.getRoot() );
		assertEquals( 1, space1.getJoins().size() );

		FromElement fromElementA = fromClause1.findFromElementByAlias( "a" );
		assertNotNull( fromElementA );
		assertSame( space1.getRoot(), fromElementA );

		FromElement fromElementC = fromClause1.findFromElementByAlias( "c" );
		assertNotNull( fromElementC );
		assertSame( space1.getJoins().get( 0 ), fromElementC );
		QualifiedAttributeJoinFromElement join = (QualifiedAttributeJoinFromElement) fromElementC;
		assertEquals( joinType, join.getJoinType() );
		assertEquals( "c", join.getAlias() );
		assertEquals( "entity", join.getJoinedAttributeDescriptor().getName() );
	}

	@Test
	public void testSimpleExplicitInnerJoin() throws Exception {
		simpleJoinAssertions(
				HqlParseTreeBuilder.INSTANCE.parseHql( "select a.basic from Something a inner join a.entity c" ),
				JoinType.INNER
		);
	}

	@Test
	public void testSimpleExplicitOuterJoin() throws Exception {
		simpleJoinAssertions(
				HqlParseTreeBuilder.INSTANCE.parseHql( "select a.basic from Something a outer join a.entity c" ),
				JoinType.LEFT
		);
	}

	@Test
	public void testSimpleExplicitLeftOuterJoin() throws Exception {
		simpleJoinAssertions(
				HqlParseTreeBuilder.INSTANCE.parseHql( "select a.basic from Something a left outer join a.entity c" ),
				JoinType.LEFT
		);
	}

	@Test
	public void testAttributeJoinWithOnClause() throws Exception {
		final HqlParser parser = HqlParseTreeBuilder.INSTANCE.parseHql( "select a from Something a left outer join a.entity c on c.basic1 > 5 and c.basic2 < 20 " );
		final ExplicitFromClauseIndexer explicitFromClauseIndexer = processFromClause( parser );
		final FromClause fromClause1 = explicitFromClauseIndexer.getRootFromClause();
		assertNotNull( fromClause1 );
		assertEquals( 0, fromClause1.getChildFromClauses().size() );
		assertEquals( 1, fromClause1.getFromElementSpaces().size() );
		FromElementSpace space1 = fromClause1.getFromElementSpaces().get( 0 );
		assertNotNull( space1 );
		assertNotNull( space1.getRoot() );
		assertEquals( 1, space1.getJoins().size() );
		FromElement fromElementC = fromClause1.findFromElementByAlias( "c" );
		assertNotNull( fromElementC );
		assertSame( space1.getJoins().get( 0 ), fromElementC );
	}
}
