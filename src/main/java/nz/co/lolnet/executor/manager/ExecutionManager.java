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

package nz.co.lolnet.executor.manager;

import nz.co.lolnet.executor.Executor;
import nz.co.lolnet.executor.configuration.Config;
import nz.co.lolnet.executor.configuration.category.ExecutionCategory;
import nz.co.lolnet.executor.util.Reference;
import nz.co.lolnet.servermanager.api.Platform;
import nz.co.lolnet.servermanager.api.data.User;
import nz.co.lolnet.servermanager.api.network.Packet;
import nz.co.lolnet.servermanager.api.network.packet.CommandPacket;
import nz.co.lolnet.servermanager.common.manager.PacketManager;
import nz.co.lolnet.servermanager.common.util.Toolbox;

import java.util.List;

public class ExecutionManager {
    
    public static void process(String state) {
        List<ExecutionCategory> executions = Executor.getInstance().getConfig().map(Config::getExecutions).map(map -> map.get(state)).orElse(null);
        if (executions == null) {
            Executor.getInstance().getLogger().warn("Invalid state: {}", state);
            return;
        }
        
        Executor.getInstance().getLogger().info("Processing {}", state);
        executions.forEach(ExecutionManager::process);
    }
    
    private static void process(ExecutionCategory category) {
        try {
            if (Toolbox.isBlank(category.getCommand()) || category.getType() == null) {
                Executor.getInstance().getLogger().warn("Invalid category: {} ({})", category.getCommand(), category.getType());
            } else if (category.getType() == ExecutionCategory.Type.INTERNAL) {
                List<String> arguments = Toolbox.newArrayList(category.getCommand().split(" "));
                if (arguments.size() < 2) {
                    Executor.getInstance().getLogger().warn("Invalid arguments: {}", category.getCommand());
                    return;
                }
                
                String key = arguments.remove(0);
                String value = String.join(" ", arguments);
                if (key.equalsIgnoreCase("sleep")) {
                    Thread.sleep(Long.parseLong(value));
                } else {
                    Executor.getInstance().getLogger().warn("Invalid command: {}", category.getCommand());
                }
            } else if (category.getType() == ExecutionCategory.Type.REDIS) {
                List<String> arguments = Toolbox.newArrayList(category.getCommand().split(" "));
                if (arguments.size() < 2) {
                    Executor.getInstance().getLogger().warn("Invalid arguments: {}", category.getCommand());
                    return;
                }
                
                String key = arguments.remove(0);
                String value = String.join(" ", arguments);
                CommandPacket packet = new CommandPacket(value, new User(String.format("%s v%s", Reference.NAME, Reference.VERSION), Platform.CONSOLE_UUID));
                packet.setSender(packet.getUser().getName());
                packet.setType(Packet.Type.REQUEST);
                PacketManager.sendPacket(key, packet, Executor.getInstance().getRedisService()::publish);
            } else if (category.getType() == ExecutionCategory.Type.SYSTEM) {
                Runtime.getRuntime().exec(category.getCommand());
            }
        } catch (Exception ex) {
            Executor.getInstance().getLogger().error("Encountered an error while executing {}", category.getCommand(), ex);
        }
    }
}