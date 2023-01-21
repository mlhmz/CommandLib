package de.f4bii.commandlib;

import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class ReflectionHelper {

    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @SneakyThrows
    public MethodHandle getMethodHandle(Method method) {
        return lookup.findVirtual(method.getDeclaringClass(), method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()));
    }

}
