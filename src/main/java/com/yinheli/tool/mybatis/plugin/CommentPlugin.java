package com.yinheli.tool.mybatis.plugin;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.*;

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
    public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return topLevelClassComment(topLevelClass, introspectedTable, false);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType(ApiModelProperty.class.getCanonicalName()));
        String remarks = introspectedColumn.getRemarks();
        remarks = remarks.replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\"");
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

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        commentResultMap(element, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        commentResultMap(element, introspectedTable);
        return true;
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

    private void commentResultMap(XmlElement element,
                          IntrospectedTable introspectedTable) {
        List<Element> es = element.getElements();
        if (es.isEmpty()) {
            return;
        }

        String alias = introspectedTable.getTableConfiguration().getAlias();
        int aliasLen = -1;
        if (alias != null) {
            aliasLen = alias.length() + 1;
        }

        Iterator<Element> it = es.iterator();

        Map<Element, Element> map = new HashMap<Element, Element>();

        while (it.hasNext()) {
            Element e = it.next();

            if (e instanceof TextElement) {
                it.remove();
                continue;
            }

            XmlElement el = (XmlElement) e;
            List<Attribute> as = el.getAttributes();
            if (as.isEmpty()) {
                continue;
            }

            String col = null;
            for (Attribute a : as) {
                if (a.getName().equalsIgnoreCase("column")) {
                    col = a.getValue();
                    break;
                }
            }

            if (col == null) {
                continue;
            }

            if (aliasLen > 0) {
                col = col.substring(aliasLen);
            }

            IntrospectedColumn ic = introspectedTable.getColumn(col);
            if (ic == null) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            if (ic.getRemarks() != null && ic.getRemarks().length() > 1) {
                sb.append("<!-- ");
                sb.append(ic.getRemarks());
                sb.append(" -->");
                map.put(el, new TextElement(sb.toString()));
            }
        }

        if (map.isEmpty()) {
            return;
        }

        Set<Element> set = map.keySet();
        for (Element e : set) {
            int id = es.indexOf(e);
            es.add(id, map.get(e));
            // es.add(id, new TextElement(""));
        }

    }
}

