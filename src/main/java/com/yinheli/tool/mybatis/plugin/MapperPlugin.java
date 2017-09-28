package com.yinheli.tool.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.annotated.AnnotatedSelectByExampleWithBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.annotated.AnnotatedSelectByExampleWithoutBLOBsMethodGenerator;

import java.util.List;

/**
 * @author yinheli
 */
public class MapperPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType()
        );
        FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(
                introspectedTable.getExampleType()
        );
        boolean hasBLOB = introspectedTable.hasBLOBColumns();
        if (hasBLOB) {
            Method method = new Method();
            method.setName(introspectedTable.getSelectByExampleWithBLOBsStatementId().replace("select", "selectOne"));
            method.setReturnType(returnType);
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(exampleType, "example"));
            AnnotatedSelectByExampleWithBLOBsMethodGenerator generator = new AnnotatedSelectByExampleWithBLOBsMethodGenerator();
            generator.setIntrospectedTable(introspectedTable);
            generator.addMapperAnnotations(interfaze, method);
            interfaze.addMethod(method);
        }
        Method method = new Method();
        method.setName(introspectedTable.getSelectByExampleStatementId().replace("select", "selectOne"));
        method.setReturnType(returnType);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(exampleType, "example"));
        AnnotatedSelectByExampleWithoutBLOBsMethodGenerator generator = new AnnotatedSelectByExampleWithoutBLOBsMethodGenerator();
        generator.setIntrospectedTable(introspectedTable);
        generator.addMapperAnnotations(interfaze, method);
        interfaze.addMethod(method);
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }
}
