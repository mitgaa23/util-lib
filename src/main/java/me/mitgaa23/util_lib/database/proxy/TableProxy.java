package me.mitgaa23.util_lib.database.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class TableProxy {
	public static final InvocationHandler INVOCATION_HANDLER = (proxy, method, args) -> {
		if (!method.isDefault()) {
			throw new UnsupportedOperationException("Method %s is not a default method in interface %s.".formatted(method.getName(), method.getDeclaringClass()));
		}

		return InvocationHandler.invokeDefault(proxy, method, args);
	};

	private TableProxy() {
	}

	@SuppressWarnings("unchecked")
	public static <T extends SQLTypeHandler> T getProxy(Class<T> annotationClass) {
		return (T) Proxy.newProxyInstance(annotationClass.getClassLoader(),
		                                  new Class<?>[]{annotationClass},
		                                  INVOCATION_HANDLER
		);
	}
}
