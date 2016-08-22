# NetSerializer
NetSerializer is a fast and simple Java object serializer which produces very small serialized data, intended for use in a networking application to reduce bandwidth.

## Features
- Simple serialization of Java objects into a DataOutput
- Produces very little overhead over the actual raw data contained in a serialized object
- Serialization of primitive Java types, Strings, Lists, Sets, Maps and Class objects built-in and optimized
- Supports inheritance and composition of serializable types
- Supports custom readers/writers for specific classes to reduce overhead or speed up serialization even further

## Limitations
- Does not allow cycles in the object graph to be serialized
- The serializer needs to know the serializable classes beforehand
- No versioning of serializable classes (assumes sender and receiver of serializable data have compatible versions of classes)
- No support for array types yet (use collections instead)
- Fields containing collections have to be declared as their interface types (i.e. "List<...>" instead of "ArrayList<...>", which is good practice anyway)

## Size of serialized objects
The primary goal of this serializer is to reduce the size of serialized objects. This allows developers to transmit plain old Java objects over the network easily while still getting performance comparable to a hand-crafted binary protocol.

| Type | Overhead over raw data |
| --- | --- |
| Primitive field | 0 bytes |
| Object reference | 2 bytes |
| List without different element types or null elements | 0 bytes per element |
| List with different element types or null elements | 2 bytes per element |
| Set without different element types | 0 bytes per element |
| Set with different element types | 2 bytes per element |
| Map without different key types, without different value types or null values | 0 bytes per entry |
| Map without different key types, with different value types or null values | 2 bytes per entry |
| Map with different key types, without different value types or null values | 2 bytes per entry |
| Map with different key types, with different value types or null values | 4 bytes per entry |

## Example
These are the classes to serialize:
```
class ContainerClass implements Serializable {
  private int something = 1234;
  private List<ContainedClass> children = new ArrayList<>();
  
  public ContainerClass() {
    for (int i = 0; i < 100; i++) {
      children.add(new ContainedClass(i));
    }
  }
}

class ContainedClass implements Serializable {
  private double size = 1.6;
  private long number;
  
  public ContainedClass(long number) {
    this.number = number;
  }
}
```

The following code will serialize an instance of `ContainerClass` into a byte array and read it back:
```
//Construct a serializer and tell it which classes it must be able to serialize
SerDes serializer = new SerDes(null, Arrays.asList(ContainerClass.class, ContainedClass.class));

//Construct a DataOutput for the serializer to write its data into
ByteArrayOutputStream baos = new ByteArrayOutputStream();
DataOutputStream output = new DataOutputStream(baos);

//Get an object to serialize
Object objectToSerialize = new ContainerClass();

//Serialize the object
serializer.writeObject(objectToSerialize, output);
output.flush();

//Create a DataInput to read the object back in from
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
DataInputStream input = new DataInputStream(bais);

//Read the object
Object readObject = serializer.readObject(input);
```
