package org.rapid.domain;

import org.rapid.domain.c.CTableConverHandler;
import org.rapid.domain.cpp.CppTableConverHandler;
import org.rapid.domain.golang.GolangTableConverHandler;
import org.rapid.domain.java.JavaTableConverHandler;
import org.rapid.domain.kuduhive.KuduTableConverHandler;
import org.rapid.domain.objectc.ObjectTableConverHandler;
import org.rapid.domain.rust.RustTableConverHandler;
import org.rapid.domain.scala.ScalaTableConverHandler;
import org.rapid.domain.swift.SwiftTableConverHandler;
import org.rapid.domain.typescript.TypescriptTableConverHandler;

import static org.rapid.common.enums.DomainType.*;

public abstract class AbstractTableConverHandler implements ITableConverHandler {

    public ITableConverHandler selectPlatForm(String path) {
        String typeName = path.substring(path.lastIndexOf("."));
        switch (getType(typeName)) {
            case JAVA:
                return new JavaTableConverHandler();
            case TYPESCRIPT:
                return new TypescriptTableConverHandler();
            case JAVASCRIPT:
                return new TypescriptTableConverHandler();
            case NODEJS:
                return new TypescriptTableConverHandler();
            case KUDUHIVE:
                return new KuduTableConverHandler();
            case REACT:
                return new TypescriptTableConverHandler();
            case RUBY:
                return new JavaTableConverHandler();
            case C:
                return new CTableConverHandler();
            case CPP:
                return new CppTableConverHandler();
            case SWIFT:
                return new SwiftTableConverHandler();
            case RUST:
                return new RustTableConverHandler();
            case GOLAND:
                return new GolangTableConverHandler();
            case PHP:
                return new JavaTableConverHandler();
            case SCALA:
                return new ScalaTableConverHandler();
            case OBJECTC:
                return new ObjectTableConverHandler();
            case KOLTIN:
                return new JavaTableConverHandler();
            default:
                return new JavaTableConverHandler();
        }
    }
}
