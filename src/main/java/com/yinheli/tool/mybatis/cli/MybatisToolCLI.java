package com.yinheli.tool.mybatis.cli;

import org.apache.commons.cli.*;

/**
 * @author yinheli
 */
public class MybatisToolCLI {


    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "show help");
        options.addOption("pc", "package", true, "package");
        options.addOption("pj", "project", true, "project");

        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            log("parse argument fail, %s", e.getMessage());
            helpFormatter.printHelp("", options, true);
            return;
        }
    }

    private static void log(String message) {
        System.out.println(message);
    }

    private static void log(String format, Object...args) {
        System.out.println(String.format(format, args));
    }
}
