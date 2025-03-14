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

package com.github.thierrysquirrel.container;

import com.github.thierrysquirrel.annotation.MessageListener;
import com.github.thierrysquirrel.annotation.RocketListener;
import com.github.thierrysquirrel.autoconfigure.RocketProperties;
import com.github.thierrysquirrel.core.factory.ThreadPoolFactory;
import com.github.thierrysquirrel.core.factory.execution.ConsumerFactoryExecution;
import com.github.thierrysquirrel.core.factory.execution.MethodFactoryExecution;
import com.github.thierrysquirrel.core.factory.execution.ThreadPoolExecutorExecution;
import com.github.thierrysquirrel.core.utils.AnnotatedMethodsUtils;
import com.github.thierrysquirrel.core.utils.JsonHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ClassName: RocketConsumerContainer
 * Description:
 * date: 2019/4/26 21:40
 *
 * @author ThierrySquirrel
 * @since JDK 1.8
 */

public class RocketConsumerContainer implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	private RocketProperties rocketProperties;
	private JsonHelper jsonHelper;
	public RocketConsumerContainer(RocketProperties rocketProperties, JsonHelper jsonHelper) {
		this.rocketProperties = rocketProperties;
		this.jsonHelper = jsonHelper;
	}

	@PostConstruct
	public void initialize() {


		ThreadPoolExecutor threadPoolExecutor = ThreadPoolFactory.createConsumeThreadPoolExecutor(rocketProperties);

		applicationContext.getBeansWithAnnotation(RocketListener.class).forEach((beanName, bean) -> {

			RocketListener rocketListener = bean.getClass().getAnnotation(RocketListener.class);
			AnnotatedMethodsUtils.getMethodAndAnnotation(bean, MessageListener.class).
					forEach((method, consumerListener) -> {
						ConsumerFactoryExecution consumerFactoryExecution = new ConsumerFactoryExecution(rocketProperties,
								rocketListener, consumerListener, new MethodFactoryExecution(bean, method, jsonHelper));
						ThreadPoolExecutorExecution.statsThread(threadPoolExecutor, consumerFactoryExecution);
					});
		});
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
