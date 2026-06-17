package me.mitgaa23.util_lib.database.annotations;

import tableGenerator.proxy.SQLTypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	/**
	 * @return The column name.
	 */
	String value();

	/**
	 * @return The index in the order of the columns, behaviour, when left as default is undefined.
	 */
	int index() default 0;

	/**
	 * @return True, if the key is a primary key.
	 */
	boolean primary() default false;

	/**
	 * @return The handler that handles the type.
	 * @see SQLTypeHandler
	 */
	Class<? extends SQLTypeHandler> handler() default SQLTypeHandler.class;

	/**
	 * @return The params of the handler.
	 */
	int[] params() default {};
}
