/*
 * Copyright 2019 lolnet.co.nz
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

package nz.co.lolnet.executor.listener;

import com.google.gson.JsonObject;
import nz.co.lolnet.executor.Executor;
import nz.co.lolnet.executor.manager.ExecutionManager;
import nz.co.lolnet.executor.util.Reference;
import nz.co.lolnet.servermanager.common.manager.ServiceManager;
import nz.co.lolnet.servermanager.common.util.Toolbox;
import redis.clients.jedis.JedisPubSub;

public class RedisListener extends JedisPubSub {
    
    @Override
    public void onMessage(String channel, String message) {
        try {
            if (!channel.equals(Reference.ID)) {
                Executor.getInstance().getLogger().warn("Unsupported channel: {}", channel);
                return;
            }
            
            JsonObject jsonObject = Toolbox.parseJson(message, JsonObject.class).orElse(null);
            if (jsonObject == null) {
                Executor.getInstance().getLogger().warn("Failed to parse message: {}", message);
                return;
            }
            
            String state = Toolbox.parseJson(jsonObject.get("state"), String.class).orElse("Unknown");
            ServiceManager.schedule(() -> ExecutionManager.process(state), 0L, 0L);
        } catch (Exception ex) {
            Executor.getInstance().getLogger().error("Encountered an error processing {}::onMessage", getClass().getSimpleName(), ex);
        }
    }
}