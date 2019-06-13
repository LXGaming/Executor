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

package io.github.lxgaming.executor.configuration;

import io.github.lxgaming.executor.configuration.category.ExecutionCategory;
import io.github.lxgaming.executor.configuration.category.RedisCategory;
import io.github.lxgaming.servermanager.common.util.Toolbox;

import java.util.List;
import java.util.Map;

public class Config {
    
    private boolean debug = false;
    private RedisCategory redisCategory = new RedisCategory();
    private Map<String, List<ExecutionCategory>> executions = Toolbox.newHashMap();
    
    public boolean isDebug() {
        return debug;
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public RedisCategory getRedisCategory() {
        return redisCategory;
    }
    
    public Map<String, List<ExecutionCategory>> getExecutions() {
        return executions;
    }
}