package be.labil.anacarde.infrastructure.import_data;

import lombok.Data;

@Data
public class CityImportDto {
	public int id;
	public String n;
	public int rid;
	public String lat;
	public String lng;
	public long pop;
}
