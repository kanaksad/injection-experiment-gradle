package persistence.mappers;

import java.util.HashMap;
import java.util.Map;

import persistence.DBmap;

import domain.Savable;
import edu.ucr.cs.riple.taint.ucrtainting.qual.RUntainted;

public class Clock implements persistence.Mapper {
	public domain.Clock load(DBmap map) {
		return new domain.Clock(map.getDouble("Multi"), map.getLong("Offset"));
	}

	public DBmap save(Savable obj) {
		DBmap ret = new DBmap(this);
		domain.Clock clock = (domain.Clock) obj;
		ret.put("Offset", clock.getOffset());
		ret.put("Multi", clock.getMultiplier());
		return ret;
	}

	public Map<@RUntainted String, @RUntainted String> getFields() {
		Map<@RUntainted String, @RUntainted String> fields = new HashMap<@RUntainted String, @RUntainted String>();
		fields.put("Offset", "BIGINT");
		fields.put("Multi", "DOUBLE");
		return fields;
	}
}
