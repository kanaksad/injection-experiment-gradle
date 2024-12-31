package persistence.mappers;

import java.util.HashMap;
import java.util.Map;

import persistence.DBmap;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;

public class Animal implements persistence.Mapper {
	@SuppressWarnings("serial")
	public domain.tiles.Animal load(DBmap map) {
		return new domain.tiles.Animal(map.getStr("type"), map.getLong("start"));
	}

	public DBmap save(domain.Savable obj) {
		domain.tiles.Animal animal = (domain.tiles.Animal) obj;
		return new DBmap(this, new String[] { "type", "start" },
				new Object[] { animal.getType(), animal.getStart().getTime() });
	}

	public Map<@RUntainted String, @RUntainted String> getFields() {
		return new HashMap<String, String>() {
			{
				put("type", "TEXT");
				put("start", "BIGINT");
			}
		};
	}
}
