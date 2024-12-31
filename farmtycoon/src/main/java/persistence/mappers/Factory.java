package persistence.mappers;

import java.util.HashMap;
import java.util.Map;

import persistence.DBmap;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;

public class Factory implements persistence.Mapper {
	public domain.tiles.Factory load(DBmap map) {
		return new domain.tiles.Factory(map.getStr("type"), map.getLong("start"), map.getLong("damage"),map.getStr("state"));
	}

	public DBmap save(domain.Savable obj) {
		domain.tiles.Factory factory = (domain.tiles.Factory) obj;
		return new DBmap(this, new String[] { "type", "start", "damage", "state" },
				new Object[] { factory.getType(), factory.getStart(), factory.getDamage(), factory.getState() });
	}

	public Map<@RUntainted String, @RUntainted String> getFields() {
		return new HashMap<String, String>() {
			{
				put("type", "TEXT");
				put("start", "BIGINT");
				put("damage", "BIGINT");
				put("state", "TEXT");
			}
		};
	}
}
