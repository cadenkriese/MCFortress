package com.chaseos.tf2.placeholders;

import com.chaseoes.tf2.TF2;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

public class Stage implements PlaceholderReplacer{
	
	
	public Stage() {
	}

	@Override
	public String update() {
		String map = TF2.getInstance().getADmap();
		if (map.endsWith("l"))
		{
			return "1";
		}
		else if (map.endsWith("2"))
		{
			return "2";
		}
		return "3";
	}

}
