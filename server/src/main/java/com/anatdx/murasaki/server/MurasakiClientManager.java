package com.anatdx.murasaki.server;

import com.anatdx.murasaki.server.ClientManager;

public class MurasakiClientManager extends ClientManager<MurasakiConfigManager> {

    public MurasakiClientManager(MurasakiConfigManager configManager) {
        super(configManager);
    }
}
