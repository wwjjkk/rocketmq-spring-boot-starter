/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.thierrysquirrel.core.factory.execution;

import com.github.thierrysquirrel.core.factory.MethodFactory;
import com.github.thierrysquirrel.core.utils.JsonHelper;
import com.github.thierrysquirrel.error.RocketException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * ClassName: MethodFactoryExecution
 * Description:
 * date: 2019/4/27 16:26
 *
 * @author ThierrySquirrel
 * @since JDK 1.8
 */
@Data
@Slf4j
public class MethodFactoryExecution {
	private Object bean;
	private Method method;
	private JsonHelper jsonHelper;

	public MethodFactoryExecution(Object bean, Method method, JsonHelper jsonHelper) {
		this.bean = bean;
		this.method = method;
		this.jsonHelper = jsonHelper;
	}

	public void methodExecution(String messageJson) throws RocketException {
		Class<?> methodParameter = MethodFactory.getMethodParameter(method);
		Object methodParameterBean = jsonHelper.fromJson(messageJson, methodParameter);
		try {
			method.invoke(bean, methodParameterBean);
		} catch (Exception e) {
			throw new RocketException(e);
		}
	}

}
