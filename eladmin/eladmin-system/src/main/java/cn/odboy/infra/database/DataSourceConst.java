package cn.odboy.infra.database;

/**
 * 数据源常量
 *
 * @author odboy
 * @date 2025-01-15
 */
public interface DataSourceConst {
    String Dev = "dev";
    String SpyDriverClassName = "com.p6spy.engine.spy.P6SpyDriver";
    String DriverClassName = "com.mysql.cj.jdbc.Driver";
//    String DbType = "com.alibaba.druid.pool.DruidDataSource";
}
