package org.hibernate.hql.parser.process;

import org.antlr.v4.runtime.Parser;

import java.text.ParseException;

/**
 * Created by johara on 30/07/15.
 */
public interface ParseTreeBuilder {
	Parser parse(Object query) throws ParseException;
}
