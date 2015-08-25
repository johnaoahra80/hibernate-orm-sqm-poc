package org.hibernate.query.parser;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by johara on 21/08/15.
 */
@StaticMetamodel(Book.class)
public class Book_ {
	public static volatile SingularAttribute<Book, Author> author;
}