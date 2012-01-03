package bkampfbot.bundesklatsche.field;

public class Field {
	public static Field getField(int position) {
		switch (position) {
		
		case 0:
			return new StartField();
		
		case 1:
		case 16:
		case 23:
		case 31:
			return new SchatzField();
			
		case 2:
			return new KampfField("Bayern");
			
		case 3:
		case 11:
		case 26:
		case 37:
			return new EreignisField();
			
		case 4:
			return new NordBahnhof();
			
		case 5:
			return new KampfField("Baden-Württemberg");
			
		case 6:
		case 18:
		case 24:
		case 34:
			return new AktionField();
			
		case 7:
			return new KampfField("Hamburg");
			
		case 8:
		case 12:
		case 21:
		case 36:
			return new VierGewinntField();
			
		case 9:
			return new KampfField("Saarland");
			
		case 10:
			return new ZumKnastField();
			
		case 13:
			return new KampfField("Brandenburg");
			
		case 14:
			return new KampfField("Thüringen");
			
		case 15:
			return new OstBahnhof();
			
		case 17:
			return new KampfField("Bremen");
			
		case 19:
			return new KampfField("Schleswig-Holstein");
			
		case 20:
			return new Knast();
			
		case 22:
			return new KampfField("Hessen");
			
		case 25:
			return new KampfField("Rheinland-Pfalz");
			
		case 27:
			return new KampfField("Sachsen");
			
		case 28:
			return new KampfField("Nordrhein-Westfalen");
			
		case 29:
			return new EWerkField();
			
		case 30:
			return new HopTopField();
			
		case 32:
			return new KampfField("Sachsen-Anhalt");
			
		case 33:
			return new KampfField("Niedersachsen");
			
		case 35:
			return new KampfField("Mecklenburg-Vorpommern");
			
		case 38:
			return new KampfField("Berlin");
		
		case 39:
		default:
			return new KampfField();
		}

	}
}
