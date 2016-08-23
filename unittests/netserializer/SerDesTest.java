package netserializer;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


//The following classes set their values via getInstance
//instead of a constructor to ensure the serializer really
//sets the fields

class TestClassWithPrimitives implements Serializable {
	public int p1;
	public double p2;
	public String str;
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		TestClassWithPrimitives other = (TestClassWithPrimitives)obj;
		return (this.p1 == other.p1) && (this.p2 == other.p2) && this.str.equals(other.str);
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(p1) + Double.hashCode(p2) + str.hashCode();
	}
	
	public static TestClassWithPrimitives getInstance() {
		TestClassWithPrimitives obj = new TestClassWithPrimitives();
		obj.p1 = 1234;
		obj.p2 = 0.51;
		obj.str = "Hello World";
		return obj;
	}
}

class TestClassSubClass extends TestClassWithPrimitives {
	public long someLong;
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!super.equals(obj)) {
			return false;
		}
		TestClassSubClass other = (TestClassSubClass)obj;
		return this.someLong == other.someLong;
	}

	@Override
	public int hashCode() {
		return super.hashCode() + Long.hashCode(someLong);
	}
	
	public static TestClassSubClass getInstance() {
		TestClassSubClass obj = new TestClassSubClass();
		obj.p1 = 1234;
		obj.p2 = 0.51;
		obj.str = "Hello World";
		obj.someLong = 837473546345L;
		return obj;
	}
}

class TestClassComposed implements Serializable {
	public TestClassWithPrimitives child;
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		TestClassComposed other = (TestClassComposed)obj;
		return this.child.equals(other.child);
	}

	@Override
	public int hashCode() {
		return this.child.hashCode();
	}

	public static TestClassComposed getInstance() {
		TestClassComposed obj = new TestClassComposed();
		obj.child = TestClassSubClass.getInstance();
		return obj;
	}
}

class TestClassBoxedPrimitive implements Serializable {
	public Integer i = 99;
	
	public TestClassBoxedPrimitive() {}
	
	public TestClassBoxedPrimitive(Integer i) {
		this.i = i;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		TestClassBoxedPrimitive other = (TestClassBoxedPrimitive)obj;
		return ((this.i != null) && (this.i.equals(other.i))) || (this.i == other.i);
	}

	@Override
	public int hashCode() {
		if (i == null) {
			return 0;
		}
		return i.hashCode();
	}
	
	
}

class TestClassListAndSet implements Serializable {
	public List<Integer> l1 = new ArrayList<>();
	public List<TestClassWithPrimitives> l2 = new LinkedList<>();
	public Set<String> l3 = new HashSet<>();
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		TestClassListAndSet other = (TestClassListAndSet)obj;
		return this.l1.equals(other.l1) && this.l2.equals(other.l2) && this.l3.equals(other.l3);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.l1.hashCode() + this.l2.hashCode() + this.l3.hashCode();
	}

	public static TestClassListAndSet getInstance() {
		TestClassListAndSet obj = new TestClassListAndSet();
		obj.l1.addAll(Arrays.asList(1, 2, 3, 9, 3, 2, 7));
		obj.l2.addAll(Arrays.asList(TestClassWithPrimitives.getInstance(), TestClassSubClass.getInstance()));
		obj.l3.addAll(Arrays.asList("Derp", "Herp", "Hello World", null, "There was a null"));
		return obj;
	}
}

class TestClassMap implements Serializable {
	public Map<Integer, String> map = new HashMap<>();
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		TestClassMap other = (TestClassMap)obj;
		return this.map.equals(other.map);
	}

	@Override
	public int hashCode() {
		return this.map.hashCode();
	}

	public static TestClassMap getInstance() {
		TestClassMap obj = new TestClassMap();
		obj.map.put(0, "Derp");
		obj.map.put(1, "Bla");
		obj.map.put(null, "Something");
		return obj;
	}
}

class TestClassFinalField implements Serializable {
	public final int i1;
	public final Integer i2;
	
	//Compile-time constant
	public final String str = "abc";
	
	public TestClassFinalField() {
		i1 = 99;
		i2 = 3;
	}
	
	public TestClassFinalField(int i1, Integer i2) {
		this.i1 = i1;
		this.i2 = i2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		TestClassFinalField other = (TestClassFinalField)obj;
		return (this.i1 == other.i1) && this.i2.equals(other.i2) && this.str.equals(other.str);
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(i1) + this.i2.hashCode() + str.hashCode();
	}
}

public class SerDesTest {
	
	private SerDes writer;
	private SerDes reader;
	
	private SerDes makeSerDes() throws NoSuchMethodException, SecurityException {
		return new SerDes(null, Arrays.asList(
				TestClassWithPrimitives.class,
				TestClassSubClass.class,
				TestClassComposed.class,
				TestClassBoxedPrimitive.class,
				TestClassListAndSet.class,
				TestClassMap.class,
				TestClassFinalField.class));
	}
	
	@Before
	public void setUp() throws Exception {
		writer = makeSerDes();
		reader = makeSerDes();
	}
	
	@After
	public void tearDown() throws Exception {
		writer = null;
		reader = null;
	}
	
	/**
	 * Tests serialization of a class containing only
	 * primitives and a String (which is also treated
	 * as a primitive by the serializer)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPrimitives() throws Exception {
		testWith(TestClassWithPrimitives.getInstance());
	}
	
	/**
	 * Tests serialization of a subclass.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInheritance() throws Exception {
		testWith(TestClassSubClass.getInstance());
	}
	
	/**
	 * Tests serialization of a class that contains an
	 * instance of another serializable class.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testComposition() throws Exception {
		testWith(TestClassComposed.getInstance());
	}
	
	/**
	 * Tests serialization of a class with a boxed primitive.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBoxedPrimitive() throws Exception {
		testWith(new TestClassBoxedPrimitive(123));
		testWith(new TestClassBoxedPrimitive(null));
	}
	
	/**
	 * Tests serialization of a class containing lists
	 * and sets, especially ones with and without
	 * tag optimization.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testListsAndSets() throws Exception {
		testWith(TestClassListAndSet.getInstance());
	}
	
	/**
	 * Tests serialization of a class containing a map.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMaps() throws Exception {
		testWith(TestClassMap.getInstance());
	}
	
	/**
	 * Tests serialization of Class objects.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testClasses() throws Exception {
		testWith(String.class);
	}
	
	/**
	 * Tests serialization of null.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNull() throws Exception {
		testWith(null);
	}
	
	/**
	 * Tests serialization of final fields.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFinalFields() throws Exception {
		String whatItShouldBe = new TestClassFinalField().str;
		
		TestClassFinalField obj = (TestClassFinalField)testWith(new TestClassFinalField());
		assertTrue(obj.str.equals(whatItShouldBe));
		
		obj = (TestClassFinalField)testWith(new TestClassFinalField(5, 87));
		assertTrue(obj.str.equals(whatItShouldBe));
	}
	
	
	private Object testWith(Object obj) throws Exception {
		Object result = this.serializeAndDeserialize(obj, writer, reader);
		if (obj != null) {
			assertTrue(obj.equals(result));
		} else {
			assertTrue(result == null);
		}
		return result;
	}
	
	private Object serializeAndDeserialize(Object obj, SerDes writer, SerDes reader) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream output = new DataOutputStream(baos);
		writer.writeObject(obj, output);
		output.flush();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		DataInputStream input = new DataInputStream(bais);
		return reader.readObject(input);
	}
}
