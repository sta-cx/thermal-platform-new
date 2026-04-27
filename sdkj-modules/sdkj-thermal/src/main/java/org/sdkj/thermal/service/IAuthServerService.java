package org.sdkj.thermal.service;

import java.util.Map;

public interface IAuthServerService {
    Map<String, Object> login(String tenantId, String username, String password);
    Map<String, Object> miniappLogin(String tenantId, String code);
    void logout(String tokenValue);
}
