package com.origin.library.infrastructure.exp;

@FunctionalInterface
public interface LambdaQueryWrapperFunction {
	LambdaQueryWrapper exec(LambdaQueryWrapper wrapper);
}
