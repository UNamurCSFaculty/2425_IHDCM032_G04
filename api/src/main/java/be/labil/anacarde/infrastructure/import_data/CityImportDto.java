package be.labil.anacarde.infrastructure.import_data;

import lombok.Data;

@Data
public class CityImportDto {
	public int id;
	public String name;
	public int region;
	public String lat;
	public String lon;
}