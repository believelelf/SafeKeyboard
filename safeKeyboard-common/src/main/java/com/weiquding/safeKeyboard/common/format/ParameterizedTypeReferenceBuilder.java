package com.weiquding.safeKeyboard.common.format;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * There is Guava equivalent of ParameterizedTypeReference and it's called TypeToken.
 * <p>
 * Guava's class is much more powerful then Spring's equivalent. You can compose the TypeTokens as you wish.
 * <p>
 * If you call mapToken(TypeToken.of(String.class), TypeToken.of(BigInteger.class)); you will create TypeToken<Map<String, BigInteger>>!
 * <p>
 * The only disadvantage here is that many Spring APIs require ParameterizedTypeReference and not TypeToken. But we can create ParameterizedTypeReference implementation which is adapter to TypeToken itself.
 * <p>
 * https://stackoverflow.com/questions/21987295/using-spring-resttemplate-in-generic-method-with-generic-parameter
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/26
 */
public class ParameterizedTypeReferenceBuilder {

    /**
     * 将TypeToken转换为ParameterizedTypeReference
     *
     * @param typeToken TypeToken
     * @param <T>       类型
     * @return ParameterizedTypeReference
     */
    public static <T> ParameterizedTypeReference<T> fromTypeToken(TypeToken<T> typeToken) {
        return new TypeTokenParameterizedTypeReference<>(typeToken);
    }

    /**
     * 构建Map类型TypeToken
     *
     * @param keyToken   key's TypeToken
     * @param valueToken value's TypeToken
     * @param <K>        key类型
     * @param <V>        value类型
     * @return Map类型TypeToken
     */
    public static <K, V> TypeToken<Map<K, V>> mapToken(TypeToken<K> keyToken, TypeToken<V> valueToken) {
        return new TypeToken<Map<K, V>>() {
        }
                .where(new TypeParameter<K>() {
                }, keyToken)
                .where(new TypeParameter<V>() {
                }, valueToken);
    }

    /**
     * 构建List类型TypeToken
     *
     * @param elementToken 元素类型TypeToken
     * @param <T>          元素类型
     * @return List类型TypeToken
     */
    public static <T> TypeToken<List<T>> listToken(TypeToken<T> elementToken) {
        return new TypeToken<List<T>>() {
        }
                .where(new TypeParameter<T>() {
                }, elementToken);
    }

    private static class TypeTokenParameterizedTypeReference<T> extends ParameterizedTypeReference<T> {

        private final Type type;

        private TypeTokenParameterizedTypeReference(TypeToken<T> typeToken) {
            this.type = typeToken.getType();
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public boolean equals(Object obj) {
            return (this == obj || (obj instanceof ParameterizedTypeReference &&
                    this.type.equals(((ParameterizedTypeReference<?>) obj).getType())));
        }

        @Override
        public int hashCode() {
            return this.type.hashCode();
        }

        @Override
        public String toString() {
            return "ParameterizedTypeReference<" + this.type + ">";
        }

    }
}
