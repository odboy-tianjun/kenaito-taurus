/*
 *  Copyright 2021-2025 Tian Jun
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cn.odboy.context;

import cn.hutool.core.util.StrUtil;
import cn.odboy.constant.DingtalkCacheKeyConst;
import cn.odboy.infra.exception.BadRequestException;
import cn.odboy.infra.exception.util.LoggerFmtUtil;
import cn.odboy.util.DingtalkClientConfigFactory;
import cn.odboy.util.RedisUtil;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.tea.TeaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * dingtalk 客户端认证
 *
 * @author odboy
 * @date 2025-01-13
 */
@Slf4j
public class DingtalkAuthAdmin {
    @Autowired
    private DingtalkProperties properties;
    @Autowired
    private RedisUtil redisUtil;

    public String auth() throws BadRequestException {
        if (properties.getEnable() == null || !properties.getEnable()) {
            throw new BadRequestException("未启用Dingtalk功能");
        }
        try {
            String accessToken = redisUtil.get(DingtalkCacheKeyConst.ACCESS_TOKEN, String.class);
            if (StrUtil.isNotEmpty(accessToken)) {
                return accessToken;
            }
            com.aliyun.dingtalkoauth2_1_0.Client client = DingtalkClientConfigFactory.createAuthClient();
            com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest()
                    .setAppKey(properties.getAppKey())
                    .setAppSecret(properties.getAppSecret());
            GetAccessTokenResponse accessTokenResponse = client.getAccessToken(getAccessTokenRequest);
            accessToken = accessTokenResponse.body.getAccessToken();
            redisUtil.set(DingtalkCacheKeyConst.ACCESS_TOKEN, accessToken, 7000, TimeUnit.SECONDS);
            return accessToken;
        } catch (TeaException teaException) {
            if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
                String exceptionMessage = "获取DingtalkAccessToken失败, code={}, message={}";
                log.error(exceptionMessage, teaException.code, teaException.message, teaException);
                throw new BadRequestException(LoggerFmtUtil.format(exceptionMessage, teaException.code, teaException.message));
            }
            String exceptionMessage = "获取DingtalkAccessToken失败";
            log.error(exceptionMessage, teaException);
            throw new BadRequestException(exceptionMessage);
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                String exceptionMessage = "获取DingtalkAccessToken失败, code={}, message={}";
                log.error(exceptionMessage, err.code, err.message, err);
                throw new BadRequestException(LoggerFmtUtil.format(exceptionMessage, err.code, err.message));
            }
            String exceptionMessage = "获取DingtalkAccessToken失败";
            log.error(exceptionMessage, exception);
            throw new BadRequestException(exceptionMessage);
        }
    }
}
