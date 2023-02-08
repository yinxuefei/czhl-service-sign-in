package com.gdczhl.saas.utils;

/**
 * Mybatis 自动生成 controller，service&iService，mapper,xml,entity
 *
 * @author hkx
 * @version 1.0.0
 * @since 2022/04/17
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class AutoGeneratorUtil {

    public static void autoGenerator() {
        // Step1：数据源配置
        String url = "jdbc:mysql://127.0.0.1:3306/info?useUnicode=true&characterEncoding=utf8" +
                "&autoReconnect=true&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "root";
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(url, username, password).build();

        // Step2：全局配置
        GlobalConfig.Builder builder = new GlobalConfig.Builder();
        // 填写代码生成的目录
        String projectPath = "C:\\utils\\profile\\czhl-service-sign-in";
        builder.outputDir(projectPath + "/src/main/java");
        // 作者
        builder.author("hkx");
        // 配置日期类型
        builder.dateType(DateType.ONLY_DATE);
        // 执行完毕后不打开目录
        //builder.disableOpenDir();
        GlobalConfig globalConfig = builder
                .build();

        // Step:3：包配置
        PackageConfig packageConfig = new PackageConfig.Builder().parent("com.gdczhl.saas").build();

        // Step4：策略配置（数据库表配置）
        StrategyConfig strategy = new StrategyConfig.Builder()
                // 生成哪些表的类
                .addInclude("sign_statistics")
                // 开启大写命名
                .enableCapitalMode()
                // lombok,字段下划线驼峰转换
                .entityBuilder().enableLombok().columnNaming(NamingStrategy.underline_to_camel)
                // 移除 is 前缀
                .enableRemoveIsPrefix()
                //开启lombok
                .enableLombok()
                // controller 使用RestController
                .controllerBuilder().enableRestStyle()
                // 继承基类实现类
                .serviceBuilder().superServiceImplClass(ServiceImpl.class)
                //覆盖原有文件
                .fileOverride()
                .build();

        // Step5：创建 代码生成器
        AutoGenerator generator = new AutoGenerator(dataSourceConfig);
        generator.global(globalConfig);
        generator.packageInfo(packageConfig);
        generator.strategy(strategy);

        // Step6：生成代码
        generator.execute();
    }

    public static void main(String[] args) {
        autoGenerator();
    }
}

