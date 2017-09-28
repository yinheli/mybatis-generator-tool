package com.yinheli.tool.mybatis.plugin;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.SelectByExampleWithoutBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.annotated.AnnotatedSelectByExampleWithBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.annotated.AnnotatedSelectByExampleWithoutBLOBsMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.SelectByExampleWithBLOBsElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.SelectByExampleWithoutBLOBsElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.SimpleSelectAllElementGenerator;

import java.util.Iterator;
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

        boolean genSqlMap = context.getSqlMapGeneratorConfiguration() != null;

        boolean hasBLOB = introspectedTable.hasBLOBColumns();
        if (hasBLOB) {
            Method method = new Method();
            method.setName(introspectedTable.getSelectByExampleWithBLOBsStatementId().replace("select", "selectOne"));
            method.setReturnType(returnType);
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(exampleType, "example"));

            if (!genSqlMap) {
                AnnotatedSelectByExampleWithBLOBsMethodGenerator generator = new AnnotatedSelectByExampleWithBLOBsMethodGenerator();
                generator.setIntrospectedTable(introspectedTable);
                generator.addMapperAnnotations(interfaze, method);
            }
            interfaze.addMethod(method);
        } else {
            Method method = new Method();
            method.setName(introspectedTable.getSelectByExampleStatementId().replace("select", "selectOne"));
            method.setReturnType(returnType);
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(exampleType, "example"));

            if (!genSqlMap) {
                AnnotatedSelectByExampleWithoutBLOBsMethodGenerator generator = new AnnotatedSelectByExampleWithoutBLOBsMethodGenerator();
                generator.setIntrospectedTable(introspectedTable);
                generator.addMapperAnnotations(interfaze, method);
            }

            interfaze.addMethod(method);
        }

        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        boolean hasBLOB = introspectedTable.hasBLOBColumns();
        XmlElement parentEl = new XmlElement("mock");
        if (hasBLOB) {
            SelectByExampleWithBLOBsElementGenerator generator = new SelectByExampleWithBLOBsElementGenerator();
            generator.setContext(getContext());
            generator.setIntrospectedTable(introspectedTable);
            generator.addElements(parentEl);
        } else {
            SelectByExampleWithoutBLOBsElementGenerator generator = new SelectByExampleWithoutBLOBsElementGenerator();
            generator.setContext(getContext());
            generator.setIntrospectedTable(introspectedTable);
            generator.addElements(parentEl);
        }

        XmlElement element = (XmlElement) parentEl.getElements().get(0);
        element.getAttributes().removeIf(attr -> "id".equals(attr.getName()));
        element.addAttribute(new Attribute("id", "selectOneByExample"));

        document.getRootElement().addElement(element);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }
}
