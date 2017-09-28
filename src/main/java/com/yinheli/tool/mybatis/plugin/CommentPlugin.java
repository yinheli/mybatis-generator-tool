package com.yinheli.tool.mybatis.plugin;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * @author yinheli
 */
public class CommentPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Method method = new Method("toString");
        method.setReturnType(new FullyQualifiedJavaType(String.class.getCanonicalName()));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addAnnotation("@Override");
        method.addBodyLine("return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);");
        topLevelClass.addImportedType(new FullyQualifiedJavaType(ToStringBuilder.class.getCanonicalName()));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(ToStringStyle.class.getCanonicalName()));
        topLevelClass.addMethod(method);
        return topLevelClassComment(topLevelClass, introspectedTable, true);
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return topLevelClassComment(topLevelClass, introspectedTable, true);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        return topLevelClassComment(interfaze, introspectedTable, false);
    }

    @Override
    public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return topLevelClassComment(topLevelClass, introspectedTable, false);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType(ApiModelProperty.class.getCanonicalName()));
        String remarks = introspectedColumn.getRemarks();
        remarks = remarks.replaceAll("\n", "\\\\n");
        String annoation = String.format("@ApiModelProperty(value = \"%s\")", remarks);
        field.addAnnotation(annoation);
        return comment(field, introspectedTable, introspectedColumn);
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return comment(method, introspectedTable, introspectedColumn);
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return comment(method, introspectedTable, introspectedColumn);
    }

    private boolean comment(JavaElement element, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        element.getJavaDocLines().clear();
        element.addJavaDocLine("/**");
        String remark = introspectedColumn.getRemarks();
        if (remark != null && remark.length() > 1) {
            element.addJavaDocLine(" * " + remark);
            element.addJavaDocLine(" *");
        }
        element.addJavaDocLine(" * Table:     " + introspectedTable.getFullyQualifiedTable());
        element.addJavaDocLine(" * Column:    " + introspectedColumn.getActualColumnName());
        element.addJavaDocLine(" * Nullable:  " + introspectedColumn.isNullable());
        element.addJavaDocLine(" */");

        return true;
    }

    private boolean topLevelClassComment(JavaElement element, IntrospectedTable introspectedTable, boolean commentTable) {
        if (element == null) {
            return true;
        }
        element.getJavaDocLines().clear();
        element.addJavaDocLine("/**");
        if (introspectedTable != null && commentTable) {
            element.addJavaDocLine(" * table: " + introspectedTable.getFullyQualifiedTable());
            element.addJavaDocLine(" * " + introspectedTable.getRemarks());
        }
        element.addJavaDocLine(" * @author yinheli");
        element.addJavaDocLine(" */");
        return true;
    }
}

