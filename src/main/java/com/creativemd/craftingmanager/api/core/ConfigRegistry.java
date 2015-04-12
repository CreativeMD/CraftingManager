package com.creativemd.craftingmanager.api.core;

import java.util.ArrayList;

public class ConfigRegistry {
	
	public static ArrayList<ConfigSystem> systems = new ArrayList<ConfigSystem>();
	
	public static ConfigSystem registerConfig(ConfigSystem system)
	{
		system.setID(systems.size());
		systems.add(system);
		return system;
	}
	
	public static ConfigSystem getConfigFromID(int id)
	{
		if(id >= 0 && id < systems.size())
			return systems.get(id);
		else{
			System.out.println("Searched an invalid system [id=" + id + "]");
			return null;
		}
	}
	
	public static ArrayList<ConfigSystem> getConfigsFromTab(ConfigTab tab)
	{
		ArrayList<ConfigSystem> result = new ArrayList<ConfigSystem>();
		for (int i = 0; i < systems.size(); i++) {
			if(systems.get(i).tab == tab)
				result.add(systems.get(i));
		}
		return result;
	}
}
