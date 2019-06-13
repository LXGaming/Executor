/*
 * Copyright 2019 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.executor;

import io.github.lxgaming.executor.configuration.Config;
import io.github.lxgaming.executor.configuration.Configuration;
import io.github.lxgaming.executor.service.RedisServiceImpl;
import io.github.lxgaming.executor.util.Reference;
import io.github.lxgaming.servermanager.common.manager.ServiceManager;
import io.github.lxgaming.servermanager.common.util.Toolbox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class Executor {
    
    private static Executor instance;
    private final Logger logger;
    private final Configuration configuration;
    private final RedisServiceImpl redisService;
    
    public Executor() {
        instance = this;
        logger = LogManager.getLogger(Reference.ID);
        configuration = new Configuration(Toolbox.getPath().orElse(null));
        redisService = new RedisServiceImpl();
    }
    
    public void loadExecutor() {
        getLogger().info("Initializing...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Thread.currentThread().setName("Shutdown Thread");
            Executor.getInstance().getLogger().info("Shutting down...");
            Executor.getInstance().getRedisService().shutdown();
            ServiceManager.shutdown();
            LogManager.shutdown();
        }));
        
        getConfiguration().loadConfiguration();
        ServerManagerImpl.init();
        ServiceManager.schedule(getRedisService());
        getConfiguration().saveConfiguration();
        getLogger().info("{} v{} has loaded", Reference.NAME, Reference.VERSION);
    }
    
    public static Executor getInstance() {
        return instance;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }
    
    public Optional<Config> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public RedisServiceImpl getRedisService() {
        return redisService;
    }
}