/*
 *  Copyright 2022-2025 Tian Jun
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
package cn.odboy.infra.database;

/**
 * 数据源常量
 *
 * @author odboy
 * @date 2025-01-15
 */
public interface DataSourceConst {
    String DEV = "dev";
    String SPY_DRIVER_CLASS_NAME = "com.p6spy.engine.spy.P6SpyDriver";
    String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
//    String DbType = "com.alibaba.druid.pool.DruidDataSource";
}
