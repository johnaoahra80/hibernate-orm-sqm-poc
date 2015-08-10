parser grammar JpqlParser2;

options {
	tokenVocab=JpqlLexer2;
}

@header {
/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.hql.parser.antlr;
}

@members {
	/**
	 * Determine if the text of the new upcoming token LT(1), if one, matches
	 * the passed argument.  Internally calls doesUpcomingTokenMatchAny( 1, checks )
	 */
	protected boolean doesUpcomingTokenMatchAny(String... checks) {
		return doesUpcomingTokenMatchAny( 1, checks );
	}

	/**
	 * Determine if the text of the new upcoming token LT(offset), if one, matches
	 * the passed argument.
	 */
	protected boolean doesUpcomingTokenMatchAny(int offset, String... checks) {
		final Token token = retrieveUpcomingToken( offset );
		if ( token != null ) {
			if ( token.getType() == IDENTIFIER ) {
				// todo : is this really a check we want?

				final String textToValidate = token.getText();
				if ( textToValidate != null ) {
					for ( String check : checks ) {
						if ( textToValidate.equalsIgnoreCase( check ) ) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	protected Token retrieveUpcomingToken(int offset) {
		if ( null == _input ) {
			return null;
		}
		return _input.LT( offset );
	}

	protected String retrieveUpcomingTokenText(int offset) {
		Token token = retrieveUpcomingToken( offset );
		return token == null ? null : token.getText();
	}
}

statement
	: (select_statement | update_statement  | delete_statement ) EOF
	;

select_statement :
	select_clause from_clause (where_clause)? (groupby_clause)?
    (having_clause) (orderby_clause)?
	;

update_statement
	: update_clause (where_clause)?
	;

delete_statement
	: delete_clause (where_clause)?
	;

from_clause
	: fromKeyword identifier_declaration
        (COMMA (identifier_declaration |
            collection_member_declaration))*
    ;

identifier_declaration
	: range_variable_declaration ( join | fetch_join )*
	;

range_variable_declaration
	: abstract_schema_name (AS)?
        IDENTIFIER
    ;

join
	: join_spec join_association_path_expression (AS)?
        IDENTIFIER
    ;

fetch_join
	: join_spec fetchKeyword join_association_path_expression
    ;

association_path_expression
	:collection_valued_path_expression |
        single_valued_association_path_expression
    ;

join_spec
	: (leftKeyword (outerKeyword)? |innerKeyword)? joinKeyword
    ;

join_association_path_expression
	: join_collection_valued_path_expression |
        join_single_valued_association_path_expression
	;

join_collection_valued_path_expression
	: IDENTIFIER.collection_valued_association_field
	;

join_single_valued_association_path_expression
	: IDENTIFIER.single_valued_association_field
	;

collection_member_declaration
	: inKeyword (collection_valued_path_expression) (asKeyword)?
        IDENTIFIER
    ;

single_valued_path_expression
	: state_field_path_expression |
        single_valued_association_path_expression
    ;

state_field_path_expression
	: (IDENTIFIER |
    single_valued_association_path_expression).state_field
    ;

single_valued_association_path_expression
	: IDENTIFIER.(single_valued_association_field.)*
    single_valued_association_field
    ;

collection_valued_path_expression
	: IDENTIFIER.(single_valued_association_field.)*
    collection_valued_association_field
    ;

state_field
	: (embedded_class_state_field.)*simple_state_field
	;

update_clause
	: updateKeyword abstract_schema_name ((asKeyword)?
    IDENTIFIER)? setKeyword update_item (COMMA update_item)*
    ;

update_item
	: (IDENTIFIER.)?(state_field |
    single_valued_association_field) EQUAL new_value
    ;

new_value :
     simple_arithmetic_expression |
    string_primary |
    datetime_primary |
    boolean_primary |
    enum_primary simple_entity_expression |
    nullKeyword
    ;

delete_clause
	: deleteKeyword fromKeyword abstract_schema_name ((asKeyword)? IDENTIFIER)?
	;

select_clause
	: selectKeyword (distinctKeyword)? select_expression (COMMA select_expression)*
	;

select_expression
	: single_valued_path_expression |
    aggregate_expression |
    IDENTIFIER |
    objectKeyword(IDENTIFIER) |
    constructor_expression
    ;

constructor_expression
	: newKeyword constructor_name(constructor_item (COMMA constructor_item)*)
	;

constructor_item
	: single_valued_path_expression
	| aggregate_expression
	;

aggregate_expression
	: (AVG |MAX |MIN |SUM) ((DISTINCT)? state_field_path_expression)
	| COUNT ((DISTINCT)? IDENTIFIER
	| state_field_path_expression
	| single_valued_association_path_expression)
	;

where_clause
	: WHERE conditional_expression
	;

groupby_clause
	: groupByKeyword groupby_item (COMMA groupby_item)*
	;

groupby_item
	: single_valued_path_expression
	;

having_clause
	: HAVING conditional_expression
	;

orderby_clause
	: orderByKeyword orderby_item (COMMA orderby_item)*
	;

orderby_item
	: state_field_path_expression (ascendingKeyword |descendingKeyword)?
	;

subquery
	: simple_select_clause subquery_from_clause
    (where_clause)? (groupby_clause)? (having_clause)?
    ;

subquery_from_clause
	:  fromKeyword subselect_identifier_declaration
        (COMMA subselect_identifier_declaration)*
        ;

subselect_identifier_declaration
	: identifier_declaration
	| association_path_expression (AS)? IDENTIFIER
	| collection_member_declaration
	;

simple_select_clause
	: SELECT (DISTINCT)? simple_select_expression
	;

simple_select_expression
	: single_valued_path_expression
	| aggregate_expression
	| IDENTIFIER
	;

conditional_expression
	: conditional_expression OR conditional_term
	| conditional_term
	;

conditional_term
	: conditional_factor
	| conditional_term AND conditional_factor
	;

conditional_factor
	: (notKeyword)? conditional_primary
	;

conditional_primary
	: simple_cond_expression
// removed indirect left recursion for conditional_expression
// need to evaluate the impact of this for gramatical correctness
//	| (conditional_expression)
	;

simple_cond_expression
	: comparison_expression
	| between_expression
	| like_expression
	| in_expression
	| null_comparison_expression
	| empty_collection_comparison_expression
	| collection_member_expression
	| exists_expression
	;

between_expression
	: arithmetic_expression (notKeyword)? BETWEEN arithmetic_expression AND arithmetic_expression
	| string_expression (notKeyword)? BETWEEN string_expression AND string_expression
	| datetime_expression (notKeyword)? BETWEEN datetime_expression AND datetime_expression
	;

in_expression
	: state_field_path_expression (notKeyword)? IN (in_item (COMMA in_item)*  | subquery)
	;

in_item
	: literal
	| input_parameter
	;

like_expression
	: string_expression (notKeyword)? LIKE pattern_value (ESCAPE escape_character)?
	;

null_comparison_expression
	: (single_valued_path_expression | input_parameter) IS (notKeyword)? NULL
	;

empty_collection_comparison_expression
	: collection_valued_path_expression IS (notKeyword)? EMPTY
	;

collection_member_expression
	: entity_expression (notKeyword)? memberKeyword (ofKeyword)? collection_valued_path_expression
	;

exists_expression
	: (notKeyword)? EXISTS (subquery)
	;

all_or_any_expression
	: (ALL |ANY |SOME) (subquery)
	;

comparison_expression
	: string_expression comparison_operator (string_expression | all_or_any_expression)
	| boolean_expression EQUAILTY_OPERATOR (boolean_expression | all_or_any_expression)
	| enum_expression EQUAILTY_OPERATOR (enum_expression | all_or_any_expression)
	| datetime_expression comparison_operator (datetime_expression | all_or_any_expression)
	| entity_expression EQUAILTY_OPERATOR (entity_expression | all_or_any_expression)
	| arithmetic_expression comparison_operator (arithmetic_expression | all_or_any_expression)
	;

//TODO: define in the lexer
comparison_operator
	: EQUAL
	| NOT_EQUAL
    |  GREATER
    |  GREATER_EQUAL
    |  LESS
    |  LESS_EQUAL
    ;

arithmetic_expression
	: simple_arithmetic_expression
	| (subquery)
	;

simple_arithmetic_expression
	: simple_arithmetic_expression ( PLUS | MINUS ) arithmetic_term
	| arithmetic_term
	;

arithmetic_term
	: arithmetic_factor | arithmetic_term ( ASTERISK | SLASH ) arithmetic_factor
	;

arithmetic_factor
	: ( PLUS | MINUS )? arithmetic_primary
	;

arithmetic_primary
	: state_field_path_expression
	| NUMERIC_LITERAL
// removed indirect left recursion, need to evaluate impact of removing this term
//	| (simple_arithmetic_expression)
	| input_parameter
	| functions_returning_numerics
	| aggregate_expression
	;

string_expression
	: string_primary | (subquery)
	;

string_primary
	: state_field_path_expression
	| STRING_LITERAL
	| input_parameter
	| functions_returning_strings
	| aggregate_expression
	;

datetime_expression
	: datetime_primary
	| (subquery)
	;

datetime_primary
	: state_field_path_expression
	| input_parameter
	| functions_returning_datetime
	| aggregate_expression
	;

boolean_expression
	: boolean_primary
	| (subquery)
	;

boolean_primary
	: state_field_path_expression
	| BOOLEAN_LITERAL
	| input_parameter
	;

 enum_expression
 	: enum_primary
 	| (subquery)
 	;

enum_primary
	: state_field_path_expression
	| ENUM_LITERAL
	| input_parameter
	;

entity_expression
	: single_valued_association_path_expression
	| simple_entity_expression
	;

simple_entity_expression
	: IDENTIFIER
	| input_parameter
	;

functions_returning_numerics
	: LENGTH(string_primary)
	| LOCATE(string_primary COMMA string_primary(COMMA  simple_arithmetic_expression)?)
	| ABS(simple_arithmetic_expression)
	| SQRT(simple_arithmetic_expression)
	| MOD(simple_arithmetic_expression COMMA simple_arithmetic_expression)
	| SIZE(collection_valued_path_expression)
	;

functions_returning_datetime
	: CURRENT_DATE
	| CURRENT_TIME
	| CURRENT_TIMESTAMP
	;

//TODO: fix rule functions_returning_strings contains an optional block with at least one alternative that can match an empty string
functions_returning_strings
	: concatKeyword(string_primary COMMA string_primary)
	| substringKeyword(string_primary COMMA  simple_arithmetic_expression COMMA simple_arithmetic_expression)
	| trimKeyword(((trim_specification)? (trim_character)? fromKeyword)? string_primary)
	| lowerKeyword(string_primary)
	| upperKeyword(string_primary)
	;

trim_specification
	: LEADING
	| TRAILING
	| BOTH
	;

//TODO: enum literal
literal
	: STRING_LITERAL
	| LONG_LITERAL
	| BIG_INTEGER_LITERAL
	| HEX_LITERAL
	| OCTAL_LITERAL
	| FLOAT_LITERAL
	| DOUBLE_LITERAL
	| BIG_DECIMAL_LITERAL
	| BOOLEAN_LITERAL
	| ENUM_LITERAL
	;

input_parameter
	: INPUT_PARAMETER
	;

//TODO; double check that this is correct
abstract_schema_name
	: IDENTIFIER
	;

single_valued_association_field
	: IDENTIFIER(DOT IDENTIFIER)*
    ;

constructor_name
	:  IDENTIFIER(DOT IDENTIFIER)*
	;
escape_character
	: ESCAPE_SEQUENCE
	;

//TODO: figure out what these syntax are
collection_valued_association_field
	:
	;

embedded_class_state_field
	:
	;

simple_state_field
	:
	;

pattern_value
	:
	;

trim_character
	:
	;

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// Key word rules


absKeyword
	: {doesUpcomingTokenMatchAny("abs")}? IDENTIFIER
	;

allKeyword
	: {doesUpcomingTokenMatchAny("all")}? IDENTIFIER
	;

andKeyword
	: {doesUpcomingTokenMatchAny("and")}? IDENTIFIER
	;

asKeyword
	: {doesUpcomingTokenMatchAny("as")}? IDENTIFIER
	;

ascendingKeyword
	: {(doesUpcomingTokenMatchAny("ascending","asc"))}? IDENTIFIER
	;

betweenKeyword
	: {doesUpcomingTokenMatchAny("between")}? IDENTIFIER
	;

bitLengthKeyword
	: {doesUpcomingTokenMatchAny("bit_length")}? IDENTIFIER
	;

bothKeyword
	: {doesUpcomingTokenMatchAny("both")}? IDENTIFIER
	;

castkeyword
	: {doesUpcomingTokenMatchAny("cast")}? IDENTIFIER
	;

charLengthKeyword
	: {doesUpcomingTokenMatchAny("character_length","char_length")}? IDENTIFIER
	;

classKeyword
	: {doesUpcomingTokenMatchAny("class")}? IDENTIFIER
	;

collateKeyword
	: {doesUpcomingTokenMatchAny("collate")}? IDENTIFIER
	;

concatKeyword
	: {doesUpcomingTokenMatchAny("concat")}? IDENTIFIER
	;

crossKeyword
	: {doesUpcomingTokenMatchAny("cross")}? IDENTIFIER
	;

currentDateKeyword
	: {doesUpcomingTokenMatchAny("current_date")}? IDENTIFIER
	;

currentTimeKeyword
	: {doesUpcomingTokenMatchAny("current_time")}? IDENTIFIER
	;

currentTimestampKeyword
	: {doesUpcomingTokenMatchAny("current_timestamp")}? IDENTIFIER
	;

dayKeyword
	: {doesUpcomingTokenMatchAny("day")}? IDENTIFIER
	;

deleteKeyword
	: {doesUpcomingTokenMatchAny("delete")}? IDENTIFIER
	;

descendingKeyword
	: {(doesUpcomingTokenMatchAny("descending","desc"))}? IDENTIFIER
	;

distinctKeyword
	: {doesUpcomingTokenMatchAny("distinct")}? IDENTIFIER
	;

elementsKeyword
	: {doesUpcomingTokenMatchAny("elements")}? IDENTIFIER
	;

emptyKeyword
	: {doesUpcomingTokenMatchAny("escape")}? IDENTIFIER
	;

escapeKeyword
	: {doesUpcomingTokenMatchAny("escape")}? IDENTIFIER
	;

exceptKeyword
	: {doesUpcomingTokenMatchAny("except")}? IDENTIFIER
	;

extractKeyword
	: {doesUpcomingTokenMatchAny("extract")}? IDENTIFIER
	;

fetchKeyword
	: {doesUpcomingTokenMatchAny("fetch")}? IDENTIFIER
	;

fromKeyword
	: {doesUpcomingTokenMatchAny("from")}? IDENTIFIER
	;

fullKeyword
	: {doesUpcomingTokenMatchAny("full")}? IDENTIFIER
	;

functionKeyword
	: {doesUpcomingTokenMatchAny("function")}? IDENTIFIER
	;

groupByKeyword
	: {doesUpcomingTokenMatchAny(1,"group") && doesUpcomingTokenMatchAny(2,"by")}? IDENTIFIER IDENTIFIER
	;

havingKeyword
	: {doesUpcomingTokenMatchAny("having")}? IDENTIFIER
	;

hourKeyword
	: {doesUpcomingTokenMatchAny("hour")}? IDENTIFIER
	;

inKeyword
	: {doesUpcomingTokenMatchAny("in")}? IDENTIFIER
	;

indexKeyword
	: {doesUpcomingTokenMatchAny("index")}? IDENTIFIER
	;

innerKeyword
	: {doesUpcomingTokenMatchAny("inner")}? IDENTIFIER
	;

isKeyword
	: {doesUpcomingTokenMatchAny("is")}? IDENTIFIER
	;

intersectKeyword
	: {doesUpcomingTokenMatchAny("intersect")}? IDENTIFIER
	;

joinKeyword
	: {doesUpcomingTokenMatchAny("join")}? IDENTIFIER
	;

leadingKeyword
	: {doesUpcomingTokenMatchAny("leading")}?  IDENTIFIER
	;

leftKeyword
	: {doesUpcomingTokenMatchAny("left")}?  IDENTIFIER
	;

lengthKeyword
	: {doesUpcomingTokenMatchAny("length")}?  IDENTIFIER
	;

likeKeyword
	: {doesUpcomingTokenMatchAny("like")}?  IDENTIFIER
	;

locateKeyword
	: {doesUpcomingTokenMatchAny("locate")}?  IDENTIFIER
	;

lowerKeyword
	: {doesUpcomingTokenMatchAny("lower")}?  IDENTIFIER
	;

memberKeyword
	: {doesUpcomingTokenMatchAny(1,"member")}?  IDENTIFIER IDENTIFIER
	;

memberOfKeyword
	: {doesUpcomingTokenMatchAny(1,"member") && doesUpcomingTokenMatchAny(2,"of")}?  IDENTIFIER IDENTIFIER
	;

minuteKeyword
	: {doesUpcomingTokenMatchAny("minute")}?  IDENTIFIER
	;

modKeyword
	: {doesUpcomingTokenMatchAny("mod")}?  IDENTIFIER
	;

monthKeyword
	: {doesUpcomingTokenMatchAny("month")}?  IDENTIFIER
	;

newKeyword
	: {doesUpcomingTokenMatchAny("new")}?  IDENTIFIER
	;

notKeyword
	: {doesUpcomingTokenMatchAny("not")}?  IDENTIFIER
	;

nullKeyword
	: {doesUpcomingTokenMatchAny("null")}?  IDENTIFIER
	;

objectKeyword
	: {doesUpcomingTokenMatchAny("object")}?  IDENTIFIER
	;

octetLengthKeyword
	: {doesUpcomingTokenMatchAny("octet_length")}?  IDENTIFIER
	;

ofKeyword
	: {doesUpcomingTokenMatchAny(1,"of")}?  IDENTIFIER IDENTIFIER
	;

onKeyword
	: {doesUpcomingTokenMatchAny("on")}?  IDENTIFIER
	;

orKeyword
	: {doesUpcomingTokenMatchAny("or")}?  IDENTIFIER
	;

orderByKeyword
	: {(doesUpcomingTokenMatchAny("order") && doesUpcomingTokenMatchAny(2, "by"))}?  IDENTIFIER IDENTIFIER
	;

outerKeyword
	: {doesUpcomingTokenMatchAny("outer")}?  IDENTIFIER
	;

positionKeyword
	: {doesUpcomingTokenMatchAny("position")}?  IDENTIFIER
	;

propertiesKeyword
	: {doesUpcomingTokenMatchAny("properties")}?  IDENTIFIER
	;

rightKeyword
	: {doesUpcomingTokenMatchAny("right")}?  IDENTIFIER
	;

secondKeyword
	: {doesUpcomingTokenMatchAny("second")}?  IDENTIFIER
	;

selectKeyword
	: {doesUpcomingTokenMatchAny("select")}?  IDENTIFIER
	;

setKeyword
	: {doesUpcomingTokenMatchAny("set")}?  IDENTIFIER
	;

sizeKeyword
	: {doesUpcomingTokenMatchAny("size")}?  IDENTIFIER
	;

sqrtKeyword
	: {doesUpcomingTokenMatchAny("sqrt")}?  IDENTIFIER
	;

substringKeyword
	: {doesUpcomingTokenMatchAny("substring")}?  IDENTIFIER
	;

timezoneHourKeyword
	: {doesUpcomingTokenMatchAny("timezone_hour")}?  IDENTIFIER
	;

timezoneMinuteKeyword
	: {doesUpcomingTokenMatchAny("timezone_minute")}?  IDENTIFIER
	;

trailingKeyword
	: {doesUpcomingTokenMatchAny("trailing")}?  IDENTIFIER
	;

treatKeyword
	: {doesUpcomingTokenMatchAny("treat")}?  IDENTIFIER
	;

trimKeyword
	: {doesUpcomingTokenMatchAny("trim")}?  IDENTIFIER
	;

unionKeyword
	: {doesUpcomingTokenMatchAny("union")}?  IDENTIFIER
	;

updateKeyword
	: {doesUpcomingTokenMatchAny("update")}?  IDENTIFIER
	;

upperKeyword
	: {doesUpcomingTokenMatchAny("upper")}?  IDENTIFIER
	;

whereKeyword
	: {doesUpcomingTokenMatchAny("where")}?  IDENTIFIER
	;

withKeyword
	: {doesUpcomingTokenMatchAny("with")}?  IDENTIFIER
	;

yearKeyword
	: {doesUpcomingTokenMatchAny("year")}?  IDENTIFIER
	;
