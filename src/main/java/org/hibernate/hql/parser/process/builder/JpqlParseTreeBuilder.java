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
import org.hibernate.hql.parser.antlr.JpqlLexer;
import org.hibernate.hql.parser.antlr.JpqlParser;
import org.hibernate.hql.parser.process.ParseTreePrinter;
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

		String jpql = (String) query;
		// Build the lexer
		JpqlLexer jpqlLexer = new JpqlLexer( new ANTLRInputStream( jpql ) );

		// Build the parser...
		final JpqlParser parser = new JpqlParser( new CommonTokenStream( jpqlLexer ) );

		// this part would be protected by logging most likely.  Print the parse tree structure
		if ( debugEnabled ) {
			ParseTreeWalker.DEFAULT.walk( new ParseTreePrinter( parser ), parser.statement() );
			jpqlLexer.reset();
			parser.reset();
		}

		return parser;
	}
}
