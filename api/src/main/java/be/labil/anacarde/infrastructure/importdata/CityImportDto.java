package be.labil.anacarde.infrastructure.importdata;

import lombok.Data;

@Data
public class CityImportDto {
	public int id;
	public String name;
	public int region;
	public String lat;
	public String lon;
}