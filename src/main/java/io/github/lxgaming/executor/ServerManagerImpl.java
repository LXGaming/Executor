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

import io.github.lxgaming.executor.util.Reference;
import io.github.lxgaming.servermanager.api.Platform;
import io.github.lxgaming.servermanager.api.ServerManager;
import io.github.lxgaming.servermanager.api.configuration.Config;
import io.github.lxgaming.servermanager.api.network.NetworkHandler;
import io.github.lxgaming.servermanager.api.network.Packet;
import io.github.lxgaming.servermanager.api.util.Logger;
import io.github.lxgaming.servermanager.common.manager.PacketManager;
import io.github.lxgaming.servermanager.common.util.LoggerImpl;

import java.util.Optional;

public class ServerManagerImpl extends ServerManager {
    
    public ServerManagerImpl() {
        super();
        this.platformType = Platform.Type.UNKNOWN;
        this.logger = new LoggerImpl();
    }
    
    public static boolean init() {
        if (getInstance() != null) {
            return false;
        }
        
        ServerManagerImpl serverManager = new ServerManagerImpl();
        serverManager.getLogger()
                .add(Logger.Level.INFO, Executor.getInstance().getLogger()::info)
                .add(Logger.Level.WARN, Executor.getInstance().getLogger()::warn)
                .add(Logger.Level.ERROR, Executor.getInstance().getLogger()::error)
                .add(Logger.Level.DEBUG, Executor.getInstance().getLogger()::debug);
        
        ServerManagerImpl.getInstance().loadServerManager();
        return true;
    }
    
    @Override
    public void loadServerManager() {
        PacketManager.buildPackets();
    }
    
    @Override
    protected void reloadServerManager() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void shutdownServerManager() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean registerNetworkHandler(Class<? extends NetworkHandler> networkHandlerClass) {
        return PacketManager.registerNetworkHandler(networkHandlerClass);
    }
    
    @Override
    public void sendRequest(Packet packet) {
        packet.setSender(Reference.ID);
        packet.setType(Packet.Type.REQUEST);
        sendPacket(packet);
    }
    
    @Override
    public void sendResponse(Packet packet) {
        packet.setSender(Reference.ID);
        packet.setType(Packet.Type.RESPONSE);
        sendPacket(packet);
    }
    
    @Override
    public void sendPacket(Packet packet) {
        PacketManager.sendPacket(Reference.ID, packet, Executor.getInstance().getRedisService()::publish);
    }
    
    public static ServerManagerImpl getInstance() {
        return (ServerManagerImpl) ServerManager.getInstance();
    }
    
    @Override
    public Optional<? extends Config> getConfig() {
        throw new UnsupportedOperationException();
    }
}