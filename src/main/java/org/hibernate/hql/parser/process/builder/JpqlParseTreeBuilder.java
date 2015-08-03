/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser.process.builder;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.hibernate.hql.parser.ParsingException;
import org.hibernate.hql.parser.antlr.HqlLexer;
import org.hibernate.hql.parser.antlr.HqlParser;
import org.hibernate.hql.parser.process.HqlParseTreePrinter;
import org.hibernate.hql.parser.process.ParseTreeBuilder;

import java.text.ParseException;

/**
 * @author Steve Ebersole
 */
public class JpqlParseTreeBuilder implements ParseTreeBuilder {
	/**
	 * Singleton access
	 */
	public static final JpqlParseTreeBuilder INSTANCE = new JpqlParseTreeBuilder();

	private boolean debugEnabled = true;

	@Override
	public Parser parse(Object query) throws ParseException{
		if (!(query instanceof String) ) {
			throw new ParsingException( "query is not of type: " + String.class.getName() );
		}

		String hql = (String) query;
		// Build the lexer
		HqlLexer hqlLexer = new HqlLexer( new ANTLRInputStream( hql ) );

		// Build the parser...
		final HqlParser parser = new HqlParser( new CommonTokenStream( hqlLexer ) );

		// this part would be protected by logging most likely.  Print the parse tree structure
		if ( debugEnabled ) {
			ParseTreeWalker.DEFAULT.walk( new HqlParseTreePrinter( parser ), parser.statement() );
			hqlLexer.reset();
			parser.reset();
		}

		return parser;
	}
}
