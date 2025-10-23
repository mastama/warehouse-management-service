package com.yolifay.warehousemanagementservice.config;

import com.yolifay.warehousemanagementservice.dto.*;
import com.yolifay.warehousemanagementservice.entity.Item;
import com.yolifay.warehousemanagementservice.entity.SalesOrder;
import com.yolifay.warehousemanagementservice.entity.SalesOrderLine;
import com.yolifay.warehousemanagementservice.entity.Variant;

import com.yolifay.warehousemanagementservice.util.ResponseCode;
import com.yolifay.warehousemanagementservice.util.ResponseUtil;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.core.DecoratingProxy;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.data.repository.Repository;

import java.util.List;

public class ApplicationRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {

        // 1) KELAS yang perlu refleksi/Jackson
        List<Class<?>> reflectiveTypes = List.of(
                // Entities
                Item.class, Variant.class, SalesOrder.class, SalesOrderLine.class,

                // Dtos
                ItemCreateRequest.class, ItemUpdateRequest.class, ItemResponse.class,
                VariantCreateRequest.class, VariantUpdateRequest.class, VariantResponse.class,
                StockSetRequest.class,
                OrderCreateRequest.class, OrderLineRequest.class,
                OrderResponse.class, OrderResponse.Line.class,
                ApiResponse.class,

                // Utils
                ResponseCode.class, ResponseUtil.class
        );

        reflectiveTypes.forEach(type ->
                hints.reflection().registerType(
                        type,
                        // constructors
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                        // fields
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.PUBLIC_FIELDS,
                        // methods
                        MemberCategory.INVOKE_PUBLIC_METHODS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        // untuk framework yang pakai getMethods()/getDeclaredMethods()
                        MemberCategory.INTROSPECT_PUBLIC_METHODS,
                        MemberCategory.INTROSPECT_DECLARED_METHODS
                )
        );

        // 2) Java Serialization (implements Serializable)
        reflectiveTypes.forEach(t -> hints.serialization().registerType(TypeReference.of(t)));

        // 3) Proxy untuk Spring Data, AOP, dll.
        hints.proxies().registerJdkProxy(
                Repository.class, SpringProxy.class, Advised.class, DecoratingProxy.class
        );

        // 4) Resource bundle untuk pesan Bean Validation
        hints.resources().registerResourceBundle("org.hibernate.validator.ValidationMessages");
    }
}

