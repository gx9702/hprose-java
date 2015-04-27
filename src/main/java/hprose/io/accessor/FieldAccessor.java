/**********************************************************\
|                                                          |
|                          hprose                          |
|                                                          |
| Official WebSite: http://www.hprose.com/                 |
|                   http://www.hprose.org/                 |
|                                                          |
\**********************************************************/
/**********************************************************\
 *                                                        *
 * FieldAccessor.java                                     *
 *                                                        *
 * FieldAccessor class for Java.                          *
 *                                                        *
 * LastModified: Apr 27, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.io.accessor;

import hprose.common.HproseException;
import static hprose.io.HproseTags.TagNull;
import hprose.io.serialize.HproseSerializer;
import hprose.io.serialize.HproseWriter;
import hprose.io.serialize.SerializerFactory;
import hprose.io.unserialize.HproseReader;
import hprose.io.unserialize.HproseUnserializer;
import hprose.io.unserialize.UnserializerFactory;
import hprose.util.ClassUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

public final class FieldAccessor implements MemberAccessor {
    private final Field accessor;
    private final Class<?> cls;
    private final Type type;
    private final HproseSerializer serializer;
    private final HproseUnserializer unserializer;

    public FieldAccessor(Field accessor) {
        accessor.setAccessible(true);
        this.accessor = accessor;
        this.type = accessor.getGenericType();
        this.cls = ClassUtil.toClass(type);
        this.serializer = SerializerFactory.get(cls);
        this.unserializer = UnserializerFactory.get(cls);
    }

    public void serialize(HproseWriter writer, Object obj) throws IOException {
        Object value;
        try {
            value = accessor.get(obj);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
        if (value == null) {
            writer.stream.write(TagNull);
        }
        else {
            serializer.write(writer, value);
        }
    }

    public void unserialize(HproseReader reader, ByteBuffer buffer, Object obj) throws IOException {
        Object value = unserializer.read(reader, buffer, cls, type);
        try {
            accessor.set(obj, value);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }

    public void unserialize(HproseReader reader, InputStream stream, Object obj) throws IOException {
        Object value = unserializer.read(reader, stream, cls, type);
        try {
            accessor.set(obj, value);
        }
        catch (Exception e) {
            throw new HproseException(e.getMessage());
        }
    }
}