import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import com.thecherno.raincloud.serialization.RCArray;
import com.thecherno.raincloud.serialization.RCBase;
import com.thecherno.raincloud.serialization.RCDatabase;
import com.thecherno.raincloud.serialization.RCField;
import com.thecherno.raincloud.serialization.RCObject;
import com.thecherno.raincloud.serialization.RCString;

public class Main {

	static Random random = new Random();
	
	static void printBytes(byte[] data) {
		for (int i = 0; i < data.length; i++) {
			System.out.printf("0x%x ", data[i]);
		}
	}
	
	public static void serializationTest() {
		int[] data = new int[50000];
		for (int i = 0; i < data.length; i++) {
			data[i] = random.nextInt();
		}
		
		RCDatabase database = new RCDatabase("Database");
		RCArray array = RCArray.Integer("RandomNumbers", data);
		RCField field = RCField.Integer("Integer", 8);
		RCField positionx = RCField.Short("xpos", (short)2);
		RCField positiony = RCField.Short("ypos", (short)43);
		
		RCObject object = new RCObject("Entity");
		object.addArray(array);
		object.addArray(RCArray.Char("String", "Hello World!".toCharArray()));
		object.addField(field);
		object.addField(positionx);
		object.addField(positiony);
		object.addString(RCString.Create("Example String", "Testing our RCString class!"));
			
		database.addObject(object);
		database.addObject(new RCObject("Cherno"));
		database.addObject(new RCObject("Cherno1"));
		RCObject c = new RCObject("Cherno2");
		c.addField(RCField.Boolean("a", false));
		database.addObject(c);
		database.addObject(new RCObject("Cherno3"));

		database.serializeToFile("test.rcd");
	}

	public static void deserializationTest() {
		RCDatabase database = RCDatabase.DeserializeFromFile("test.rcd");
		System.out.println("Database: " + database.getName());
		for (RCObject object : database.objects) {
			System.out.println("\t" + object.getName());
			for (RCField field : object.fields)
				System.out.println("\t\t" + field.getName());
			System.out.println();
			for (RCString string : object.strings)
				System.out.println("\t\t" + string.getName() + " = " + string.getString());
			System.out.println();
			for (RCArray array : object.arrays)
				System.out.println("\t\t" + array.getName());
			System.out.println();
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		// serializationTest();
		// deserializationTest();
		
		Sandbox sandbox = new Sandbox();
		sandbox.play();
	}

}
