# mybatis generator tool

一个 mybatis 配置 cli 自动生成工具，实现了几个小的 plugin，（xml 注释，model 类注释等）

## 打包

```
gradle dist
```

打包完成后，编译好的文件在 `build/dist` 目录，可以将他们移动到 `PATH` 路径内方便使用。

## 使用

```
usage: mybatis-generator-tool.sh [-c <arg>] [-h] [-ov] [-pc <arg>] [-pj
       <arg>]
 -c,--config file <arg>   xml config file
 -h,--help                show help
 -ov,--overwrite          overwrite generate files, default is false
 -pc,--package <arg>      target package
 -pj,--project <arg>      target project
```

用 `-c` 参数传递 generator xml 配置文件即可，默认查找当前目录的 `generatorConfig.xml` 文件
有关配置文件具体配置，请参考： http://www.mybatis.org/generator/configreference/xmlconfig.html
