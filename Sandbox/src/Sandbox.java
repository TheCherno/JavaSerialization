import java.util.ArrayList;
import java.util.List;

import com.thecherno.raincloud.serialization.RCDatabase;
import com.thecherno.raincloud.serialization.RCField;
import com.thecherno.raincloud.serialization.RCObject;
import com.thecherno.raincloud.serialization.RCString;

public class Sandbox {

	static class Level {

		private String name;
		private String path;
		private int width, height;
		private List<Entity> entities = new ArrayList<Entity>();

		private Level() {
		}

		public Level(String path) {
			this.name = "Level One";
			this.path = path;
			width = 64;
			height = 128;
		}

		public void add(Entity e) {
			e.init(this);
			entities.add(e);
		}

		public void update() {
		}

		public void render() {
		}

		public void save(String path) {
			RCDatabase database = new RCDatabase(name);
			RCObject object = new RCObject("LevelData");
			object.addString(RCString.Create("name", name));
			object.addString(RCString.Create("path", this.path));
			object.addField(RCField.Integer("width", width));
			object.addField(RCField.Integer("height", height));
			object.addField(RCField.Integer("entityCount", entities.size()));
			database.addObject(object);
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);

				RCObject entity = new RCObject("E" + i);
				byte type = 0;
				if (e instanceof Player)
					type = 1;
				entity.addField(RCField.Byte("type", type));
				entity.addField(RCField.Integer("arrayIndex", i));
				e.serialize(entity);
				database.addObject(entity);
			}

			database.serializeToFile(path);
		}

		public static Level load(String path) {
			RCDatabase database = RCDatabase.DeserializeFromFile(path);
			RCObject levelData = database.findObject("LevelData");

			Level result = new Level();
			result.name = levelData.findString("name").getString();
			result.path = levelData.findString("path").getString();
			result.width = levelData.findField("width").getInt();
			result.height = levelData.findField("height").getInt();
			int entityCount = levelData.findField("entityCount").getInt();

			for (int i = 0; i < entityCount; i++) {
				RCObject entity = database.findObject("E" + i);
				Entity e;
				if (entity.findField("type").getByte() == 1)
					e = Player.deserialize(entity);
				else
					e = Entity.deserialize(entity);
				result.add(e);
			}
			return result;
		}

	}

	static class Entity {

		protected int x, y;
		protected boolean removed = false;
		protected Level level;

		public Entity() {
			x = 0;
			y = 0;
		}

		public void init(Level level) {
			this.level = level;
		}

		public void serialize(RCObject object) {
			object.addField(RCField.Integer("x", x));
			object.addField(RCField.Integer("y", y));
			object.addField(RCField.Boolean("removed", removed));
		}

		public static Entity deserialize(RCObject object) {
			Entity result = new Entity();
			result.x = object.findField("x").getInt();
			result.y = object.findField("y").getInt();
			result.removed = object.findField("removed").getBoolean();
			return result;
		}

	}

	static class Player extends Entity {

		private String name;
		private String avatarPath;

		private Player() {
		}

		public Player(String name, int x, int y) {
			this.x = x;
			this.y = y;

			this.name = name;
			avatarPath = "res/avatar.png";
		}

		public void serialize(RCObject object) {
			super.serialize(object);
			object.addString(RCString.Create("name", name));
			object.addString(RCString.Create("avatarPath", avatarPath));
		}

		public static Player deserialize(RCObject object) {
			Entity e = Entity.deserialize(object);

			Player result = new Player();
			result.x = e.x;
			result.y = e.y;
			result.removed = e.removed;

			result.name = object.findString("name").getString();
			result.avatarPath = object.findString("avatarPath").getString();

			return result;
		}

	}

	public void play() {
		{
			Entity mob = new Entity();
			Player player = new Player("Cherno", 40, 28);

			Level level = new Level("res/level.lvl");
			level.add(mob);
			level.add(player);

			level.save("level.rcd");
		}
		{
			Level level = Level.load("level.rcd");
			System.out.println();
		}
	}

}
