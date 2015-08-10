/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.hibernate.hql.parser.antlr.HqlParser;
import org.hibernate.hql.parser.process.builder.HqlParseTreeBuilder;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Simple tests to make sure the basics are working and to see a visual of the parse tree.
 *
 * @author Steve Ebersole
 */
public class JpqlParserTest {
	@Test
	public void justTestIt() throws Exception {
		HqlParser parser = HqlParseTreeBuilder.INSTANCE.parseHql( "select generatedAlias0 from Book as generatedAlias0 order by generatedAlias0.id desc" );

		Collection<ParseTree> fromClauses = XPath.findAll( parser.statement(), "//fromClause", parser );
		assertEquals( 1, fromClauses.size() );
	}

}
