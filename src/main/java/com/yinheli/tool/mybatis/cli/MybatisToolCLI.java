package com.yinheli.tool.mybatis.cli;

import org.apache.commons.cli.*;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yinheli
 */
public class MybatisToolCLI {


    public static void main(String[] args) {
        String cmdLineSyntax = "mybatis-generator-tool.sh";
        Options options = new Options();
        options.addOption("h", "help", false, "show help");
        options.addOption("c", "config file", true, "xml config file");
        options.addOption("pc", "package", true, "target package");
        options.addOption("pj", "project", true, "target project");
        options.addOption("ov", "overwrite", false, "overwrite generate files, default is false");

        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log("parse argument fail, %s", e.getMessage());
            helpFormatter.printHelp(cmdLineSyntax, options, true);
            return;
        }

        if (cmd.hasOption("h")) {
            helpFormatter.printHelp(cmdLineSyntax, options, true);
            return;
        }

        File configFile = new File(cmd.getOptionValue("c", "generatorConfig.xml"));
        if (!configFile.exists()) {
            log("config file not found");
            return;
        }

        List<String> warnings = new ArrayList<>();

        Configuration config;
        try {
            config = parseConfig(configFile, warnings);
        } catch (Exception e) {
            log("parse config file exception, %s", e.getMessage());
            return;
        }

        String targetPackage = cmd.getOptionValue("pc");
        String targetProject = cmd.getOptionValue("pj");

        // overwrite config
        for (Context context : config.getContexts()) {

            JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = context.getJavaModelGeneratorConfiguration();
            JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = context.getJavaClientGeneratorConfiguration();
            SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = context.getSqlMapGeneratorConfiguration();

            if (targetPackage != null) {
                if (javaModelGeneratorConfiguration != null) {
                    javaModelGeneratorConfiguration.setTargetPackage(targetPackage);
                }

                if (javaClientGeneratorConfiguration != null) {
                    javaClientGeneratorConfiguration.setTargetPackage(targetPackage);
                }

                if (sqlMapGeneratorConfiguration != null) {
                    sqlMapGeneratorConfiguration.setTargetPackage(targetPackage);
                }
            }

            if (targetProject != null) {
                if (javaModelGeneratorConfiguration != null) {
                    javaModelGeneratorConfiguration.setTargetProject(targetProject);
                }

                if (javaClientGeneratorConfiguration != null) {
                    javaClientGeneratorConfiguration.setTargetProject(targetProject);
                }

                if (sqlMapGeneratorConfiguration != null) {
                    sqlMapGeneratorConfiguration.setTargetProject(targetProject);
                }
            }
        }


        try {
            DefaultShellCallback callback = new DefaultShellCallback(cmd.hasOption("ov"));
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);

            if (!warnings.isEmpty()) {
                for (String w : warnings) {
                    log(w);
                }
            }
            log("Done!");
        } catch (Exception e) {
            log("generate exception: %s", e.getMessage());
            e.printStackTrace();
        }
    }

    private static Configuration parseConfig(File configFile, List<String> warnings) throws Exception {
        ConfigurationParser configurationParser = new ConfigurationParser(warnings);
        return configurationParser.parseConfiguration(configFile);
    }

    private static void log(String message) {
        System.out.println(message);
    }

    private static void log(String format, Object...args) {
        log(String.format(format, args));
    }
}
