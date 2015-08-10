/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser.process;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.hql.parser.antlr.HqlParserBaseListener;

/**
 * @author Steve Ebersole
 */
public class ParseTreePrinter extends HqlParserBaseListener {
	private final Parser parser;

	private int depth = 0;

	public ParseTreePrinter(Parser parser) {
		this.parser = parser;
	}

	@Override
	public void enterEveryRule(@NotNull ParserRuleContext ctx) {
		final String ruleName = parser.getRuleNames()[ctx.getRuleIndex()];

		if ( !ruleName.endsWith( "Keyword" ) ) {
			System.out.println(
					String.format(
							"%s %s (%s) [`%s`]",
							enterRulePadding(),
							ctx.getClass().getSimpleName(),
							ruleName,
							ctx.getText()
					)
			);
		}
		super.enterEveryRule( ctx );
	}

	private String enterRulePadding() {
		return pad( depth++ ) + "->";
	}

	private String pad(int depth) {
		StringBuilder buf = new StringBuilder( 2 * depth );
		for ( int i = 0; i < depth; i++ ) {
			buf.append( "  " );
		}
		return buf.toString();
	}

	@Override
	public void exitEveryRule(@NotNull ParserRuleContext ctx) {
		super.exitEveryRule( ctx );

		final String ruleName = parser.getRuleNames()[ctx.getRuleIndex()];

		if ( !ruleName.endsWith( "Keyword" ) ) {
			System.out.println(
					String.format(
							"%s %s (%s) [`%s`]",
							exitRulePadding(),
							ctx.getClass().getSimpleName(),
							parser.getRuleNames()[ctx.getRuleIndex()],
							ctx.getText()
					)
			);
		}
	}

	private String exitRulePadding() {
		return pad( --depth ) + "<-";
	}
}
