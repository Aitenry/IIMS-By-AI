package com.toryu.iims.ai.tools.calculator;

interface BiFunction<T, U, R> {
    R apply(T t, U u);
}