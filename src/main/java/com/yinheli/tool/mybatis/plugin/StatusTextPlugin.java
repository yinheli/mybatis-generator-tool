package com.yinheli.tool.mybatis.plugin;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author yinheli
 */
public class StatusTextPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 解析处理注释，增加 getXxxText() 方法
     *    注释格式范例：
     *      @enum 0: 有效期; 1: 无效
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {

        String remarks = introspectedColumn.getRemarks();

        remarks = StringUtils.trimToNull(remarks);

        if (remarks == null) {
            return true;
        }

        if (!remarks.contains("@enum")) {
            return true;
        }

        String annotation = remarks.substring(remarks.indexOf("@enum") + 5);
        String[] items = StringUtils.split(annotation, ';');

        StringBuilder mapInitSb = new StringBuilder("new HashMap<Object, String>(){{");
        for (String item : items) {
            if (!item.contains(":")) {
                continue;
            }
            String[] kv = item.split(":");
            String k = StringUtils.trim(kv[0]);
            String v = StringUtils.trim(kv[1]);
            mapInitSb.append("put(")
                    .append(k).append(",")
                    .append("\"").append(v).append("\");");
        }
        mapInitSb.append("}}");

        // 增加常量
        String fieldName = String.format("%sMap", introspectedColumn.getJavaProperty());

        Field field = new Field(
                fieldName,
                new FullyQualifiedJavaType("java.util.HashMap<Object, String>")
        );
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setStatic(true);
        field.setFinal(true);
        field.setInitializationString(mapInitSb.toString());

        topLevelClass.addField(field);


        // 增加方法
        Method textMethod = new Method(String.format("%sText", method.getName()));
        textMethod.setReturnType(new FullyQualifiedJavaType(String.class.getCanonicalName()));
        textMethod.setVisibility(JavaVisibility.PUBLIC);
        textMethod.addBodyLine(String.format("return %s.get(this.%s);", fieldName, introspectedColumn.getJavaProperty()));

        textMethod.addAnnotation(
            String.format("@ApiModelProperty(value = \"%s\")", remarks)
        );

        topLevelClass.addImportedType(new FullyQualifiedJavaType(HashMap.class.getCanonicalName()));
        topLevelClass.addMethod(textMethod);

        return true;
    }
}
